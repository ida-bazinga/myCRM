/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.phoneintegration.web;

/**
 * Created by k.khoroshilov on 02.06.2016.
 */
public class ServiceCommand {

    protected String title;
    protected String param1;
    protected String param2;

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setParam1(String param1) {
        this.param1 = param1;
    }

    public String getParam1() {
        return param1;
    }

    public void setParam2(String param2) {
        this.param2 = param2;
    }

    public String getParam2() {
        return param2;
    }
}
