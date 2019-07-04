/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.scripts

import com.haulmont.cuba.core.global.AppBeans
import com.haulmont.cuba.core.global.Configuration
import com.haulmont.cuba.core.global.GlobalConfig
import com.haulmont.cuba.core.global.UserSessionSource
import com.haulmont.cuba.security.entity.User
import org.apache.commons.lang.StringUtils

User cu = AppBeans.get(UserSessionSource.class).getUserSession().getCurrentOrSubstitutedUser()
String lang = cu.language ? cu.language : AppBeans.get(UserSessionSource.class).getUserSession().getLocale().getLanguage()
String cuName = cu.getName()
String email = StringUtils.isBlank(cu.email) ? "sky@expoforum.ru" : cu.email

def table = buildTable()

if (lang == 'ru') {
    def subject = "Сообщения о новых заказах."
    def body = """
    <html><body>
    ${table}
    <hr> 
    <b>Уведомление от</b> (${cuName} <a href="mailto:${email}">${email}</a>): <br>   
    </body></html>
    """
    return ['subject': subject, 'body': body.toString()];
} else {
    def subject = "New order message."
    def body = """
    <html><body>
    <b>Notice from</b> (${cuName} <a href="mailto:${email}">${email}</a>): <br>
    <hr>  
     <p><i> ${docs} </i></p><br>   
    </body></html>
    """
    return ['subject': subject, 'body': body.toString()];
}


String buildTable() {
    def s = new StringBuilder()
    s.append("<table border=\"1\">")
    s.append("<caption>Заказы за текущую неделю</caption>")
    s.append("<tr><th>№</th><th>Название</th><th>Сумма с НДС</th><th>Контрагент</th></tr>")
    int i = 1
    for (doc in docs) {
        String docLink = "<a href=\"${cardLink(1, UUID.fromString(doc[0].toString()))}\">${doc[3]}</a>"
        String companyLink = "<a href=\"${cardLink(2, UUID.fromString(doc[1].toString()))}\">${doc[4]}</a>"
        s.append(String.format("<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>", i++, docLink, doc[2], companyLink))
    }
    s.append("</table>")
    return s.toString()
}


String getInstanceName(int i) {
    switch (i) {
        case 1: return 'crm$OrdDoc'; break
        case 2: return 'crm$Company'; break
        default: return ''
    }
}

String cardLink(int i, UUID id) {
    Configuration configuration = AppBeans.get(Configuration.NAME)
    GlobalConfig conf = configuration.getConfig(GlobalConfig.class)

    return  "${conf.webAppUrl}/open?" +
            "screen=${getInstanceName(i)}.edit&" +
            "item=${getInstanceName(i)}-${id}&" +
            "params=item:${getInstanceName(i)}-${id}"
}
