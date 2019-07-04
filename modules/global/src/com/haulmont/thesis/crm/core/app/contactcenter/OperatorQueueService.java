/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */
package com.haulmont.thesis.crm.core.app.contactcenter;

import com.haulmont.thesis.crm.entity.Operator;

import java.util.LinkedHashSet;
import java.util.LinkedList;

/**
 * @author k.khoroshilov
 */
@Deprecated
public interface OperatorQueueService {
    String NAME = "crm_OperatorQueueService";

    LinkedList<Operator> getQueue();
    Operator getFirstOperator();
    void addOperator(Operator operator);
    int getQueueSize();
    void removeOperator(Operator operator);
}