package com.haulmont.thesis.crm.web.ui.basicdoc;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.reports.entity.Report;
import com.haulmont.thesis.core.app.MorphologyTools;
import com.haulmont.thesis.core.entity.Contract;
import com.haulmont.thesis.core.entity.Doc;
import com.haulmont.thesis.core.entity.Project;
import com.haulmont.thesis.core.enums.MorphologyCase;
import com.haulmont.thesis.crm.core.app.bp.Work1C;
import com.haulmont.thesis.crm.entity.*;
import com.haulmont.thesis.crm.web.ui.app.NewThesisPrintManager;
import com.haulmont.thesis.web.actions.PrintReportAction;
import com.haulmont.thesis.web.ui.basic.editor.AbstractCardEditor;
import com.haulmont.thesis.web.ui.basicdoc.editor.AbstractDocEditor;
import com.haulmont.workflow.core.app.WfUtils;

import java.util.*;

public class SalesAbstractDocEditor<T extends Doc> extends AbstractDocEditor<T> {

    protected String windowId;
    protected String entityName;
    protected NewThesisPrintManager thesisPrintManager;
    protected AbstractCardEditor editor;
    protected String dialogHeader;
    protected Work1C work1C;
    protected boolean isTemplate;



    @Override
    public void ready() {
        super.ready();
        work1C = AppBeans.get(Work1C .class);
        work1C.setExtSystem(work1C.getExtSystem(initialOrganization));
        isTemplate = getItem().getTemplate();
        entityName = getItem().getMetaClass().getName();
    }



    //убираем штрихкода; журнал
    @Override
    protected void initPrintActions(Doc doc) {
        thesisPrintManager = AppBeans.get(NewThesisPrintManager.class);
        windowId = this.getId();
        editor = this;
        dialogHeader = messages.getMessage(SalesAbstractDocEditor.class, "Имеются несохраненные изменения");
        reportList();
    }

    protected void reportList() {

        /*
        for (final DocKindReport report : getItem().getDocKind().getReports()) {
            addPrintActions(report.getReport());
        }
        */

        List<Report> reportList = thesisPrintManager.loadReports(windowId);
        for (Report report:reportList) {
            addPrintActions(report);
        }
    }


    protected void addPrintActions(final Report report) {
        final Doc item = getItem();
        String actionId = WfUtils.Translit.toTranslit(report.getName().toLowerCase().trim()).replace(" ", "_");
        printButton.addAction(new AbstractAction(actionId) {
            @Override
            public void actionPerform(Component component) {
                if (item != null) {
                    final Collection<Entity> entities = new ArrayList<>();
                    entities.add(item);
                    if (editor.needShowSaveCardDialog()) {
                        editor.showOptionDialog(dialogHeader, getDialogMessage(), IFrame.MessageType.CONFIRMATION,
                                new Action[]{
                                        new DialogAction(DialogAction.Type.YES, true) {
                                            @Override
                                            public void actionPerform(Component component) {
                                                if (editor.commit()) {
                                                    editor.reopen();
                                                    thesisPrintManager.printBulkReport(report, entities, false);
                                                }
                                            }
                                        },
                                        new DialogAction(DialogAction.Type.NO)
                                }
                        );
                    } else {
                        thesisPrintManager.printBulkReport(report, entities, false);
                    }

                } else {
                    showNotification(messages.getMessage(this.getClass(), "Не указаны обязательные параметры"), IFrame.NotificationType.HUMANIZED);
                }

            }
            @Override
            public String getCaption() {
                return report.getLocName() != null ? report.getLocName() : report.getName();
            }
        });
    }

    public String getDialogMessage() {
        Entity item = editor.getItem();
        MorphologyTools morphologyTools = AppBeans.get(MorphologyTools.NAME);
        String cardType = morphologyTools.getEntityCaption(item.getClass(), userSession.getLocale(), MorphologyCase.ACCUSATIVE);
        String message = messages.formatMessage(PrintReportAction.class, "printUnsave.msgPattern", cardType);
        if (message == null) {
            return messages.getMessage(PrintReportAction.class, "printUnsave.msg");
        }
        return message;
    }



    @Override
    protected boolean preCommit() {

        if (isTemplate) {
            return true;
        }

        if (entityName.equals("crm$OrdDoc")) {
            OrdDoc doc = (OrdDoc) getItem();
            if (!validateCompany(doc.getCompany())) {
                return false;
            }

            return preCommitOrdDoc(doc);
        }

        if (entityName.equals("crm$InvDoc")) {
            InvDoc doc = (InvDoc) getItem();
            if (!validateCompany(doc.getCompany())) {
                return false;
            }

            return preCommitInvDoc(doc);
        }

        if (entityName.equals("crm$AcDoc")) {
            AcDoc doc = (AcDoc) getItem();
            if (!validateCompany(doc.getCompany())) {
                return false;
            }

            return preCommitAcDoc(doc);
        }

        return true;
    }

    protected IntegrationResolver integrationResolverUpdateStatus(UUID Id, StatusDocSales statusDocSales) {
        IntegrationResolver ir = work1C.setIntegrationResolver(Id, entityName, statusDocSales, null);
        return ir;
    }

