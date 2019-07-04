/*
 * Copyright (c) 2018 com.haulmont.thesis.crm.core.app.restApiService
 */
package com.haulmont.thesis.crm.core.app.restApiService;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.global.*;
import com.haulmont.thesis.core.app.TaskWorker;
import com.haulmont.thesis.core.entity.Task;
import com.haulmont.thesis.core.entity.TaskPattern;
import com.haulmont.thesis.crm.core.config.CrmConfig;
import com.haulmont.thesis.crm.entity.RoomSeatingType;
import com.haulmont.thesis.crm.entity.SeatingType;
import com.haulmont.workflow.core.app.WfEngineAPI;
import com.haulmont.workflow.core.entity.CardAttachment;
import com.haulmont.workflow.core.entity.CardRole;
import com.haulmont.workflow.core.entity.Proc;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

/**
 * @author d.ivanov
 */
@Service(CreateRoomRequestService.NAME)
public class CreateRoomRequestServiceBean implements CreateRoomRequestService {

    @Inject
    private DataManager dataManager;
    protected StringBuilder stringBuilder;
    protected String customerFullName, customerOrganizationName;
    protected TaskWorker taskWorker;
    protected WfEngineAPI wfEngineAPI;
    protected Persistence persistence;
    protected Messages messages;
    protected Log log = LogFactory.getLog(CreateRoomRequestServiceBean.class);



    public Boolean createRoomRequestService(String param) {
        log.warn(String.format("CreateRoomRequestServiceBean createRoomRequestService (param): %s", param));

        try {
            this.persistence = AppBeans.get(Persistence.class);
            this.taskWorker = AppBeans.get(TaskWorker.class);
            this.wfEngineAPI = AppBeans.get(WfEngineAPI.class);
            this.messages = AppBeans.get(Messages.NAME);
            this.stringBuilder = new StringBuilder();
            TaskPattern taskPattern = AppBeans.get(Configuration.class).getConfig(CrmConfig.class).getTaskRoomRequest();

            JsonParser jsonParser = new JsonParser();
            JsonElement json = jsonParser.parse(param);
            buildGeneral(json);

            JsonElement jsonContent = json.getAsJsonObject().get("content");
            buildConfiguration(jsonContent);
            buildAdditional(jsonContent);
            buildRequest(jsonContent);

            Task task = createTaskFromPattern(taskPattern);
            startProcess(task);

            return true;
        } catch (Exception e) {
            log.warn(String.format("CreateRoomRequestServiceBean createRoomRequestService: %s", e.getMessage()));
            return false;
        }
    }

    protected void buildGeneral(JsonElement json) {

        this.customerFullName = json.getAsJsonObject().get("customerFullName").getAsString();
        this.customerOrganizationName = json.getAsJsonObject().get("customerOrganizationName").getAsString();
        this.stringBuilder.append("Идентификатор заявки: ");
        this.stringBuilder.append(json.getAsJsonObject().get("id").getAsString());
        this.stringBuilder.append(System.lineSeparator());
        this.stringBuilder.append("ФИО отправителя заявки: ");
        this.stringBuilder.append(this.customerFullName);
        this.stringBuilder.append(System.lineSeparator());
        this.stringBuilder.append("Название организации: ");
        this.stringBuilder.append(this.customerOrganizationName);
        this.stringBuilder.append(System.lineSeparator());
        this.stringBuilder.append("Телефон контактного лица в заявке: ");
        this.stringBuilder.append(json.getAsJsonObject().get("customerPhone"));
        this.stringBuilder.append(System.lineSeparator());
        this.stringBuilder.append("Email контактного лица в заявке: ");
        this.stringBuilder.append(json.getAsJsonObject().get("customerEmail"));
        this.stringBuilder.append(System.lineSeparator());
        this.stringBuilder.append("Идентификатор тематики мероприятия в заявке: ");
        this.stringBuilder.append(getStringEventSubject(json.getAsJsonObject().get("eventSubjectId").getAsString()));
        this.stringBuilder.append(System.lineSeparator());
        this.stringBuilder.append("Комментарий к заявке: ");
        this.stringBuilder.append(json.getAsJsonObject().get("comment"));
    }


