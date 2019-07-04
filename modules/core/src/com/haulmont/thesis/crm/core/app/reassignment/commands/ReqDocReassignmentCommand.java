/*
 * Copyright (c) 2019 com.haulmont.thesis.crm.core.app.reassignment.commands
 */
package com.haulmont.thesis.crm.core.app.reassignment.commands;


import com.haulmont.thesis.core.app.reassignment.commands.AbstractDocReassignmentCommand;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;

import com.haulmont.thesis.crm.entity.ReqDoc;

/**
 * @author d.ivanov
 */
@ManagedBean(ReqDocReassignmentCommand.NAME)
public class ReqDocReassignmentCommand extends AbstractDocReassignmentCommand<ReqDoc> {
    protected static final String NAME = "reqdoc_reassignment_command";

    @PostConstruct
    protected void postInit() {
        type = "ReqDoc";
        docQuery = String.format(DOC_QUERY_TEMPLATE, "crm$ReqDoc");
    }

    @Override
    public String getName() {
        return NAME;
    }
}