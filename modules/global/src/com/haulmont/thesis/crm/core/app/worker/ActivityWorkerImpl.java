package com.haulmont.thesis.crm.core.app.worker;

import com.haulmont.thesis.crm.entity.BaseActivity;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Kirill Khoroshilov, 2018
 */

@Component(ActivityWorker.NAME)
public class ActivityWorkerImpl implements ActivityWorker {
    protected Log log = LogFactory.getLog(getClass());
    protected static SimpleDateFormat SDF = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    @Override
    public String createDescription(BaseActivity entity) {
        StringBuilder result = new StringBuilder();

        if (entity.getKind() != null) {
            result.append(entity.getKind().getInstanceName());
        }

        if (entity.getAddress() != null) {
            result.append(", ")
                    .append(entity.getAddress());
        }

        if (entity.getCreateTime() != null) {
            result.append(", ")
                    .append(SDF.format(entity.getCreateTime()));
        } else {
            result.append(", ");
            result.append(SDF.format(new Date()));
        }

        if (entity.getCompany()!= null) {
            result.append(", ")
                    .append(entity.getCompany().getInstanceName());
        }

        if (entity.getOwner() != null) {
            result.append(", ");
            result.append(entity.getOwner().getName());
        }

        return result.toString().trim();
    }
}
