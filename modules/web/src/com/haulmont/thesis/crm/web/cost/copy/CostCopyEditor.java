package com.haulmont.thesis.crm.web.cost.copy;

import com.haulmont.cuba.gui.components.*;

import javax.inject.Named;
import java.util.Calendar;
import java.util.Map;

public class CostCopyEditor extends AbstractWindow {

    @Named("startDate")
    protected DateField startDate;
    @Named("project")
    protected PickerField project;
    @Named("closeButton")
    protected Button closeButton;
    @Named("selectButton")
    protected Button selectButton;

    @Override
    public void init(Map<String, Object> params) {
        getDialogParams().setHeight(200).setWidth(350).setResizable(false);
        selectButton.setAction(createCopyAction());
        closeButton.setAction(close());

    }

    @Override
    public void ready() {
        startDate.setValue(Calendar.getInstance().getTime());
    }

    public Action close() {
        return new AbstractAction("close") {
            @Override
            public void actionPerform(Component component) {
                getContext().getParams().remove("startDate");
                getContext().getParams().remove("project");
                close(Window.CLOSE_ACTION_ID);
            }
        };
    }

    public Action createCopyAction (){
        return new AbstractAction("nextCreateCopyAction") {
            @Override
            public void actionPerform(Component component) {
                if(project.getValue() == null) {
                    String txtMesage = "Не выбран \"Проект\"!";
                    showNotification(getMessage(txtMesage), NotificationType.HUMANIZED);
                    return;
                }
                getContext().getParams().put("startDate",startDate.getValue());
                getContext().getParams().put("project",project.getValue());
                close(Window.COMMIT_ACTION_ID);
            }
        };
    }

}