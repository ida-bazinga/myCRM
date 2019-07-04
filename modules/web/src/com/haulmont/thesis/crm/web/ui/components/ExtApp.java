/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.ui.components;


import com.haulmont.cuba.web.gui.WebUIPaletteManager;
import com.haulmont.thesis.web.DocflowApp;

/**
 * @author
 * @version $Id$
 */
public class ExtApp extends DocflowApp {
    static {
        // Register gui palettes
        WebUIPaletteManager.registerPalettes(new ExtComponentGoogleMapPalette());
        //WebUIPaletteManager.registerPalettes(new ExtComponentCalendarPalette());
    }
}
