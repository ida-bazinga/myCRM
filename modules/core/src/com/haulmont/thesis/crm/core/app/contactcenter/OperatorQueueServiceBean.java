/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */
package com.haulmont.thesis.crm.core.app.contactcenter;

import com.haulmont.thesis.crm.entity.Operator;
import org.springframework.stereotype.Service;

import java.util.LinkedList;

/**
 * @author k.khoroshilov
 */
@Deprecated
@Service(OperatorQueueService.NAME)
public class OperatorQueueServiceBean implements OperatorQueueService {

    private LinkedList<Operator> operatorsQueue = new LinkedList<>();

    @Override
    public LinkedList<Operator> getQueue(){
        return operatorsQueue;
    }

    @Override
    public Operator getFirstOperator()
    {
        return (operatorsQueue.size()>0 ) ? operatorsQueue.getFirst(): null;
    }

    @Override
    public void addOperator(Operator operator){
        if (operator == null) return;
        if (!operatorsQueue.contains(operator)) operatorsQueue.add(operator);
    }

    @Override
    public int getQueueSize() {
        return operatorsQueue.size();
    }

    @Override
    public void removeOperator(Operator operator){
        operatorsQueue.remove(operator);
    }
}
