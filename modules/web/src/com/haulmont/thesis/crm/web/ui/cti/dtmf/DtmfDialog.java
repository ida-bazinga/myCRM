package com.haulmont.thesis.crm.web.ui.cti.dtmf;

import com.haulmont.cuba.gui.AppConfig;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.GridLayout;
import com.haulmont.thesis.crm.web.softphone.actions.SendDTMFAction;
import com.haulmont.thesis.crm.web.softphone.core.SoftphoneConstants;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.UUID;

public class DtmfDialog extends AbstractWindow {

    protected GridLayout buttonPanel;
    protected String buttonsWidth = "100%";

    public void init(Map<String, Object> params) {
        super.init(params);
        findStandardComponents();

        // TODO: 23.10.2017 else for error init
        if (params.containsKey("callId")) {
            String callId = (String) params.get("callId");
            UUID softPhoneSessionId = (UUID) params.get("softphoneSessionId");
            initButtonPanel(callId, softPhoneSessionId);
        }
        getDialogParams().setWidthAuto().setResizable(false);
    }

    protected void findStandardComponents() {
        buttonPanel = getComponentNN("buttonPanel");
    }

    protected void initButtonPanel(String callId, UUID softPhoneSessionId) {
        buttonPanel.removeAll();

        String[] chars = new String[]{ "7", "8", "9", "4", "5", "6", "1", "2", "3", "*", "0", "#"};
        int i = 0;
        int rMax = buttonPanel.getRows();
        int cMax = buttonPanel.getColumns();

        for (int r = 0; r < rMax; r++){
            for (int c = 0; c < cMax; c++){
                Action action = new SendDTMFAction(callId, chars[i], softPhoneSessionId);
                Button button = AppConfig.getFactory().createComponent(Button.NAME);
                button.setAction(action);
                button.setId(action.getId() + SoftphoneConstants.BTN_SUFFIX);
                button.setCaption(chars[i]);
                addButton(button, c, r);
                i++;
            }
        }
    }

    public void addButton(@Nonnull Button button, int column, int row) {
        button.setWidth(getButtonsWidth());
        buttonPanel.add(button, column, row);
    }

    public String getButtonsWidth() {
        return buttonsWidth;
    }
}