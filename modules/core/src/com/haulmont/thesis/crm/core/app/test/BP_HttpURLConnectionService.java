/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 * d.ivanov
 */

package com.haulmont.thesis.crm.core.app.test;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Configuration;
import com.haulmont.thesis.crm.core.config.CrmConfig;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.lang.reflect.Field;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Arrays;
import java.lang.reflect.Modifier;




public class BP_HttpURLConnectionService {

    String urlWebServerAndPablicName;
    String entityName;
    String stringJson;
    String requestMethod;
    static String charset = StandardCharsets.UTF_8.toString();



    BP_HttpURLConnectionService(String requestMethod, String entityName, String stringJson) {
        this.entityName = entityName;
        this.stringJson = stringJson;
        this.requestMethod = requestMethod;
        this.urlWebServerAndPablicName = AppBeans.get(Configuration.class).getConfig(CrmConfig.class).getEfi1cUrl();
    }


    String httpURLConnectionService() {

        HttpURLConnection connection = null;

        try {
            String urlString = this.urlWebServerAndPablicName + "/odata/standard.odata/" + URLEncoder.encode(this.entityName, this.charset)  + "?$format=json";
            //авторизация
            java.net.Authenticator.setDefault(new java.net.Authenticator() {
                @Override
                protected java.net.PasswordAuthentication getPasswordAuthentication() {
                    return new java.net.PasswordAuthentication("s-sync-thezis", "Kcivgos5".toCharArray());
                }
            });

            URL url = new URL(urlString);
            connection = (HttpURLConnection)url.openConnection();
            connection.addRequestProperty("Content-Type", "application/json");

            if (this.requestMethod.equals("PATCH")) {
                allowMethods("PATCH");
                //not work ->
                //connection.setRequestProperty("X-HTTP-Method-Override", "PATCH");
                //this.requestMethod = "POST";
                //<--
                //use case HttpPatch
                //CloseableHttpClient httpClient = HttpClients.createDefault();
                //HttpPatch httpPatch = new HttpPatch(new URI("http://example.com"));
                //CloseableHttpResponse response = httpClient.execute(httpPatch);
            }

            connection.setRequestMethod(this.requestMethod);


            //создать - используется метод POST;
            //изменить - используем метод PATCH;
            //получить - используем метод GET;
            if (this.requestMethod == "POST" || this.requestMethod == "PATCH") {
                connection.setUseCaches(false);
                connection.setDoInput(true);
                connection.setDoOutput(true);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8);
                outputStreamWriter.write(stringJson);
                outputStreamWriter.flush();
                outputStreamWriter.close();
            }

            int HttpResult = connection.getResponseCode();
            // String result = connection.getResponseMessage();

            if(HttpResult == HttpURLConnection.HTTP_OK){
                return reader(connection.getInputStream());
            }else if (HttpResult == HttpURLConnection.HTTP_CREATED || HttpResult == HttpURLConnection.HTTP_NO_CONTENT) {
                return connection.getResponseMessage();
            } else {
                return String.format("ResponseCode: %s; ResponseMessage: %s; ErrorStream: %s",
                        HttpResult, connection.getResponseMessage(), reader(connection.getErrorStream()));
            }

        } catch (IOException e) {
            return e.getMessage();
        }finally{
            if(connection != null) {
                connection.disconnect();
            }
        }
    }

    protected JsonObject getAsJsonObject(String sb) {
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(sb);
        return jsonElement.getAsJsonObject();
    }

    protected String reader(InputStream inputStream) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, this.charset));
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();
            return sb.toString();
        } catch (IOException e) {
            return e.getMessage();
        }
    }


    private static void allowMethods(String... methods) {
        try {
            Field methodsField = HttpURLConnection.class.getDeclaredField("methods");

            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(methodsField, methodsField.getModifiers() & ~Modifier.FINAL);

            methodsField.setAccessible(true);

            String[] oldMethods = (String[]) methodsField.get(null);
            Set<String> methodsSet = new LinkedHashSet<>(Arrays.asList(oldMethods));
            methodsSet.addAll(Arrays.asList(methods));
            String[] newMethods = methodsSet.toArray(new String[0]);

            methodsField.set(null/*static field*/, newMethods);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }



}
