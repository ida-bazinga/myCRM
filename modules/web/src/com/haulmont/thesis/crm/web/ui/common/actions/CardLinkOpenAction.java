/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.ui.common.actions;

import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.web.App;
import com.haulmont.cuba.web.gui.components.ShowLinkAction;
import com.haulmont.thesis.web.actions.CardLinkHandler;

import java.util.HashMap;
import java.util.Map;

public class CardLinkOpenAction extends ShowLinkAction {

    public CardLinkOpenAction(CollectionDatasource ds) {
        super(ds, new CardLinkHandler());
    }

    public CardLinkOpenAction(CollectionDatasource ds, Handler handler){
        super(ds, handler);
    }

    @Override
    public void actionPerform(com.haulmont.cuba.gui.components.Component component) {
        WindowManager manager = App.getInstance().getWindowManager();
        Map<String, Object> params = new HashMap<>();
        String url = handler.makeLink(ds.getItem());
        manager.showWebPage(url,params);
    }

    @Override
    public String getCaption() {
        return messages.getMainMessage("Открыть в новом окне");
    }
}
