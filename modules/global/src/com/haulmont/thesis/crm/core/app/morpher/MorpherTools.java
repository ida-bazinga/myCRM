/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.morpher;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ru.morpher.russian.Case;
import ru.morpher.russian.Declension;
import ru.morpher.russian.IParse;

import javax.annotation.ManagedBean;

/**
 * Created by d.ivanov on 25.01.2017.
 */

@ManagedBean(MorpherTools.NAME)
public class MorpherTools {


    public static final String NAME = "crm_MorpherTools";
    protected static Log log = LogFactory.getLog(MorpherTools.class);
    private Declension declension = null;

    public MorpherTools() {
        try {
            this.declension = new Declension();
        }
        catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public String getGenetive(String nominative){
        String result = "";
        if (declension != null) {
            IParse parse = declension.parse(nominative);
            result = parse.getForm(Case.Gen, false);
        }
        return (result != "") ? result : nominative;
    }

    public String getDative(String nominative){
        String result = "";
        if (declension != null) {
            IParse parse = declension.parse(nominative);
            result = parse.getForm(Case.Dat, false);
        }
        return (result != "") ? result : nominative;
    }

    public String getAccusative(String nominative){
        String result = "";
        if (declension != null) {
            IParse parse = declension.parse(nominative);
            result = parse.getForm(Case.Acc, false);
        }
        return (result != "") ? result : nominative;
    }

    public String getInstrumental(String nominative){
        String result = "";
        if (declension != null) {
            IParse parse = declension.parse(nominative);
            result = parse.getForm(Case.Ins, false);
        }
        return (result != "") ? result : nominative;
    }

    public String getPrepositional(String nominative){
        String result = "";
        if (declension != null) {
            IParse parse = declension.parse(nominative);
            result = parse.getForm(Case.Pre, false);
        }
        return (result != "") ? result : nominative;
    }

    public String getLocal(String nominative){
        String result = "";
        if (declension != null) {
            IParse parse = declension.parse(nominative);
            result = parse.getForm(Case.Loc, false);
        }
        return (result != "") ? result : nominative;
    }
}