    protected boolean preCommitOrdDoc(OrdDoc doc) {
        if (doc.getIntegrationResolver() == null) {
            IntegrationResolver ir = integrationResolverUpdateStatus(doc.getId(), StatusDocSales.NEW);
            doc.setIntegrationResolver(ir);
        }
        return true;
    }

    protected boolean preCommitInvDoc(InvDoc doc) {
        if (doc.getIntegrationResolver() == null) {
            IntegrationResolver ir = integrationResolverUpdateStatus(doc.getId(), StatusDocSales.NEW);
            doc.setIntegrationResolver(ir);
        }

        return true;
    }

    protected boolean preCommitAcDoc(AcDoc doc) {
        if (doc.getIntegrationResolver() == null) {
            IntegrationResolver ir = integrationResolverUpdateStatus(doc.getId(), StatusDocSales.NEW);
            doc.setIntegrationResolver(ir);
        }
        return true;
    }

    protected void messageInfo(String masage) {
        showNotification(masage, NotificationType.TRAY);
    }

    protected boolean validateCompany(ExtCompany extCompany) {
        if (extCompany == null) {
            messageInfo("Не выбран контрагент!");
            return false;
        }
        return validateCompanyParams(getCompany(extCompany.getId()));
    }

    private boolean validateCompanyParams(ExtCompany company) {
        if (company.getCompanyType() == null) {
            messageInfo( "Не заполнено поле \"Вид\" в контрагенте!");
            return false;
        }

        if (company.getFullName() == null || company.getFullName().isEmpty()) {
            messageInfo( "Не заполнено поле \"Название для документов\" в контрагенте!");
            return false;
        }

        if(company.getExtLegalAddress() == null) {
            messageInfo( "Не заполнено поле \"Юридический адрес\" в контрагенте!");
            return false;
        }

        if(company.getExtLegalAddress().getAddressFirstDoc() == null) {
            messageInfo( "Не заполнено поле \"Адрес для первичных документов\" в юр. адресе котрагента!");
            return false;
        }

        if (company.getCompanyType().equals(CompanyTypeEnum.branch)) {
            if(company.getParentCompany() == null) {
                messageInfo( "Не заполнено поле \"Головной контрагент\" в контрагенте!");
                return false;
            }
        }

        if (company.getCompanyType().equals(CompanyTypeEnum.legal) ||
                company.getCompanyType().equals(CompanyTypeEnum.government) ||
                company.getCompanyType().equals(CompanyTypeEnum.branch))
        {
            if (company.getInn() == null) {
                messageInfo("Не заполнено поле \"ИНН\" в контрагенте!");
                return false;
            }

            if (company.getCompanyType().equals(CompanyTypeEnum.legal)) {
                if (company.getInn().length() != 10) {
                    messageInfo("ИНН должен быть 10 цифр!");
                    return false;
                }
            }

            if (company.getCompanyType().equals(CompanyTypeEnum.person)) {
                if (company.getInn().length() != 12) {
                    messageInfo("ИНН должен быть 12 цифр!");
                    return false;
                }
            }

            if(company.getKpp() == null) {
                messageInfo( "Не заполнено поле \"КПП\" в контрагенте!");
                return false;
            }
        }

        return true;
    }

    protected boolean validateProject(Project project) {
        if (project == null) {
            messageInfo("Не выбран \"Проект\"!");
            return false;
        }
        return validateProjectParams(getProject(project.getId()));
    }

    private boolean validateProjectParams(ExtProject project) {
        if (project.getDateStartFact() == null) {
            messageInfo( "Не заполнено поле \"Начало проведения\" в проекте!");
            return false;
        }

        if(project.getDateFinishFact() == null) {
            messageInfo( "Не заполнено поле \"Завершение проведения\" в проекте!");
            return false;
        }

        return true;
    }

    protected boolean validateContract(Contract contract) {
        if (contract == null) {
            messageInfo("Не выбран \"Договор\"!");
            return false;
        }
        return validateContractParams(contract);
    }

    private boolean validateContractParams(Contract contract) {
        if (contract.getNumber().length() > 20) {
            messageInfo("Номер \"Договора\" больше 20 символов. Отправка в 1С запрещена.");
            return false;
        }

        return true;
    }

    protected <T> boolean validateDetails(Collection<T> details) {
        if (details == null || details.size() < 1)  {
            messageInfo("Не выбраны Услуги!");
            return false;
        }

        return true;
    }

    protected <T> boolean validateDoc(T doc) {
        if (PersistenceHelper.isNew(doc)) {
            messageInfo("Сохраните документ!");
            return false;
        }

        return true;
    }

    protected ExtCompany getCompany(UUID companyId) {
        Map<String, Object> params = new HashMap<>();
        params.put("view", "1c");
        params.put("id", companyId);
        return work1C.buildQuery(ExtCompany.class, params);
    }

    protected ExtProject getProject(UUID projectId) {
        Map<String, Object> params = new HashMap<>();
        params.put("view", "1c");
        params.put("id", projectId);
        return work1C.buildQuery(ExtProject.class, params);
    }


}