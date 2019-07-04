package com.haulmont.thesis.crm.core.app.cashmachine.evotor.ws;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.haulmont.bali.util.Preconditions;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.TypedQuery;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.*;
import com.haulmont.thesis.crm.core.app.cashmachine.common.data.entity.*;
import com.haulmont.thesis.crm.core.app.cashmachine.common.data.enums.PaymentType;
import com.haulmont.thesis.crm.core.app.cashmachine.common.data.enums.TranType;
import com.haulmont.thesis.crm.core.app.cashmachine.evotor.parser.EvotorInfoParser;
import com.haulmont.thesis.crm.core.config.CrmConfig;
import com.haulmont.thesis.crm.core.exception.ServiceNotAvailableException;
import com.haulmont.thesis.crm.entity.Currency;
import com.haulmont.thesis.crm.entity.*;
import com.haulmont.thesis.crm.enums.CashDocumentType;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.ManagedBean;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.io.InterruptedIOException;
import java.math.BigDecimal;
import java.security.GeneralSecurityException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by k.khoroshilov on 23.04.2017.
 */
// TODO: 03.12.2017 Refactor to REST Template
@ManagedBean(EvotorManager.NAME)
public class EvotorManagerMBean implements EvotorManager {
    private Log log = LogFactory.getLog(getClass());

    protected static final String REST_DELETE_SUFFIX = "/delete";

    protected Map<UUID, ExhibitSpace> exhibitSpacesCache = new HashMap<>();
    protected Map<UUID, CashMachine> cashMachineCache = new HashMap<>();
    protected Map<String, Tax> taxesCache = new HashMap<>();
    protected Map<String, Unit> unitsCache = new HashMap<>();

    protected Currency currency;

    @Inject
    protected Persistence persistence;
    @Inject
    private DataManager dataManager;
    @Inject
    protected Metadata metadata;
    @Inject
    protected Configuration configuration;

    protected void updateCaches(){
        currency = getCurrency();

        for (ExhibitSpace space: getExhibitSpaces()){
            exhibitSpacesCache.put(space.getEvotorStoreId(), space);
        }

        for (CashMachine machine : getCashMachines()){
            cashMachineCache.put(machine.getEvotorId(), machine);
        }

        for (Tax tax: getTaxes()){
            taxesCache.put(tax.getCode(), tax);
        }

        for (Unit unit: getUnits()){
            unitsCache.put(unit.getCode(), unit);
        }
    }

    protected CrmConfig getConfig() {
        return configuration.getConfig(CrmConfig.class);
    }

    protected String getEvotorRestServiceURI(){
        return getConfig().getEvotorRestServiceURI();
    }

    protected String getAuthToken(){
        return getConfig().getAuthToken();
    }

    @Override
    public String getStoreInfo() throws ServiceNotAvailableException {
        StringBuilder result = new StringBuilder();
        List<StoreInfo> stores = getStores();
        for (StoreInfo store : stores) {
            result.append(store.toString()).append("\n");
        }
        return result.toString();
    }

    @Override
    public Set<UUID> getStoreUuids() throws ServiceNotAvailableException {
        Set<UUID> result = new HashSet<>();

        for(StoreInfo storeInfo : getStores()){
            result.add(storeInfo.getUuid());
        }
        return result;
    }

    protected List<StoreInfo> getStores() throws ServiceNotAvailableException {
        List<StoreInfo> result = new ArrayList<>();
        StringBuilder uri = new StringBuilder(getEvotorRestServiceURI());
        uri.append(getConfig().getEvotorStoresURI());

        try {
            EvotorServiceClient serviceClient = new EvotorServiceClient();
            String response = serviceClient.executeGet(uri.toString(), getAuthToken());
            EvotorInfoParser parser = new EvotorInfoParser(response);
            result = parser.parse(StoreInfo.class);
        } catch (GeneralSecurityException e) {
            log.error(e.getMessage());
        } catch (ParseException e) {
            log.error("ParseException" + e.getMessage());
        }
        return result;
    }

