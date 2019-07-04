/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.ui.basic.browse;

import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.client.ClientConfig;
import com.haulmont.cuba.client.ClientConfiguration;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.Security;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.WindowParams;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.components.actions.RefreshAction;
import com.haulmont.cuba.gui.config.WindowConfig;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.settings.Settings;
import com.haulmont.cuba.security.entity.EntityOp;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.thesis.core.app.CardService;
import com.haulmont.thesis.core.app.UserSessionTools;
import com.haulmont.thesis.crm.core.app.service.ActivityService;
import com.haulmont.thesis.crm.core.config.CrmConfig;
import com.haulmont.thesis.crm.entity.BaseActivity;
import com.haulmont.thesis.crm.enums.ActivityStateEnum;
import com.haulmont.thesis.crm.web.CrmApp;
import com.haulmont.thesis.crm.web.ui.baseactivity.ActivityCreatorCloseListener;
import com.haulmont.thesis.crm.web.ui.baseactivity.ActivityHolderWindow;
import com.haulmont.thesis.web.actions.CardLinkAction;
import com.haulmont.thesis.web.actions.CardNewTabAction;
import com.haulmont.thesis.web.ui.common.RemoveCardNullChildAction;
import com.haulmont.workflow.core.app.WfService;
import com.haulmont.workflow.core.app.WfUtils;
import com.haulmont.workflow.core.entity.Card;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.*;

public class AbstractActivityCardBrowser<T extends BaseActivity> extends AbstractLookup { //extends AbstractCardBrowser<T>
    protected Log log = LogFactory.getLog(getClass());
    protected GroupTable cardsTable;
    protected GroupDatasource<T, UUID> cardsDs;
    protected PopupButton createButton;
    protected SplitPanel split;
    protected CrmConfig crmConfig;

    @Inject
    protected UserSession userSession;
    @Inject
    protected UserSessionTools userSessionTools;
    @Inject
    protected WfService wfService;
    @Inject
    protected Metadata metadata;
    @Inject
    protected CardService cardService;
    @Inject
    protected Security security;
    @Inject
    protected ClientConfiguration configuration;
    @Inject
    protected ActivityService activityService;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        crmConfig = configuration.getConfigCached(CrmConfig.class);

        findStandardComponents();

        Boolean paramMultiSelect = WindowParams.MULTI_SELECT.get(params);
        cardsTable.setMultiSelect(paramMultiSelect == null || paramMultiSelect);

        if (getFrame() instanceof Lookup) {
            cardsTable.setMultiSelect(BooleanUtils.isTrue(paramMultiSelect));
        }

        createActions();

        addLocStateColumn();

        createLookupValidator();

