package com.haulmont.thesis.crm.web.company.history;

import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.thesis.crm.entity.Address;
import com.haulmont.thesis.crm.entity.CompanyCellsEnum;
import com.haulmont.thesis.crm.entity.HistoryCompanyCells;

import javax.inject.Inject;
import java.util.Map;

public class HistoryCompanyCellsEdit<T extends HistoryCompanyCells> extends AbstractEditor<T> {

    @Inject
    protected Metadata metadata;


    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
    }

    @Override
    protected void postInit() {
        HistoryCompanyCells item = getItem();
        boolean isAddressFirstDoc = item.getCompanyCells().equals(CompanyCellsEnum.addressFirstDoc) ? true : false;

        Button createAddressButton = getComponent("createAddressButton");
        Button lookupAddressButton = getComponent("lookupAddressButton");
        createAddressButton.setVisible(isAddressFirstDoc);
        lookupAddressButton.setVisible(isAddressFirstDoc);
        if (isAddressFirstDoc) {
            createAddressButton.setAction(new AbstractAction("createAddressButton") {
                @Override
                public void actionPerform(Component component) {
                    actionAddressButton("create");
                }
            });
            lookupAddressButton.setAction(new AbstractAction("lookupAddressButton") {
                @Override
                public void actionPerform(Component component) {
                    actionAddressButton("lookup");
                }
            });
        }

    }

    public void actionAddressButton(String open) {
        final HistoryCompanyCells item = getItem();
        Address newItem = open.equals("lookup") ? item.getAddress() :  metadata.create(Address.class);
        final Window editor = openEditor("crm$Address.edit", newItem, WindowManager.OpenType.DIALOG);
        editor.addListener(new CloseListener() {
            @Override
            public void windowClosed(String actionId) {
                Address address = (Address) editor.getDsContext().get("mainDs").getItem();
                if (!address.getAddressFirstDoc().isEmpty()) {
                    item.setAddress(address);
                    item.setItem(address.getAddressFirstDoc());
                }
            }
        });
    }


}