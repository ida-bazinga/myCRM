package com.haulmont.thesis.crm.web.ui.activity;

import java.util.Map;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.GroupTable;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.thesis.crm.entity.Activity;
import com.haulmont.thesis.crm.entity.CallDirectionEnum;

import javax.annotation.Nullable;
import javax.inject.Named;

public class ActivityBrowser extends AbstractLookup {

    @Named("activitiesTable")
    protected GroupTable activitiesTable;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        activitiesTable.setIconProvider(getIconProvider());

    }

    private Table.IconProvider<Activity> getIconProvider(){
        return new Table.IconProvider<Activity>() {
            @Nullable
            @Override
            public String getItemIcon(Activity entity) {
                if (entity.getDirection() == null) return null;
                if (entity.getDirection().equals(CallDirectionEnum.OUTBOUND)) {
                    return "theme:icons/income.png";
                } else if (entity.getDirection().equals(CallDirectionEnum.INBOUND)) {
                    return "theme:icons/outcome.png";
                }
                return null;
            }
        };
    }
}