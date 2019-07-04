package com.haulmont.thesis.crm.web.ui.baseactivity;

import com.haulmont.cuba.client.ClientConfiguration;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.thesis.crm.core.config.CrmConfig;
import com.haulmont.thesis.crm.entity.CallActivity;
import com.haulmont.thesis.crm.enums.ActivityDirectionEnum;
import com.haulmont.thesis.crm.web.softphone.actions.ActivateAction;
import com.haulmont.thesis.crm.web.softphone.actions.DropCallAction;
import org.apache.commons.lang.time.DateUtils;

import javax.annotation.Nonnull;
import javax.inject.Named;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class ActiveCallWindow extends AbstractWindow {
    private int ticks = 0;
    private Date ticksTime;

    public static final String  IS_DROP_CALL_DELAY =  "isDropCallDelay";
    private boolean isAutoDrop = false;

    @Named("company")
    protected Label company;
    @Named("contact")
    protected Label contact;
    @Named("phoneNumber")
    protected Label phoneNumberLabel;
    @Named("ticksTimerLabel")
    protected Label ticksTimerLabel;
    @Named("activateCallBtn")
    protected Button activateCallBtn;
    @Named("dropCallBtn")
    protected Button dropCallBtn;
    @Named("ticksTimer")
    protected Timer ticksTimer;

    @WindowParam(name ="currentActivity")
    protected CallActivity currentActivity;

    protected CrmConfig crmConfig;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        crmConfig = AppBeans.get(ClientConfiguration.class).getConfigCached(CrmConfig.class);
        setWindowCaption();
        addWindowActions();

        ticksTime = DateUtils.setMinutes(new Date(), 0);
        ticksTime = DateUtils.setSeconds(ticksTime, 0);

        getDialogParams()
                .setWidth(400)
                .setHeight(200)
                .setCloseable(false)
                .setResizable(false);

        this.addListener(new Window.CloseListener() {
            @Override
            public void windowClosed(String actionId) {
                ticksTimer.stop();
                remove(ticksTimer);
            }
        });
    }

    @Override
    public void ready() {
        Timer dropCallTimer = this.getTimer("dropCallTimer");

        if (dropCallTimer != null)
            dropCallTimer.start();

        phoneNumberLabel.setValue(
                getActivity().getCommunication() != null ?  getActivity().getCommunication().getMaskedAddress() : getActivity().getAddress()
        );
        company.setValue(
                getActivity().getContactPerson() != null ? getActivity().getContactPerson().getCompany() : getMessage("unknownCompany")
        );
        contact.setValue(getActivity().getContactPerson() != null ? getActivity().getContactPerson() : getMessage("unknownContactPerson"));
    }

    @Nonnull
    protected final UUID getSessionId(){
        return getActivity().getSoftphoneSessionId();
    }

    @Nonnull
    protected final CallActivity getActivity(){
        if (currentActivity == null)
            throw new IllegalStateException("Unable to find current activity");
        return currentActivity;
    }

    protected final boolean IsIncoming(){
        return getActivity().getDirection().equals(ActivityDirectionEnum.INBOUND);
    }

    protected void setWindowCaption() {
        setCaption(getMessage(IsIncoming() ? "Caption.incoming" : "Caption.outgoing"));
    }

    protected void addWindowActions() {
        if (IsIncoming()){
            Action activateAction = createActivateAction();
            addAction(activateAction);
            activateCallBtn.setCaption(activateAction.getCaption());
            activateCallBtn.setAction(activateAction);
            activateCallBtn.setVisible(true);
        }

        Action dropCallAction = createDropCallAction();
        dropCallAction.setVisible(true);
        addAction(dropCallAction);
        dropCallBtn.setCaption(dropCallAction.getCaption());
        dropCallBtn.setAction(dropCallAction);

        //Звонок можно сбросить через клиента 3СХ
        dropCallBtn.setVisible(!getActivity().getCampaign().getName().equals(crmConfig.getDefaultOutboundCampaign()));
    }

    protected Action createActivateAction(){
        final Boolean isIncoming = IsIncoming();
        return new ActivateAction(getActivity().getCallId(), getSessionId()){

            @Override
            public void setVisible(boolean visible) {
                super.setVisible(isIncoming);
            }

            @Override
            public String getCaption() {
                return getMessage("activateCall");
            }
        };
    }

    protected Action createDropCallAction(){
        return new DropCallAction(getActivity().getCallId(), getSessionId()) {

            @Override
            protected void afterInvoke(){

                close(isAutoDrop ? IS_DROP_CALL_DELAY : Editor.WINDOW_CLOSE);
            }

            @Override
            public String getCaption() {
                return getMessage("dropCall");
            }
        };
    }

    public void ticks(Timer timer){
        DateFormat sdf = new SimpleDateFormat("mm:ss");
        ticks += timer.getDelay();
        ticksTimerLabel.setValue(sdf.format(DateUtils.addMilliseconds(ticksTime, ticks)));

        if (ticks >= crmConfig.getDropCallMDelay()){
            Action dropCallAction = getAction("dropCall");
            if (dropCallAction != null){
                isAutoDrop = true;
                timer.stop();
                ticksTimerLabel.setValue(getMessage("timeIsUp"));
                dropCallAction.actionPerform(null);
            }
        }
    }
}