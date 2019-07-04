/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.cashmachine.common.data.entity;

import com.google.gson.annotations.SerializedName;
import com.haulmont.thesis.crm.core.app.cashmachine.common.data.enums.DocType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by k.khoroshilov on 23.04.2017.
 */
public class DocumentInfo extends AbstractInfo {

    private static Log log = LogFactory.getLog(DocumentInfo.class);

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    @SerializedName("type")
    protected DocType type;

    @SerializedName("deviceId")
    protected String deviceId;

    @SerializedName("deviceUuid")
    protected UUID deviceUuid;

    @SerializedName("closeDate")
    protected String closeDate;

    @SerializedName("openDate")
    protected String openDate;

    @SerializedName("openUserCode")
    protected String openUserCode;

    @SerializedName("openUserUuid")
    protected UUID openUserUuid;

    @SerializedName("closeUserCode")
    protected String closeUserCode;

    @SerializedName("closeUserUuid")
    protected UUID closeUserUuid;

    @SerializedName("sessionUUID")
    protected UUID sessionUUID;

    @SerializedName("sessionNumber")
    protected String sessionNumber;

    @SerializedName("number")
    protected int number;

    @SerializedName("closeResultSum")
    protected String closeResultSum;

    @SerializedName("closeSum")
    protected String closeSum;

    @SerializedName("storeUuid")
    protected UUID storeUuid;

    @SerializedName("completeInventory")
    protected boolean isCompleteInventory;

    //@SerializedName("extras")
    //protected String extras;

    //@SerializedName("printGroups")
    //protected List<String> printGroups;

    @SerializedName("version")
    protected String version;

    @SerializedName("transactions")
    protected LinkedList<DocTransactionsInfo> transactions;

    //region Getters

    public DocType getType(){
        return type;
    }

    public String getDeviceId(){
        return deviceId;
    }

    public UUID getDeviceUuid(){
        return deviceUuid;
    }

    public Date getCloseDate(){
        try {
            return sdf.parse(closeDate);
        } catch (ParseException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    public Date getOpenDate(){
        try {
            return sdf.parse(openDate);
        } catch (ParseException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    public String getOpenUserCode(){
        return openUserCode;
    }

    public UUID getOpenUserUuid(){
        return openUserUuid;
    }

    public String getCloseUserCode(){
        return closeUserCode;
    }

    public UUID getCloseUserUuid(){
        return closeUserUuid;
    }

    public UUID getSessionUUID(){
        return sessionUUID;
    }

    public String getSessionNumber(){
        return sessionNumber;
    }

    public int getNumber(){
        return number;
    }

    public String getCloseResultSum(){
        return closeResultSum;
    }

    public String getCloseSum(){
        return closeSum;
    }

    public UUID getStoreUuid(){
        return storeUuid;
    }

    public boolean getIsCompleteInventory(){
        return isCompleteInventory;
    }

    //public String getExtras(){
    //    return extras;
    //}

    //public String getPrintGroups(){
    //    return printGroups;
    //}

    public String getVersion(){
        return version;
    }

    public List<DocTransactionsInfo> getTransactions(){
        return transactions;
    }

}