    @Override
    public List<CashMachine> getDevices() throws ServiceNotAvailableException {
        List<CashMachine> result = new ArrayList<>();
        updateCaches();
        StringBuilder uri = new StringBuilder(getEvotorRestServiceURI());
        uri.append(getConfig().getEvotorDevicesURI());
        try {
            EvotorServiceClient serviceClient = new EvotorServiceClient();
            String response = serviceClient.executeGet(uri.toString(), getAuthToken());

            EvotorInfoParser parser = new EvotorInfoParser(response);
            List<DeviceInfo> deviceInfos = parser.parse(DeviceInfo.class);

            for (DeviceInfo device : deviceInfos) {
                CashMachine machine = metadata.create(CashMachine.class);
                machine.setVendor("Эвотор");
                machine.setEvotorId(device.getUuid());
                machine.setSerialNumber(device.getId());
                machine.setName(device.getName());
                machine.setExhibitSpace(getExhibitSpaceByStoreId(device.getStoreUuid()));

                result.add(machine);
            }
        } catch (GeneralSecurityException e) {
            log.error(e.getMessage());
        } catch (ParseException e) {
            log.error(e.getMessage());
        }
        return result;
    }

    @Override
    public void updateDevices() throws ServiceNotAvailableException{
        Set<Entity> toCommit = new HashSet<>();
        updateCaches();
        for (CashMachine device : getDevices()) {
            CashMachine cashMachine = getCashMachine(device.getEvotorId());
            if (cashMachine == null){
                toCommit.add(device);
            }else{
                cashMachine.setVendor(device.getVendor());
                cashMachine.setSerialNumber(device.getSerialNumber());
                cashMachine.setExhibitSpace(device.getExhibitSpace());
                cashMachine.setName(device.getName());
                toCommit.add(cashMachine);
            }
        }
        dataManager.commit(new CommitContext(toCommit));
    }

    @Override
    public List<CashMachineProduct> getProducts(@Nonnull UUID storeId) throws ServiceNotAvailableException {
        Preconditions.checkNotNullArgument(storeId);

        updateCaches();
        List<CashMachineProduct> result = new ArrayList<>();

        StringBuilder uri = new StringBuilder(getEvotorRestServiceURI());
        uri.append(getConfig().getEvotorProductsURI());

        try {
            EvotorServiceClient serviceClient = new EvotorServiceClient();
            String response = serviceClient.executeGet(String.format(uri.toString(), storeId.toString().toUpperCase()), getAuthToken());

            EvotorInfoParser parser = new EvotorInfoParser(response);
            List<ProductsInfo> productsInfos = parser.parse(ProductsInfo.class);

            for (ProductsInfo item : productsInfos) {
                CashMachineProduct ep = metadata.create(CashMachineProduct.class);
                ep.setProductId(item.getUuid().toString());
                ep.setStoreId(storeId);
                ep.setExhibitSpace(getExhibitSpaceByStoreId(storeId));
                ep.setName(item.getName());
                ep.setCode(item.getCode());
                ep.setIsGroup(item.getGroup());
                if (item.getGroup()) {
                    ep.setParentUuid("");
                    ep.setProject(null);
                    ep.setMeasureName("");
                    ep.setUnit(null);
                    ep.setTaxCode("");
                    ep.setTax(null);
                    ep.setAllowToSell(true);
                    ep.setDescription("");
                    ep.setArticleNumber("");
                    ep.setPrice(0d);
                    ep.setCostPrice(0d);
                    ep.setQuantity(0d);
                    ep.setBarCodes("");
                } else {
                    if (item.getParentUuid() != null) {
                        ep.setParentUuid(item.getParentUuid().toString());
                        ep.setProject(getProject(item.getParentUuid()));
                    } else {
                        ep.setParentUuid("");
                        ep.setProject(null);
                    }
                    if (item.getMeasureUnit()!= null) {
                        ep.setMeasureName(item.getMeasureUnit().toString());
                        ep.setUnit(getUnit(item.getMeasureUnit().getValue()));
                    }
                    if (item.getTaxRate() != null) {
                        ep.setTaxCode(item.getTaxRate().toString());
                        ep.setTax(getTax(item.getTaxRate().toString()));
                    }
                    ep.setAllowToSell(item.getAllowToSell());
                    ep.setDescription(item.getDescription());
                    ep.setArticleNumber(item.getArticleNumber());
                    ep.setPrice(item.getPrice());
                    ep.setCostPrice(item.getCostPrice());
                    ep.setQuantity(item.getQuantity());
                    if (item.getBarCodes() != null) {
                        StringUtils.join(item.getBarCodes(), ",");
                    } else {
                        ep.setBarCodes("");
                    }
                }

                result.add(ep);
            }
        } catch (GeneralSecurityException e) {
            log.error("SecurityException " + e.getMessage());
        } catch (ParseException e) {
            log.error("ParseException" + e.getMessage());
        }
        return result;
    }


