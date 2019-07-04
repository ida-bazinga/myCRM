package com.haulmont.thesis.crm.core.app.worker;

import com.haulmont.thesis.crm.entity.Communication;
import com.haulmont.thesis.crm.entity.CommunicationTypeEnum;
import com.haulmont.thesis.crm.entity.ExtContactPerson;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * @author Kirill Khoroshilov
 */

public interface ContactPersonWorker {
    String NAME = "crm_ContactPersonWorker";

    String formatName(String firstName, String lastName, String middleName);

    String formatName(ExtContactPerson entity, @Nullable String pattern);

    String formatName(String firstName, String lastName, String middleName, @Nullable String pattern);

    @Nonnull
    List<Communication> getPrefCommunications(@Nonnull ExtContactPerson entity, @Nonnull CommunicationTypeEnum type, int length);

    @Nonnull
    List<Communication> findCommunication(@Nonnull CommunicationTypeEnum type, @Nonnull String address);
}