    protected void buildConfiguration(JsonElement jsonContent) {

        this.stringBuilder.append(System.lineSeparator());
        this.stringBuilder.append("-------------------------------------------");
        this.stringBuilder.append(System.lineSeparator());
        this.stringBuilder.append("---Выбранные конфигурации---");
        this.stringBuilder.append(System.lineSeparator());
        this.stringBuilder.append("-------------------------------------------");

        for (JsonElement jConfiguration : jsonContent.getAsJsonObject().get("configuration").getAsJsonArray()) {
            this.stringBuilder.append(System.lineSeparator());
            this.stringBuilder.append(" Идентификатор конфигурации: ");
            this.stringBuilder.append(getRoomSeatingTypeName(jConfiguration.getAsJsonObject().get("id").getAsString()));
            this.stringBuilder.append(System.lineSeparator());
            this.stringBuilder.append(" Начало мероприятия: ");
            this.stringBuilder.append(jConfiguration.getAsJsonObject().get("startDate"));
            this.stringBuilder.append(System.lineSeparator());
            this.stringBuilder.append(" Окончание мероприятия: ");
            this.stringBuilder.append(jConfiguration.getAsJsonObject().get("endDate"));
            this.stringBuilder.append(System.lineSeparator());
            this.stringBuilder.append(" Комментарий к конфигурации: ");
            this.stringBuilder.append(jConfiguration.getAsJsonObject().get("comment"));
            this.stringBuilder.append(System.lineSeparator());
            this.stringBuilder.append("   --------------------------------------------------------");
            this.stringBuilder.append(System.lineSeparator());
            this.stringBuilder.append("   Дополнительные услуги к конфигурации");
            this.stringBuilder.append(System.lineSeparator());
            this.stringBuilder.append("   --------------------------------------------------------");

            for (JsonElement jAdditional : jConfiguration.getAsJsonObject().get("additional").getAsJsonArray()) {
                this.stringBuilder.append(System.lineSeparator());
                this.stringBuilder.append("    Идентификатор доп. услуги к конфигурации: ");
                this.stringBuilder.append(getAdditinalConfig(jAdditional.getAsJsonObject().get("id").getAsString()));
                this.stringBuilder.append(System.lineSeparator());
                this.stringBuilder.append("    Текст доп. услуги к конфигурации: ");
                this.stringBuilder.append(jAdditional.getAsJsonObject().get("text"));
                this.stringBuilder.append(System.lineSeparator());
                this.stringBuilder.append("    -------------------------------------------------------------------");
            }
        }
    }

    protected void buildAdditional(JsonElement jsonContent) {

        this.stringBuilder.append(System.lineSeparator());
        this.stringBuilder.append("----------------------------------------------------");
        this.stringBuilder.append(System.lineSeparator());
        this.stringBuilder.append("---Дополнительные услуги к заявке---");
        this.stringBuilder.append(System.lineSeparator());
        this.stringBuilder.append("----------------------------------------------------");

        for (JsonElement jAdditional : jsonContent.getAsJsonObject().get("additional").getAsJsonArray()) {
            this.stringBuilder.append(System.lineSeparator());
            this.stringBuilder.append(" Идентификатор доп. услуги к заявке: ");
            this.stringBuilder.append(getAdditinalOrd(jAdditional.getAsJsonObject().get("id").getAsString()));
            this.stringBuilder.append(System.lineSeparator());
            this.stringBuilder.append(" Текст доп. услуги к заявке: ");
            this.stringBuilder.append(jAdditional.getAsJsonObject().get("text"));
            this.stringBuilder.append(System.lineSeparator());
            this.stringBuilder.append("-------------------------------------------------------");
        }
    }

    protected void buildRequest (JsonElement jsonContent) {

        this.stringBuilder.append(System.lineSeparator());
        this.stringBuilder.append("-------------------------------------------------------");
        this.stringBuilder.append(System.lineSeparator());
        this.stringBuilder.append("---Параметры подбора конфигурации---");
        this.stringBuilder.append(System.lineSeparator());
        this.stringBuilder.append("-------------------------------------------------------");

        for (JsonElement jRequest : jsonContent.getAsJsonObject().get("request").getAsJsonArray()) {
            this.stringBuilder.append(System.lineSeparator());
            this.stringBuilder.append(" Начало мероприятия: ");
            this.stringBuilder.append(jRequest.getAsJsonObject().get("startDate"));
            this.stringBuilder.append(System.lineSeparator());
            this.stringBuilder.append(" Окончание мероприятия: ");
            this.stringBuilder.append(jRequest.getAsJsonObject().get("endDate"));
            this.stringBuilder.append(System.lineSeparator());
            this.stringBuilder.append(" Идентификатор типа рассадки: ");
            this.stringBuilder.append(getSeatingTypeName(jRequest.getAsJsonObject().get("seatingTypeId").getAsString()));
            this.stringBuilder.append(System.lineSeparator());
            this.stringBuilder.append(" Вместимость: ");
            this.stringBuilder.append(jRequest.getAsJsonObject().get("capacity"));
            this.stringBuilder.append(System.lineSeparator());
            this.stringBuilder.append(" Площадь помещения: ");
            this.stringBuilder.append(jRequest.getAsJsonObject().get("square"));
            this.stringBuilder.append(System.lineSeparator());
            this.stringBuilder.append("--------------------------------------------------");
        }
    }

