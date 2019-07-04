package com.haulmont.thesis.crm.web.ui.activityresult;

import com.google.common.collect.Lists;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.OptionsGroup;
import com.haulmont.cuba.gui.components.TokenList;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.ValueListener;
import com.haulmont.thesis.crm.entity.ActivityRes;
import com.haulmont.thesis.crm.entity.CampaignKind;
import org.apache.commons.lang.BooleanUtils;

import javax.inject.Named;
import java.util.Map;
import java.util.UUID;

public class ActivityResultCampaignKindsEditor extends AbstractEditor<ActivityRes> {

    @Named("useAllKindsOption")
    protected OptionsGroup useAllKindsOption;
    @Named("listKinds")
    protected TokenList listKinds;
    @Named("kindsDs")
    protected CollectionDatasource<CampaignKind, UUID> kindsDs;
    @Named("allKindsDs")
    protected CollectionDatasource<CampaignKind, UUID> allKindsDs;

    protected String ALL_KINDS;
    protected String ONLY_SELECTED;

    @Override
    public void init(Map<String, Object> params) {
        ALL_KINDS = getMessage("allKinds");
        ONLY_SELECTED = getMessage("onlySelected");
        useAllKindsOption.setOptionsList(Lists.newArrayList(ALL_KINDS, ONLY_SELECTED ));

        useAllKindsOption.addListener(new ValueListener<Object>() {
            @Override
            public void valueChanged(Object source, String property, Object prevValue, Object value) {
                if (value.equals(ONLY_SELECTED)) {
                    listKinds.setVisible(true);
                } else {
                    listKinds.setVisible(false);
                }
            }
        });

        listKinds.setItemChangeHandler(new TokenList.ItemChangeHandler() {
            @Override
            public void addItem(Object item) {
                kindsDs.addItem((CampaignKind) item);
                allKindsDs.excludeItem((CampaignKind) item);
            }

            @Override
            public void removeItem(Object item) {
                kindsDs.removeItem((CampaignKind) item);
                allKindsDs.addItem((CampaignKind) item);
            }
        });
    }

    @Override
    protected void postInit() {
        super.postInit();
        setCaption(getItem().getName_ru());
        useAllKindsOption.setValue(BooleanUtils.isFalse(getItem().getUseAllKinds()) ? ONLY_SELECTED : ALL_KINDS);
        listKinds.setVisible(useAllKindsOption.getValue().equals(ONLY_SELECTED));
        allKindsDs.refresh();
    }

    @Override
    public void commitAndClose() {
        ActivityRes activityResult = getItem();
        activityResult.setUseAllKinds(useAllKindsOption.getValue().equals(ALL_KINDS) ? Boolean.TRUE : Boolean.FALSE);
        super.commitAndClose();
    }
}