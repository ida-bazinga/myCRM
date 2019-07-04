/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.softphone.core;

public class SoftphoneConstants {

    public static final String SESSION_ID_ATTR_NAME = "softphoneSessionId";

    public static final String BTN_SUFFIX = "Btn";
    public static final String BUTTON_STYLE_NAME ="cti-icon-button";

    public static final String SUCCESS_CALLBACK = "success";
    public static final String ERROR_CALLBACK = "error";
    public static final String HOLD_ON_PARAM ="holdOn";
    public static final String MUTE_ON_PARAM ="muteOn";

    public static final String AUTH_TOKEN_NAME = "authtoken";
    // TODO: 21.09.2017 Add secret value to config
    public static final String AUTH_TOKEN_VALUE = "ThesisSecurityToken";

    public static final String MUTE_ICON = "components/spipanel/icons/mute.png";
    public static final String UNMUTE_ICON = "components/spipanel/icons/unmute.png";
    public static final String HOLD_ICON = "components/spipanel/icons/hold.png";
    public static final String UNHOLD_ICON = "components/spipanel/icons/unhold.png";

    public static final String NO_SESSION_ICON = "VAADIN/themes/halo/components/spipanel/icons/phone_off.png";
    public static final String ACTIVE_CALL_WINDOW_NAME = "activeCallWindow";

    public static final String CLOSE_SP ="closeSoftphone";
}
