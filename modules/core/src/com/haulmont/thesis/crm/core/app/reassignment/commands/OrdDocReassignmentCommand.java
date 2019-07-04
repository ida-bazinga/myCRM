/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.core.app.reassignment.commands
 */
package com.haulmont.thesis.crm.core.app.reassignment.commands;


import com.haulmont.thesis.core.app.reassignment.commands.AbstractDocReassignmentCommand;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;

import com.haulmont.thesis.crm.entity.OrdDoc;

/**
 * @author p.chizhikov
 */
@ManagedBean(OrdDocReassignmentCommand.NAME)
public class OrdDocReassignmentCommand extends AbstractDocReassignmentCommand<OrdDoc> {
    protected static final String NAME = "orddoc_reassignment_command";

    @PostConstruct
    protected void postInit() {
        type = "OrdDoc";
        docQuery = String.format(DOC_QUERY_TEMPLATE, "crm$OrdDoc");
    }

    @Override
    public String getName() {
        return NAME;
    }
}