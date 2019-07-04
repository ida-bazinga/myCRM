package com.haulmont.thesis.crm.web.webAddress;

import java.util.Collections;
import java.util.Map;

import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.cuba.gui.data.Datasource;

import javax.inject.Inject;

@Deprecated
public class WebAddressFrame extends AbstractFrame {
    protected String propertyName;
    protected Datasource parentDs;

    @Inject
    protected Table websTable;

    public void init() {
        websTable.addAction(new CreateAction(websTable, WindowManager.OpenType.DIALOG) {
            @Override
            public Map<String, Object> getInitialValues() {
                return Collections.<String, Object>singletonMap(propertyName, parentDs.getItem());
            }

        });
        websTable.addAction(new EditAction(websTable, WindowManager.OpenType.DIALOG));
        websTable.addAction(new RemoveAction(websTable, false));
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public void setParentDs(Datasource parentDs) {
        this.parentDs = parentDs;
    }
}
