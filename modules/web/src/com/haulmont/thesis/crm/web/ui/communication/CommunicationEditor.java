package com.haulmont.thesis.crm.web.ui.communication;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.BoxLayout;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.impl.DsListenerAdapter;
import com.haulmont.thesis.crm.entity.CommKind;
import com.haulmont.thesis.crm.entity.Communication;
import com.haulmont.thesis.crm.entity.CommunicationTypeEnum;
import org.apache.commons.lang.StringUtils;

import javax.inject.Named;
import java.util.Map;

public class CommunicationEditor extends AbstractEditor<Communication> {

    final static String DEFUALT_PHONE_MASK ="+#(###)###-##-##";

    @Named("communicationDs")
    private Datasource<Communication> communicationDs;

    @Named("labelMainPart")
    protected Label labelMainPart;
    @Named("addPartBox")
    protected BoxLayout addPartBox;
    @Named("maskBox")
    protected BoxLayout maskBox;
    @Named("statusBox")
    protected BoxLayout statusBox;

    @Override
    public void init(Map<String, Object> params) {
        communicationDs.addListener(new DsListenerAdapter<Communication>() {
            @Override
            public void valueChanged(Communication source, String property, Object prevValue, Object value) {
                if ("commKind".equals(property)) {
                    CommunicationTypeEnum commType = ((CommKind) value).getCommunicationType();
                    maskBox.setVisible(commType.equals(CommunicationTypeEnum.phone));
                    addPartBox.setVisible(commType.equals(CommunicationTypeEnum.phone));
                    statusBox.setVisible(commType.equals(CommunicationTypeEnum.email));
                    labelMainPart.setValue(commType.equals(CommunicationTypeEnum.phone)
                            ? messages.getMessage(getClass(), "numString")
                            : messages.getMessage(getClass(), "addressString"));

                    if (commType.equals(CommunicationTypeEnum.phone) && StringUtils.isBlank(source.getMask()))
                        source.setMask(DEFUALT_PHONE_MASK);
                }
            }
        });
    }

    @Override
    public void postInit(){
        if (getItem().getCommKind() != null && getItem().getCommKind().getCommunicationType() != null) {
            CommunicationTypeEnum commType = getItem().getCommKind().getCommunicationType();
            addPartBox.setVisible(commType.equals(CommunicationTypeEnum.phone));
            maskBox.setVisible(commType.equals(CommunicationTypeEnum.phone));
            statusBox.setVisible(commType.equals(CommunicationTypeEnum.email));
        }
    }
}