package com.haulmont.thesis.crm.web.product;

import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.data.GroupDatasource;

import javax.inject.Inject;
import java.util.Map;

public class ProductLookup extends AbstractLookup {

    @Inject
    protected GroupDatasource mainDs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        //Map<String, Object> param = new HashMap<>();
        //param.put("type", "004");
        //param.put("flg", params.get("flg"));
        //param.put("project", params.get("project"));
        //param.put("exhibitSpace", params.get("exhibitSpace"));
        mainDs.refresh(params);
    }
}