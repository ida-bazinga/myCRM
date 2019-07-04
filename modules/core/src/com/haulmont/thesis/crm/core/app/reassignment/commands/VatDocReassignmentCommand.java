/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.core.app.reassignment.commands
 */
package com.haulmont.thesis.crm.core.app.reassignment.commands;


import com.haulmont.thesis.core.app.reassignment.commands.AbstractDocReassignmentCommand;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;

import com.haulmont.thesis.crm.entity.VatDoc;

/**
 * @author p.chizhikov
 */
@ManagedBean(VatDocReassignmentCommand.NAME)
public class VatDocReassignmentCommand extends AbstractDocReassignmentCommand<VatDoc> {
    protected static final String NAME = "vatdoc_reassignment_command";

    @PostConstruct
    protected void postInit() {
        type = "VatDoc";
        docQuery = String.format(DOC_QUERY_TEMPLATE, "crm$VatDoc");
    }

    @Override
    public String getName() {
        return NAME;
    }
}