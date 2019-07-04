package com.haulmont.thesis.crm.web.ui.cti.transfer;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.thesis.crm.entity.ExtEmployee;
import com.haulmont.thesis.crm.web.softphone.actions.BeginTransferAction;
import com.haulmont.thesis.crm.web.softphone.actions.BlindTransferAction;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CallTransferDialog extends AbstractWindow {

    protected Table employeesTable;
    protected BeginTransferAction beginTransferAction;
    protected BlindTransferAction blindTransferAction;

    public void init(Map<String, Object> params) {
        super.init(params);
        findStandardComponents();

        setBeginTransferAction(params.get("beginTransferAction"));
        setBlindTransferActions(params.get("blindTransferAction"));

        initPhoneColumn();

        getDialogParams().setHeight(500).setWidth(900).setResizable(true);
    }

    protected void setBeginTransferAction(Object object){
        if (object instanceof BeginTransferAction) {
            beginTransferAction =((BeginTransferAction)object);
        }
    }

    protected void setBlindTransferActions(Object object){
        if (object instanceof BlindTransferAction) {
            blindTransferAction =(BlindTransferAction)object;
        }
    }

    protected void findStandardComponents() {
        employeesTable = getComponentNN("employeesTable");
    }

    protected void initPhoneColumn(){
        addPhonePopupsColumn("phoneNumExtnesion", "phoneNumExtnesion");
        addPhonePopupsColumn("mobilePhone", "mobilePhone");
    }

    protected void addPhonePopupsColumn(final String columnId, final String valueName) {
        employeesTable.addGeneratedColumn(columnId, new Table.ColumnGenerator<ExtEmployee>() {

            @Override
            public Component generateCell(ExtEmployee entity) {
                Object propValue = entity.getValue(valueName);
                return new Table.PlainTextCell(propValue != null ? propValue.toString() : "");
            }
        });

        employeesTable.setClickListener(columnId, new Table.CellClickListener() {
            @Override
            public void onClick(Entity item, String columnId) {
                ExtEmployee selected = employeesTable.getSingleSelected();
                if (selected != null && !selected.equals(item)) {
                    employeesTable.setSelected(item);
                }
                Object propValue = item.getValue(valueName);

                if (propValue != null && StringUtils.isNotBlank(propValue.toString().trim())) {
                    List<Action> actions = getTransferActions(propValue.toString().trim());
                    if (CollectionUtils.isNotEmpty(actions)) {
                        employeesTable.showCustomPopupActions(actions);
                    }
                }
            }
        });

        employeesTable.addStyleProvider(new Table.StyleProvider<ExtEmployee>() {
            @Override
            public String getStyleName(ExtEmployee entity, String property) {
                Object propValue = entity.getValue(valueName);
                if (columnId.equals(property) && propValue != null && StringUtils.isNotBlank(propValue.toString().trim())) {
                    return "thesis-dashed thesis-pop";
                }
                return null;
            }
        });
    }

    protected List<Action> getTransferActions(String destination) {
        List<Action> result = new LinkedList<>();
        beginTransferAction.setDestination(destination);
        beginTransferAction.setDialogWindow(this);

        result.add(beginTransferAction);
        blindTransferAction.setDestination(destination);
        blindTransferAction.setDialogWindow(this);
        result.add(blindTransferAction);

        return result;
    }
}