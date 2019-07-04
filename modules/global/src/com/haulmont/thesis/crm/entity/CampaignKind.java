/*
 * Copyright (c) 2018 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.*;

import com.haulmont.bali.util.Dom4j;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.chile.core.model.MetaProperty;
import com.haulmont.cuba.core.entity.Category;
import com.haulmont.cuba.core.entity.annotation.Listeners;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DeletePolicy;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.reports.entity.Report;
import com.haulmont.thesis.core.app.TypedEntitiesTools;
import com.haulmont.thesis.core.entity.*;
import com.haulmont.thesis.core.enums.CategoryAttrsPlace;
import com.haulmont.workflow.core.entity.Proc;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Kirill Khoroshilov
 */
@Listeners({"crm_CampaignKindEntityListener"})
@NamePattern("%s|name")
@PrimaryKeyJoinColumn(name = "CATEGORY_ID", referencedColumnName = "ID")
@DiscriminatorValue("4")
@Table(name = "CRM_CAMPAIGN_KIND")
@Entity(name = "crm$CampaignKind")
public class CampaignKind extends Category implements BelongOrganization, FieldInfoContainer {
    private static final long serialVersionUID = 1327586007208916317L;

    @Column(name = "CODE", length = 50)
    protected String code;

    @Column(name = "DESCRIPTION", length = 2000)
    protected String description;

    @Column(name = "PREFIX", length = 100)
    protected String prefix;

