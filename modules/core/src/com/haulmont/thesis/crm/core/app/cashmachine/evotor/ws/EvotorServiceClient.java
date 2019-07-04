package com.haulmont.thesis.crm.core.app.cashmachine.evotor.ws;

import com.haulmont.thesis.crm.core.app.UriBuilder;
import com.haulmont.thesis.crm.core.exception.ServiceNotAvailableException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.BasicClientConnectionManager;
import org.apache.http.protocol.HTTP;
import org.springframework.remoting.RemoteAccessException;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

/**
 * Created by k.khoroshilov on 01.05.2017.
 */
final class EvotorServiceClient {
    private static Log log = LogFactory.getLog(EvotorServiceClient.class);

    protected ClientConnectionManager clientConnManager;

    protected EvotorServiceClient() throws GeneralSecurityException {
        clientConnManager = getConnectionManager();
    }

    protected String executeGet(String url, String authToken) throws GeneralSecurityException, ServiceNotAvailableException {
        return executeGet(url, authToken, null);
    }

    protected String executeGet(String url, String authToken, Map<String, String> params)
            throws GeneralSecurityException, ServiceNotAvailableException {
        String response;
        HttpClient httpClient = new DefaultHttpClient(clientConnManager);
        UriBuilder uriBuilder = new UriBuilder(url);
        if (params != null) uriBuilder.setParameters(params);
        try {
            HttpGet method = new HttpGet(uriBuilder.build());
            method.addHeader("Content-Type", "application/json;charset=UTF-8");
            method.addHeader("X-Authorization", authToken);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            response = httpClient.execute(method, responseHandler);
        } catch (RemoteAccessException e){  //or org.apache.http.conn.HttpHostConnectException;
            log.error(e.getMessage());
            throw new ServiceNotAvailableException("Evotor");
        } catch (Exception e) {
            log.error(e.getMessage());
            response = e.getMessage();
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
        return response;
    }

    protected boolean executePost(String url, String authToken)
            throws GeneralSecurityException, InterruptedIOException, ServiceNotAvailableException {
        return executePost(url, authToken, null, null);
    }

    protected boolean executePost(String url, String authToken, String json)
            throws GeneralSecurityException, InterruptedIOException, ServiceNotAvailableException {
        return executePost(url, authToken, null, json);
    }

    protected boolean executePost(String url, String authToken, Map<String, String> params, String json)
            throws InterruptedIOException, GeneralSecurityException, ServiceNotAvailableException {
        Boolean result = false;
        HttpClient httpClient = new DefaultHttpClient(clientConnManager);
        UriBuilder uriBuilder = new UriBuilder(url);
        if (params != null) uriBuilder.setParameters(params);
        try {
            HttpPost method = new HttpPost(uriBuilder.build());
            method.addHeader("Content-Type", "application/json;charset=UTF-8");
            method.addHeader("X-Authorization", authToken);

            if (StringUtils.isNotBlank(json)) {
                HttpEntity entity = new StringEntity(json, HTTP.UTF_8);
                method.setEntity(entity);
            }

            HttpResponse coreResponse = httpClient.execute(method);
            int statusCode = coreResponse.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                log.debug("Unable to POST to " + url + "\n" + coreResponse.getStatusLine());
                result = true;
            }
        } catch (RemoteAccessException e){
            log.error(e.getMessage());
            throw new ServiceNotAvailableException("Evotor");
        } catch (InterruptedIOException e) {
            log.trace("POST has been interrupted");
            throw e;
        } catch (IOException e) {
            log.debug("Unable to POST to " + url + "\n" + e);
        } catch (URISyntaxException e) {
            log.error(e.getMessage());
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
        return result;
    }

    protected ClientConnectionManager getConnectionManager() throws GeneralSecurityException {
        TrustStrategy acceptingTrustStrategy = new TrustStrategy() {
            @Override
            public boolean isTrusted(X509Certificate[] certs, String authType) throws CertificateException {
                return true;
            }
        };

        SSLSocketFactory sf = new SSLSocketFactory(acceptingTrustStrategy, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("https", 443, sf));
        return new BasicClientConnectionManager(registry);
    }
}
