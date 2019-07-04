/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.bp;


import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.*;
import com.haulmont.thesis.core.config.DefaultEntityConfig;
import com.haulmont.thesis.core.entity.Organization;
import com.haulmont.thesis.crm.core.app.bp.efdata.*;
import com.haulmont.thesis.crm.core.app.bp.efdata.PayOrd;
import com.haulmont.thesis.crm.core.app.bp.efwebservice1c.EfWebService1C;
import com.haulmont.thesis.crm.core.app.bp.efwebservice1c.EfWebService1CPortType;
import com.haulmont.thesis.crm.core.config.CrmConfig;
import com.haulmont.thesis.crm.entity.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.WebServiceException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

@ManagedBean(Work1C.NAME)
public class Work1C {

    public static final String NAME = "crm_Work1C";

    @Inject
    public DataManager dataManager;
    @Inject
    protected Configuration configuration;
    @Inject
    protected Messages messages;
    @Inject
    protected Metadata metadata;

    private Log logFactory = LogFactory.getLog(this.getClass());
    private ExtSystem extSystem;



    private URL getWsdlLocation(String connectionString) {
        URL url = null;
        try {
            url = new URL(connectionString);
        } catch (MalformedURLException ex) {
            WebServiceException e = new WebServiceException(ex);
            logFactory.error(String.format("getWsdlLocation: %s", e.toString()));
        }
        return url;
    }

    //конверт Date в XMLGregorianCalendar
    public XMLGregorianCalendar dateToXMLGregorianCalendar(Date date) {
        GregorianCalendar gCalendar = new GregorianCalendar();
        XMLGregorianCalendar xmlCalendar = null;
        if (date != null){
            gCalendar.setTime(date);
        } else {
            gCalendar.set(0001,01,01,00,00);
        }
        try {
            xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gCalendar);
        } catch (DatatypeConfigurationException e) {
            logFactory.info(String.format("dateToXMLGregorianCalendar: %s", e.toString()));
        }
        return xmlCalendar;
    }

    //конверт XMLGregorianCalendar в Date
    public Date XMLGregorianCalendarToDate(XMLGregorianCalendar dateXMLGregorianCalendar){
        if ("0001-01-01T00:00:00".equals(dateXMLGregorianCalendar.toString())) {
            return null;
        }
        return dateXMLGregorianCalendar.toGregorianCalendar().getTime();
    }

    //проверка на null, возвращаем Empty
    public String getNullToEmpty(String value)
    {
        return (value == null || "".equals(value) ? "" : value);
    }


    public String getCodeFormat(String code) {
        String[] m = code.split("-");
        return m[m.length-1];
    }

    public Boolean validate(Object... args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i] == null) {
                return false;
            }
        }
        return true;
    }
