package com.haulmont.thesis.crm.web.product;

import com.haulmont.cuba.core.app.UniqueNumbersService;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.ValueListener;
import com.haulmont.cuba.gui.settings.Settings;
import com.haulmont.thesis.crm.core.config.CrmConfig;
import com.haulmont.thesis.crm.entity.Cost;
import com.haulmont.thesis.crm.entity.Product;
import com.haulmont.thesis.crm.entity.ProductType;
import com.haulmont.thesis.web.ui.tools.AppIntegrationTools;
import org.apache.commons.lang.StringUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

public class ProductEditor<T extends Product> extends AbstractEditor<T> {

    @Inject
    protected AppIntegrationTools integrationTools;
    @Inject
    protected Datasource<T> mainDs;
    @Inject
    protected Table productCostsTable;
    @Inject
    private UniqueNumbersService un;
    @Inject
    protected CollectionDatasource<Cost, UUID> unitDs;
    @Inject
    protected CollectionDatasource<Cost, UUID> productCostsDs;
    @Inject
    protected TabSheet tabsheet;
    @Inject
    protected TextField title_ru, title_en; //, unit;
    @Inject
    protected LookupField productType, tourismProductType;
    @Inject
    protected Label projectLabel, exhibitSpaceLabel, eventOrganizerLabel, tourismProductTypeLabel;
    @Inject
    protected LookupPickerField characteristic, project, exhibitSpace;
    @Inject
    protected SearchPickerField nomenclature;
    @Inject
    protected CrmConfig config;

    @Named("eventOrganizer")
    protected PickerField eventOrganizer;

    @Override
    public void setItem(Entity item) {
        super.setItem(item);
        initAppIntegrationComponents();
    }

    @Override
    protected void initNewItem(T item) {
        if (item.getProductType() == null) {
            item.setProductType(config.getProductTypeOrganizerService());
        }
        if (item.getMinQuantity() == null) {
            item.setMinQuantity(BigDecimal.valueOf(1));
        }
        if (item.getMaxQuantity() == null) {
            item.setMaxQuantity(BigDecimal.valueOf(100000));
        }
    }

    @Override
    public void init (Map < String, Object > params){
       
        super.init(params);
        productCostsTable.addAction(new CreateAction(productCostsTable, WindowManager.OpenType.DIALOG){
            @Override
            public Map<String, Object> getInitialValues(){
                return Collections.<String, Object>singletonMap("product", mainDs.getItem());
            }
        });
        productCostsTable.addAction(new EditAction(productCostsTable, WindowManager.OpenType.DIALOG));
        productCostsTable.addAction(new RemoveAction(productCostsTable, false));
        productType.addListener(new ValueListener<Object>() {
            @Override
            public void valueChanged(Object source, String property, Object prevValue, Object value) {
                Product item = getItem();
                ProductType type = productType.getValue();
                switch (type.getCode()) { //Здесь и далее, "001" - усл. Площадки, "002" - Организатора, "003" - Мероприятия, "004" - Туристические
                    case "001":
                        eventOrganizer.setVisible(false);
                        project.setVisible(false);
                        exhibitSpace.setVisible(true);
                        eventOrganizer.setValue(null);
                        exhibitSpace.setValue(null);
                        eventOrganizerLabel.setVisible(false);
                        projectLabel.setVisible(false);
                        exhibitSpaceLabel.setVisible(true);
                        tourismProductTypeLabel.setVisible(false);
                        tourismProductType.setVisible(false);
                        tourismProductType.setValue(null);
                        break;
                    case "002":
                        eventOrganizer.setVisible(true);
                        project.setVisible(false);
                        exhibitSpace.setVisible(false);
                        project.setValue(null);
                        exhibitSpace.setValue(null);
                        eventOrganizerLabel.setVisible(true);
                        projectLabel.setVisible(false);
                        exhibitSpaceLabel.setVisible(false);
                        tourismProductTypeLabel.setVisible(false);
                        tourismProductType.setVisible(false);
                        tourismProductType.setValue(null);
                        break;
                    case "003":
                        eventOrganizer.setVisible(false);
                        project.setVisible(true);
                        exhibitSpace.setVisible(false);
                    	eventOrganizer.setValue(null);
                        exhibitSpace.setValue(null);
                        eventOrganizerLabel.setVisible(false);
                        projectLabel.setVisible(true);
                        exhibitSpaceLabel.setVisible(false);
                        tourismProductTypeLabel.setVisible(false);
                        tourismProductType.setVisible(false);
                        tourismProductType.setValue(null);
                        break;
                    case "004":
                        eventOrganizer.setVisible(false);
                        project.setVisible(false);
                        exhibitSpace.setVisible(false);
                    	eventOrganizer.setValue(null);
                        project.setValue(null);
                        exhibitSpace.setValue(null);
                        eventOrganizerLabel.setVisible(false);
                        projectLabel.setVisible(false);
                        exhibitSpaceLabel.setVisible(false);
                        tourismProductTypeLabel.setVisible(true);
                        tourismProductType.setVisible(true);
                        break;
                }
            }
        });
        
        
        //cardTabSheetHelper.addCounterOnTab(productCostsDs, tabsheet, "productCostsTab", null);
        //initLazyTabs();

       /*
       fieldGroup1.getField(characteristic).addListener(new ValueListener<Object>() {
            @Override
            public void valueChanged(Object source, String property, Object prevValue, Object value) {
                this.title_en = String.format("%s %s", nomenclature != null ? nomenclature.getPublicName_en() : "", characteristic != null ? characteristic.getName_en() : "" );
    			this.title_ru = String.format("%s %s", nomenclature != null ? nomenclature.getPublicName_ru() : "", characteristic != null ? characteristic.getName_ru() : "" );
    	
            }
        });
        */

       characteristic.addListener(new ValueListener<Object>() {
            @Override
            public void valueChanged(Object source, String property, Object prevValue, Object value) {
                    generateTitle();
            }
        });
       nomenclature.addListener(new ValueListener<Object>() {
            @Override
            public void valueChanged(Object source, String property, Object prevValue, Object value) {
                generateTitle();
            }
       });
       initDialogParams();
    };

