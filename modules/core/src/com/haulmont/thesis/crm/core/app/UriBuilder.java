/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by k.khoroshilov on 23.04.2017.
 */
public class UriBuilder {
    protected String address;

    protected Map<String, String> parameters;

    public UriBuilder(String address) {
        super();
        this.address = address;
    }

    public UriBuilder addParameter(String id, String value) {
        if (id != null && id.trim().length() != 0) {
            if (this.parameters == null)
                this.parameters = new HashMap<>();
            this.parameters.put(id, value);
        }
        return this;
    }

    public URI build() throws URISyntaxException {
        return new URI(buildQuery());
    }

    public String getAddress() {
        return this.address;
    }

    public UriBuilder setAddress(String address) {
        this.address = address;
        return this;
    }

    public Map<String, String> getParameters() {
        return this.parameters;
    }

    public UriBuilder setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
        return this;
    }

    protected String buildQuery() {
        if (this.parameters == null || this.parameters.size() == 0)
            return this.address;

        StringBuilder sb = new StringBuilder(this.address);
        sb.append("?");
        int i = 0;
        for (Map.Entry<String, String> entry : this.parameters.entrySet()) {
            String key = entry.getKey();
            if (key != null && key.trim().length() != 0) {
                sb.append(key).append("=");
                if (entry.getValue() != null)
                    sb.append(entry.getValue().trim());
            }
            if (++i != this.parameters.size())
                sb.append("&");

        }
        return sb.toString();
    }
}
