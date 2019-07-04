package com.haulmont.thesis.crm.web.nomenclature;

import com.haulmont.cuba.client.ClientConfiguration;
import com.haulmont.cuba.core.app.UniqueNumbersService;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.WindowParams;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.settings.Settings;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.thesis.core.app.OrgStructureService;
import com.haulmont.thesis.core.config.DefaultEntityConfig;
import com.haulmont.thesis.core.entity.Organization;
import com.haulmont.thesis.crm.core.app.bp.Work1C;
import com.haulmont.thesis.crm.core.config.CrmConfig;
import com.haulmont.thesis.crm.entity.*;
import com.haulmont.thesis.web.ui.entitylogframe.EntityLogFrame;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

public class NomenclatureEditor<T extends Nomenclature> extends AbstractEditor<T> {

    protected boolean confirmNotUse;

    @Named("nomenclatureDs")
    protected Datasource<T> mainDs;
    @Named("productsDs")
    protected CollectionDatasource<Product, UUID> productsDs;
    @Named("costsDs")
    protected CollectionDatasource<Cost, UUID> costsDs;
    @Named("tabSheet")
    protected TabSheet tabsheet;
    @Named("productsTable")
    protected Table productsTable;
    @Named("costsTable")
    protected Table costsTable;
    @Named("integrationStateLabel")
    protected Label integrationStateLabel;

    @Inject
    protected UniqueNumbersService un;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected CrmConfig config;
    @Inject
    protected UserSession userSession;
    @Inject
    protected Work1C work1C;
    @Inject
    protected Metadata metadata;
    @Inject
    protected UserSessionSource userSessionSource;
    @Inject
    protected ClientConfiguration configuration;
    @Inject
    protected OrgStructureService orgStructureService;

    protected DefaultEntityConfig defaultEntityConfig;


    @Override
    public void setItem(Entity item) {
        super.setItem(item);
        //notUseInitialValue= getItem().getNotInUse();
    }

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        defaultEntityConfig = configuration.getConfigCached(DefaultEntityConfig.class);
        productsTable.addAction(new CreateAction(productsTable, WindowManager.OpenType.DIALOG){
            @Override
            public Map<String,Object> getInitialValues(){
                return Collections.<String,Object>singletonMap("nomenclature", mainDs.getItem());
            }
        });
        productsTable.addAction(new EditAction(productsTable, WindowManager.OpenType.DIALOG));
        productsTable.addAction(new RemoveAction(productsTable, false));

        costsTable.addAction(new CreateAction(costsTable, WindowManager.OpenType.DIALOG){
            @Override
            public Map<String,Object> getInitialValues(){
                return Collections.<String,Object>singletonMap("product", productsDs.getItem());
            }
        });
        costsTable.addAction(new EditAction(costsTable, WindowManager.OpenType.DIALOG));
        costsTable.addAction(new RemoveAction(costsTable, false));

        //initLazyTabs();
        getDialogParams().setWidth(1000).setHeight(600).setResizable(true);

