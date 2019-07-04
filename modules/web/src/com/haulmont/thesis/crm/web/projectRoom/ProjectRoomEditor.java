package com.haulmont.thesis.crm.web.projectRoom;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.CheckBox;
import com.haulmont.cuba.gui.components.DateField;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.data.ValueListener;
import com.haulmont.thesis.crm.entity.ProjectRoom;

import javax.annotation.Nullable;
import javax.inject.Named;
import java.util.Map;

public class ProjectRoomEditor extends AbstractEditor<ProjectRoom> {

    @Named("isOption")
    protected CheckBox isOptionField;
    @Named("optionDate")
    protected DateField optionDateField;
    @Named("optionDateLabel")
    protected Label optionDateLabel;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        initListeners();
    }

    protected void initListeners(){
        isOptionField.addListener(new ValueListener() {
            @Override
            public void valueChanged(Object source, String property, @Nullable Object prevValue, @Nullable Object value) {
                boolean v = value != null ? (Boolean)value : false;

                if (!v) getItem().setOptionDate(null);

                visibleOption(v);
            }
        });
    }

    @Override
    protected void postInit() {
        super.postInit();

        visibleOption(getItem().isOption());
    }

    protected void visibleOption(boolean isOption) {
        optionDateLabel.setVisible(isOption);
        optionDateField.setVisible(isOption);
        optionDateField.setRequired(isOption);
    }
}