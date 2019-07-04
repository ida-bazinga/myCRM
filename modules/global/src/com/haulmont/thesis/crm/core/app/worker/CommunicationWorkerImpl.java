package com.haulmont.thesis.crm.core.app.worker;

import com.haulmont.cuba.core.global.Messages;
import com.haulmont.thesis.crm.entity.CommKind;
import com.haulmont.thesis.crm.entity.Communication;
import com.haulmont.thesis.crm.entity.CommunicationTypeEnum;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.swing.text.MaskFormatter;
import java.text.ParseException;

/**
 * @author Kirill Khoroshilov
 */

@Component(CommunicationWorker.NAME)
public class CommunicationWorkerImpl implements CommunicationWorker {

    protected Log log = LogFactory.getLog(getClass());

    @Inject
    protected Messages messages;

    @Override
    public String getMaskedAddress(String mainPart, String additionalPart, String mask, CommKind commKind) {

        if (StringUtils.isNotBlank(mainPart)) {
            StringBuilder title = new StringBuilder();

            if (StringUtils.isNotBlank(mask) && commKind != null && commKind.getCommunicationType().equals(CommunicationTypeEnum.phone)){
                try {
                    title.append(formatString(mainPart, mask));
                } catch (ParseException e) {
                    log.error(e.getMessage());
                    return "";
                }
            } else {
                title.append(mainPart);
            }

            if (StringUtils.isNotBlank(additionalPart)){
                title.append(messages.getMessage(Communication.class, "Communication.shortAddPart"));
                title.append(additionalPart);
            }
            return title.toString();
        }
        return "";
    }

    @Override
    public String getMaskedAddress(Communication entity){
        return entity != null ? getMaskedAddress(entity.getMainPart(), entity.getAdditionalPart(), entity.getMask(), entity.getCommKind()) : "" ;
    }

    protected static String formatString(String string, String mask) throws java.text.ParseException {
        MaskFormatter mf = new MaskFormatter(mask);
        mf.setValueContainsLiteralCharacters(false);
        return mf.valueToString(string);
    }
}
