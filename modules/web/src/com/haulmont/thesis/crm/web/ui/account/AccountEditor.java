package com.haulmont.thesis.crm.web.ui.account;

import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.DatasourceListener;
import com.haulmont.cuba.gui.data.impl.DsListenerAdapter;
import com.haulmont.thesis.crm.core.app.dadata.DaData;
import com.haulmont.thesis.crm.core.app.dadata.entity.SuggestBank;
import com.haulmont.thesis.crm.core.config.CrmConfig;
import com.haulmont.thesis.crm.entity.ExtContractorAccount;
import com.haulmont.thesis.crm.entity.ExternalSystem;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Map;

public class AccountEditor<T extends ExtContractorAccount> extends AbstractEditor<T> {

    @Inject
    private CrmConfig config;
    @Inject
    private Datasource<T> mainDs;

    private DaData daData;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
    }

    @Override
    protected void postInit() {
        final ExtContractorAccount item = getItem();
        initDaData();
        mainDsLisener();
        final Button searchBtn = getComponentNN("searchBtn");
        final TextField searchBikSwift = getComponent("searchBikSwift");
        searchBtn.setAction(new AbstractAction(PickerField.OpenAction.NAME) {
            public void actionPerform(Component component) {
                try {
                    if (searchBikSwift.getValue() == null) {
                        String txtMesage = "Введите (БИК/SWIFT) для поиска!";
                        showNotification(getMessage(txtMesage), NotificationType.HUMANIZED);
                        return;
                    }
                    SuggestBank bank = daData.bank(searchBikSwift.getValue().toString());
                    if (bank != null) {
                        item.setSwift(bank.getSuggestBankData().getSwift());
                        item.setBik(bank.getSuggestBankData().getBik());
                        item.setName(bank.getBank_value());
                        item.setCorrespondentNo(bank.getSuggestBankData().getCorrespondent_account());
                        item.setAdress(bank.getSuggestBankData().getSuggestAddres().getAddress_unrestricted_value());
                        item.setRegion(bank.getSuggestBankData().getSuggestAddres().getAddress_data() != null ? bank.getSuggestBankData().getSuggestAddres().getAddress_data().getRegionWithType() : "");
                    } else {
                        String txtMesage = "Банк не найден!\nПроверьте правильность введенного номер БИК/SWIFT\nили заполните реквизиты банковского счета вручную.";
                        showNotification(getMessage(txtMesage), NotificationType.HUMANIZED);
                    }

                } catch (Exception e) {
                    String txtMesage = "При получении данных банка, произошла ошибка!";
                    showNotification(getMessage(txtMesage), NotificationType.HUMANIZED);
                }

            }
        });
    }

    private void initDaData() {
        ExternalSystem row = config.getExternelSystemDaDataSuggestion();
        if (row != null) {
            daData = new DaData(row.getLogin(), row.getPassword(), row.getConnectionString());
        }
    }

    protected void mainDsLisener() {
        mainDs.addListener(new DatasourceListener<T>() {
            @Override
            public void itemChanged(Datasource<T> ds, @Nullable T prevItem, @Nullable T item) {

            }

            @Override
            public void stateChanged(Datasource<T> ds, Datasource.State prevState, Datasource.State state) {

            }

            @Override
            public void valueChanged(T source, String property, @Nullable Object prevValue, @Nullable Object value) {
                switch (property) {
                    case "no":
                        if (value != null) {
                            String fixValue = value.toString().replaceAll("\\D", "");
                            getItem().setNo(fixValue);
                        }
                        break;
                }
            }
        });
    }

}

