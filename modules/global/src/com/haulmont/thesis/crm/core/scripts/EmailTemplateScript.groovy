/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.scripts

import com.haulmont.cuba.core.global.AppBeans
import com.haulmont.cuba.core.global.Configuration
import com.haulmont.cuba.core.global.GlobalConfig
import com.haulmont.cuba.core.global.UserSessionSource
import com.haulmont.cuba.security.entity.User
import com.haulmont.thesis.crm.entity.ExtCompany
import org.apache.commons.lang.StringUtils


String user_text = user_text
String link = makeLink(card)
User cu = AppBeans.get(UserSessionSource.class).getUserSession().getCurrentOrSubstitutedUser()
String cuName = (cu.lastName != null ? cu.lastName : "") + " " +
        (cu.firstName != null ? cu.firstName : "") + " " +
        (cu.middleName != null ? " " + cu.middleName : "")
String lang = cu.language ? cu.language : AppBeans.get(UserSessionSource.class).getUserSession().getLocale().getLanguage()
String email = StringUtils.isBlank(cu.email) ? "" : cu.email;
ExtCompany c = card



if (lang == 'ru') {
    def subject = "#ТЕЗИС Запрос на изменение контрагента № ${c.code}  / ${c.name}"
    def body = """
    <html><body>
    <b>Уведомление от</b> (${cuName} <a href="mailto:${email}">${email}</a>): <br>
    <hr>
    <p><i>Прошу исправить в контрагенте следующие данные: </i></p><br>
    <p> ${user_text} </p><br>
    <hr>
    Для открытия карточки перейдите по <a href="${link}">этой ссылке</a>.
    </body></html>
    """
    return ['subject': subject, 'body': body.toString()];
} else {
    def subject = "#ТЕЗИС Request to change business partner № ${c.code}  / ${c.name}"
    def body = """
    <html><body>
    <b>Notice from</b> (${cuName} <a href="mailto:${email}">${email}</a>): <br>
    <hr>
    <p><i>Please correct the following data in the counterparty: </i></p><br>
    <p> ${user_text} </p><br>
    <hr>
    To open the card, go to <a href="${link}">this link</a>.
    </body></html>
    """
    return ['subject': subject, 'body': body.toString()];
}


String getInstanceName(ExtCompany card) {
    switch (card) {
        case ExtCompany: return 'crm$Company'
        default: return ''
    }
}

String makeLink(ExtCompany c) {
    Configuration configuration = AppBeans.get(Configuration.NAME)
    GlobalConfig conf = configuration.getConfig(GlobalConfig.class)

    return  "${conf.webAppUrl}/open?" +
            "screen=${getInstanceName(c)}.edit&" +
            "item=${getInstanceName(c)}-${c.id}&" +
            "params=item:${getInstanceName(c)}-${c.id}"
}
