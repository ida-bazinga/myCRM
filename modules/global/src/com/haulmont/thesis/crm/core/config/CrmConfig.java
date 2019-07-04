package com.haulmont.thesis.crm.core.config;

import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;
import com.haulmont.cuba.core.config.defaults.Default;
import com.haulmont.cuba.core.config.defaults.DefaultDouble;
import com.haulmont.cuba.core.config.defaults.DefaultInt;
import com.haulmont.cuba.core.config.defaults.DefaultString;
import com.haulmont.cuba.core.config.type.DateFactory;
import com.haulmont.cuba.core.config.type.Factory;
import com.haulmont.cuba.core.config.type.StringListTypeFactory;
import com.haulmont.cuba.core.entity.Category;
import com.haulmont.thesis.core.entity.Contract;
import com.haulmont.thesis.core.entity.DocCategory;
import com.haulmont.thesis.core.entity.TaskPattern;
import com.haulmont.thesis.crm.entity.*;

import java.util.Date;
import java.util.List;

/**
 * Created by k.khoroshilov on 28.06.2016.
 */
@Source(type = SourceType.APP)
public interface CrmConfig extends Config {

    @Property("crm.contactCentre.serviceAddress")
    @DefaultString("ws://localhost:8004/callservice")
    String getServiceAddress();

    @Property("crm.contactCentre.mainInboundCampaignCode")
    @DefaultString("ICC_CC")
    String getMainInboundCampaignCode();

    @Property("crm.contactCentre.activityDefaultCampaignCode")
    @DefaultString("OCC_DM")
    String getActivityDefaultCampaign();

    @Property("crm.characteristicType.code")
    @DefaultString("005")
    String getCharacteristicTypeNA();

    @Property("crm.externalSystem.daDataStandart")
    @Default("crm$ExternalSystem-761557FC-1B15-8AFF-18D5-39799C07EE6F-1c")
    ExternalSystem getExternelSystemDaDataStandart();

    @Property("crm.externalSystem.daDataSuggestion")
    @Default("crm$ExternalSystem-26065BD0-FFE5-4EE8-5B4D-22441D7160FB-1c")
    ExternalSystem getExternelSystemDaDataSuggestion();

    @Property("crm.evotor.restServiceURI")
    @DefaultString("https://api.evotor.ru/api")
    String getEvotorRestServiceURI();

    @Property("crm.evotor.storesURI")
    @DefaultString("/v1/inventories/stores/search")
    String getEvotorStoresURI();

    @Property("crm.evotor.devicesURI")
    @DefaultString("/v1/inventories/devices/search")
    String getEvotorDevicesURI();

    @Property("crm.evotor.productsURI")
    @DefaultString("/v1/inventories/stores/%s/products")
    String getEvotorProductsURI();

    @Property("crm.evotor.documentsURI")
    @DefaultString("/v1/inventories/stores/%s/documents")
    String getEvotorDocumentsURI();

    @Property("crm.evotor.authToken")
    @DefaultString("8d795172-20fb-414b-bd80-997e3952c3d5")
    String getAuthToken();

    @Property("crm.evotor.docTypes")
    @DefaultString("SELL,PAYBACK")
    String getDocTypes();

    @Property("crm.unisender.key")
    @DefaultString("5kiaipcazqitujziwpqpr3343pdr68u4nc5rg4jo")
    //@DefaultString("6srqmy6tbgss8c9sjffqzbuzsg4iw79pdjmyx3ka")
    String getUniKey();

    @Default("crm$ExternalSystem-01788A3B-B91E-0B37-99CD-55E6E82D8CB9-1c")
    ExternalSystem getExternelSystemBPEFI();

    @Default("crm$ExternalSystem-2C9F9FF7-AD54-5128-F1CA-D050833C54AE-1c")
    ExternalSystem getExternelSystemBPNeva();

    //формат даты = 2017-06-01 00:00:00.000
    @Source(type = SourceType.DATABASE)
    @Property("crm.bp.dateQuartal")
    @Factory(factory = DateFactory.class)
    Date getDataQuartal();

    @Source(type = SourceType.DATABASE)
    @Property("crm.activities.ActivityKindEmail")
    @Default("crm$ActivityKind-94CC288D-72A1-2D28-EDB7-9A25F24B8419-browse")   //crm$ActivityKind-94CC288D-72A1-2D28-EDB7-9A25F24B8419-browse
    ActivityKind getActivityKindEmail();

    //папка с логами
    @Source(type = SourceType.DATABASE)
    @Property("crm.folder.log")
    @Default("/logs/")
    String getFolderLog();

    @Source(type = SourceType.DATABASE)
    @Property("crm.contract.offerEfi")
    @Default("df$Contract-B67FA177-27E4-DFA0-2E49-FC30A3E4C046-_minimal")
    Contract getContractOfferEfi();

    @Source(type = SourceType.DATABASE)
    @Property("crm.contract.offerNeva")
    @Default("df$Contract-B67FA177-27E4-DFA0-2E49-FC30A3E4C046-_minimal")
    Contract getContractOfferNeva();

    @Source(type = SourceType.DATABASE)
    @Property("crm.docCategory.exhibitionProject")
    @Default("df$Category-39D620F9-C46C-8374-192A-ECF6FA82EB7A-_minimal")
    DocCategory getDocCategotyExhibitionProject();

