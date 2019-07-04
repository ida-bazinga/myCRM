package com.haulmont.thesis.crm.web.ordDoc.copy;

import com.haulmont.cuba.gui.components.*;
import com.haulmont.thesis.crm.entity.OrdDoc;

import java.util.Collections;
import java.util.Map;


public class CopyOrdDocEditor extends AbstractWindow {

    protected PickerField project;

    @Override
    public void init(Map<String, Object> params) {
        getDialogParams().setHeight(250).setWidth(350).setResizable(false);
        project = getComponent("project");
        OrdDoc ordDoc = (OrdDoc) params.get("ordDoc");
        initProject(ordDoc);
        Button selectButton = getComponent("selectButton");
        selectButton.setAction(createCopyAction());
        Button closeButton = getComponent("closeButton");
        closeButton.setAction(close());
    }

    public Action createCopyAction (){
        return new AbstractAction("nextCreateCopyAction") {
            @Override
            public void actionPerform(Component component) {
                PickerField company = getComponent("company");
                //PickerField project = getComponent("project");
                if(company.getValue() == null || project.getValue() == null) {
                    String txtMesage = "Поля \"Контрагент\" и \"Проект\" обязательны для заполнения!";
                    showNotification(getMessage(txtMesage), NotificationType.HUMANIZED);
                    return;
                }
                getContext().getParams().put("company",company.getValue());
                getContext().getParams().put("project",project.getValue());
                close(Window.COMMIT_ACTION_ID);
            }
        };
    }

    public Action close() {
        return new AbstractAction("close") {
            @Override
            public void actionPerform(Component component) {
                getContext().getParams().remove("company");
                getContext().getParams().remove("project");
                close(Window.CLOSE_ACTION_ID);
            }
        };
    }

    private void initProject(OrdDoc item) {
        project.removeAction(project.getAction(PickerField.LookupAction.NAME));
        final PickerField.LookupAction projectLookupAction = new PickerField.LookupAction(project) {
            @Override
            public void actionPerform(Component component) {
                super.actionPerform(component);
            }
        };
        projectLookupAction.setLookupScreen("tm$Project.lookup");
        projectLookupAction.setLookupScreenParams(Collections.<String, Object>singletonMap("organization", item.getOrganization().getId()));
        project.addAction(projectLookupAction);
        project.addOpenAction();
    }


}