package com.haulmont.thesis.crm.web.ui.cashmachineproduct;

import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.components.actions.ItemTrackingAction;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.thesis.crm.core.app.cashmachine.evotor.EvotorService;
import com.haulmont.thesis.crm.core.exception.ServiceNotAvailableException;
import com.haulmont.thesis.crm.entity.CashMachineProduct;
import com.haulmont.thesis.crm.entity.Cost;
import com.haulmont.thesis.crm.entity.ExhibitSpace;
import com.haulmont.thesis.crm.entity.ExtProject;
import com.haulmont.thesis.gui.components.TableSettingsPopupButton;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

public class CashMachineProductBrowser extends AbstractLookup {

    protected boolean isHideGroups = true;

    @Named("productsTable")
    protected GroupTable productsTable;
    @Named("removeBtn")
    protected PopupButton removeBtn;
    @Named("productsDs")
    protected GroupDatasource<CashMachineProduct, UUID> productsDs;

    @Inject
    protected TableSettingsPopupButton tableSettings;
    @Inject
    protected EvotorService evotorService;
    @Inject
    protected Metadata metadata;

    @Override
    public void ready(){
        productsTable.expandAll();
    }

    @Override
    public void init(Map<String, Object> params) {
        productsTable.addAction(initAddAction());
        productsTable.addAction(initRemoveSelectedAction());
        removeBtn.addAction(initRemoveSelectedAction());
        removeBtn.addAction(initRemoveAllByStoreAction());
        productsTable.addAction(initRemoveAllByStoreAction());

        initTableSettings();
    }

    protected void initTableSettings() {
        if (tableSettings == null) return;

        tableSettings.removeAllActions();
        tableSettings.removeAllLabels();
        tableSettings.removeAllSeparators();
        tableSettings.setChecked("hideGroups", isHideGroups);

        tableSettings.addAction(new BaseAction("hideGroups") {
            @Override
            public void actionPerform(Component component) {
                setShowGroups(!isHideGroups);
            }
        });
    }

    protected void setShowGroups(boolean isHideGroups) {
        Map<String, Object> params = new HashMap<>();
        params.put("hideGroups", isHideGroups);
        productsDs.refresh(params);

        this.isHideGroups = isHideGroups;
        setShowGroupsChecked(isHideGroups);
    }

    protected void setShowGroupsChecked(boolean showHideChecked) {
        if (tableSettings != null) {
            tableSettings.setChecked("hideGroups", showHideChecked);
        }
    }

    protected Action initAddAction() {
        return new BaseAction("add") {
            @Override
            public void actionPerform(Component component) {
                showLookup();
            }
        };
    }

    protected void showLookup() {
        openLookup("crm$Cost.browse", new Window.Lookup.Handler() {

            @Override
            public void handleLookup(Collection items) {
                if (items == null || items.isEmpty()) return;
                Map<UUID, CashMachineProduct> productMap = new HashMap<>();

                for (final Object item : items) {
                    Cost cost = (Cost) item;
                    // TODO: 05.06.2017 Подумать в какую группу добавлять услуги без проекта, но пока это обязательное условие
                    if (cost.getProject() != null) {
                        if (!productMap.containsKey(cost.getProject().getId()))
                            productMap.put(cost.getProject().getId(), getProductByProject(cost.getProject()));
                        if (!productMap.containsKey(cost.getId())) productMap.put(cost.getId(), getProductByCost(cost));
                    }
                }
                if (productMap.isEmpty()) return;
                showStoresOptionDialog(productMap);
            }
        }, WindowManager.OpenType.THIS_TAB);
    }

    protected CashMachineProduct getProductByProject(@Nonnull ExtProject project) {
        CashMachineProduct result = metadata.create(CashMachineProduct.class);
        result.setId(project.getId());
        result.setProductId(project.getId().toString());
        result.setCode(project.getCode());
        result.setName(project.getName());
        result.setPrice(0d);
        result.setQuantity(0d);
        result.setCostPrice(0d);
        result.setMeasureName("");
        result.setTaxCode("NO_VAT");
        result.setAllowToSell(true);
        result.setDescription("");
        result.setArticleNumber("");
        result.setIsGroup(true);

        return result;
    }

    protected CashMachineProduct getProductByCost(@Nonnull Cost cost) {
        CashMachineProduct result = metadata.create(CashMachineProduct.class);

        result.setId(cost.getId());
        result.setProductId(cost.getId().toString());
        result.setCode(cost.getCode());
        result.setName(cost.getProduct().getTitle_ru());
        result.setPrice(cost.getPrimaryCost().doubleValue());
        // TODO: 05.06.2017 add Barcodes string
        result.setQuantity(cost.getProduct().getMaxQuantity().doubleValue());
        result.setCostPrice(cost.getPrimaryCost().doubleValue());
        result.setMeasureName(cost.getProduct().getNomenclature().getUnit().getName_ru());
        result.setTaxCode(cost.getProduct().getNomenclature().getTax().getCode());
        // TODO: 05.06.2017 use InUse fields or add condition for filter on lookup screen
        result.setAllowToSell(true);
        result.setDescription(cost.getComment_ru());
        result.setArticleNumber(cost.getProduct().getCode());
        result.setParentUuid(cost.getProject().getId().toString());
        result.setIsGroup(false);

        return result;
    }