    @Source(type = SourceType.DATABASE)
    @Property("crm.tax.20")
    @Default("crm$Tax-70739FA6-6555-0F55-C4A4-9FBA8E978924-_minimal")
    Tax getTax20();

    @Source(type = SourceType.DATABASE)
    @Property("crm.currency.rub")
    @Default("crm$Currency-3D5ACDCC-FF4D-DB88-04D4-A72827728AFD-_local")
    Currency getCurrencyRub();

    @Source(type = SourceType.DATABASE)
    @Property("crm.payOrd.category")
    //@Default("sys$Category-5D1E6CB1-FC64-2411-F612-C9AD3F9817AB-_minimal")
    @Default("sys$Category-040A3D09-126D-E1DA-70F2-994958EFD7A2-_minimal")
    Category getPayOrdCategory();

    @Property("crm.support.mail")
    @DefaultString("support@expoforum.ru")
    String getSupportMail();

    @Property("crm.unisender.timezone")
    @DefaultString("Europe/Moscow")
    String getLocalTimezone();

    @Source(type = SourceType.DATABASE)
    @Property("crm.catalog.url")
    @Default("http://vm-srv-efctlg.expoforum.ru/api/tezis/v1/configuration/change")
    String getCatalogUrl();

    @Source(type = SourceType.DATABASE)
    @Property("crm.task.roomRequest")
    @Default("tm$TaskPattern-c04577b6-2eab-4a35-dabf-0b91284b8c49-cloning")
    TaskPattern getTaskRoomRequest();

    @Source(type = SourceType.DATABASE)
    @Property("crm.task.scheduleCallDate")
    @Default("tm$TaskPattern-de2f4063-9b02-ebab-3919-c613e7ff8fce-cloning")
    TaskPattern getTaskScheduleCallDate();

    @Source(type = SourceType.DATABASE)
    @Property("crm.efi1c.url")
    @Default("http://vm-srv-zeta/test_efi.bp.83")
    String getEfi1cUrl();

    @Source(type = SourceType.DATABASE)
    @Property("crm.product.scheduleCallDate")
    @Default("crm$ProductType-5B1900B1-90C5-F024-5DC1-7E7C492BF790-_minimal")
    ProductType getProductTypeOrganizerService();

    @Source(type = SourceType.DATABASE)
    @Property("crm.nomenclature.advance")
    @Default("crm$Nomenclature-1a31d2ed-58e9-dd5c-ddda-0ac4c6da2877-_minimal")
    Nomenclature getNomenclatureAdvance();

    @Source(type = SourceType.DATABASE)
    @Property("crm.product.advance")
    @Default("crm$Product-92637745-2c1c-28d3-5dc5-e8bf814857dc-edit")
    Product getProductAdvance();

    @Property("crm.contactCentre.defaultIncomingCampaign")
    @DefaultString("ICC_Default")
    String getDefaultIncomingCampaign();

    @Property("crm.contactCentre.defaultOutboundCampaign")
    @DefaultString("OCC_Default")
    String getDefaultOutboundCampaign();

    @Property("crm.contactCentre.minOperatorQueue")
    @DefaultInt(3)
    int getMinOperatorQueue();

    @Property("crm.contactCentre.maxOperatorQueue")
    @DefaultInt(10)
    int getMaxOperatorQueue();

    @Property("crm.contactCentre.operatorQueueMultiplier")
    @DefaultDouble(0.1)
    double getOperatorQueueMultiplier();

    @Property("crm.contactCentre.dropCallMDelay")
    @DefaultInt(30000)
    int getDropCallMDelay();

    @Property("crm.contactCentre.maxCountCommunicationsForCampaign")
    @DefaultInt(3)
    int getMaxCountCommunicationsForCampaign();

    @Property("crm.unisender.maxCountCommunicationsForEmailCampaign")
    @DefaultInt(10)
    int getMaxCountCommunicationsForEmailCampaign();

    @Property("crm.contactCentre.minMainPartLengthForCampaign")
    @DefaultInt(7)
    int getMinMainPartLengthForCampaign();

    @Property("crm.contactCentre.defaultCampaignProcCode")
    @DefaultString("CallCampaignManagement")
    String getDefaultCampaignProcCode();

    // List of excluded BaseActivity in activity-kind-edit.getCardTypes
    @Property("crm.activityKindEditor.excludedMetaClasses")
    @Factory(factory = StringListTypeFactory.class)
    @DefaultString("crm$CallActivity")
    List<String> getExcludedActivityKindMetaClasses();

    // List of excluded Campaign in campaign-kind-edit.getCardTypes
    @Property("crm.campaignKindEditor.excludedMetaClasses")
    @Factory(factory = StringListTypeFactory.class)
    List<String> getExcludedCampaignKindMetaClasses();

    @Property("crm.contactPerson.namePattern")
    @DefaultString("{LL| }{FF| }{MM|}")
    String getContactPersonNamePattern();

    /*
    @Property("crm.webAddressPattern")
    @DefaultString("(https?:\\/\\/)+([А-яёЁA-z0-9]+(\\.)+)*([-А-яёЁA-z0-9_]+\\.)+([-А-яёЁA-z0-9_]+)(:[0-9]{2,10})*(\\/(.)*)*")
    String getWebAddressPattern();
    */
}
