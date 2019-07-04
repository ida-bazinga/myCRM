package com.haulmont.thesis.crm.web.ui.activity;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.impl.DsListenerAdapter;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.thesis.core.entity.Employee;
import com.haulmont.thesis.crm.core.app.contactcenter.CallerIdService;
import com.haulmont.thesis.crm.core.app.MyUtilsService;
import com.haulmont.thesis.crm.core.config.CrmConfig;
import com.haulmont.thesis.crm.entity.*;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

public class ActivityEditor<T extends Activity> extends AbstractEditor<T> {

    protected String incomingPhoneNumber;
    protected List<ExtCompany> companyList = new ArrayList<>();

    @Named("activityDs")
    protected Datasource<Activity> activityDs;
    @Named("communicationsDs")
    protected CollectionDatasource<Communication, UUID> communicationsDs;
    @Named("contactPersonsDs")
    protected CollectionDatasource<ExtContactPerson, UUID> contactPersonsDs;

    @Named("company")
    protected LookupPickerField companyFiled;
    @Named("contact")
    protected PickerField contactField;
    @Named("communication")
    protected LookupPickerField communicationField;
    @Named("result")
    protected LookupField resultFiled;
    @Named("details")
    protected TextArea detailsFiled;

    @Named("redirectedBox")
    protected BoxLayout redirectedBox;
    @Named("recallBox")
    protected BoxLayout recallBox;
    @Named("header")
    protected BoxLayout headerBox;
    @Named("separator")
    protected BoxLayout separator;
    @Named("commBox")
    protected BoxLayout commBox;
    @Named("phoneBox")
    protected BoxLayout phoneBox;
    @Named("mainBox")
    protected BoxLayout mainBox;
    @Named("scheduleCallDateBox")
    protected BoxLayout scheduleCallDateBox;
    @Named("scheduleCallDate")
    protected DateField scheduleCallDate;
    @Named("details")
    protected TextArea details;

    @Inject
    protected Metadata metadata;
    @Inject
    protected CrmConfig config;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected UserSession userSession;
    @Inject
    protected CallerIdService callerIdService;

    protected MyUtilsService myUtilsService;

    @Override
    public void init(Map<String, Object> params) {
        getDialogParams().setWidth(600).setHeight(530);

        if  (params.get("phone") != null){
            incomingPhoneNumber = (String) params.get("phone");
        }

        activityDs.addListener(new DsListenerAdapter<Activity>() {
            @Override
            public void valueChanged(Activity source, String property, Object prevValue, Object value) {
                if ("result".equals(property)) {
                    ActivityResult result = (ActivityResult) value;
                    if (result == null) return;
                    redirectedBox.setVisible(result.getResultType().equals(ActivityResultTypeEnum.REDIRECT));
                    //TODO создание уведомления
                    //recallBox.setVisible(result.getResultType().equals(ActivityResultTypeEnum.CALLBACKLATER));
                    scheduleCallDateBox.setVisible(result.getResultType().equals(ActivityResultTypeEnum.CALLBACKLATER));
                    scheduleCallDate.setRequired(result.getResultType().equals(ActivityResultTypeEnum.CALLBACKLATER));
                    scheduleCallDate.setRequiredMessage(String.format("Заполните поле %s", getMessage("scheduleCallDate")));
                    details.setRequired(result.getIsNeedDetails());

                }
                if ("campaign".equals(property)){
                    OutboundCampaign campaign =(OutboundCampaign)value;
                    if (campaign != null) getItem().setProject((ExtProject) campaign.getProject());
                }
            }
        });
    }

    @Override
    protected void postValidate(ValidationErrors errors) {
        if (getItem().getPhone() == null && communicationField.getValue() == null) {
            errors.add(getMessage("emptyComm"));
        }
    }

    @Override
    protected boolean preCommit() {
        Communication comm =  communicationField.getValue();
        if (comm != null) {
            getItem().setPhone(comm.getMaskedAddress());
        }

        if(PersistenceHelper.isNew(getItem()) && getItem().getResult().getResultType().equals(ActivityResultTypeEnum.CALLBACKLATER)) {
            //создаем задачу
            createTaskgScheduleCallDate();
        }
        return true;
    }

    @Override
    protected void initNewItem(T item) {
        item.setCampaign(getActivityDefaultCampaign());
        item.setOwner(getEmployee(userSession.getCurrentOrSubstitutedUser()));

        if (incomingPhoneNumber != null) {
            item.setDirection(CallDirectionEnum.INBOUND);
            item.setPhone(incomingPhoneNumber);

            if (incomingPhoneNumber.length() > 6){
                companyList.addAll(callerIdService.getCompaniesByPhoneNumber(incomingPhoneNumber));
            }

            if (companyList.size() == 1) {
                item.setCompany(companyList.get(0));
            }
            commBox.setVisible(false);
            addContactPersonAction();
            addCommunicationAction();

        } else {
            item.setDirection(CallDirectionEnum.OUTBOUND);
            companyList.add(item.getCompany());
            commBox.setVisible(true);
        }

        for (Component comp :  mainBox.getComponents()){
            if (comp instanceof PickerField){
                ((PickerField)comp).setEditable(true);
            }else if (comp instanceof Field){
                ((Field)comp).setEditable(true);
            }
        }
        companyFiled.setEditable(true);
    }