    protected void showStoresOptionDialog(@Nonnull Map<UUID, CashMachineProduct> productMap) {
        getDialogParams().setWidthAuto().setHeight(200);
        showOptionDialog(getMessage("ChoiceStoreTitle"),
                getMessage("chooseStoreQuestion"),
                MessageType.CONFIRMATION,
                getAddActions(productMap)
        );
        getDialogParams().reset();
    }

    protected Action[] getAddActions(Map<UUID, CashMachineProduct> productMap) {
        List<Action> actions = new ArrayList<>();
        final List<CashMachineProduct> added = new ArrayList<>();
        added.addAll(productMap.values());
        for (final ExhibitSpace store : getStores()) {
            BaseAction newAction = new BaseAction(store.getName_ru()) {
                @Override
                public void actionPerform(Component component) {
                    try {
                        evotorService.addProducts(store.getEvotorStoreId(), added);
                        getDsContext().refresh();
                    } catch (ServiceNotAvailableException e) {
                        showNotification(e.getMessage(), NotificationType.WARNING);
                    }
                }
            };
            actions.add(newAction);
        }
        actions.add(getAllStoresAddAction(added));
        actions.add(new DialogAction(DialogAction.Type.CANCEL, true));

        return actions.toArray(new Action[actions.size()]);
    }

    protected Action getAllStoresAddAction(final List<CashMachineProduct> added) {
        return new BaseAction(getMessage("allStoresAction")) {
            @Override
            public void actionPerform(Component component) {
                try {
                    evotorService.addProducts(added);
                    getDsContext().refresh();
                } catch (ServiceNotAvailableException e) {
                    showNotification(e.getMessage(), NotificationType.WARNING);
                }
            }
        };
    }

    protected Action initRemoveSelectedAction() {
        return new ItemTrackingAction(productsTable, "remove") {
            @Override
            public void actionPerform(Component component) {
                if (target.getSelected().size() > 0) {
                    showOptionDialog(
                            getMessage("confirmRemove.title"),
                            getMessage("confirmRemove.msg"),
                            MessageType.CONFIRMATION,
                            new Action[]{
                                    new DialogAction(DialogAction.Type.YES) {
                                        public void actionPerform(Component component) {
                                            Set<CashMachineProduct> selected = target.getSelected();
                                            for (ExhibitSpace store : getStores()) {
                                                Set<UUID> deleted = filerSelectedByStore(selected, store.getEvotorStoreId());
                                                if (!deleted.isEmpty()) {
                                                    removeProductsFromStore(store.getEvotorStoreId(), deleted);
                                                    getDsContext().refresh();
                                                }
                                            }
                                        }
                                    },
                                    new DialogAction(DialogAction.Type.NO, Action.Status.PRIMARY)
                            }
                    );
                }
            }
        };
    }

    protected Set<UUID> filerSelectedByStore(Set<CashMachineProduct> selected, final UUID storeId) {
        Set<UUID> result = new HashSet<>();
        for (CashMachineProduct item : selected) {
            if (item.getStoreId().equals(storeId)) {
                result.add(UUID.fromString(item.getProductId()));
            }
        }
        return result;
    }

    protected void removeProductsFromStore(UUID storeId, Set<UUID> deleted) {
        try {
            evotorService.deleteProducts(storeId, deleted);
        } catch (ServiceNotAvailableException e) {
            showNotification(e.getMessage(), NotificationType.WARNING);
        }
    }

    protected Action initRemoveAllByStoreAction() {
        return new BaseAction("removeAllByStore") {
            @Override
            public void actionPerform(Component component) {
                getDialogParams().setWidthAuto().setHeight(200);
                showOptionDialog(getMessage("ChoiceStoreTitle"),
                        getMessage("chooseStoreQuestion"),
                        MessageType.CONFIRMATION,
                        getRemoveActions()
                );
                getDialogParams().reset();
            }
        };
    }

    protected Action[] getRemoveActions() {
        List<Action> actions = new ArrayList<>();
        for (final ExhibitSpace store : getStores()) {
            BaseAction newAction = new BaseAction(store.getName_ru()) {
                @Override
                public void actionPerform(Component component) {
                    showOptionDialog(
                            getMessage("confirmRemove.title"),
                            getMessage("confirmRemove.msg"),
                            MessageType.CONFIRMATION,
                            new Action[]{
                                    new DialogAction(DialogAction.Type.YES) {
                                        public void actionPerform(Component component) {
                                            try {
                                                evotorService.deleteProducts(store.getEvotorStoreId());
                                                getDsContext().refresh();
                                            } catch (ServiceNotAvailableException e) {
                                                showNotification(e.getMessage(), NotificationType.WARNING);
                                            }
                                        }
                                    },
                                    new DialogAction(DialogAction.Type.NO, Action.Status.PRIMARY)
                            }
                    );
                }
            };
            actions.add(newAction);
        }
        actions.add(new DialogAction(DialogAction.Type.CANCEL, true));

        return actions.toArray(new Action[actions.size()]);
    }

    protected List<ExhibitSpace> getStores() {
        LoadContext loadContext = new LoadContext(ExhibitSpace.class).setView("_local");
        loadContext.setQueryString("select e from crm$ExhibitSpace e where e.evotorStoreId is not null");

        return getDsContext().getDataSupplier().loadList(loadContext);
    }
}