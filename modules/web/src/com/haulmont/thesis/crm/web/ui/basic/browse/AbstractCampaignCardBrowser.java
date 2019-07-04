/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.ui.basic.browse;

import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.client.ClientConfig;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Configuration;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.WindowParams;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.Window;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.config.WindowConfig;
import com.haulmont.cuba.security.app.UserSettingService;
import com.haulmont.cuba.security.entity.EntityOp;
import com.haulmont.thesis.crm.entity.BaseCampaign;
import com.haulmont.thesis.crm.enums.CampaignState;
import com.haulmont.thesis.crm.web.ui.campaign.CampaignCreatorCloseListener;
import com.haulmont.thesis.crm.web.ui.campaign.CampaignCreatorWindow;
import com.haulmont.thesis.web.ui.basic.browse.AbstractCardBrowser;
import com.haulmont.workflow.core.app.WfUtils;
import com.haulmont.workflow.core.entity.CardInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AbstractCampaignCardBrowser<T extends BaseCampaign> extends AbstractCardBrowser<T> {

    protected UserSettingService userSettingService;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        this.userSettingService = AppBeans.get(UserSettingService.class);
    }

    //todo make&move to createCampaignAction.class & from CreateCampaignMenuItemController -> com.haulmont.thesis.web.ui.common.actions.DocEditAction
    @Override
    protected Action createCreateAction() {
        return new CreateAction(cardsTable) {
            {
                ClientConfig config = AppBeans.get(Configuration.class).getConfig(ClientConfig.class);
                setShortcut(config.getTableInsertShortcut());
            }

            @Override
            public void actionPerform(Component component) {
                Map<String, Object> params = getParams();

                final CampaignCreatorWindow creator = openWindow("campaignCreator", WindowManager.OpenType.DIALOG, params);
                creator.addListener(
                        new CampaignCreatorCloseListener(creator, WindowManager.OpenType.THIS_TAB, getEntityMetaClass().getName(), cardsDs)
                );
            }

            @Override
            public String getCaption() {
                return getMessage("actions.Create");
            }

            @Override
            public boolean isEnabled() {
                MetaClass metaClass = cardsTable.getDatasource().getMetaClass();
                return security.isEntityOpPermitted(metaClass, EntityOp.CREATE);
            }

            @Override
            protected void afterWindowClosed(Window window) {
                target.refresh();
            }
        };
    }

    protected Map<String, Object> getParams(){
        Map<String, Object> params = new HashMap<>();
        params.put("metaClassName", getEntityMetaClass().getName());
        return params;
    }

    @Override
    protected Action createEditAction() {
        return new EditAction(cardsTable) {
            @SuppressWarnings("unchecked")
            @Override
            public void actionPerform(Component component) {
                Set selected = cardsTable.getSelected();
                if (selected.size() == 1) {
                    openEditor((T) selected.iterator().next());
                }
            }

            @Override
            protected void afterWindowClosed(Window window) {
                target.refresh();
            }
        };
    }

    @Override
    protected boolean isRemoveActionEnabled(T card) {
        boolean isAdmin = userSessionTools.isCurrentUserAdministrator();
        boolean cardInNewState = WfUtils.isCardInState(card, CampaignState.New.getName());
        return isAdmin || (cardInNewState && userSession.getCurrentOrSubstitutedUser().equals(card.getSubstitutedCreator()));
    }

    /**
     * Override this method to be implemented in subclasses.<br/>
     * Called by {@link #createCreateAction()} for create new entity and open editor.<br/>
     */
    protected MetaClass getEntityMetaClass() {
        return cardsTable.getDatasource().getMetaClass();
    }

    protected void openEditor(T entity) {
        openEditor(entity, null);
    }

    protected void openEditor(T entity, String columnId) {
        Window window = openEditor(getWindowId(), entity, getOpenType());
        window.addListener(new CloseListener() {
            @Override
            public void windowClosed(String actionId) {
                getDsContext().refresh();
            }
        });
    }

    protected String getWindowId() {
        MetaClass metaClass = cardsTable.getDatasource().getMetaClass();
        WindowConfig windowConfig = AppBeans.get(WindowConfig.NAME);
        return windowConfig.getEditorScreenId(metaClass);
    }

    protected WindowManager.OpenType getOpenType() {
        return "newTab".equals(userSettingService.loadSetting("openEditorMode"))
                ? WindowManager.OpenType.NEW_TAB
                : WindowManager.OpenType.THIS_TAB;
    }

    protected List<CardInfo> loadCurrentCardInfo() {
        return documentTools.loadCurrentCardInfo(cardsDs.getItemIds(), WindowParams.FOLDER_ID.get(getContext()));
    }
}
