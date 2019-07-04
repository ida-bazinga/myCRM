package com.haulmont.thesis.crm.web.lineOfBusiness;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.LookupPickerField;
import com.haulmont.cuba.gui.components.PickerField;
import com.haulmont.thesis.crm.entity.LineOfBusiness;

import java.util.Map;
import javax.inject.Named;

public class LineOfBusinessGroupEditor<T extends LineOfBusiness> extends AbstractEditor<T> {

    @Named("parentGroup")
    protected LookupPickerField parentGroup;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        initParentProject(params);
    }

    private void initParentProject(final Map<String, Object> params) {
        PickerField.LookupAction parentLookupAction = (PickerField.LookupAction)parentGroup.getActionNN(PickerField.LookupAction.NAME);

        parentLookupAction.setLookupScreen("crm$LineOfBusinessGroup.lookup");
        parentLookupAction.setLookupScreenParams(params);
        parentGroup.addOpenAction();
    }

    @Override
    protected void initNewItem(T item) {
        super.initNewItem(item);
        item.setIsGroup(true);
    }
}