package com.haulmont.thesis.crm.web.characteristic;

import com.haulmont.cuba.core.app.UniqueNumbersService;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.components.LookupPickerField;
import com.haulmont.cuba.gui.components.TextField;
import com.haulmont.cuba.gui.data.ValueListener;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.thesis.crm.entity.Characteristic;
import com.haulmont.thesis.crm.entity.CharacteristicType;

import javax.inject.Inject;
import java.util.Map;

public class CharacteristicEditor<T extends Characteristic> extends AbstractEditor<T> {

    @Inject
    protected UserSession userSession;
    @Inject
    private UniqueNumbersService un;
    @Inject
    protected LookupPickerField characteristicType, hotel;
    @Inject
    protected Label hotelLabel;
    @Inject
    protected TextField name_ru, name_en;


    @Override
    public void setItem(Entity item) {
        super.setItem(item);
    }

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        characteristicType.addListener(new ValueListener<Object>() {
            @Override
            public void valueChanged(Object source, String property, Object prevValue, Object value) {
                CharacteristicType type = characteristicType.getValue();
                if (type.getCode().equals("tourism")) {
                    hotelLabel.setVisible(true);
                    hotel.setVisible(true);
                    hotel.setRequired(true);
                } else {
                    hotelLabel.setVisible(false);
                    hotel.setVisible(false);
                    hotel.setValue(null);
                    hotel.setRequired(false);
                }
            }
        });

        hotel.addListener(new ValueListener<Object>() {
            @Override
            public void valueChanged(Object source, String property, Object prevValue, Object value) {
                Characteristic characteristic = getItem();
                if(characteristic.getGuesthouse() != null){
                    name_ru.setValue(characteristic.getGuesthouse().getName_ru());
                    if(characteristic.getGuesthouse().getName_en() != null){
                        name_en.setValue(characteristic.getGuesthouse().getName_en());
                    }
                }
            }
        });
    }

    @Override
    protected boolean preCommit() {
        if (PersistenceHelper.isNew(getItem())){
            Long l = un.getNextNumber("Characteristic");
            getItem().setCode(String.format("%05d",l));
        }
        return true;
    }

}