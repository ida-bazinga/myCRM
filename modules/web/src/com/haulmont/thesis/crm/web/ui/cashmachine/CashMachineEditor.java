package com.haulmont.thesis.crm.web.ui.cashmachine;

import com.haulmont.cuba.gui.components.AbstractEditor;

import java.util.Map;

public class CashMachineEditor extends AbstractEditor {

    @Override
    public void init(Map<String, Object> params){
        getDialogParams().setHeight(270).setWidthAuto();
    }
}