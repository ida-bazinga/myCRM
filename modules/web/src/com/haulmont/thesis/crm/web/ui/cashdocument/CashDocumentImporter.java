package com.haulmont.thesis.crm.web.ui.cashdocument;

import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.cuba.gui.backgroundwork.BackgroundWorkWindow;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.ValueListener;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.thesis.crm.entity.ExhibitSpace;

import javax.inject.Inject;
import javax.inject.Named;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class CashDocumentImporter extends AbstractWindow {

    @Named("month")
    protected LookupField monthField;
    @Named("storesBox")
    protected BoxLayout storesBox;
    @Named("stores")
    protected LookupField storesField;
    @Named("okButton")
    protected Button okButton;
    @Named("cancelButton")
    protected Button cancelButton;

    @Inject
    private TimeSource timeSource;
    @Inject
    private UserSession userSession;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        getDialogParams().setWidthAuto().setHeight(160);

        initListeners();

        initStoresField();
        initMonthField();
        initButtons();
    }

    protected void initListeners(){
        monthField.addListener(new ValueListener<Object>() {
            @Override
            public void valueChanged(Object source, String property, Object prevValue, Object value) {
                okButton.setEnabled(value!= null);
            }
        });
    }


    protected void initButtons() {
        okButton.setAction(new BaseAction("okAction") {
            @Override
            public void actionPerform(Component component) {
                if (monthField.getValue() == null) return;
                Date selected = monthField.getValue();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(selected);
                Date sDate = calendar.getTime();
                calendar.add(Calendar.MONTH, 1);
                calendar.add(Calendar.SECOND, -1);
                Date eDate = calendar.getTime();

                long timeout = 1L;
                if (storesField.getValue() == null){
                    timeout = getStores().size() * 1;
                }

                DownloadDocTask task = new DownloadDocTask( (UUID)storesField.getValue(), sDate, eDate, timeout, TimeUnit.MINUTES,
                        CashDocumentImporter.this);
                BackgroundWorkWindow.show(task, getMessage("title"), getMessage("body"), false);
                close("ok");
            }
        });

        cancelButton.setAction(new AbstractAction("cancelAction") {
            @Override
            public void actionPerform(Component component) {
                close("cancel");
            }

        });
    }

    protected void initMonthField() {
        Map<String, Object> map = new LinkedHashMap<>();
        SimpleDateFormat monthFormat = new SimpleDateFormat("LLLL yyyy", userSession.getLocale());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(timeSource.currentTimestamp());
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        calendar.add(Calendar.MONTH, 1);

        for(int i = 0; i < 6; i++){
            calendar.add(Calendar.MONTH, -1);
            map.put(monthFormat.format(calendar.getTime()) , calendar.getTime());
        }
        monthField.setOptionsMap(map);
    }

    protected void initStoresField(){
        Map<String, Object> storesMap = getStores();
        if (!storesMap.isEmpty()){
            storesBox.setVisible(true);
            storesField.setOptionsMap(storesMap);
        }
    }

    protected Map<String, Object> getStores(){
        Map<String, Object> result = new LinkedHashMap<>();
        LoadContext loadContext = new LoadContext(ExhibitSpace.class).setView("_local");
        loadContext.setQueryString("select e from crm$ExhibitSpace e where e.evotorStoreId is not null");
        List<ExhibitSpace> list = getDsContext().getDataSupplier().loadList(loadContext);

        for(ExhibitSpace es : list){
            result.put(es.getName_ru(), es.getEvotorStoreId());
        }
        return result;
    }
}