    @Override
    public void postInit() {
        if (getItem().getResult() != null && getItem().getResult().getResultType() != null) {
            redirectedBox.setVisible(getItem().getResult().getResultType().equals(ActivityResultTypeEnum.REDIRECT));
        }

        if (getItem().getResult() == null) {
            resultFiled.setEditable(true);
            detailsFiled.setEditable(true);
        }

        boolean hideHeader = getItem().getOperatorSession() != null;
        headerBox.setVisible(hideHeader);
        separator.setVisible(hideHeader);

        phoneBox.setVisible(getItem().getPhone() != null);
        contactField.addOpenAction();
        companyFiled.setOptionsList(companyList);
    }

    private void addCommunicationAction() {
        communicationField.addAction(new BaseAction("add") {
            @Override
            public void actionPerform(Component component) {
                if (getItem().getContact() == null) {
                    showNotification("Выберите контакт", NotificationType.HUMANIZED);
                    return;
                }
                final Communication newComm = metadata.create(Communication.class);
                newComm.setContactPerson(getItem().getContact());
                Window editor = openEditor("crm$Communication.edit", newComm, WindowManager.OpenType.DIALOG);
                editor.addListener(new CloseListener() {
                    @Override
                    public void windowClosed(String actionId) {
                        communicationsDs.refresh();
                        getItem().setCommunication(newComm);
                    }
                });
            }

            @Override
            public String getIcon() {
                return "icons/plus-btn.png";
            }

            @Override
            public String getCaption() {
                return null;
            }
        });
    }

    private void addContactPersonAction() {
        //contactField.removeAction("clear");
        contactField.addAction(new BaseAction("add") {
            @Override
            public void actionPerform(Component component) {
                final ExtContactPerson newEntity = metadata.create(ExtContactPerson.class);
                newEntity.setCompany(getItem().getCompany());
                Window editor = openEditor("df$ContactPerson.edit", newEntity, WindowManager.OpenType.DIALOG);
                editor.addListener(new CloseListener() {
                    @Override
                    public void windowClosed(String actionId) {
                        contactPersonsDs.refresh();
                        communicationsDs.refresh();
                        getItem().setContact(newEntity);
                    }
                });
            }

            @Override
            public String getIcon() {
                return "icons/plus-btn.png";
            }

            @Override
            public String getCaption() {
                return null;
            }
        });
    }

    protected OutboundCampaign getActivityDefaultCampaign() {
        LoadContext loadContext = new LoadContext(OutboundCampaign.class).setView("edit");
        loadContext.setQueryString("select e from crm$OutboundCampaign e where e.docKind.code = 'OCC' and e.docCategory.code = :code")
                .setParameter("code", config.getActivityDefaultCampaign());
        return dataManager.load(loadContext);
    }

    protected ExtEmployee getEmployee(Entity user) {
        LoadContext loadContext = new LoadContext(Employee.class).setView("_local");
        loadContext.setQueryString("select e from df$Employee e where e.user.id = :user")
                .setParameter("user", user);
        List<ExtEmployee> list = dataManager.loadList(loadContext);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public void ready() {
        Map<String, Object> param = new HashMap<>();
        param.put("ds$activityDs.company", getItem().getCompany());
        contactPersonsDs.refresh(param);
        if (PersistenceHelper.isNew(getItem()) && contactPersonsDs.size() == 1) {
            for (ExtContactPerson contactPerson : contactPersonsDs.getItems()) {
                getItem().setContact(contactPerson);
                contactField.setValue(getItem().getContact());
                param.clear();
                param.put("ds$activityDs.contact", contactPerson);
            }
            communicationsDs.refresh(param);
            if (communicationsDs.size() == 1) {
                for (Communication communication : communicationsDs.getItems()) {
                    getItem().setCommunication(communication);
                    communicationField.setValue(getItem().getCommunication());
                }
            }
        }
    }

    protected void createTaskgScheduleCallDate() {
        Map<String, Object> param = new HashMap<>();
        param.put("project", getItem().getProject());
        param.put("company", getItem().getCompany());
        param.put("scheduleCallDate", scheduleCallDate.getValue());
        param.put("details", getItem().getResultDetails());
        this.myUtilsService = AppBeans.get(MyUtilsService.class);
        this.myUtilsService.createTaskgScheduleCallDate(param);
    }

}