    protected void generateTitle() {    //Создание названия услуги вынесено в отдельный метод для удобства
        Product item = getItem();

        String nomenNameRu = (item.getNomenclature() != null)? getItem().getNomenclature().getName_ru(): StringUtils.EMPTY;
        String nomenNameEn = (item.getNomenclature() != null)? getItem().getNomenclature().getPublicName_en() : StringUtils.EMPTY;
        String charNameRu = (item.getCharacteristic() != null) ? getItem().getCharacteristic().getName_ru() : StringUtils.EMPTY;
        String charNameEn = (item.getCharacteristic() != null) ? getItem().getCharacteristic().getName_en(): StringUtils.EMPTY;
        /*
        if(item!=null && item.getNomenclature()!=null){
            //unit.setValue(item.getNomenclature().getUnit().getFullName_ru());
        }
        */
        /*
        if (item.getNomenclature() != null) {
            //nomenNameRu = getItem().getNomenclature().getPublicName_ru();
            //nomenNameRu = getItem().getNomenclature().getName_ru();
        }
        if (item.getNomenclature() != null) {
            //nomenNameEn = getItem().getNomenclature().getPublicName_en();
            nomenNameEn = getItem().getNomenclature().getPublicName_en();
        }
        if (item.getCharacteristic() != null) {
            charNameRu = getItem().getCharacteristic().getName_ru();
        }
        if (item.getCharacteristic() != null) {
            charNameEn = getItem().getCharacteristic().getName_en();
        }
        */
        item.setTitle_en(String.format("%s %s", nomenNameEn, charNameEn));
        item.setTitle_ru(String.format("%s %s", nomenNameRu, charNameRu));
        //title_en.setValue(String.format("%s %s", nomenNameEn, charNameEn));
        //title_ru.setValue(String.format("%s %s", nomenNameRu, charNameRu));
    }

    @Override
    protected boolean preCommit() {
        if (PersistenceHelper.isNew(getItem())){
            Long l = un.getNextNumber("Product");
            getItem().setCode(String.format("%05d",l));
        }
        return true;
    }

    private void initLazyTabs() {
        tabsheet.addListener(new TabSheet.TabChangeListener() {
            public void tabChanged(TabSheet.Tab newTab) {
                String tabName = newTab.getName();
                /*
                if ("mainTab".equals(tabName) && !initializedTabs.contains(tabName)) {
                    AccountsFrame accountsFrame = getComponent("accountsFrame");
                    accountsFrame.setPropertyName("contractor");
                    accountsFrame.setParentDs(companyDs);
                    accountsFrame.init();
                    initializedTabs.add(tabName);
                } else if ("correspondenceHistoryTab".equals(newTab.getName())) {
                    CorrespondenceHistoryFrame historyFrame = getComponent("correspondenceHistoryFrame");
                    historyFrame.init();
                    historyFrame.refreshDs();
                } else if ("contractorLogTab".equals(newTab.getName())) {
                    EntityLogFrame contractorLogFrame = getComponentNN("contractorLogFrame");
                    contractorLogFrame.init(Collections.<String, Object>singletonMap(WindowParams.ITEM.toString(), companyDs.getItem()));
                }
                */
            }
        });
    }

    @Override
    public void applySettings(Settings settings) {
        super.applySettings(settings);
        getDialogParams().reset();
    }

    protected void initDialogParams() {
        getDialogParams().setWidth(1000);
        getDialogParams().setHeight(500);
    }

    protected void initAppIntegrationComponents() {
        integrationTools.initAppIntegrationComponents(this);
    }

    public void enqueueToExport() {
        integrationTools.enqueueToExport(this);
    }
    
    
        
}