    protected Task createTaskFromPattern(TaskPattern pattern) {
        Transaction tx = persistence.createTransaction();
        Task newTask;
        try {
            EntityManager em = persistence.getEntityManager();
            newTask = taskWorker.cloneTask(pattern, true, true, true);
            newTask.setCreateDate(java.util.Calendar.getInstance().getTime());
            newTask.setTaskName(String.format("%s %s %s", pattern.getTaskName(), this.customerOrganizationName, this.customerFullName));
            newTask.setFullDescr(this.stringBuilder.toString());

            for (CardRole cr : newTask.getRoles()) {
                em.persist(cr);
            }

            for (CardAttachment c : newTask.getAttachments()) {
                em.persist(c);
            }

            Proc taskProc = taskWorker.getDefaultProc();
            newTask.setProc(taskProc);

            em.persist(newTask);

            tx.commit();
        } finally {
            tx.end();
        }
        return newTask;
    }

    protected void startProcess(Task task) {
        Transaction tx = persistence.createTransaction();
        try {
            wfEngineAPI.startProcess(task);
            tx.commit();
        } finally {
            tx.end();
        }
    }

    protected String getStringEventSubject(String Id) {
        if ("7".equals(Id)) {
            return "Презентация";
        }
        if ("8".equals(Id)) {
            return "Фестиваль";
        }
        if ("9".equals(Id)) {
            return "Спортивное мероприятие";
        }
        if ("10".equals(Id)) {
            return "Концерт";
        }
        if ("11".equals(Id)) {
            return "Ивент";
        }
        if ("12".equals(Id)) {
            return "Конкурсы";
        }
        if ("13".equals(Id)) {
            return "Ярмарка";
        }
        if ("14".equals(Id)) {
            return "Совещание";
        }
        if ("15".equals(Id)) {
            return "Съезд";
        }
        if ("16".equals(Id)) {
            return "Выставка-конференция";
        }
        if ("17".equals(Id)) {
            return "Конференция";
        }
        if ("18".equals(Id)) {
            return "Семинар";
        }
        if ("19".equals(Id)) {
            return "Форум";
        }
        if ("20".equals(Id)) {
            return "Конгресс";
        }
        if ("21".equals(Id)) {
            return "Выставка";
        }
        if ("22".equals(Id)) {
            return "Ассамблея";
        }
        return "";
    }

    protected String getAdditinalOrd(String Id) {
        if ("31".equals(Id)) {
            return "Кейтринг";
        }
        if ("32".equals(Id)) {
            return "Строительство выставочных стендов";
        }
        if ("33".equals(Id)) {
            return "Реклама, продвижение, PR";
        }
        if ("34".equals(Id)) {
            return "Регистрация участников";
        }
        if ("35".equals(Id)) {
            return "Художественное оформление";
        }
        if ("36".equals(Id)) {
            return "Флористическое оформление";
        }
        if ("37".equals(Id)) {
            return "Туристические услуги";
        }
        if ("38".equals(Id)) {
            return "Таможенно-логистические услуги";
        }
        if ("39".equals(Id)) {
            return "Инженерные слуги и услуги сязи";
        }
        if ("40".equals(Id)) {
            return "Временный персонал";
        }
        return "";
    }

    protected String getAdditinalConfig(String Id) {
        if ("23".equals(Id)) {
            return "AV, световое оборудование";
        }
        if ("24".equals(Id)) {
            return "Сценические конструкции, декорации";
        }
        if ("25".equals(Id)) {
            return "Мебель, аксессуары";
        }
        if ("26".equals(Id)) {
            return "Фуршет в зале";
        }
        if ("27".equals(Id)) {
            return "Оборудование для синхронного перевода";
        }
        if ("28".equals(Id)) {
            return "Организация трансляции";
        }
        if ("29".equals(Id)) {
            return "Оргтехника";
        }
        if ("30".equals(Id)) {
            return "Флористическое оформление";
        }
        return "";
    }

    protected String getRoomSeatingTypeName(String code) {
        LoadContext loadContext = new LoadContext(RoomSeatingType.class).setView("edit");
        loadContext.setQueryString("select e from crm$RoomSeatingType e where e.code = :code")
                .setParameter("code", code);
        List<RoomSeatingType> list = dataManager.loadList(loadContext);
        if (list.size() > 0) {
            for (RoomSeatingType roomSeatingType: list) {
                code = roomSeatingType.getName_ru();
            }
        }
        return code;
    }

    protected String getSeatingTypeName(String code) {
        if (!code.isEmpty()) {
           int i = Integer.valueOf(code);
           code = this.messages.getMessage(SeatingType.fromId(i));
        }
        return code;
    }

}