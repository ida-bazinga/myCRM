/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.cashmachine.evotor.parser;

import com.google.gson.*;
import com.haulmont.bali.util.Preconditions;
import com.haulmont.thesis.crm.entity.CashMachineProduct;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Nonnull;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by k.khoroshilov on 23.04.2017.
 */
public class EvotorInfoParser {

    protected Gson gson;
    protected String response;

    public EvotorInfoParser(String response) {
        this();
        this.response = response;
    }

    public EvotorInfoParser() {
        this.gson = new GsonBuilder().create();
    }

    public String populateProductsInfo(@Nonnull List<CashMachineProduct> itemsList) throws Exception {
        Preconditions.checkNotNullArgument(itemsList);

        JsonArray jsonArray = new JsonArray();

        for (CashMachineProduct product : itemsList) {
            if (product != null) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("uuid", product.getUuid().toString());
                jsonObject.addProperty("code", product.getCode());
                jsonObject.addProperty("name", product.getName());
                jsonObject.addProperty("price", product.getPrice());
                jsonObject.addProperty("quantity", product.getQuantity());
                jsonObject.addProperty("costPrice", product.getCostPrice());
                jsonObject.addProperty("measureName", product.getMeasureName());
                jsonObject.addProperty("tax", product.getTaxCode());
                jsonObject.addProperty("allowToSell", product.getAllowToSell());
                jsonObject.addProperty("description", product.getDescription());
                jsonObject.addProperty("articleNumber", product.getArticleNumber());
                jsonObject.addProperty("parentUuid", product.getParentUuid());
                jsonObject.addProperty("group", product.getIsGroup());
                jsonObject.addProperty("type", product.getType());
                jsonObject.addProperty("alcoholByVolume", product.getAlcoholByVolume());
                jsonObject.addProperty("alcoholProductKindCode", product.getAlcoholProductKindCode());
                jsonObject.addProperty("tareVolume", product.getTareVolume());
                jsonObject.add("barCodes",
                            gson.toJsonTree(StringUtils.isNotBlank(product.getBarCodes()) ? product.getBarCodes().split(","): new String[]{}));
                jsonObject.add("alcoCodes",
                        gson.toJsonTree(StringUtils.isNotBlank(product.getAlcoCodes()) ? product.getAlcoCodes().split(",") : new String[]{}));
                jsonObject.add("fields", new JsonObject());

                jsonArray.add(jsonObject);
            }
        }
        return gson.toJson(jsonArray);
    }

    public String populate(@Nonnull Set<UUID> values) throws Exception {
        Preconditions.checkNotNullArgument(values);
        String result = null;
        JsonArray jsonArray = new JsonArray();
        for (UUID value : values) {
            if (StringUtils.isNotBlank(value.toString())) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("uuid", value.toString());
                jsonArray.add(jsonObject);
            }
        }
        result = gson.toJson(jsonArray);

        return result;
    }

    private String populate(Object sources, Class tClass) throws Exception {
        return gson.toJson(sources, tClass);
    }

    public <T> List<T> parse(Class<T> tClass) throws ParseException {
        List<T> result = new ArrayList<>();
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(response);
        if (jsonElement.isJsonArray()) {
            JsonArray items = jsonElement.getAsJsonArray();
            for (JsonElement item : items) {
                T info = gson.fromJson(item.getAsJsonObject(), tClass);
                result.add(info);
            }
        } else if (jsonElement.isJsonObject()) {
            T info = gson.fromJson(jsonElement.getAsJsonObject(), tClass);
            result.add(info);
        }

        return result;
    }

}
