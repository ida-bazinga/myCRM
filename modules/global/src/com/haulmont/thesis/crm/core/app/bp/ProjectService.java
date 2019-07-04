/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.bp;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.thesis.crm.core.app.bp.efdata.Project;
import com.haulmont.thesis.crm.entity.ExtProject;
import com.haulmont.thesis.crm.entity.IntegrationResolver;
import com.haulmont.thesis.crm.entity.Log1C;
import com.haulmont.thesis.crm.entity.StatusDocSales;

import javax.annotation.ManagedBean;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * @author d.ivanov
 */
@ManagedBean(IProjectService.NAME)
public class ProjectService implements IProjectService {

    private static Log1C  log;
    private Work1C work1C;

    public boolean setProjectOne(Log1C log)
    {
        ProjectService.log = log;
        work1C = AppBeans.get(Work1C.class);
        work1C.setExtSystem(log.getExtSystem());
        return one();
    }

    private boolean one()
    {
        try
        {
            // обращаемся к веб-сервису и выводим результат
            String Id1C = work1C.setProject(xdto());
            // проверка запись в маппинг
            log.setExtId(Id1C);
            work1C.setIntegrationResolver(log.getEntityId(), log.getEntityName(), StatusDocSales.IN1C, Id1C);

            // запись в лог
            work1C.setLog1c(log, false);

            return true;
        }
        catch (Exception e)
        {
            log.setError(e.toString());
            work1C.setLog1c(log, true);

            return false;
        }
    }

    //заполняю данными XDTO веб-сервиса
    private Project xdto()
    {
        // 1 - получаю данные строки сущности из тезиса
        ExtProject row = getExtProject();
        // 2 - поиск соответсвий (маппинг)
        //************************
        IntegrationResolver integrationResolverProject = work1C.getIntegrationResolverEntityId(log.getEntityId(), row.getMetaClass().getName());
        String ref = (integrationResolverProject != null) ? integrationResolverProject.getExtId() : "";
        UUID rowGroup = (row.getParentProject() == null) ? UUID.fromString("00000000-0000-0000-0000-000000000000") : row.getParentProject().getId();
        IntegrationResolver integrationResolverGroup = work1C.getIntegrationResolverEntityId(rowGroup, row.getMetaClass().getName());
        String refGroup = (integrationResolverGroup != null) ? integrationResolverGroup.getExtId() : "00000000-0000-0000-0000-000000000000";
        //************************
        Project project = new Project();
        project.setRef(work1C.getNullToEmpty(ref));
        project.setNameRu(projectName(row));
        project.setNameEn(work1C.getNullToEmpty(row.getName_en()));
        project.setDateStart(work1C.dateToXMLGregorianCalendar(row.getDateStartFact()));
        project.setDateEnd(work1C.dateToXMLGregorianCalendar(row.getDateFinishFact()));
        project.setInstallationDate(work1C.dateToXMLGregorianCalendar(row.getInstallationDateFact()));
        project.setDeInstallationDate(work1C.dateToXMLGregorianCalendar(row.getDeinstallationDateFact()));
        project.setRefGroup(work1C.getNullToEmpty(refGroup));

        return project;
    }

    private String projectName(ExtProject item) {
        if (work1C.validate(item.getDateStartFact(), item.getDateFinishFact())) {
            SimpleDateFormat formatDate = new SimpleDateFormat("dd.MM.yyyy");
            String startFact = formatDate.format(item.getDateStartFact());
            String finishFact = formatDate.format(item.getDateFinishFact());
            String[] startFact_mas = startFact.split("\\.");
            String[] finishFact_mas = finishFact.split("\\.");

            if (work1C.validate(item.getFormat())) {
                if ("0040".equals(item.getFormat().getCode())) {
                    return item.getFullName_ru();
                }
            }
            if (startFact.equals(finishFact)) {
                return String.format("%s(%s)", item.getFullName_ru(), startFact);
            }
            if (startFact_mas[1].equals(finishFact_mas[1]) && startFact_mas[2].equals(finishFact_mas[2])) {
                return String.format("%s(%s-%s)", item.getFullName_ru(), startFact.substring(0, 2), finishFact);
            }
            if (!startFact_mas[1].equals(finishFact_mas[1]) && startFact_mas[2].equals(finishFact_mas[2])) {
                return String.format("%s(%s-%s)", item.getFullName_ru(), startFact.substring(0, 5), finishFact);
            }
            return String.format("%s(%s-%s)", item.getFullName_ru(), startFact, finishFact);
        } else {
            return String.format("%s(%s-%s)", item.getFullName_ru(), "н.д", "н.д");
        }
    }

    private ExtProject getExtProject() {
        Map<String, Object> params = new HashMap<>();
        params.put("view", "1c");
        params.put("id", log.getEntityId());
        return work1C.buildQuery(ExtProject.class, params);
    }
}