        initTableColoring();
    }

    @Override
    protected void postInit() {
        initWork1CExtSystem();
        integrationStateLabel.setValue(work1C.getStausIntegrationResolver(getItem().getId(), getItem().getMetaClass().getName()));
    }

    private void initWork1CExtSystem() {
        work1C.setExtSystem(work1C.getExtSystem(getItem().getOrganization()));
    }

    protected void initTableColoring() {
        productsTable.addStyleProvider(new Table.StyleProvider<Product>() {
            @Nullable
            @Override
            public String getStyleName(Product entity, @Nullable String property) {
                if (entity != null && property != null)
                    return NomenclatureEditor.this.getStyleName(entity);
                return null;
            }
        });
    }

    protected String getStyleName(Product product) {
        return (product.getNotInUse() != null && product.getNotInUse()) ? "thesis-task-finished" : null ;
    }

    @Override
    protected boolean preCommit() {
        T item = getItem();

        // TODO: 29.01.2017 Доделать проверку или listener
        /*if (!notUseInitialValue && item.getNotInUse()) {
            confirmNotUseProducts();
            if (!confirmNotUse) return false;
        }*/
        
        if (item.getPublicName_ru() == null) {item.setPublicName_ru(item.getPrintName_ru());}
        if (item.getPublicName_en() == null) {item.setPublicName_en(item.getPrintName_en());}
        if (PersistenceHelper.isNew(item)){
            Long l = un.getNextNumber("Nomenclature");
            item.setCode(String.format("%05d",l));
        }

        return true;
    }

    // TODO: 29.01.2017  сделать Listener на изменение галочки номенклатуры "не использовать"
    private void confirmNotUseProducts(){
        showOptionDialog(
                getMessage("confirm"),
                getMessage("confirmMsg"),
                MessageType.CONFIRMATION,
                new Action[]{
                        new DialogAction(DialogAction.Type.YES) {
                            public void actionPerform(Component component) {
                                confirmNotUse = true;
                                boolean nomNotUse = getItem().getNotInUse();
                                List<Product> products = getItem().getProducts();
                                if (products != null && !products.isEmpty()) {
                                    for (Product p : products) {
                                        if (nomNotUse) p.setNotInUse(true);
                                    }
                                }
                            }
                        },
                        new DialogAction(DialogAction.Type.NO){
                            public void actionPerform(Component component) {
                                confirmNotUse = false;
                            }
                        }
                }
        );
    }

    private void initLazyTabs() {
        tabsheet.addListener(new TabSheet.TabChangeListener() {
            public void tabChanged(TabSheet.Tab newTab) {
                String tabName = newTab.getName();
                if ("logTabCaption".equals(newTab.getName())) {
                    EntityLogFrame nomenclatureLogFrame = getComponentNN("nomenclatureLogFrame");
                    nomenclatureLogFrame.init(Collections.<String, Object>singletonMap(WindowParams.ITEM.toString(), mainDs.getItem()));
                }
            }
        });
    }

    // TODO: 29.01.2017 убрать?! 
    @Override
    public void applySettings(Settings settings) {
        super.applySettings(settings);
        getDialogParams().reset();
    }

    public void export1cBtn() {
        //to log1c
        if(PersistenceHelper.isNew(getItem())) {
            showNotification(getMessage("Сохраните Номенклатуру!"), NotificationType.HUMANIZED);
            return;
        }
        initWork1CExtSystem();
        int priority = userSessionSource.getUserSession().getRoles().contains("1СPriority") ? 0 : 1;
        Map<String, Object> params = new HashMap<>();
        params.put(getItem().getMetaClass().getName(), getItem().getId());
        boolean isNomenclature = work1C.regLog(params, priority);
        if (isNomenclature) {
            String txtMesage = "Номенклатура отправлена в 1С";
            showNotification(getMessage(txtMesage), NotificationType.HUMANIZED);
        }
    }

    @Override
    protected void initNewItem(Nomenclature item) {
        item.setTax(config.getTax20());
        List<CharacteristicType> characteristicType = loadCharacteristicType();              //Характеристика
        if (!characteristicType.isEmpty()) item.setCharacteristicType(characteristicType.get(0));
        if (item.getOrganization() == null) {
            Organization org = configuration.getConfigCached(DefaultEntityConfig.class).getOrganizationDefault();
            item.setOrganization(org != null ?
                    org : orgStructureService.getOrganizationByUser(userSession.getCurrentOrSubstitutedUser()));
        }
        if (item.getNomenclatureType() == null) {
            item.setNomenclatureType(NomenclatureTypeEnum.service);
        }
    }
    
    private List<CharacteristicType> loadCharacteristicType() {   //Поиск характеристики по коду
        LoadContext loadContext = new LoadContext(CharacteristicType.class)
                .setView("_local");
        loadContext.setQueryString("select e from crm$CharacteristicType e where e.code = :code")
                .setParameter("code", config.getCharacteristicTypeNA()).setMaxResults(1);
        return dataManager.loadList(loadContext);
    }

}