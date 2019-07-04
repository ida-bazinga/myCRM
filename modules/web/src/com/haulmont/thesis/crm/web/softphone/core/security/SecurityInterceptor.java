/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.web.softphone.core.security;

import com.haulmont.thesis.crm.web.softphone.core.SoftphoneConstants;
import org.atmosphere.config.service.AtmosphereInterceptorService;
import org.atmosphere.cpr.*;
import org.atmosphere.interceptor.InvokationOrder;
import org.atmosphere.websocket.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static org.atmosphere.cpr.HeaderConfig.X_ATMOSPHERE_ERROR;

@AtmosphereInterceptorService
public class SecurityInterceptor implements AtmosphereInterceptor, InvokationOrder {

    private final static Logger logger = LoggerFactory.getLogger(SecurityInterceptor.class);
    private AtmosphereConfig config;

    @Override
    public void configure(AtmosphereConfig config) {
        this.config = config;
        logger.trace("{} started.", SecurityInterceptor.class.getName());
    }

    @Override
    public Action inspect(AtmosphereResource r) {
        AtmosphereRequest req = r.getRequest();
        AtmosphereResponse res = r.getResponse();
        WebSocket webSocket = AtmosphereResourceImpl.class.cast(r).webSocket();

        if (req.headersMap().containsKey(SoftphoneConstants.AUTH_TOKEN_NAME)
                && req.headersMap().get(SoftphoneConstants.AUTH_TOKEN_NAME).equalsIgnoreCase(SoftphoneConstants.AUTH_TOKEN_VALUE))
            return Action.CONTINUE;

        if (webSocket != null && webSocket.isOpen()) {
            try {
                res.addHeader(X_ATMOSPHERE_ERROR, "Inncorrect AuthToken");
                res.sendError(1008, "Inncorrect AuthToken");
                webSocket.close();

            } catch (IOException e) {
                logger.warn("SecurityInterceptor", e);
            } finally {
                config.getBroadcasterFactory().removeAllAtmosphereResource(r);
                config.resourcesFactory().unRegisterUuidForFindCandidate(r);
            }
        }
        return Action.CANCELLED;
    }

    @Override
    public void postInspect(AtmosphereResource r) {
    }

    public String toString() {
        return "Thesis Security";
    }

    @Override
    public PRIORITY priority() {
        return PRIORITY.AFTER_DEFAULT;
    }
}
