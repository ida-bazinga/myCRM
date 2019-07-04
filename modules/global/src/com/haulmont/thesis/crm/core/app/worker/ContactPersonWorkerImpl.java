package com.haulmont.thesis.crm.core.app.worker;

import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.security.global.UserUtils;
import com.haulmont.thesis.crm.core.config.CrmConfig;
import com.haulmont.thesis.crm.entity.Communication;
import com.haulmont.thesis.crm.entity.CommunicationTypeEnum;
import com.haulmont.thesis.crm.entity.ExtContactPerson;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.text.ParseException;
import java.util.List;

/**
 * @author Kirill Khoroshilov
 */

@Component(ContactPersonWorker.NAME)
public class ContactPersonWorkerImpl implements ContactPersonWorker {

    @Inject
    protected Messages messages;
    @Inject
    protected DataManager dataManager;

    protected Log log = LogFactory.getLog(getClass());

    @Override
    public String formatName(String firstName, String lastName, String middleName) {
        return formatName(firstName, lastName, middleName, null);
    }

    @Override
    public String formatName(String firstName, String lastName, String middleName, @Nullable String pattern) {
        CrmConfig config = AppBeans.get(Configuration.class).getConfig(CrmConfig.class);
        // TODO: 22.03.2018 fix it
        String result;
        pattern = StringUtils.defaultIfBlank(pattern, config.getContactPersonNamePattern());
        firstName = StringUtils.defaultIfBlank(firstName, "");
        lastName = StringUtils.defaultIfBlank(lastName, "");
        middleName = StringUtils.defaultIfBlank(middleName, "");

        try {
            result = UserUtils.formatName(pattern, firstName, lastName, middleName);
        } catch (ParseException e) {
            log.error(e.getMessage());
            result = String.format("%s: %s", ContactPersonWorker.NAME, " parse exception");
        }
        return result;
    }

    @Override
    public String formatName(ExtContactPerson entity, @Nullable String pattern) {
        return formatName(entity.getFirstName(), entity.getLastName(), entity.getMiddleName(), pattern);
    }

    @Override
    @Nonnull
    public List<Communication> getPrefCommunications(@Nonnull ExtContactPerson entity, @Nonnull CommunicationTypeEnum type, int length){
        LoadContext ctx = new LoadContext(Communication.class).setView("browse");
        ctx.setQueryString("select e from crm$Communication e " +
                "where e.mainPart is not null and e.contactPerson.id = :contact and e.pref = true and e.commKind.communicationType = :type" +
                " and LENGTH(e.mainPart) >= :length" +
                " and (e.emailStatus.id is null or e.emailStatus.unisenderValid = true)")
                .setParameter("contact", entity)
                .setParameter("length", length)
                .setParameter("type", type);
        return dataManager.loadList(ctx);
    }

    @Nonnull
    public List<Communication> findCommunication(@Nonnull CommunicationTypeEnum type, @Nonnull String address){
        address = address.replaceAll("\\W", "");
        LoadContext ctx = new LoadContext(Communication.class).setView("with-contact");
        ctx.setQueryString("select e from crm$Communication e where e.mainPart like :address and e.commKind.communicationType = :type")
                .setParameter("address", "%".concat(address))
                .setParameter("type", type);
        return dataManager.loadList(ctx);

    }
}
