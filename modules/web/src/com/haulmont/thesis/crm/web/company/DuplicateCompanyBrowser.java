package com.haulmont.thesis.crm.web.company;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.thesis.core.entity.Company;
import com.haulmont.thesis.crm.entity.DuplicateCompany;
import com.haulmont.thesis.crm.entity.DuplicateCompanyDetail;
import com.haulmont.thesis.crm.entity.DuplicateStatus;
import org.apache.commons.lang.StringUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

public class DuplicateCompanyBrowser<T extends DuplicateCompany> extends AbstractLookup {

    @Inject
    protected GroupTable duplicateCompanyTable;
    @Named("duplicateCompanyDs")
    protected Datasource<T> duplicateCompanyDs;
    @Inject
    private DataManager dataManager;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        duplicateCompanyTable.addAction(EditAction());
    }

    protected Action EditAction()  {
        return new EditAction(duplicateCompanyTable) {

            @Override
            public void actionPerform(Component component) {
                openWinEdit();
            }
        };
    }

    public void onEdit(Component source) {
        openWinEdit();
    }

    private void openWinEdit()
    {
        String txtMesage = duplicateCompanyTable.getSelected().size() == 0 ? "Не выбранны записи!" :
                !isDuplicateDetail() ? "Кол-во записей для объединения, должно быть больше 1 записи!" : "";
        if (!StringUtils.isEmpty(txtMesage)) {
            showNotification(getMessage(txtMesage), NotificationType.HUMANIZED);
            return;
        }

        Map<String, Object> params = new HashMap<>();
        DuplicateCompany duplicateCompany = duplicateCompanyTable.getSingleSelected();
        List<DuplicateCompanyDetail> duplicateCompanyDetailList = duplicateCompany.getDuplicateCompanyDetail();
        if (duplicateCompanyDetailList.size() > 0) {
            List<Company> extCompanyList = getCompanyList(duplicateCompanyDetailList);
            params.put("searchString", extCompanyList);
            params.put("duplicateCompany", duplicateCompany);
            Window window = openWindow("df$Company.mergingdup", WindowManager.OpenType.DIALOG, params);
            window.addListener(new CloseListener() {
                @Override
                public void windowClosed(String actionId) {
                    duplicateCompanyDs.refresh();
                }
            });
        }
    }

    private List<Company> getCompanyList(List<DuplicateCompanyDetail> duplicateCompanyDetailList)
    {
        List<Company> companyList = new ArrayList<>();
        for (DuplicateCompanyDetail duplicateCompanyDetail:duplicateCompanyDetailList) {
            companyList.add(duplicateCompanyDetail.getCompany());
        }
        return companyList;
    }

    private boolean isDuplicateDetail()
    {
        DuplicateCompany duplicateCompany = duplicateCompanyTable.getSingleSelected();
        LoadContext loadContext = new LoadContext(DuplicateCompanyDetail.class).setView("browse");
        loadContext.setQueryString("select e from crm$DuplicateCompanyDetail e where e.duplicateCompany.id = :Id").setParameter("Id", duplicateCompany.getId());
        List<DuplicateCompanyDetail> duplicateCompanyDetailList = dataManager.loadList(loadContext);
        if(duplicateCompanyDetailList.size() > 1) {
            return true;
        }
        if (duplicateCompany.getCountDuplicate() != duplicateCompanyDetailList.size()) {
            duplicateCompany.setCountDuplicate(duplicateCompanyDetailList.size());
            duplicateCompany.setStatus(DuplicateStatus.notduplicate);
            dataManager.commit(duplicateCompany);
        }
        return false;
    }

}