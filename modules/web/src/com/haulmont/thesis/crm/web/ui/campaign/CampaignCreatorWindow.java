/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.ui.campaign;

import com.haulmont.cuba.gui.components.Window;
import com.haulmont.thesis.crm.entity.BaseCampaign;

public interface CampaignCreatorWindow extends Window {

    <T extends BaseCampaign> T getCampaign();
}

