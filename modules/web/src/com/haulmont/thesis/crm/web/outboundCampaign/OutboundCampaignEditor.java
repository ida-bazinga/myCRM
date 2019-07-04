package com.haulmont.thesis.crm.web.outboundCampaign;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.gui.components.DateField;
import com.haulmont.cuba.gui.components.LookupPickerField;
import com.haulmont.cuba.gui.data.ValueListener;
import com.haulmont.thesis.core.entity.Employee;
import com.haulmont.thesis.crm.entity.PriorityEnum;
import com.haulmont.thesis.web.actions.PrintReportAction;
import com.haulmont.thesis.web.ui.basicdoc.editor.AbstractDocEditor;
import com.haulmont.workflow.core.app.WfUtils;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.Label;
import org.apache.commons.lang.StringUtils;
import com.haulmont.cuba.core.entity.Entity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import com.haulmont.thesis.crm.entity.OutboundCampaign;

import javax.inject.Inject;

public class OutboundCampaignEditor extends AbstractDocEditor<OutboundCampaign> {

    @Inject
    protected LookupPickerField project, docCategory;

    @Inject
    private DataManager dataManager;

    @Inject
    protected DateField startTime;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        project.addListener(new ValueListener<Object>() {
            @Override
            public void valueChanged(Object source, String property, Object prevValue, Object value) {
                setName();
            }
        });
        docCategory.addListener(new ValueListener<Object>() {
            @Override
            public void valueChanged(Object source, String property, Object prevValue, Object value) {
                setName();
            }
        });
        startTime.addListener(new ValueListener<Object>() {
            @Override
            public void valueChanged(Object source, String property, Object prevValue, Object value) {
                setName();
            }
        });
    }

    @Override
    protected void initNewItem(OutboundCampaign item) {    //Значения для новой записи
        super.initNewItem(item);
        //item.setMaxAttemptCount(3);
        //item.setPriority(PriorityEnum.medium);
        List<Employee> employee = loadEmployee();   //Сотрудники
        if (!employee.isEmpty())item.setOwner(employee.get(0));
    }

    @Override
    protected String getHiddenTabsConfig() {
        return "correspondenceHistoryTab,docLogTab,cardLinksTab,securityTab,versionsTab,docTreeTab";
    }

    @Override
    public void setItem(Entity item) {
        super.setItem(item);
        printButton.addAction(new PrintReportAction("printExecutionList", this, "printDocExecutionListReportName"));
    }

    @Override
    protected Component createState() {
        if (WfUtils.isCardInState(getItem(), "New") || StringUtils.isEmpty(getItem().getState())) {
            Label label = componentsFactory.createComponent(Label.NAME);
            label.setValue(StringUtils.isEmpty(getItem().getState()) ? "" : getItem().getLocState());
            return label;
        } else {
            return super.createState();
        }
    }

    @Override
    protected void fillHiddenTabs() {
        hiddenTabs.put("office", getMessage("office"));
        hiddenTabs.put("attachmentsTab", getMessage("attachmentsTab"));
        hiddenTabs.put("docTreeTab", getMessage("docTreeTab"));
        hiddenTabs.put("cardCommentTab", getMessage("cardCommentTab"));
        super.fillHiddenTabs();
    }

    public void setName() { //Генератор названия (Контрагент [Проект])
        StringBuilder nameProject = new StringBuilder();
        StringBuilder startTime = new StringBuilder();
        StringBuilder category = new StringBuilder();
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.YYYY");
        OutboundCampaign item = getItem();
        if (item.getDocCategory().getName()!=null) category.append(item.getDocCategory().getName());
        if (item.getProject()!=null) {
            nameProject.append(" ");
            nameProject.append(item.getProject().getName());
        }
        if (item.getStartTime()!=null) {
            startTime.append(" ");
            startTime.append(dateFormat.format(item.getStartTime()));
        }

        getItem().setTheme(String.format("%s %s %s",nameProject.toString(), category.toString(), startTime.toString()));
        getItem().setDescription(String.format("%s %s %s",nameProject.toString(), category.toString(), startTime.toString()));
    }

    private List<Employee> loadEmployee() { //Поиск организации по коду
        LoadContext loadContext = new LoadContext(Employee.class)
                .setView("_local");
        loadContext.setQueryString("select e from df$Employee e where e.user.id = :user")
                .setParameter("user", userSession.getCurrentOrSubstitutedUser());
        //.setParameter("user", AppBeans.get(UserSessionSource.class).getUserSession().getCurrentOrSubstitutedUser());
        return dataManager.loadList(loadContext);
    }
}