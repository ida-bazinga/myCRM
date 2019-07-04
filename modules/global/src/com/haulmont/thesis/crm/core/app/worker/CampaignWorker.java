package com.haulmont.thesis.crm.core.app.worker;

/**
 * @author Kirill Khoroshilov, 2018
 */

import com.haulmont.thesis.crm.entity.BaseCampaign;
import com.haulmont.thesis.crm.entity.CallCampaign;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Date;

public interface CampaignWorker <T extends BaseCampaign> {
    String NAME = "crm_CampaignWorker";

    String createName(@Nonnull T entity);

    String createName(String projectName, String kindName, String orgName, String number, Date date);

    String createDescription(@Nonnull T entity);

    String createDescription(String kindName, String orgName, String number, Date date);

    @Nullable
    CallCampaign getDefaultCampaign(@Nonnull String number, String viewName);
}