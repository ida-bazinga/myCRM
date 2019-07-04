package com.haulmont.thesis.crm.web.resource;

        import com.haulmont.cuba.gui.components.AbstractEditor;
        import com.haulmont.cuba.gui.components.LookupPickerField;
        import com.haulmont.cuba.gui.components.PickerField;

        import javax.inject.Inject;
        import javax.inject.Named;
        import java.util.*;

public class ResourceGroupEditor extends AbstractEditor {


    @Named("parentGroup")
    protected LookupPickerField parentGroup;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        initParentProject(params);
    }



    private void initParentProject(final Map<String, Object> params) {
        PickerField.LookupAction projectLookupAction = (PickerField.LookupAction)parentGroup.getActionNN(PickerField.LookupAction.NAME);

        projectLookupAction.setLookupScreen("crm$ResourceGroup.lookup");
        projectLookupAction.setLookupScreenParams(params);

/*        PickerField.OpenAction projectOpenAction = (PickerField.OpenAction)parentGroup.getActionNN(PickerField.OpenAction.NAME);
        projectOpenAction.setEditScreen("tm$ProjectGroup.edit");
        projectOpenAction.setEditScreenParams(params);*/
    }

}