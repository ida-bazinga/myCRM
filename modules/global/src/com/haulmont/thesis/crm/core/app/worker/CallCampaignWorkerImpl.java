package com.haulmont.thesis.crm.core.app.worker;

import com.google.common.collect.Lists;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.core.global.*;
import com.haulmont.thesis.crm.core.config.CrmConfig;
import com.haulmont.thesis.crm.entity.CallCampaign;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author Kirill Khoroshilov, 2018
 */

@Component(CampaignWorker.NAME)
public class CallCampaignWorkerImpl <T extends CallCampaign> implements CampaignWorker<T> {
    protected Log log = LogFactory.getLog(getClass());
    protected static SimpleDateFormat SDF = new SimpleDateFormat("dd.MM.yyyy");
    protected List<String> defaultCampaignNames;

    @Inject
    protected DataManager dataManager;
    @Inject
    protected Messages messages;
    @Inject
    protected Metadata metadata;
    @Inject
    protected Configuration configuration;

    @PostConstruct
    protected void init(){
        CrmConfig config = configuration.getConfig(CrmConfig.class);
        defaultCampaignNames = Lists.newArrayList(config.getDefaultIncomingCampaign(), config.getDefaultOutboundCampaign());
    }

    @Override
    public String createName(@Nonnull T entity) {
        if (entity.getKind() != null && entity.getOrganization()!= null && entity.getNumber() != null)
            return createName(
                    entity.getProject() != null ? entity.getProject().getInstanceName() : "",
                    entity.getKind().getInstanceName(),
                    entity.getOrganization().getInstanceName(),
                    entity.getNumber(),
                    entity.getDate());

        return StringUtils.defaultIfBlank(entity.getName(), "");
    }

    @Override
    public String createName(String projectName, String kindName, String orgName, String number, Date date) {
        StringBuilder result = new StringBuilder();
        boolean skip = defaultCampaignNames.contains(number);

        if (StringUtils.isNotBlank(projectName) && !skip) {
            result.append(projectName)
                    .append(" ");
        }

        if (StringUtils.isNotBlank(kindName)) {
            result.append(kindName)
                    .append(" ");
        }

        if (StringUtils.isNotBlank(orgName) && skip){
            result.append(trimOrgName(orgName))
                    .append(" ");
        }

        if(skip) {
            result.append("(")
                    .append(messages.getMessage(CallCampaign.class, "CallCampaign.defaultName"))
                    .append(")")
                    .append(" ");
        }

        if (date != null && !skip) {
            result.append(messages.getMessage(CallCampaign.class, "Card.datePrefix"))
                    .append(" ")
                    .append(SDF.format(date))  //String.format("%1$td.%1$tm.%1$tY", date));
                    .append(" ");
        }
        return result.toString().trim();
    }

    @Override
    public String createDescription(@Nonnull T entity) {
        if (entity.getKind() != null && entity.getOrganization()!= null && entity.getNumber() != null)
            return createDescription(
                    entity.getKind().getInstanceName(),
                    entity.getOrganization().getInstanceName(),
                    entity.getNumber(),
                    entity.getDate());

        return StringUtils.defaultIfBlank(entity.getDescription(), "");
    }

    @Override
    public String createDescription(String kindName, String orgName, String number, Date date) {
        StringBuilder result = new StringBuilder();
        boolean skip = defaultCampaignNames.contains(number);

        if (StringUtils.isNotBlank(kindName)) {
            result.append(kindName)
                    .append(" ");
        }

        if (StringUtils.isNotBlank(orgName) && skip){
            result.append(trimOrgName(orgName))
                    .append(" ");
        }

        if(skip) {
            result.append("(")
                    .append(messages.getMessage(CallCampaign.class, "CallCampaign.defaultName"))
                    .append(")")
                    .append(" ");
        }

        if (StringUtils.isNotBlank(number) && !skip) {
            result.append(messages.getMessage(CallCampaign.class, "Card.numberPrefix"))
                    .append(" ")
                    .append(number)
                    .append(" ");

        }

        if (date != null && !skip) {
            result.append(messages.getMessage(CallCampaign.class, "Card.datePrefix"))
                    .append(" ")
                    .append(SDF.format(date))  //String.format("%1$td.%1$tm.%1$tY", date));
                    .append(" ");
        }

        MetaClass metaClass = metadata.getClassNN(CallCampaign.class);
        result.append(messages.getTools().getEntityCaption(metaClass));

        return result.toString().trim();
    }

    protected String trimOrgName(String name) {
        int pos = name.lastIndexOf(",");
        if (pos > 0)
            return name.substring(0, pos);
        else
            return name;
    }

    @Nullable
    @Override
    public T getDefaultCampaign(@Nonnull String number, String viewName) {
        LoadContext ctx = new LoadContext(CallCampaign.class)
                .setView(StringUtils.defaultIfBlank(viewName, View.MINIMAL));
        ctx.setQueryString("select e from crm$CallCampaign e where e.number = :number")
                .setParameter("number", number);
        return dataManager.load(ctx);
    }
}