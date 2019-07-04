/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.service;

import com.haulmont.bali.datastruct.Pair;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.thesis.core.entity.Employee;
import com.haulmont.thesis.crm.entity.*;
import com.haulmont.thesis.crm.enums.ActivityResultEnum;
import com.haulmont.thesis.crm.enums.ActivityStateEnum;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by k.khoroshilov on 19.02.2017.
 */
public interface ActivityService {
    String NAME = "crm_ActivityService";

    Long getCountWithSpecifiedKind(UUID kindId);

    List<BaseActivity> getWithKind(UUID kindId);

    boolean isSameKindExist(ActivityKind kind);

    String createDescription(BaseActivity entity);

    Set<MetaClass> getCardTypes(List<String> excludedTypes);
    Set<MetaClass> getCardTypes(String entityName, List<String> excludedTypes);

    ActivityKind getKindByCode(@Nonnull String kindCode);

    CallActivity getActivityByCallId(@Nonnull String callId, @Nonnull UUID softphoneSessionId);

    ActivityRes getResultByCode(@Nonnull ActivityResultEnum resultCode);

    @Nonnull
    List<Pair<CallActivity, CallCampaignTrgt>> getOperatorQueue(@Nonnull ExtEmployee operator);

    @Nonnull
    CallActivity copyActivity(@Nonnull CallActivity activity, @Nullable ActivityStateEnum state, @Nullable ExtEmployee operator);

    @Nonnull
    CallActivity createOutgoingActivity(ExtEmployee operator);
    @Nonnull
    CallActivity createIncomingActivity(ExtEmployee operator);
    @Nonnull
    CallActivity createCampaignSchedulingActivity(ExtEmployee operator);

    EmailActivity createActivitiesFromTargets(EmailCampaign campaign, EmailCampaignTarget targetItem);

    <T extends BaseActivity> T createActivity(ActivityKind activityKind, @Nonnull Class<? extends BaseActivity> clazz, Employee employee);
    <T extends BaseActivity> T createActivity(@Nonnull ActivityKind activityKind, @Nonnull String metaClassName, @Nullable Employee employee);
}
