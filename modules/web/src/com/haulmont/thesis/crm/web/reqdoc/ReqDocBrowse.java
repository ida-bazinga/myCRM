package com.haulmont.thesis.crm.web.reqdoc;

import com.haulmont.thesis.crm.entity.ReqDoc;
import com.haulmont.thesis.web.ui.basicdoc.browse.AbstractDocBrowser;

import java.util.Map;

public class ReqDocBrowse extends AbstractDocBrowser<ReqDoc> {

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        entityName = "crm$ReqDoc";
    }
}