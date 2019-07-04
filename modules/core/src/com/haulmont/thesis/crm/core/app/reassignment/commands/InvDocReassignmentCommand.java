/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.core.app.reassignment.commands
 */
package com.haulmont.thesis.crm.core.app.reassignment.commands;


import com.haulmont.thesis.core.app.reassignment.commands.AbstractDocReassignmentCommand;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;

import com.haulmont.thesis.crm.entity.InvDoc;

/**
 * @author p.chizhikov
 */
@ManagedBean(InvDocReassignmentCommand.NAME)
public class InvDocReassignmentCommand extends AbstractDocReassignmentCommand<InvDoc> {
    protected static final String NAME = "invdoc_reassignment_command";

    @PostConstruct
    protected void postInit() {
        type = "InvDoc";
        docQuery = String.format(DOC_QUERY_TEMPLATE, "crm$InvDoc");
    }

    @Override
    public String getName() {
        return NAME;
    }
}