    @Override
    public void deleteProducts(@Nonnull UUID storeId, Set<UUID> products) throws ServiceNotAvailableException {
        Preconditions.checkNotNullArgument(storeId);

        StringBuilder uri = new StringBuilder(getEvotorRestServiceURI());
        uri.append(getConfig().getEvotorProductsURI());
        uri.append(REST_DELETE_SUFFIX);
        String json = null;

        try {
            if (products != null){
                EvotorInfoParser parser = new EvotorInfoParser();
                json = parser.populate(products);
            }
            EvotorServiceClient serviceClient = new EvotorServiceClient();
            serviceClient.executePost(String.format(uri.toString(), storeId.toString().toUpperCase()), getAuthToken(), json);
            log.info(String.format("products in store with uuid %s was be deleted", storeId));
        } catch (GeneralSecurityException e) {
            log.error(e.getMessage());
        } catch (InterruptedIOException e) {
            log.error(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void addProducts(@Nonnull UUID storeId, @Nonnull List<CashMachineProduct> added) throws ServiceNotAvailableException {
        Preconditions.checkNotNullArgument(storeId);
        Preconditions.checkNotNullArgument(added);

        StringBuilder uri = new StringBuilder(getEvotorRestServiceURI());
        uri.append(getConfig().getEvotorProductsURI());

        try {
            EvotorInfoParser parser = new EvotorInfoParser();
            String json = parser.populateProductsInfo(added);
            EvotorServiceClient serviceClient = new EvotorServiceClient();
            serviceClient.executePost(String.format(uri.toString(), storeId.toString().toUpperCase()), getAuthToken(), json);
        } catch (GeneralSecurityException e) {
            log.error(e.getMessage());
        } catch (InterruptedIOException e) {
            log.error(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    // TODO: 01.05.2017 add param String types. For extend if need is
    //  кассовые документы - SELL,PAYBACK,CASH_INCOME,CASH_OUTCOME,OPEN_SESSION,FPRINT,CLOSE_SESSION.
    //  инвентаризация - INVENTORY;
    //  приемка - ACCEPT;
    //  возврат поставщику - RETURN;
    //  списание - WRITE_OFF;
    //  акт переоценки - REVALUATION;
    //  вскрытие тары - OPEN_TARE.
    // TODO: 14.05.2017 Add filterDate
    // TODO: 03.12.2017 Add loop by deviceId param
    @Override
    public List<CashDocument> getSellDocs(@Nonnull UUID storeId, Date beginDate, Date endDate) throws ServiceNotAvailableException {
        Preconditions.checkNotNullArgument(storeId);

        updateCaches();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        List<CashDocument> result = new ArrayList<>();

        StringBuilder uri = new StringBuilder(getEvotorRestServiceURI());
        uri.append(getConfig().getEvotorDocumentsURI());
        Map<String, String> params = new HashMap<>();
        params.put("types", getConfig().getDocTypes());
        if (beginDate != null)  params.put("gtCloseDate", sdf.format(beginDate));
        if (endDate != null) params.put("ltCloseDate", sdf.format(endDate));

        try {
            EvotorServiceClient serviceClient = new EvotorServiceClient();
            String response = serviceClient.executeGet(String.format(uri.toString(), storeId.toString().toUpperCase()), getAuthToken(), params);
            EvotorInfoParser parser = new EvotorInfoParser(response);
            List<DocumentInfo> documentInfos = parser.parse(DocumentInfo.class);
            log.info(String.format("EVOTOR cloud. Store id: %s. Download %d documents", storeId, documentInfos.size()));

            for (DocumentInfo docInfo : documentInfos) {
                CashDocument cr = metadata.create(CashDocument.class);
                cr.setId(docInfo.getUuid());

                switch (docInfo.getType()) {
                    case SELL:
                        cr.setDocType(CashDocumentType.SELL);
                        break;
                    case PAYBACK:
                        cr.setDocType(CashDocumentType.PAYBACK);
                        break;
                    default:
                        cr.setDocType(CashDocumentType.UNKNOWN);
                        break;
                }
                cr.setDeviceId(docInfo.getDeviceId());
                cr.setCashMachine(getCashMachine(docInfo.getDeviceUuid()));
                cr.setOpenDate(docInfo.getOpenDate());
                cr.setOpenUserCode(docInfo.getOpenUserCode());
                cr.setOpenUserUuid(docInfo.getOpenUserUuid());
                cr.setCloseDate(docInfo.getCloseDate());
                cr.setCloseUserCode(docInfo.getCloseUserCode());
                cr.setCloseUserUuid(docInfo.getCloseUserUuid());
                cr.setSessionUUID(docInfo.getSessionUUID());
                cr.setSessionNumber(Integer.parseInt(docInfo.getSessionNumber()));
                cr.setNumber(docInfo.getNumber());
                cr.setCloseResultSum(Double.parseDouble(docInfo.getCloseResultSum()));
                cr.setCloseSum(Double.parseDouble(docInfo.getCloseSum()));
                cr.setExhibitSpace(getExhibitSpaceByStoreId(docInfo.getStoreUuid()));
                cr.setCompleteInventory(docInfo.getIsCompleteInventory());
                cr.setVer(docInfo.getVersion());

                DocTransactionsInfo docTranInfo = getFirstTranDocInfo(docInfo.getTransactions(), TranType.DOCUMENT_OPEN);
                if (docTranInfo != null) {
                    cr.setClientName(docTranInfo.getClientName());
                    cr.setClientPhone(docTranInfo.getClientPhone());
                    cr.setCouponNumber(docTranInfo.getCouponNumber());
                }

                docTranInfo = getFirstTranDocInfo(docInfo.getTransactions(), TranType.DOCUMENT_CLOSE_FPRINT);
                if (docTranInfo != null){
                    cr.setDocumentNumber(Integer.parseInt(docTranInfo.getDocumentNumber()));
                    cr.setReceiptNumber(Integer.parseInt(docTranInfo.getReceiptNumber()));
                    cr.setReceiptCreationDateTime(docTranInfo.getCreationDate());
                    cr.setReceiptSum(docTranInfo.getTotal());
                }

                List<DocTransactionsInfo> docPaymentInfos = getTranDocInfos(docInfo.getTransactions(), TranType.PAYMENT);
                if (!docPaymentInfos.isEmpty()) {
                    for (DocTransactionsInfo docPaymentInfo : docPaymentInfos) {
                        if (cr.getCashPaymentSum() == null) cr.setCashPaymentSum(0d);
                        if (cr.getCardPaymentSum() == null) cr.setCardPaymentSum(0d);

                        if (docPaymentInfo.getPaymentType().equals(PaymentType.CARD)) {
                            cr.setCardPaymentSum(docPaymentInfo.getSum());
                        }
                        if (docPaymentInfo.getPaymentType().equals(PaymentType.CASH)) {
                            if (cr.getCashPaymentSum() == null) cr.setCashPaymentSum(0d);
                            cr.setCashPaymentSum(cr.getCashPaymentSum() + docPaymentInfo.getSum());
                        }
                        cr.setCardPaymentNum(docPaymentInfo.getRrn());
                    }
                }

                List<DocTransactionsInfo> docPositionInfos = getTranDocInfos(docInfo.getTransactions(), TranType.REGISTER_POSITION);
                if (!docPositionInfos.isEmpty()) {
                    List<CashDocumentPosition> docPos = new ArrayList<>();

                    for (final DocTransactionsInfo docPositionInfo : docPositionInfos) {
                        CashDocumentPosition pos = metadata.create(CashDocumentPosition.class);
                        pos.setCashDocument(cr);

                        pos.setPosId(docPositionInfo.getId());
                        pos.setBarcode(docPositionInfo.getBarcode());
                        pos.setCommodityCode(docPositionInfo.getCommodityCode());
                        pos.setCommodityUuid(docPositionInfo.getCommodityUuid());
                        pos.setCost(getCostProductFromDb(docPositionInfo.getCommodityUuid()));
                        pos.setCommodityName(docPositionInfo.getCommodityName());
                        pos.setCostPrice(docPositionInfo.getCostPrice());
                        pos.setMeasureName(docPositionInfo.getMeasureName() != null ? docPositionInfo.getMeasureName().toString() : "");
                        pos.setUnit(getUnit(docPositionInfo.getMeasureName() != null ? docPositionInfo.getMeasureName().getValue() : null));
                        pos.setPrice(docPositionInfo.getPrice());
                        pos.setQuantity(docPositionInfo.getQuantity());
                        pos.setResultPrice(docPositionInfo.getResultPrice());
                        pos.setResultSum(docPositionInfo.getResultSum());
                        pos.setPosSum(docPositionInfo.getSum());
                        pos.setCurrency(currency);

                        //POSITION_TAX
                        List<DocTransactionsInfo> filteredList = Lists.newArrayList(Collections2.filter(
                                docInfo.getTransactions(), new Predicate<DocTransactionsInfo>() {
                                    @Override
                                    public boolean apply(DocTransactionsInfo input) {
                                        return input != null && input.getType().equals(TranType.POSITION_TAX) &&
                                                Integer.parseInt(input.getId()) == Integer.parseInt(docPositionInfo.getId()) + 1;
                                    }
                                }));
                        if (!filteredList.isEmpty()) {
                            DocTransactionsInfo taxInfo = filteredList.get(0);

                            pos.setResultTaxSum(taxInfo.getResultTaxSum());
                            pos.setTax(getTax(taxInfo.getResultSum(), taxInfo.getResultTaxSum()));
                            pos.setTaxPercent(taxInfo.getTaxPercent());
                            pos.setTaxSum(taxInfo.getTaxSum());
                        }

                        docPos.add(pos);
                    }
                    cr.setPositions(docPos);
                    cr.setQuantityPosition(docPositionInfos.size());
                }
                result.add(cr);
            }

        } catch (GeneralSecurityException e) {
            log.error("SecurityException " + e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return result;
    }

    protected List<DocTransactionsInfo> getTranDocInfos(@Nonnull List<DocTransactionsInfo> tranInfo, final TranType type) {
        Preconditions.checkNotNullArgument(tranInfo, "Document transactions info is not specified");

        return Lists.newArrayList(
                Collections2.filter(tranInfo, new Predicate<DocTransactionsInfo>() {
                    @Override
                    public boolean apply(@Nullable DocTransactionsInfo input) {
                        com.google.common.base.Preconditions.checkNotNull(input);
                        return input.getType().equals(type);
                    }
                })
        );
    }

    protected DocTransactionsInfo getFirstTranDocInfo(List<DocTransactionsInfo> tranInfo, final TranType type) {
        List<DocTransactionsInfo> filteredList = getTranDocInfos(tranInfo, type);

        return filteredList.isEmpty() ? null : filteredList.get(0);
    }

    protected CashMachine getCashMachine(UUID evotorId) {
        return cashMachineCache.get(evotorId);
    }

    protected List<CashMachine> getCashMachines() {
        LoadContext loadContext = new LoadContext(CashMachine.class).setView("edit");
        loadContext.setQueryString("select e from crm$CashMachine e");
        return dataManager.loadList(loadContext);
    }

    protected ExhibitSpace getExhibitSpaceByStoreId(UUID storeId) {
        return exhibitSpacesCache.get(storeId);
    }

    protected List<ExhibitSpace> getExhibitSpaces() {
        LoadContext loadContext = new LoadContext(ExhibitSpace.class).setView("edit");
        loadContext.setQueryString("select e from crm$ExhibitSpace e");
        return dataManager.loadList(loadContext);
    }

    public Currency getCurrency() {
        LoadContext loadContext = new LoadContext(Currency.class).setView("_local");
        loadContext.setQueryString("select e from crm$Currency e where e.code = :code")
                .setParameter("code", "643");
        return dataManager.load(loadContext);
    }

    protected Unit getUnit(String code) {
        return unitsCache.get(code);
    }

    protected List<Unit> getUnits() {
        LoadContext loadContext = new LoadContext(Unit.class).setView("_local");
        loadContext.setQueryString("select e from crm$Unit e");
        return dataManager.loadList(loadContext);
    }


    protected Tax getTax(double resultSum, double resultTaxSum){

        BigDecimal calcRate = new BigDecimal(resultSum/(resultSum-resultTaxSum) - 1d);
        BigDecimal roundedWithScale = calcRate.setScale(2, BigDecimal.ROUND_HALF_UP);
        for (Tax tax :taxesCache.values()){
            if (tax.getRate().toString().equals(roundedWithScale.toString())){
                return tax;
            }
        }
        return getTax("VAT_20");
    }

    protected Tax getTax(String code) {
        return taxesCache.get(code);
    }

    protected List<Tax> getTaxes() {
        LoadContext loadContext = new LoadContext(Tax.class).setView("_local");
        loadContext.setQueryString("select e from crm$Tax e");
        return dataManager.loadList(loadContext);
    }

    @Nullable
    protected ExtProject getProject(UUID id) {
        if (id == null) return null;
        LoadContext loadContext = new LoadContext(ExtProject.class).setId(id).setView("_minimal");
        return dataManager.load(loadContext);
    }

    @Nullable
    protected Cost getCostProductFromDb(UUID commodityUuid) {
        if (commodityUuid == null) return null;
        LoadContext loadContext = new LoadContext(Cost.class).setId(commodityUuid).setView("_minimal");
        return dataManager.load(loadContext);
    }

    // TODO: 05.06.2017  УДАЛИТЬ после тестирования
    protected List<CashMachineProduct> getProductsInfo() {
        List<CashMachineProduct> result = new ArrayList<>();
        Transaction tx = persistence.createTransaction();
        try {
            EntityManager em = persistence.getEntityManager();
            TypedQuery<CashMachineProduct> query = em.createNativeQuery(
                    "SELECT proj.ID as productId, proj.CODE as code, proj.NAME as [name], 0 as price, 0 as quantity, 0 as costPrice\n" +
                            ", '' measureName, 'NO_VAT' taxCode, '1' as allowToSell, '' as [description], '' as articleNumber, NULL as parentUuid\n" +
                            ", '1' as isGroup \n" +
                            "FROM CRM_COST c\n" +
                            "  JOIN CRM_PRODUCT p ON p.ID = c.PRODUCT_ID\n" +
                            "  JOIN CRM_NOMENCLATURE n ON n.ID = p.NOMENCLATURE_ID\n" +
                            "  LEFT JOIN TM_PROJECT proj ON proj.ID = c.PROJECT_ID \n" +
                            "WHERE n.id = ?1\n" +
                            "  AND n.DELETE_TS is null AND p.DELETE_TS is null AND c.DELETE_TS is null\n" +
                            "  AND (n.NOT_IN_USE = '0' OR n.NOT_IN_USE is null) AND (p.NOT_IN_USE = '0' OR p.NOT_IN_USE is null)\n" +
                            "UNION ALL\n" +
                            "SELECT c.ID as productId, c.[CODE] as code, n.PRINT_NAME_RU + ISNULL(' ' + ch.NAME_RU, '') as [name]\n" +
                            ", [PRIMARY_COST] as price, p.MAX_QUANTITY as quantity, [PRIMARY_COST] as costPrice, u.NAME_EN measureName\n" +
                            ", tax.Code as taxCode, '1' as allowToSell, ISNULL(c.COMMENT_RU, '') as [description], p.CODE as articleNumber\n" +
                            ", proj.id as parentUuid, '0' as isGroup \n" +
                            " FROM CRM_COST c\n" +
                            "  JOIN CRM_PRODUCT p ON p.ID = c.PRODUCT_ID\n" +
                            "  JOIN CRM_NOMENCLATURE n ON n.ID = p.NOMENCLATURE_ID\n" +
                            "  JOIN CRM_UNIT u ON u.id = n.UNIT_ID\n" +
                            "  LEFT JOIN CRM_CHARACTERISTIC ch ON ch.ID = p.CHARACTERISTIC_ID\n" +
                            "  LEFT JOIN TM_PROJECT proj ON proj.ID = c.PROJECT_ID\n" +
                            "  JOIN CRM_TAX tax ON tax.ID = n.TAX_ID \n" +
                            "WHERE n.id = ?1\n" +
                            "  AND n.DELETE_TS is null AND p.DELETE_TS is null AND c.DELETE_TS is null\n" +
                            "  AND (n.NOT_IN_USE = '0' OR n.NOT_IN_USE is null) AND (p.NOT_IN_USE = '0' OR p.NOT_IN_USE is null)", CashMachineProduct.class);
            query.setParameter(1, "a4a1de0c-b98c-cbe3-6abf-b033e3e50571");
            result = query.getResultList();
            tx.commit();
        } finally {
            tx.end();
        }
        return result;
    }
}