    @OnDelete(DeletePolicy.UNLINK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NUMERATOR_ID")
    protected Numerator numerator;

    @Column(name = "NUMERATOR_TYPE")
    protected Integer numeratorType = NumeratorType.ON_COMMIT.getId();

    @Column(name = "FIELDS_XML", length = 2000)
    protected String fieldsXml;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORGANIZATION_ID")
    protected Organization organization;

    @Column(name = "CATEGORY_ATTRS_PLACE")
    protected Integer categoryAttrsPlace = 1 ;

    @JoinTable(name = "CRM_CAMPAIGN_KIND_PROC_LINK",
            joinColumns = @JoinColumn(name = "CATEGORY_ID", referencedColumnName = "CATEGORY_ID"),
            inverseJoinColumns = @JoinColumn(name = "PROC_ID", referencedColumnName = "ID"))
    @ManyToMany
    protected Set<Proc> procs;

    @Column(name = "ADDITIONAL_FIELDS_TAB_NAME", length = 30)
    protected String additionalFieldsTabName;

    @JoinTable(name = "CRM_CAMPAIGN_KIND_REPORT_LINK",
            joinColumns = @JoinColumn(name = "CATEGORY_ID"),
            inverseJoinColumns = @JoinColumn(name = "REPORT_ID"))
    @ManyToMany
    protected Set<Report> reports;

    @JoinTable(name = "CRM_CAMPAIGN_TYPE_ACTIVITY_RESULT_LINK",
            joinColumns = @JoinColumn(name = "CAMPAIGN_KIND_ID"),
            inverseJoinColumns = @JoinColumn(name = "ACTIVITY_RESULT_ID"))
    @OnDelete(DeletePolicy.CASCADE)
    @ManyToMany
    protected Set<ActivityRes> activityResults;

    @Column(name = "USE_ALL_PROCS", nullable = false)
    protected Boolean useAllProcs = true;

    @com.haulmont.chile.core.annotations.MetaProperty(related = "entityType")
    public String getEntityTypeName() {
        if (StringUtils.isNotBlank(entityType)){
            MetaClass metaClass = AppBeans.get(Metadata.class).getClassNN(entityType);
            return AppBeans.get(Messages.class).getTools().getDetailedEntityCaption(metaClass);
        }
        return "";
    }

    @Transient
    protected List<FieldInfo> metaPropertyFields;

    @Transient
    protected List<FieldInfo> virtualFields;
    public void setUseAllProcs(Boolean useAllProcs) {
        this.useAllProcs = useAllProcs;
    }

    public Boolean getUseAllProcs() {
        return useAllProcs;
    }

    public void setCategoryAttrsPlace(CategoryAttrsPlace categoryAttrsPlace) {
        this.categoryAttrsPlace = categoryAttrsPlace == null ? null : categoryAttrsPlace.getId();
    }

    public CategoryAttrsPlace getCategoryAttrsPlace() {
        return categoryAttrsPlace == null ? null : CategoryAttrsPlace.fromId(categoryAttrsPlace);
    }

    public void setAdditionalFieldsTabName(String additionalFieldsTabName) {
        this.additionalFieldsTabName = additionalFieldsTabName;
    }

    public String getAdditionalFieldsTabName() {
        return additionalFieldsTabName;
    }

    public void setActivityResults(Set<ActivityRes> activityResults) {
        this.activityResults = activityResults;
    }

    public Set<ActivityRes> getActivityResults() {
        return activityResults;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setNumerator(Numerator numerator) {
        this.numerator = numerator;
    }

    public Numerator getNumerator() {
        return numerator;
    }

    public void setNumeratorType(NumeratorType numeratorType) {
        this.numeratorType = numeratorType == null ? null : numeratorType.getId();
    }

    public NumeratorType getNumeratorType() {
        return numeratorType == null ? null : NumeratorType.fromId(numeratorType);
    }

    public void setFieldsXml(String fieldsXml) {
        this.fieldsXml = fieldsXml;
    }

    public String getFieldsXml() {
        return fieldsXml;
    }

    public void setProcs(Set<Proc> procs) {
        this.procs = procs;
    }

    public Set<Proc> getProcs() {
        return procs;
    }

    public void setReports(Set<Report> reports) {
        this.reports = reports;
    }

    public Set<Report> getReports() {
        return reports;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public List<FieldInfo> getFields() {
        return getFields(false);
    }

    @Override
    public List<FieldInfo> getFields(boolean includeVirtualFields) {
        if (StringUtils.isBlank(entityType))
            return new ArrayList<>();

        if (metaPropertyFields == null)
            metaPropertyFields = new ArrayList<>();

        if (CollectionUtils.isEmpty(metaPropertyFields)){

            MetaClass metaClass = AppBeans.get(Metadata.class).getClassNN(entityType);
            TypedEntitiesTools typedEntitiesTools = AppBeans.get(TypedEntitiesTools.class);
            List<String> controlledByTypeFields = typedEntitiesTools.getControlledByTypeFields(entityType);
            Element rootElem = getRootElement();

            for (MetaProperty property : metaClass.getProperties()){
                String propertyName = property.getName();

                if(controlledByTypeFields.contains(propertyName) && !property.isMandatory()){
                    FieldInfo fieldInfo = createFieldInfo(metaClass, propertyName, rootElem);
                    metaPropertyFields.add(fieldInfo);
                }
            }

            if (includeVirtualFields) {
                List<FieldInfo> virtualFields = getVirtualFields(metaClass, typedEntitiesTools, rootElem);
                metaPropertyFields.addAll(virtualFields);
            }
        }

        return metaPropertyFields;
    }

    public List<FieldInfo> getVirtualFields(MetaClass metaClass, TypedEntitiesTools typedEntitiesTools, Element rootElem ) {
        if (virtualFields == null)
            virtualFields = new ArrayList<>();

        if(CollectionUtils.isEmpty(virtualFields)){
            List<String> controlledByTypeVirtualFields = typedEntitiesTools.getControlledByTypeVirtualFields(entityType);

            for (String propertyName : controlledByTypeVirtualFields) {
                FieldInfo field = createFieldInfo(metaClass, propertyName, rootElem);
                field.setVirtual(true);
                field.setLocName(typedEntitiesTools.getVirtualFieldCaption(metaClass, propertyName));
                virtualFields.add(field);
            }
        }
        return virtualFields;
    }

    protected Element getRootElement() {
        Element rootElem = null;
        if (!StringUtils.isBlank(fieldsXml)) {
            Document doc = Dom4j.readDocument(fieldsXml);
            rootElem = doc.getRootElement();
        }
        return rootElem;
    }

    protected FieldInfo createFieldInfo(MetaClass metaClass, String propertyName, Element rootElem) {
        FieldInfo field = new FieldInfo();
        field.setName(propertyName);
        field.setLocName(AppBeans.get(Messages.class).getTools().getPropertyCaption(metaClass, propertyName));

        if (rootElem != null) {
            for (Element e : Dom4j.elements(rootElem, "field")) {
                if (propertyName.equals(e.attributeValue("name"))) {

                    String v = e.attributeValue("visible");
                    if (v != null)
                        field.setVisible(Boolean.valueOf(v));

                    String r = e.attributeValue("required");
                    if (r != null)
                        field.setRequired(Boolean.valueOf(r));

                    break;
                }
            }
        }

        return field;
    }

    public void resetFields() {
        metaPropertyFields = new ArrayList<>();
        virtualFields = new ArrayList<>();
    }

    public void updateFieldsXml() {
        if (CollectionUtils.isEmpty(metaPropertyFields) && CollectionUtils.isEmpty(virtualFields))
            return;

        Document doc = DocumentHelper.createDocument();
        Element root = doc.addElement("fields");

        for (FieldInfo field : metaPropertyFields)
            writeFieldElement(root, field);

        for (FieldInfo virtualField : virtualFields)
            writeFieldElement(root, virtualField);


        StringWriter writer = new StringWriter();
        Dom4j.writeDocument(doc, true, writer);
        setFieldsXml(writer.toString());
    }

    protected void writeFieldElement(Element root, FieldInfo field) {
        Element element = root.addElement("field");
        element.addAttribute("name", field.getName());
        element.addAttribute("visible", String.valueOf(field.getVisible()));
        element.addAttribute("required", String.valueOf(field.getRequired()));
    }
}