package com.haulmont.thesis.crm.web.sys;

import com.haulmont.thesis.web.sys.ThesisApplicationServlet;
import com.vaadin.server.DefaultDeploymentConfiguration;
import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.VaadinServlet;

import java.util.Properties;

public class CrmApplicationServlet extends ThesisApplicationServlet {

    @Override
    protected DeploymentConfiguration createDeploymentConfiguration(Properties initParameters) {
        return new DefaultDeploymentConfiguration(VaadinServlet.class, initParameters);
    }
}
