/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.contactcenter;

import com.haulmont.thesis.crm.entity.ExtEmployee;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.util.LinkedList;

@Service(CallCampaignOperatorQueueService.NAME)
public class CallCampaignOperatorQueueServiceBean implements CallCampaignOperatorQueueService {
    private LinkedList<ExtEmployee> operatorsQueue = new LinkedList<>();

    @Override
    public boolean isOperatorInQueue(ExtEmployee operator){
        return  operator != null && !operatorsQueue.isEmpty() && operatorsQueue.contains(operator);
    }

    @Override
    public int getQueueSize() {
        return operatorsQueue.size();
    }

    @Override
    @Nullable
    public ExtEmployee getFirstOperator(){
        return (operatorsQueue.isEmpty()) ? null : operatorsQueue.pollFirst();//getFirst();
    }

    @Override
    public void addOperator(ExtEmployee operator){
        if (!isOperatorInQueue(operator)){
            operatorsQueue.add(operator);
        }
    }

    @Override
    public boolean removeOperator(ExtEmployee operator){
        if (isOperatorInQueue(operator)){
            return operatorsQueue.remove(operator);
        }
        return true;
    }
}
