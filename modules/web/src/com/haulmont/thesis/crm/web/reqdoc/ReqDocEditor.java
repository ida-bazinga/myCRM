package com.haulmont.thesis.crm.web.reqdoc;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.web.gui.components.WebAbstractTable;
import com.haulmont.thesis.core.entity.Doc;
import com.haulmont.thesis.crm.entity.ReqDetail;
import com.haulmont.thesis.crm.entity.ReqDoc;
import com.haulmont.thesis.crm.web.reqdockind.ReqDocKindFields;
import com.haulmont.thesis.web.actions.PrintReportAction;
import com.haulmont.thesis.web.ui.basicdoc.editor.AbstractDocEditor;
import com.haulmont.workflow.core.app.WfUtils;
import org.apache.commons.lang.StringUtils;

import javax.inject.Named;
import java.util.Map;
import java.util.UUID;

public class ReqDocEditor extends AbstractDocEditor<ReqDoc> {

    @Named("reqDetailTable")
    protected WebAbstractTable reqDetailTable;

    @Named("reqDetailDs")
    protected CollectionDatasource<ReqDetail, UUID> reqDetailDs;


    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
    }

    @Override
    protected void postInit() {
        super.postInit();
        boolean isTemplate = isTemplate(getItem());
        if (!isTemplate && getItem().getDocCategory() != null) {
            ReqDocKindFields reqDocKindFields = new ReqDocKindFields(getItem().getDocCategory(), reqDetailTable, this);
            reqDocKindFields.initTable();
        }
    }

    @Override
    protected String getHiddenTabsConfig() {
        return "correspondenceHistoryTab,docLogTab,office,cardLinksTab,processTab,securityTab,docTransferLogTab,cardProjectsTab,versionsTab,openHistoryTab";
    }

    @Override
    public void setItem(Entity item) {
        super.setItem(item);
        printButton.addAction(new PrintReportAction("printExecutionList", this, "printDocExecutionListReportName"));
    }

    @Override
    protected Component createState() {
        if (WfUtils.isCardInState(getItem(), "New") || StringUtils.isEmpty(getItem().getState())) {
            Label label = componentsFactory.createComponent(Label.NAME);
            label.setValue(StringUtils.isEmpty(getItem().getState()) ? "" : getItem().getLocState());
            return label;
        } else {
            return super.createState();
        }
    }

    @Override
    protected void fillHiddenTabs() {
        hiddenTabs.put("office", getMessage("office"));
        hiddenTabs.put("attachmentsTab", getMessage("attachmentsTab"));
        hiddenTabs.put("docTreeTab", getMessage("docTreeTab"));
        hiddenTabs.put("cardCommentTab", getMessage("cardCommentTab"));
        super.fillHiddenTabs();
    }

    @Override
    protected void initDocHeader(Doc doc){
        super.initDocHeader(doc);
        if (doc.getTemplate()) {
            setHeaderComponentVisible("reqDetailTable", false);
        }
    }

    public void addReqDetail() {
        ReqDetail reqDetail = metadata.create(ReqDetail.class);
        reqDetail.setReqDoc(getItem());
        reqDetailDs.addItem(reqDetail);
    }

    @Override
    public boolean validateAll() {
        boolean b = super.validateAll();
        if (b) {
            try {
                reqDetailTable.validate();
            } catch (ValidationException e) {
                return false;
            }
        }
        return b;
    }

    @Override
    protected void adjustForTemplate(Doc doc) {
        super.adjustForTemplate(doc);
        if (doc.getTemplate()) {
            getComponentNN("docCategoryLabel").setVisible(true);
            getComponentNN("headerInfo").setVisible(false);
            getComponentNN("detailsTabsheet").setVisible(false);
            LookupPickerField docCategory = getComponentNN("docCategory");
            docCategory.setVisible(true);
            docCategory.setRequired(true);
        }
    }


}