        initTableColoring();
    }

    protected void findStandardComponents() {
        cardsTable = getCardsTable();
        cardsDs = getCardDs();
        createButton = getCreateButton();
        split = getSplit();
    }

    public GroupTable getCardsTable() {
        return getComponentNN("cardsTable");
    }

    public GroupDatasource<T, UUID> getCardDs() {
        return getDsContext().getNN("cardsDs");
    }

    public SplitPanel getSplit() {
        return getComponent("split");
    }

    public PopupButton getCreateButton() {
        return getComponent("createButton");
    }

    protected void createActions() {
        createCreateActions();
        cardsTable.addAction(createEditAction());
        cardsTable.addAction(createRemoveAction());
        cardsTable.addAction(new RefreshAction(cardsTable));
        cardsTable.addAction(new AbstractAction("deleteNotification") {
            @Override
            public void actionPerform(Component component) {
                Set<Card> selected = cardsTable.getSelected();
                User user = userSession.getCurrentOrSubstitutedUser();
                for (Card card : selected) {
                    wfService.deleteNotifications(card, user);
                }

                cardsTable.getDatasource().refresh();
                CrmApp.getInstance().getAppWindow().reloadAppFolders();
            }

            @Override
            public String getCaption() {
                return messages.getMessage(getClass(), id);
            }

        });
        cardsTable.addAction(new CardLinkAction(cardsDs));
        cardsTable.addAction(new CardNewTabAction());
        createPrintAction();
    }

    protected void createCreateActions(){
        Set<MetaClass> activityTypes = getEntityTypes();
        if (!activityTypes.isEmpty()){
            for (MetaClass activityType : activityTypes){
                Action action = createCreateAction(activityType);
                cardsTable.addAction(action);
                createButton.addAction(action);
            }
        }
    }

    protected Action createCreateAction(final MetaClass activityType) {
        return new BaseAction(activityType.getName()) {
            {
                setShortcut(configuration.getConfigCached(ClientConfig.class).getTableInsertShortcut());
            }

            @Override
            public void actionPerform(Component component) {
                Map<String, Object> params = new HashMap<>();
                params.put("metaClassName", activityType.getName());

                final ActivityHolderWindow creator = openWindow("activityCreator", WindowManager.OpenType.DIALOG, params);
                creator.addListener(
                        new ActivityCreatorCloseListener(creator, WindowManager.OpenType.DIALOG, activityType.getName(), cardsDs)
                );
            }

            @Override
            public String getCaption() {
                return messages.getTools().getEntityCaption(activityType);
            }

            @Override
            public boolean isEnabled() {
                return security.isEntityOpPermitted(activityType, EntityOp.CREATE);
            }

            //@Override
            //protected void afterWindowClosed(Window window) {
            //    target.getDatasource().refresh();
            //}
        };
    }

    protected Action createEditAction() {
        return new EditAction(cardsTable) {
            {
                setShortcut(configuration.getConfigCached(ClientConfig.class).getTableEditShortcut());
            }

            @SuppressWarnings("unchecked")
            @Override
            public void actionPerform(Component component) {
                T selected = cardsTable.getSingleSelected();
                if (selected!= null){
                    openEditor((T) selected, WindowManager.OpenType.DIALOG);
                }
            }
        };
    }

    protected Set<MetaClass> getEntityTypes() {

        List<String> excludedTypes = crmConfig.getExcludedActivityKindMetaClasses();
        Collection<MetaClass> metaClasses = activityService.getCardTypes(excludedTypes);

        return new HashSet<>(metaClasses);
    }

    protected Action createRemoveAction() {
        return new RemoveCardNullChildAction("remove", this, cardsTable) {
            @SuppressWarnings("unchecked")
            @Override
            public boolean isRemoveActionEnabled(Card item) {
                return AbstractActivityCardBrowser.this.isRemoveActionEnabled((T) item);
            }
        };
    }

    protected boolean isRemoveActionEnabled(T card) {
        boolean isAdmin = userSessionTools.isCurrentUserAdministrator();
        boolean cardInNewState = WfUtils.isCardInState(card, ActivityStateEnum.NEW.getName());
        return isAdmin || (cardInNewState && userSession.getCurrentOrSubstitutedUser().equals(card.getSubstitutedCreator()));
    }

    protected void createPrintAction() {}

    protected void createLookupValidator() {
        final Card exclItem = getContext().getParamValue("exclItem");
        if (exclItem != null) {
            this.setLookupValidator(new Validator() {
                @Override
                public boolean validate() {
                    Card selectedCard = cardsTable.getSingleSelected();
                    if (selectedCard != null && cardService.isDescendant(exclItem, selectedCard,
                            metadata.getViewRepository().getView(Card.class, "_minimal"), "parentCard")) {
                        showNotification(getMessage("cardIsChild"), NotificationType.WARNING);
                        return false;
                    }
                    return true;
                }
            });
        }
    }

    protected void addLocStateColumn() {
        cardsTable.addGeneratedColumn("locState", new Table.ColumnGenerator<T>() {
            @Override
            public Component generateCell(T entity) {
                return new Table.PlainTextCell(entity.getLocState());
            }
        });
    }

    protected void initTableColoring() {
        cardsTable.addStyleProvider(new Table.StyleProvider<T>() {
            @Nullable
            @Override
            public String getStyleName(T entity, @Nullable String property) {
                if (entity != null && property != null)
                    return AbstractActivityCardBrowser.this.getStyleName(entity);
                return null;
            }
        });
    }

    protected String getStyleName(T entity) {
        return null;
    }

    protected void openEditor(T entity, WindowManager.OpenType openType) {
        Window window = openEditor(getWindowId(entity), entity, openType);
        window.addListener(new CloseListener() {
            @Override
            public void windowClosed(String actionId) {
                getDsContext().refresh();
            }
        });
    }

    protected String getWindowId(T entity) {
        MetaClass metaClass = metadata.getClassNN(entity.getKind().getEntityType());
        WindowConfig windowConfig = AppBeans.get(WindowConfig.NAME);
        return windowConfig.getEditorScreenId(metaClass);
    }

    // TODO: 04.04.2018 move to editor adapt for call script  for editor AbstractCardBrowser.java:392
    @Override
    public void applySettings(Settings settings) {
        super.applySettings(settings);
    }

    @Override
    public void saveSettings() {
        super.saveSettings();
    }
}
