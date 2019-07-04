/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.dadata;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.haulmont.thesis.crm.core.app.dadata.entity.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.net.ssl.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.X509Certificate;


/**
 * @edit ivanov D.A.
 */
public final class DaData {

    private static final Log LOGGER = LogFactory.getLog(DaData.class);
    private final String authKey;
    private final String authSecret;
    private final String strUrl;
    private Gson gson = new GsonBuilder().create();

    /**
     * @param key
     * @param secret
     * @param url
     */
    public DaData(String key, String secret, String url) {
        authKey = key;
        authSecret = secret;
        strUrl = url;
    }


    public Address cleanAddress(String source) throws Exception {
        return cleanAddresses(source)[0];
    }

    public Address[] cleanAddresses(String... sources) throws Exception {
        return populate(Address[].class, "address", sources);
    }

    public SuggestCompany[] companysByInn(String... sources) throws Exception {
        return suggest(SuggestCompany[].class, "party", sources);
    }

    public SuggestCompany companyByInn(String source) throws Exception {
        SuggestCompany[] companies = companysByInn(source);
        if (companies.length != 0) {
            return findCompany(companies, source);
        }
        throw new RuntimeException("ИНН не найден!");
    }

    private SuggestCompany findCompany(SuggestCompany[] companies, String inn) {
        for (SuggestCompany company:companies) {
            SuggestCompanyData suggestCompanyData = company.getSuggestCompanyData();
            if ("MAIN".equals(suggestCompanyData.getBranch_type()) && inn.equals(suggestCompanyData.getInn())) {
                return company;
            }
        }
        return companies[0];
    }

    public SuggestBank bank(String source) throws Exception {
        SuggestBank[] banks = banks(source);
        if (banks.length != 0) {
            return banks[0];
        }
        return null;
    }

    public SuggestBank[] banks(String... sources) throws Exception {
        return suggest(SuggestBank[].class, "bank", sources);
    }

/*
    public Phone cleanPhone(String source) throws Exception {
        return cleanPhones(source)[0];
    }

    public Phone[] cleanPhones(String... sources) throws Exception {
        return populate(Phone[].class, "clean/phone", sources);
    }

    public Passport cleanPassport(String source) throws Exception {
        return cleanPassports(source)[0];
    }

    public Passport[] cleanPassports(String... sources) throws Exception {
        return populate(Passport[].class, "clean/passport", sources);
    }

    public Name cleanName(String source) throws Exception {
        return cleanNames(source)[0];
    }

    public Name[] cleanNames(String... sources) throws Exception {
        return populate(Name[].class, "clean/name", sources);
    }

    public Email cleanEmail(String source) throws Exception {
        return cleanEmails(source)[0];
    }

    public Email[] cleanEmails(String... sources) throws Exception {
        return populate(Email[].class, "clean/email", sources);
    }

    public Birthdate cleanBirthdate(String source) throws Exception {
        return cleanBirthdates(source)[0];
    }

    public Birthdate[] cleanBirthdates(String... sources) throws Exception {
        return populate(Birthdate[].class, "clean/birthdate", sources);
    }

    public Vehicle cleanVehicle(String source) throws Exception {
        return cleanVehicles(source)[0];
    }

    public Vehicle[] cleanVehicles(String... sources) throws Exception {
        return populate(Vehicle[].class, "clean/vehicle", sources);
    }
    */

    private <T> T populate(Class<T> tClass, String method, String... sources) throws Exception {
        //String url = "https://dadata.ru/api/v2/clean/";
        return gson.fromJson(new_fetchJson(method, sources), tClass);
    }

    private <T> T suggest(Class<T> tClass, String method, String... sources) throws Exception {
        //String url = "https://suggestions.dadata.ru/suggestions/api/4_1/rs/suggest/";
        String fJS = new_fetchJson(method, sources); //query
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(fJS);
        return  gson.fromJson(jsonElement.getAsJsonObject().get("suggestions"), tClass);
    }

    private String new_fetchJson(String method, String... sources) throws Exception {
        String toReturn = null;

        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
            public void checkClientTrusted(X509Certificate[] certs, String authType) { }
            public void checkServerTrusted(X509Certificate[] certs, String authType) { }

        } };

        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) { return true; }
        };
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

        URL url = new URL(strUrl + method);
        URLConnection connection = url.openConnection();

        connection.addRequestProperty("Content-Type", "application/json");
        connection.addRequestProperty("Authorization", "Token " + authKey);
        connection.addRequestProperty("X-Secret", authSecret);

        connection.setDoOutput(true);
        OutputStream outputStream = connection.getOutputStream();
        outputStream.write("address".equals(method) ? gson.toJson(sources).getBytes() : gson.toJson(new SuggestQuery(sources[0])).getBytes());
        outputStream.flush();
        outputStream.close();

        InputStream inputStream = connection.getInputStream();
        toReturn = IOUtils.toString(inputStream, "UTF-8");
        inputStream.close();

        return toReturn;
    }

}
