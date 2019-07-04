package com.haulmont.thesis.crm.web.position;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.thesis.core.app.PositionService;
import com.haulmont.thesis.crm.core.app.morpher.MorpherTools;
import com.haulmont.thesis.crm.entity.ExtPosition;
import org.apache.commons.lang.StringUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

public class ExtPositionEditor<T extends ExtPosition> extends AbstractEditor<T> {

    protected String positionName;
    @Named("positionDs")
    protected Datasource<T> positionDs;
    @Inject
    protected MorpherTools morpherTools;
    @Inject
    protected PositionService positionService;



    @Override
    public void setItem(Entity item) {
        super.setItem(item);
        positionName = getItem().getName();
    }

    @Override
    public void init(Map<String, Object> params) {
    }

/*
    @Override
    public void commitAndClose() {
        ExtPosition position = getItem();
        if (!PersistenceHelper.isNew(position) && positionDs.isModified()) {
            if (StringUtils.isNotBlank(position.getName()) && !position.getName().equals(positionName)) {
                positionService.updateUsersPosition(getItem().getName(), positionName);
            }
        }
        super.commitAndClose();
    }
*/

    @Override
    protected boolean preCommit() {
        ExtPosition item = getItem();
        item.setGenName(morpherTools.getGenetive(item.getName()));
        item.setDatName(morpherTools.getDative(item.getName()));
        return true;
    }

    @Override
    protected boolean postCommit(boolean committed, boolean close) {
        if (committed) {
            ExtPosition position = getItem();
            if (!PersistenceHelper.isNew(position) && positionDs.isModified()) {
                if (StringUtils.isNotBlank(position.getName()) && !position.getName().equals(positionName)) {
                    positionService.updateUsersPosition(getItem().getName(), positionName);
                }
            }
        }
        return super.postCommit(committed, close);
    }

    public void morph() {
        ExtPosition item = getItem();

        item.setGenName(morpherTools.getGenetive(item.getName()));
        item.setDatName(morpherTools.getDative(item.getName()));
    }
}