/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.core.listener
 */
package com.haulmont.thesis.crm.core.listener;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.swing.text.MaskFormatter;

import antlr.StringUtils;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.listener.BeforeDetachEntityListener;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.security.global.UserUtils;
import com.haulmont.thesis.crm.entity.Communication;
import com.haulmont.thesis.crm.entity.CommunicationTypeEnum;
import com.haulmont.thesis.crm.entity.ExtContactPerson;
import com.haulmont.cuba.core.listener.BeforeInsertEntityListener;
import com.haulmont.cuba.core.listener.BeforeUpdateEntityListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.ParseException;

/**
 * @author k.khoroshilov
 */
@ManagedBean("crm_ContactPersonListener")
public class ExtContactPersonListener implements BeforeDetachEntityListener<ExtContactPerson>
        , BeforeInsertEntityListener<ExtContactPerson>, BeforeUpdateEntityListener<ExtContactPerson> {

    @Inject
    protected Messages messages;

    protected Log log = LogFactory.getLog(ExtContactPersonListener.class);

    @Override
    public void onBeforeDetach(ExtContactPerson entity, EntityManager entityManager) {

        String firstName = (entity.getFirstName() != null ? entity.getFirstName() : "");
        String middleName = (entity.getMiddleName() != null ? entity.getMiddleName() : "");

        try {
            entity.setFullName(UserUtils.formatName("{LL| }{F|.}{M|.}", firstName, entity.getLastName(), middleName));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (entity.getCommunications() == null) return;

        boolean isNotSetEmail = true;
        int i = 0;
        for(Communication comm : entity.getCommunications()){
            if (comm.getCommKind().getCommunicationType().equals(CommunicationTypeEnum.email) && comm.getPref() != null && comm.getPref()){
                if (isNotSetEmail) entity.setEmail1(comm.getMainPart());
                isNotSetEmail = false;
            }

            if (comm.getCommKind().getCommunicationType().equals(CommunicationTypeEnum.phone) && comm.getPref() != null && comm.getPref()){
                i++;
                if (i > 3) return;
                entity.setValue(String.format("phone%s",i), buildTitle(comm));
            }
        }
    }

    @Override
    public void onBeforeInsert(ExtContactPerson entity) {

        String firstName = (entity.getFirstName() != null ? entity.getFirstName() : "");
        String middleName = (entity.getMiddleName() != null ? entity.getMiddleName() : "");

        try {
            entity.setName(UserUtils.formatName("{LL| }{FF| }{MM}", firstName, entity.getLastName(), middleName));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBeforeUpdate(ExtContactPerson entity) {
        onBeforeInsert(entity);
    }

//todo вынести в сервис
    private String buildTitle(Communication entity){
        StringBuilder title = new StringBuilder();

        if (org.apache.commons.lang.StringUtils.isNotBlank(entity.getMainPart())) {
            if (org.apache.commons.lang.StringUtils.isNotBlank(entity.getMask())){
                try {
                    title.append(formatString(entity.getMainPart(), entity.getMask()));
                } catch (ParseException e) {
                    log.error(e.getMessage());
                }
            } else {
                title.append(entity.getMainPart());
            }

            if (org.apache.commons.lang.StringUtils.isNotBlank(entity.getAdditionalPart())){
                title.append(messages.getMessage(Communication.class, "Communication.shortAddPart"));
                title.append(entity.getAdditionalPart());
            }
        }
        return title.toString();
    }

    private static String formatString(String string, String mask) throws java.text.ParseException {
        MaskFormatter mf = new MaskFormatter(mask);
        mf.setValueContainsLiteralCharacters(false);
        return mf.valueToString(string);
    }

}