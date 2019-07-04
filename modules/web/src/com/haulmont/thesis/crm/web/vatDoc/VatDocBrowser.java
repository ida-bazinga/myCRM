package com.haulmont.thesis.crm.web.vatDoc;

import java.util.Map;

import com.haulmont.thesis.crm.entity.VatDoc;
import com.haulmont.thesis.web.ui.basicdoc.browse.AbstractDocBrowser;
@Deprecated
public class VatDocBrowser extends AbstractDocBrowser<VatDoc> {

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        entityName = "crm$VatDoc";
    }
}