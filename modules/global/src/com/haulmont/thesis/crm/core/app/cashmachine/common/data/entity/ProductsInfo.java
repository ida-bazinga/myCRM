/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.cashmachine.common.data.entity;

import com.google.gson.annotations.SerializedName;
import com.haulmont.thesis.crm.core.app.cashmachine.common.data.enums.CommodityType;
import com.haulmont.thesis.crm.core.app.cashmachine.common.data.enums.MeasureUnit;
import com.haulmont.thesis.crm.core.app.cashmachine.common.data.enums.TaxRate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by k.khoroshilov on 23.04.2017.
 */
public class ProductsInfo extends AbstractInfo {

    @SerializedName("code")
    protected String code;

    @SerializedName("barCodes")
    protected List<String> barCodes;

    @SerializedName("alcoCodes")
    protected final List<String> alcoCodes = new ArrayList<>();

    @SerializedName("name")
    protected String name;

    @SerializedName("price")
    protected double price;

    @SerializedName("quantity")
    protected double quantity;

    @SerializedName("costPrice")
    protected double costPrice;

    @SerializedName("measureName")
    protected MeasureUnit measureUnit;

    @SerializedName("tax")
    protected TaxRate taxRate;

    @SerializedName("allowToSell")
    protected boolean isAllowToSell;

    @SerializedName("description")
    protected String description;

    @SerializedName("articleNumber")
    protected String articleNumber;

    @SerializedName("parentUuid")
    protected UUID parentUuid;

    @SerializedName("group")
    protected boolean isGroup;

    @SerializedName("type")
    protected final CommodityType type = CommodityType.NORMAL;

    @SerializedName("alcoholByVolume")
    protected final double alcoholByVolume = 0;

    @SerializedName("alcoholProductKindCode")
    protected final int alcoholProductKindCode = 0;

    @SerializedName("tareVolume")
    protected final double tareVolume = 0;

    //@SerializedName("fields")
    //protected List<String> fields;

    public String getCode(){
        return code;
    }

    public List<String> getBarCodes(){
        return barCodes;
    }

    public List<String> getAlcoCodes(){
        return alcoCodes;
    }

    public String getName(){
        return name;
    }

    public double getPrice(){
        return price;
    }

    public double getQuantity(){
        return quantity;
    }

    public double getCostPrice(){
        return costPrice;
    }

    public MeasureUnit getMeasureUnit(){
        return measureUnit;
    }

    public TaxRate getTaxRate(){
        return taxRate;
    }

    public boolean getAllowToSell(){
        return isAllowToSell;
    }

    public String getDescription(){
        return description;
    }

    public String getArticleNumber(){
        return articleNumber;
    }

    public UUID getParentUuid(){
        return parentUuid;
    }

    public boolean getGroup(){
        return isGroup;
    }

    public CommodityType getType(){
        return type;
    }

    public double getAlcoholByVolume(){
        return alcoholByVolume;
    }

    public int getAlcoholProductKindCode(){
        return alcoholProductKindCode;
    }

    public double getTareVolume(){
        return tareVolume;
    }
}
