/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.core.app.reassignment.commands
 */
package com.haulmont.thesis.crm.core.app.reassignment.commands;


import com.haulmont.thesis.core.app.reassignment.commands.AbstractDocReassignmentCommand;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;

import com.haulmont.thesis.crm.entity.AcDoc;

/**
 * @author p.chizhikov
 */
@ManagedBean(AcDocReassignmentCommand.NAME)
public class AcDocReassignmentCommand extends AbstractDocReassignmentCommand<AcDoc> {
    protected static final String NAME = "acdoc_reassignment_command";

    @PostConstruct
    protected void postInit() {
        type = "AcDoc";
        docQuery = String.format(DOC_QUERY_TEMPLATE, "crm$AcDoc");
    }

    @Override
    public String getName() {
        return NAME;
    }
}