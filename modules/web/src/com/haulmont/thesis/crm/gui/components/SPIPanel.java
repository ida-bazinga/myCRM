/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.gui.components;

import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.thesis.crm.entity.CallStatus;
import com.haulmont.thesis.crm.entity.SoftPhoneSession;
import com.haulmont.thesis.crm.enums.SoftPhoneProfileName;

import java.util.UUID;

public interface SPIPanel extends Component.BelongToFrame {
    String NAME = "SPIPanel";

    UUID getSoftphoneSessionId();
    SoftPhoneSession getSoftphoneSession();

    String getLineNumber();
    void setLineNumber(String lineNumber);

    void setProfile(SoftPhoneProfileName value);
    SoftPhoneProfileName getProfile();

    CallStatus getActiveCall();

    void setActiveCall(CallStatus activeCall);

    /**
     * @return datasource instance
     */
    Datasource<SoftPhoneSession> getDatasource();

    /**
     * Set datasource and its property.
     */
    void setDatasource(Datasource<SoftPhoneSession> datasource);

    void repaintComponent();
}
