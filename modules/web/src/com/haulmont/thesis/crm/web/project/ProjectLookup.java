package com.haulmont.thesis.crm.web.project;

import java.util.Map;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.data.HierarchicalDatasource;

import javax.inject.Inject;

public class ProjectLookup extends AbstractLookup {

    @Inject
    protected HierarchicalDatasource projectsDs;




    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        projectsDs.refresh(params);
    }
}