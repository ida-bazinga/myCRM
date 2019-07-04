/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.unisender.entity;

/**
 * Created by a.donskoy on 24.11.2017.
 */
public class InvalidArgumentListException extends Exception{
    public InvalidArgumentListException() {}
    public InvalidArgumentListException(String message)
    {
        super(message);
    }
}