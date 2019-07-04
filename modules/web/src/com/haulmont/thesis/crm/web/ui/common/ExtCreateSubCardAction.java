/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.ui.common;

import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.IFrame;
import com.haulmont.thesis.web.ui.common.CreateSubCardAction;
import com.haulmont.thesis.web.ui.doc_type_select.DocTypeSelect;
import com.haulmont.thesis.web.ui.doc_type_select.DocTypeSelectConfig;
import com.haulmont.thesis.web.ui.doc_type_select.DocTypeSelectMode;

import javax.inject.Inject;
import java.util.List;



public class ExtCreateSubCardAction extends CreateSubCardAction {

    @Inject
    protected DocTypeSelectConfig docTypeSelectConfig;

    public ExtCreateSubCardAction(String id, IFrame frame) {
        super(id, frame, true);
    }

    @SuppressWarnings("unused")
    @Override
    protected void subCardActionPerform(Component component) {
        DocTypeSelect.runDialog(DocTypeSelectMode.CREATE, getExcludedDocTypes(), createAfterSelectAction());
    }

    protected List<String> getExcludedDocTypes() {
        return docTypeSelectConfig.getExcludedMetaClasses();
    }

}
