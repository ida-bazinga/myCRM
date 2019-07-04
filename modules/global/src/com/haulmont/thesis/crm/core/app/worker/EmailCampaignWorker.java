/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.worker;

import com.haulmont.thesis.crm.entity.EmailCampaign;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Date;

public interface EmailCampaignWorker<T extends EmailCampaign> {
    String NAME = "ext_EmailCampaignWorker";

    String createName(@Nonnull T entity);

    String createName(String num, String projectName, String kindName, String anyTxt);

    String createDescription(@Nonnull T entity);

    String createDescription(String kindName, String orgName, String number, Date date);

    @Nullable
    EmailCampaign getDefaultCampaign(@Nonnull String number, String viewName);
}
