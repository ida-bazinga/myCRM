package com.haulmont.thesis.crm.web.projectTeam;

import java.util.Map;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.ValueListener;
import com.haulmont.thesis.crm.entity.ProjectTeam;

import javax.inject.Inject;

public class ProjectTeamEditor<T extends ProjectTeam> extends AbstractEditor<T> {

    @Inject
    protected LookupPickerField document;

    @Inject
    protected LookupField signatory;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        document.setRequired(false);
        signatory.addListener(new ValueListener<Object>() {
            @Override
            public void valueChanged(Object source, String property, Object prevValue, Object value) {
                if (getItem().getSignatory()!=null)document.setRequired(true);
                else document.setRequired(false);
            }
        });

    }
}