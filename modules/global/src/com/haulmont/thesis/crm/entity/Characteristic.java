/*
 * Copyright (c) 2016 com.haulmont.thesis.crm.entity
 */
package com.haulmont.thesis.crm.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.InheritanceType;
import javax.persistence.Inheritance;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import com.haulmont.chile.core.annotations.NamePattern;

/**
 * @author k.khoroshilov
 */
@NamePattern("%s|name_ru")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "CRM_CHARACTERISTIC")
@Entity(name = "crm$Characteristic")
public class Characteristic extends BaseLookup {
    private static final long serialVersionUID = 8621805936314521038L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHARACTERISTIC_TYPE_ID")
    protected CharacteristicType characteristicType;

    @Column(name = "DURATION")
    protected Integer duration;

    @Column(name = "ID1C", length = 50)
    protected String id1c;

    @Column(name = "NAME_EN", length = 500)
    protected String name_en;




    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GUESTHOUSE_ID")
    protected Guesthouse guesthouse;

    public void setGuesthouse(Guesthouse guesthouse) {
        this.guesthouse = guesthouse;
    }

    public Guesthouse getGuesthouse() {
        return guesthouse;
    }


    public void setName_en(String name_en) {
        this.name_en = name_en;
    }

    public String getName_en() {
        return name_en;
    }


    public void setCharacteristicType(CharacteristicType characteristicType) {
        this.characteristicType = characteristicType;
    }

    public CharacteristicType getCharacteristicType() {
        return characteristicType;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setId1c(String id1c) {
        this.id1c = id1c;
    }

    public String getId1c() {
        return id1c;
    }


}