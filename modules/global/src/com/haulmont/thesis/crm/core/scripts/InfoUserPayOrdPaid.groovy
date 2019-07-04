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
    def subject = "Оплаченные расходные счета."
    def body = """
    <html><body>
    ${table}
    <hr> 
    <b>Уведомление от</b> (${cuName} <a href="mailto:${email}">${email}</a>): <br>   
    </body></html>
    """
    return ['subject': subject, 'body': body.toString()];
} else {
    def subject = "Expense account paid."
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
    s.append("<caption>Оплаченные расходные счета</caption>")
    s.append("<tr><th>Номер счета</th><th>Контрагент</th></tr>")

    for (doc in docs) {
        String docLink = "<a href=\"${cardLink(UUID.fromString(doc[2].toString()))}\">${doc[0]}</a>"
        s.append(String.format("<tr><td>%s</td><td>%s</td></tr>", docLink, doc[1]))
    }
    s.append("</table>")
    return s.toString()
}


String cardLink(UUID id) {
    Configuration configuration = AppBeans.get(Configuration.NAME)
    GlobalConfig conf = configuration.getConfig(GlobalConfig.class)
    String instanceName = 'crm$InvDoc'

    return  "${conf.webAppUrl}/open?" +
            "screen=${instanceName}.edit&" +
            "item=${instanceName}-${id}&" +
            "params=item:${instanceName}-${id}"
}
