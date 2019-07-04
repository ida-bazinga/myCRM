/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.core.app.reassignment.commands
 */
package com.haulmont.thesis.crm.core.app.reassignment.commands;


import com.haulmont.thesis.core.app.reassignment.commands.AbstractDocReassignmentCommand;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;

import com.haulmont.thesis.crm.entity.OutboundCampaign;

/**
 * @author p.chizhikov
 */
@ManagedBean(OutboundCampaignReassignmentCommand.NAME)
public class OutboundCampaignReassignmentCommand extends AbstractDocReassignmentCommand<OutboundCampaign> {
    protected static final String NAME = "outboundcampaign_reassignment_command";

    @PostConstruct
    protected void postInit() {
        type = "OutboundCampaign";
        docQuery = String.format(DOC_QUERY_TEMPLATE, "crm$OutboundCampaign");
    }

    @Override
    public String getName() {
        return NAME;
    }
}