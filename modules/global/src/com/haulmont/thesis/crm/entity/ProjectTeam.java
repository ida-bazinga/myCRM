/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.InheritanceType;
import javax.persistence.Inheritance;
import com.haulmont.thesis.core.entity.SimpleDoc;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import java.util.Set;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.thesis.core.entity.Project;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;

/**
 * @author p.chizhikov
 */
@NamePattern("%s %s|code,comment_ru")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "CRM_PROJECT_TEAM")
@Entity(name = "crm$ProjectTeam")
public class ProjectTeam extends BaseUuidEntity {
    private static final long serialVersionUID = -7188599628240367222L;

    @Column(name = "CODE", length = 50)
    protected String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EMPLOYEE_ID")
    protected ExtEmployee employee;

    @Column(name = "COMMENT_RU", length = 4000)
    protected String comment_ru;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DOCUMENT_ID")
    protected SimpleDoc document;

    @Column(name = "ANOTHER_ROLE")
    protected String anotherRole;





    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EXT_PROJECT_ID")
    protected ExtProject extProject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROLE_IN_PROJECT_ID")
    protected RoleInProject roleInProject;

    @Column(name = "SIGNATORY")
    protected Integer signatory;

    public void setSignatory(SignatoryEnum signatory) {
        this.signatory = signatory == null ? null : signatory.getId();
    }

    public SignatoryEnum getSignatory() {
        return signatory == null ? null : SignatoryEnum.fromId(signatory);
    }


    public void setRoleInProject(RoleInProject roleInProject) {
        this.roleInProject = roleInProject;
    }

    public RoleInProject getRoleInProject() {
        return roleInProject;
    }


    public void setExtProject(ExtProject extProject) {
        this.extProject = extProject;
    }

    public ExtProject getExtProject() {
        return extProject;
    }


    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setEmployee(ExtEmployee employee) {
        this.employee = employee;
    }

    public ExtEmployee getEmployee() {
        return employee;
    }

    public void setComment_ru(String comment_ru) {
        this.comment_ru = comment_ru;
    }

    public String getComment_ru() {
        return comment_ru;
    }

    public void setDocument(SimpleDoc document) {
        this.document = document;
    }

    public SimpleDoc getDocument() {
        return document;
    }

    public void setAnotherRole(String anotherRole) {
        this.anotherRole = anotherRole;
    }

    public String getAnotherRole() {
        return anotherRole;
    }


}