/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.ui.common.actions;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.IFrame;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.components.Window;
import com.haulmont.cuba.gui.config.WindowConfig;
import com.haulmont.cuba.security.entity.EntityOp;
import com.haulmont.cuba.web.App;
import com.haulmont.thesis.core.app.NumerationService;
import com.haulmont.thesis.core.entity.Doc;
import com.haulmont.thesis.core.entity.DocKind;
import com.haulmont.thesis.core.entity.NumeratorType;
import com.haulmont.thesis.crm.entity.AcDoc;
import com.haulmont.thesis.crm.entity.InvDoc;
import com.haulmont.thesis.crm.entity.InvDocBugetDetail;
import com.haulmont.thesis.crm.entity.OrdDoc;
import com.haulmont.thesis.web.ui.common.actions.CopyDocAction;
import org.apache.commons.lang.BooleanUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class NewCopyDocAction extends CopyDocAction {


    public NewCopyDocAction(Table table) {
       super(table);
    }

    @Override
    public void actionPerform(Component component) {
        Doc srcDoc = (Doc) table.getDatasource().getItem();
        if (srcDoc != null) {
            Doc reloadedDoc = documentCopySupport.reloadSrcDoc(srcDoc);

            if (BooleanUtils.isTrue(reloadedDoc.getDocKind().getCreateOnlyByTemplate())
                    && BooleanUtils.isTrue(reloadedDoc.getTemplate())
                    && !security.isEntityOpPermitted(DocKind.class, EntityOp.UPDATE)) {
                App.getInstance().getWindowManager().showNotification(messages.getMessage(CopyDocAction.class,
                        "templateDocKindCreationViolation"), IFrame.NotificationType.ERROR);
                return;
            }

            Doc doc = metadata.create(reloadedDoc.getMetaClass());
            doc.setTemplate(reloadedDoc.getTemplate());
            doc.copyFrom(reloadedDoc);
            if (NumeratorType.ON_CREATE.equals(doc.getDocKind().getNumeratorType()) && !srcDoc.isTemplateInstance()) {
                NumerationService ns = AppBeans.get(NumerationService.NAME);
                String num = ns.getNextNumber(doc);
                if (num != null)
                    doc.setNumber(num);
            }
            doc.setCreator(userSession.getUser());
            defaultInitParams(doc);


            Map<String, Object> params = new HashMap<>();
            params.put("justCreated", true);
            doc.setCopyFrom(reloadedDoc);

            String entityName = doc.getMetaClass().getName();

            WindowConfig windowConfig = AppBeans.get(WindowConfig.class);
            final Window.Editor window = App.getInstance().getWindowManager().openEditor(windowConfig.getWindowInfo(entityName + ".edit"),
                    doc, WindowManager.OpenType.THIS_TAB, params);
            window.addListener(
                    new Window.CloseListener() {
                        public void windowClosed(String actionId) {
                            if (Window.COMMIT_ACTION_ID.equals(actionId)) {
                                table.getDatasource().refresh();
                            }
                        }
                    }
            );

        }
    }

    protected void defaultInitParams(Doc doc) {
        String entityName = doc.getMetaClass().getName();

        if(entityName.equals("crm$OrdDoc")) {
            ((OrdDoc) doc).setIntegrationResolver(null);
        }

        if(entityName.equals("crm$InvDoc")) {
            initInvDoc(((InvDoc) doc));
        }

        if(entityName.equals("crm$AcDoc")) {
            ((AcDoc) doc).setIntegrationResolver(null);
        }
    }

    protected void initInvDoc(InvDoc doc) {
        doc.setAttachments(null);
        doc.setIntegrationResolver(null);
        doc.setFullSum(BigDecimal.ZERO);
        doc.setTaxSum(BigDecimal.ZERO);
        doc.setDate(null);
        doc.setPaymentDate(null);
        doc.setPaymentDestination("");
        for (InvDocBugetDetail detail:doc.getInvDocBugetDetail()) {
            detail.setFullSum(BigDecimal.ZERO);
        }
    }

}
