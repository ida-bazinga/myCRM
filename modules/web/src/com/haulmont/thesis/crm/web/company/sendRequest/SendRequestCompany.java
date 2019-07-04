package com.haulmont.thesis.crm.web.company.sendRequest;

import com.haulmont.cuba.core.app.EmailService;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.TextArea;
import com.haulmont.cuba.gui.components.Window;
import com.haulmont.thesis.crm.core.config.CrmConfig;
import com.haulmont.thesis.crm.entity.ExtCompany;

import java.util.Map;


public class SendRequestCompany extends AbstractWindow {

    protected ExtCompany company;

    @Override
    public void init(Map<String, Object> params) {
       this.company = (ExtCompany) params.get("company");
    }

    public void ready() {
    }

    public void cancel() {
        close(Window.CLOSE_ACTION_ID);
    }

    public void send() {
        try {
            EmailService emailService = AppBeans.get(EmailService.class);
            CrmConfig crmConfig = AppBeans.get(Configuration.class).getConfig(CrmConfig.class);
            String mail = crmConfig.getSupportMail();
            TextArea textArea = getComponent("comment");
            String user_text = textArea.getValue();
            groovy.lang.Binding binding = new groovy.lang.Binding();
            binding.setProperty("card", this.company);
            binding.setProperty("user_text", user_text);
            Scripting scripting = AppBeans.get(Scripting.NAME);

            Map<String, Object> result = scripting.runGroovyScript("com/haulmont/thesis/crm/core/scripts/EmailTemplateScript.groovy", binding);

            emailService.sendEmail(mail, result.get("subject").toString(), result.get("body").toString(), new EmailAttachment[]{});
            cancel();
        } catch (EmailException e) {
            showNotification(getMessage(e.toString()), NotificationType.HUMANIZED);
        }
    }

}