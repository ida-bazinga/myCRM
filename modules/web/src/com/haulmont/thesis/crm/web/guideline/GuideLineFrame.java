package com.haulmont.thesis.crm.web.guideline;

import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.thesis.crm.entity.ExtProject;
import com.haulmont.thesis.crm.entity.GuideLine;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

public class GuideLineFrame extends AbstractFrame {

    @Inject
    protected Metadata metadata;

    @Named("guideLineDs")
    protected CollectionDatasource<GuideLine, UUID> guideLineDs;

    @Named("guideLineTable")
    protected Table guideLineTable;

    @Named("guideLineTable.create")
    private CreateAction createAction;

    protected ExtProject project;

    public void init(Map<String, Object> params) {
        this.createAction.setInitialValues(Collections.<String, Object>singletonMap("project", params.get("ITEM")));
    }


}