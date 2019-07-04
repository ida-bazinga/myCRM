package com.haulmont.thesis.crm.web.ui.campaignkind;

import com.google.common.collect.Lists;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.OptionsGroup;
import com.haulmont.cuba.gui.components.TokenList;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.ValueListener;
import com.haulmont.thesis.crm.entity.CampaignKind;
import com.haulmont.workflow.core.entity.Proc;
import org.apache.commons.lang.BooleanUtils;

import javax.inject.Named;
import java.util.Map;
import java.util.UUID;

public class CampaignKindProcsEditor extends AbstractEditor<CampaignKind> {

    @Named("useAllProcsOption")
    protected OptionsGroup useAllProcsOption;
    @Named("listProcs")
    protected TokenList listProcs;
    @Named("procDs")
    protected CollectionDatasource<Proc, UUID> procDs;
    @Named("processesDs")
    protected CollectionDatasource<Proc, UUID> processesDs;

    protected String ALL_PROCESSES;
    protected String ONLY_SELECTED;

    @Override
    public void init(Map<String, Object> params) {
        ALL_PROCESSES = getMessage("allProcesses");
        ONLY_SELECTED = getMessage("onlySelected");
        useAllProcsOption.setOptionsList(Lists.newArrayList(ALL_PROCESSES, ONLY_SELECTED ));

        useAllProcsOption.addListener(new ValueListener<Object>() {
            @Override
            public void valueChanged(Object source, String property, Object prevValue, Object value) {
                if (value.equals(ONLY_SELECTED)) {
                    listProcs.setVisible(true);
                } else {
                    listProcs.setVisible(false);
                }
            }
        });

        listProcs.setItemChangeHandler(new TokenList.ItemChangeHandler() {
            @Override
            public void addItem(Object item) {
                procDs.addItem((Proc) item);
                processesDs.excludeItem((Proc) item);
            }

            @Override
            public void removeItem(Object item) {
                procDs.removeItem((Proc) item);
                processesDs.addItem((Proc) item);
            }
        });
    }

    @Override
    protected void postInit() {
        super.postInit();
        setCaption(getItem().getName());
        useAllProcsOption.setValue(BooleanUtils.isFalse(getItem().getUseAllProcs()) ? ONLY_SELECTED : ALL_PROCESSES);
        listProcs.setVisible(useAllProcsOption.getValue().equals(ONLY_SELECTED));
        processesDs.refresh();
    }

    @Override
    public void commitAndClose() {
        CampaignKind campaignKind = getItem();
        campaignKind.setUseAllProcs(useAllProcsOption.getValue().equals(ALL_PROCESSES) ? Boolean.TRUE : Boolean.FALSE);
        super.commitAndClose();
    }
}