/*
    public String getStringDataQuartal() {
        String dateQuartal = null;
        Calendar c = new GregorianCalendar();
        c.setTime(java.util.Calendar.getInstance().getTime());
        int quarter = c.get(Calendar.MONTH)/3+1;
        int year = c.get(Calendar.YEAR);
        switch (quarter)
        {
            case 1: dateQuartal = String.format("%s0101", year);
                break;
            case 2: dateQuartal = String.format("%s0401", year);
                break;
            case 3: dateQuartal = String.format("%s0701", year);
                break;
            case 4: dateQuartal = String.format("%s1001", year);
                break;
        }
        return dateQuartal;
    }
*/

    public String getNDS_1c(String nds)
    {
        if("VAT_0".equals(nds)) {
            return "НДС0";
        }
        if("VAT_10".equals(nds)) {
            return "НДС10";
        }
        if("VAT_18".equals(nds)) {
            return "НДС18";
        }
        if("NO_VAT".equals(nds)) {
            return "БезНДС";
        }
        if("VAT_18_118".equals(nds)) {
            return "НДС18_118";
        }
        if("VAT_10_110".equals(nds)) {
            return "НДС10_110";
        }
        if("VAT_20".equals(nds)) {
            return "НДС20";
        }
        if("VAT_20_120".equals(nds)) {
            return "НДС20_120";
        }
        return "";
    }

    public String getTypeTransaction_1c(TypeTransactionDebiting type) {
        if (TypeTransactionDebiting.PAYSUPPLINER.equals(type)) {
            return "ОплатаПоставщику";
        }
        if (TypeTransactionDebiting.TRANSFERUNDERCONTRACT.equals(type)) {
            return "ПеречислениеСотрудникуПоДоговоруПодряда";
        }
        if (TypeTransactionDebiting.TRANSFERTAX.equals(type)) {
            return "ПеречислениеНалога";
        }
        return "";
    }

    public void setExtSystem(ExtSystem extSystem) {
        this.extSystem = extSystem;
    }

    public ExtSystem getExtSystem() {
        return this.extSystem;
    }

    public ExtSystem getExtSystem(Organization organization) {
        Organization organizationDefault = configuration.getConfig(DefaultEntityConfig.class).getOrganizationDefault();
        return organizationDefault.equals(organization) ? ExtSystem.BP1CEFI : ExtSystem.BP1CNEVA;
    }

    //запись в лог
    public void setLog1c(Log1C log, Boolean isError) {
        //меняем статус у взятой записи с лога
        //1-не обработано; 2-успешно; 3-ошибка
        log.setShortServiceOperationResults((isError) ? ServiceOperationResultsEnum.err : ServiceOperationResultsEnum.success);
        dataManager.commit(log);
    }

    //поиск в логе
    public Log1C getMyLog1C() {
        Map<String, Object> params = new HashMap<>();
        params.put("view", "1c");
        params.put("sort", "order by e.priority, e.startDate");
        params.put("shortServiceOperationResults", 1);
        return buildQuery(Log1C.class, params);
    }

    //поиск по entityId+++
    public IntegrationResolver getIntegrationResolverEntityId(UUID entityId, String entityName) {
        Map<String, Object> params = new HashMap<>();
        params.put("view", "1c");
        params.put("entityName", entityName);
        params.put("entityId", entityId);
        params.put("extSystem", extSystem);
        return buildQuery(IntegrationResolver.class, params);
    }

    //запись в IntegrationResolver новой строки или обновление Win
    public IntegrationResolver setIntegrationResolver(UUID entityId, String entityName, StatusDocSales status, String extId){
        IntegrationResolver integrationResolver = getIntegrationResolverEntityId(entityId, entityName);
        if (!validate(integrationResolver)) {
            integrationResolver = metadata.create(IntegrationResolver.class);
            integrationResolver.setDel(false);//*
            integrationResolver.setExtSystem(extSystem);
        }
        integrationResolver.setEntityId(entityId);//*
        integrationResolver.setEntityName(entityName);//*
        integrationResolver.setExtId(validate(extId) ? extId : integrationResolver.getExtId());
        if (integrationResolver.getDel()) {
            status = StatusDocSales.DELETE;
            integrationResolver.setPosted(false);
        } else {
            if (validate(integrationResolver.getExtId())) {
                if (entityName.equals(PersistenceHelper.getEntityName(OrdDoc.class)) || entityName.equals(PersistenceHelper.getEntityName(InvDoc.class))) {
                    integrationResolver.setPosted(true);
                } else {
                    integrationResolver.setPosted(false);
                }
            }
        }
        integrationResolver.setStateDocSales(status);//*

        return dataManager.commit(integrationResolver);
    }

    //[0] номенклатурная группа
    //[1] контрагент
    //[2] договор контрагента (создается авт в 1С)
    //[3] заказ
    //[4] счет
    //[5] реализация
    //[6] счет-фактура (создается авт в 1С)
    public Boolean regLog(Map<String, Object> params, int priority) {

        Set<Entity> toCommit = new HashSet<>();
        Object obj = params.get(PersistenceHelper.getEntityName(Nomenclature.class));
        if (obj != null) {
            toCommit.add(createLog1C(obj, PersistenceHelper.getEntityName(Nomenclature.class), priority));
        }

        obj = params.get(PersistenceHelper.getEntityName(ExtCompany.class));
        if (obj != null) {
            toCommit.add(createLog1C(obj, PersistenceHelper.getEntityName(ExtCompany.class), priority));
        }

        obj = params.get(PersistenceHelper.getEntityName(ExtProject.class));
        if (obj != null) {
            toCommit.add(createLog1C(obj, PersistenceHelper.getEntityName(ExtProject.class), priority));
        }

        obj = params.get(PersistenceHelper.getEntityName(OrdDoc.class));
        if (obj != null) {
            toCommit.add(createLog1C(obj, PersistenceHelper.getEntityName(OrdDoc.class), priority));
        }

        obj = params.get(PersistenceHelper.getEntityName(InvDoc.class));
        if (obj != null) {
            toCommit.add(createLog1C(obj, PersistenceHelper.getEntityName(InvDoc.class), priority));
        }

        obj = params.get(PersistenceHelper.getEntityName(AcDoc.class));
        if (obj != null) {
            toCommit.add(createLog1C(obj, PersistenceHelper.getEntityName(AcDoc.class), priority));
        }

        obj = params.get("crm$VatDoc");
        if (obj != null) {
            toCommit.add(createLog1C(obj, "crm$VatDoc", priority));
        }

        obj = params.get(PersistenceHelper.getEntityName(com.haulmont.thesis.crm.entity.PayOrd.class));
        if (obj != null) {
            toCommit.add(createLog1C(obj, PersistenceHelper.getEntityName(com.haulmont.thesis.crm.entity.PayOrd.class), priority));
        }

        if (toCommit.size() > 0)
        dataManager.commit(new CommitContext (toCommit));
        return true;
    }

    protected Log1C createLog1C (Object obj, String entityName, int priority) {
        Log1C log = metadata.create(Log1C.class);
        UUID entityId = UUID.fromString(obj.toString());
        try {
            Thread.sleep(200);
            log.setEntityName(entityName);
            log.setEntityId(entityId);
            log.setPriority(priority);
            log.setStartDate(Calendar.getInstance().getTime());
            log.setShortServiceOperationResults(ServiceOperationResultsEnum.notwork);
            log.setExtSystem(extSystem);
            return log;
        } catch (InterruptedException e) {
            logFactory.warn(String.format("createLog1C.InterruptedException: %s", e.toString()));
            return log;
        }
    }



    //для отображения статуса в справочниках (на формах)
    public String getStausIntegrationResolver(UUID Id, String entityName) {
        String status = getMassegePack(StatusDocSales.NEW);
        IntegrationResolver integrationResolver = getIntegrationResolverEntityId(Id, entityName);
        if (validate(integrationResolver)) {
            if (validate(integrationResolver.getExtId())) {
                status = getMassegePack(integrationResolver.getStateDocSales());
            }
        }
        return status;
    }

    public String getMassegePack(Enum e) {
        return messages.getMessage(e);
    }


    //справочники
    public ReferenceList getReferenceList(ParamsGeneric paramsList) {
        return  wsConnect().getReferenceList(paramsList);
    }

    //документы
    public DocList getDocList(ParamsGeneric paramsList)
    {
        return wsConnect().getDocList(paramsList);
    }

    //номенклатурные группы
    public String setProject(Project project)
    {
        return  wsConnect().setProject(project);
    }

    //контрагенты
    public String setAccount(Account account)
    {
        return wsConnect().setAccount(account);
    }

    //договор (не используется в новой верии)
    public String setContract(Pact contract)
    {
        return wsConnect().setContract(contract);
    }

    //номенклатура
    public String setNomenclature(Item nomenclature)
    {
        return wsConnect().setNomenclature(nomenclature);
    }

    //заявка
    public String setOrd(Doc doc)
    {
        return wsConnect().setOrd(doc);
    }

    //счет
    public String setInv(Doc doc)
    {
        return wsConnect().setInv(doc);
    }

    //реализация
    public String setAc(Doc doc)
    {
        return wsConnect().setAc(doc);
    }

    //счет-фактура
    public Vat setVat(Vat vat)
    {
        return wsConnect().setVat(vat);
    }

    //оплата
    public PayList getPayList()
    {
        return wsConnect().getPayList();
    }

    //ПКО
    public String setCash(Cash cash) { return wsConnect().setCash(cash);}

    //Дата закрытого периода
    public Date getDataDisable() {
        XMLGregorianCalendar dateXMLGregorianCalendar = wsConnect().getDisableDate();
        Date dateDisable = XMLGregorianCalendarToDate(dateXMLGregorianCalendar);
        return dateDisable;
    }

    //Платежное поручение
    public PayOrd setPayOrd(PayOrd doc) { return wsConnect().setPayOrd(doc); }

    //Статусы платежного поручения
    public ListParamStatus getStatusList(ListParamStatus status) { return wsConnect().getStatusList(status); }


    private EfWebService1CPortType wsConnect() {
        EfWebService1CPortType efWebService1CPortType = null;
        try {
            //логин и пароль веб-сервиса
            ExternalSystem externalSystem = configuration.getConfig(CrmConfig.class).getExternelSystemBPEFI();
            if (extSystem.equals(ExtSystem.BP1CNEVA)) {
                externalSystem = configuration.getConfig(CrmConfig.class).getExternelSystemBPNeva();
            }
            final String USERNAME_1C = externalSystem.getLogin();
            final String PASSWORD_1C = externalSystem.getPassword();
            URL WSDL_LOCATION = getWsdlLocation(externalSystem.getConnectionString());
            java.net.Authenticator.setDefault(new java.net.Authenticator() {
                @Override
                protected java.net.PasswordAuthentication getPasswordAuthentication() {
                    return new java.net.PasswordAuthentication(USERNAME_1C, PASSWORD_1C.toCharArray());
                }
            });
            // подключаемся к тегу service в wsdl описании
            EfWebService1C helloService = new EfWebService1C(WSDL_LOCATION);
            // получив информацию из тега service подключаемся к самому веб-сервису
            efWebService1CPortType = helloService.getEfWebService1CSoap();
            logFactory.info(String.format("wsConnect: %s", "Connect ws1C"));

        } catch (Exception e) {
            logFactory.error(String.format("wsConnect: %s", e.toString()));
            this.wsConnect();
        }
        return efWebService1CPortType;
    }

    /*
        Map<String, Object> params = new HashMap<>();
        params.put("view", "1c");
        params.put("sort", "order by e.priority, e.startDate");
        params.put("shortServiceOperationResults", 1);
        params.put("externalSystem.id", this.externalSystem.getId());
     */
    public <T> T buildQuery(Class<T> tClass, Map<String, Object> stringObjectMap) {
        int i = 0;
        String entityName = PersistenceHelper.getEntityName(tClass);
        Map<String, Object> params = new HashMap<>();
        String sort = (String)stringObjectMap.get("sort");
        String viewName = (String)stringObjectMap.get("view");
        LoadContext loadContext = new LoadContext(tClass);
        loadContext.setView(viewName);

        String queryStringDefault = String.format("select e from %s e where", entityName);
        for (Map.Entry entry:stringObjectMap.entrySet()) {
            String key = entry.getKey().toString();
            Object obj = entry.getValue();
            if (!"sort".equals(key) && !"view".equals(key)) {
                queryStringDefault = queryStringDefault + String.format(" e.%s=:p%s and", key, i);
                params.put(String.format("p%s", i), obj);
                i++;
            }
        }

        queryStringDefault = queryStringDefault.endsWith(" and") ? queryStringDefault.substring(0, queryStringDefault.length() - 3) : queryStringDefault;
        if (sort != null) {
            queryStringDefault = String.format("%s %s", queryStringDefault, sort);
        }

        LoadContext.Query query = loadContext.setQueryString(queryStringDefault);
        for (Map.Entry entry:params.entrySet()) {
            String key = entry.getKey().toString();
            Object obj = entry.getValue();
            if (!"sort".equals(key) && !"view".equals(key)) {
                query.setParameter(String.format("%s", key), obj);
            }
        }

        query.setMaxResults(1);
        return dataManager.load(loadContext);
    }

    /*
        Map<String, Object> params = new HashMap<>();
        params.put("view", "1c");
        params.put("sort", "order by e.priority, e.startDate");
        params.put("not", String.format("e.entityName in ('crm$OrdDoc','crm$InvDoc','crm$AcDoc') and (e.del is null or e.del = false) and e.extId is not null and e.createTs>='%s' and", work1C.getStringDataQuartal()));
        params.put("externalSystem.id", work1C.initExternalSystem().getId());
     */
    public <T> List<T> buildQueryList(Class<T> tClass, Map<String, Object> stringObjectMap) {
        int i = 0;
        String entityName = PersistenceHelper.getEntityName(tClass);
        Map<String, Object> params = new HashMap<>();
        String sort = (String)stringObjectMap.get("sort");
        String viewName = (String)stringObjectMap.get("view");
        LoadContext loadContext = new LoadContext(tClass);
        loadContext.setView(viewName);

        String queryStringDefault = String.format("select e from %s e where", entityName);
        for (Map.Entry entry:stringObjectMap.entrySet()) {
            String key = entry.getKey().toString();
            Object obj = entry.getValue();
            if (!"sort".equals(key) && !"view".equals(key)) {
                if ("not".equals(key)) {
                    queryStringDefault = String.format("%s %s", queryStringDefault, obj.toString());
                } else {

                    queryStringDefault = queryStringDefault + String.format(" e.%s=:p%s and", key, i);
                    params.put(String.format("p%s", i), obj);
                }
                i++;
            }
        }

        queryStringDefault = queryStringDefault.endsWith(" and") ? queryStringDefault.substring(0, queryStringDefault.length() - 3) : queryStringDefault;
        if (sort != null) {
            queryStringDefault = String.format("%s %s", queryStringDefault, sort);
        }

        LoadContext.Query query = loadContext.setQueryString(queryStringDefault);
        for (Map.Entry entry:params.entrySet()) {
            String key = entry.getKey().toString();
            Object obj = entry.getValue();
            if (!"sort".equals(key) && !"view".equals(key)) {
                query.setParameter(String.format("%s", key), obj);
            }
        }

        return (List<T>) dataManager.loadList(loadContext);
    }

}