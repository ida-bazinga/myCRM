/*
 * Copyright (c) 2019 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.*;

import com.haulmont.bali.util.Dom4j;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.chile.core.model.MetaProperty;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.thesis.core.app.TypedEntitiesTools;
import com.haulmont.thesis.core.entity.DocCategory;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.thesis.core.entity.FieldInfo;
import com.haulmont.thesis.core.entity.FieldInfoContainer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author d.ivanov
 */
@NamePattern("%s|name")
@Table(name = "CRM_REQ_DOC_KIND")
@Entity(name = "crm$ReqDocKind")
public class ReqDocKind extends BaseUuidEntity implements FieldInfoContainer {
    private static final long serialVersionUID = 1055610130253187918L;

    @Column(name = "NAME", nullable = false)
    protected String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "DOC_CATEGORY_ID")
    protected DocCategory docCategory;

    @Lob
    @Column(name = "FIELDS_XML")
    protected String fieldsXml;

    @Column(name = "DESCRIPTION", length = 1000)
    protected String description;

    @Transient
    protected String entityType;

    @Transient
    protected List<FieldInfo> metaPropertyFields;

    @Transient
    protected List<FieldInfo> virtualFields;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }


    public void setDocCategory(DocCategory docCategory) {
        this.docCategory = docCategory;
    }

    public DocCategory getDocCategory() {
        return docCategory;
    }

    public void setFieldsXml(String fieldsXml) {
        this.fieldsXml = fieldsXml;
    }

    public String getFieldsXml() {
        return fieldsXml;
    }


    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public List<FieldInfo> getFields() {
        return getFields(false);
    }

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

                    String c = e.attributeValue("locName");
                    if(c != null)
                        field.setLocName(c);

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
        element.addAttribute("locName", field.getLocName());
    }


}