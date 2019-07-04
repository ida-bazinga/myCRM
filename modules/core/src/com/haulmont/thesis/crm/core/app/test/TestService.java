/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.security.app.Authenticated;
import com.haulmont.thesis.crm.core.app.projectRoom.ICreateProjectRoomMBean;
import com.haulmont.thesis.crm.core.app.unisender.UniSender;
import com.haulmont.thesis.crm.core.app.unisender.entity.FieldData;
import com.haulmont.thesis.crm.core.app.unisender.exceptions.UniSenderException;
import com.haulmont.thesis.crm.core.app.unisender.requests.ImportContactsRequest;
import com.haulmont.thesis.crm.core.app.unisender.responses.ImportContactsResponse;

import javax.annotation.ManagedBean;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;


@ManagedBean(ITestServiceMBean.NAME)
public class TestService implements ITestServiceMBean {




    public String test1() {
        try {
            BP_HttpURLConnectionService httpURLConnection =
                    new BP_HttpURLConnectionService("GET", "Catalog_Организации", "");


            return String.format("Result: %s", httpURLConnection.httpURLConnectionService());
        } catch (Exception e) {
            return String.format("Exception: %s", e.getMessage());
        }
    }


    public String test2() {
        try {

            String uniqueID = "1df478f9-f168-4b07-8a61-3f85ca2be16e";
                    //UUID.randomUUID().toString();

            String stringJson =
                    "{" +
                            "\"АдресДоставки\": \"\"," +
                            "\"СтруктурнаяЕдиница_Key\": \"d366068c-c9f2-11e2-918b-0025b320dd1e\"," +
                            "\"Комментарий\": \"#Загружен из CRM\"," +
                            "\"Number\": \"1010\"," +
                            "\"КратностьВзаиморасчетов\": \"1\",\n" +
                            "\"Организация_Key\": \"89ddd967-dbab-11e1-a676-1c6f65d61cbb\"," +
                            "\"Услуги\": [" +
                            "{" +
                            "\"LineNumber\": \"1\"," +
                            "\"Содержание\": \"Test Rest api\"," +
                            "\"Количество\": 1," +
                            "\"Цена\": 20520," +
                            "\"Сумма\": 20520," +
                            "\"СтавкаНДС\": \"НДС18\"," +
                            "\"СуммаНДС\": 3130.17," +
                            "\"Номенклатура_Key\": \"b21b86b8-413d-11e7-a1ff-001e67377921\"," +
                            "\"ЕдиницаИзмерения_Key\": \"c22ab577-015e-11e2-a645-1c6f65d61cbb\"," +
                            "\"Департамент_Key\": \"00000000-0000-0000-0000-000000000000\"" +
                            "}" +
                            "]," +
                            "\"Склад_Key\": \"00000000-0000-0000-0000-000000000000\"," +
                            "\"Отгружено\": false," +
                            "\"твб_Утвержден\": false," +
                            "\"Контрагент_Key\": \"8ebf363c-7920-11e7-b78a-001e67377921\"," +
                            "\"СуммаДокумента\": 20520," +
                            "\"Оплачено\": false," +
                            "\"ДоговорКонтрагента_Key\": \"768ac7f3-9978-11e8-80ba-32b21e38a38e\"," +
                            "\"DeletionMark\": false," +
                            "\"Статус\": \"\"," +
                            "\"ВалютаДокумента_Key\": \"da284893-c105-11e1-a449-0025b320dd1e\"," +
                            "\"ВозвратнаяТара\": []," +
                            "\"УчитыватьНДС\": true," +
                            "\"Товары\": []," +
                            "\"СуммаВключаетНДС\": true," +
                            "\"Date\": \"2018-10-10T12:20:37\"," +
                            "\"Posted\": true," +
                            "\"ЕдИзмерения_Key\": \"00000000-0000-0000-0000-000000000000\"," +
                            "\"Ответственный_Key\": \"d6311873-aa4e-11e6-a32d-001e67377921\"," +
                            "\"КурсВзаиморасчетов\": 1," +
                            "\"Ref_Key\": \"" + uniqueID +
                            "\"}"
                    ;



            BP_HttpURLConnectionService httpURLConnection =
                    new BP_HttpURLConnectionService("POST", "Document_ЗаявкаНаВыставку", stringJson);


            return String.format("Result: %s", httpURLConnection.httpURLConnectionService());
        } catch (Exception e) {
            return String.format("Exception: %s", e.getMessage());
        }
    }

    public String test3() {
        try {

            String stringJson = "{" +
                    "\"Комментарий\": \"#Загружен из CRM\"" +
                    "}"
                    ;

            BP_HttpURLConnectionService httpURLConnection =
                    new BP_HttpURLConnectionService("PATCH", "Document_ЗаявкаНаВыставку(guid:'1df478f9-f168-4b07-8a61-3f85ca2be16e')", stringJson);


            return String.format("Result: %s", httpURLConnection.httpURLConnectionService());
        } catch (Exception e) {
            return String.format("Exception: %s", e.getMessage());
        }
    }

    public String test4() {
        try {
            BP_HttpURLConnectionService httpURLConnection =
                    new BP_HttpURLConnectionService("DELETE", "Document_ЗаявкаНаВыставку(guid:'1df478f9-f168-4b07-8a61-3f85ca2be16e')", "");


            return String.format("Result: %s", httpURLConnection.httpURLConnectionService());
        } catch (Exception e) {
            return String.format("Exception: %s", e.getMessage());
        }
    }

    @Authenticated
    public boolean сreateProjectRoom(String bookingEventId) {
        UUID Id = UUID.fromString(bookingEventId);
        ICreateProjectRoomMBean iCreateProjectRoomMBean = AppBeans.get(ICreateProjectRoomMBean.class);
        return  iCreateProjectRoomMBean.create(Id);
    }

    @Authenticated
    public boolean removeProjectRoom(String bookingEventId) {
        UUID Id = UUID.fromString(bookingEventId);
        ICreateProjectRoomMBean iCreateProjectRoomMBean = AppBeans.get(ICreateProjectRoomMBean.class);
        return  iCreateProjectRoomMBean.remove(Id);
    }

    public String testBuildJsonAccount() {

        try {

            OutputStream outputStream = new ByteArrayOutputStream();

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode rootNode = mapper.createObjectNode();
            rootNode.put("Ref_Key", "29f6f971-c896-11e4-82ea-001e67377921");
            rootNode.put("НаименованиеПолное", "Общество с ограниченной ответственностью \"ФАРЭКСПО2\"");
            rootNode.put("Комментарий", "###Загружен из CRM");
            ObjectNode childNode = rootNode.putObject("ИсторияКонтактнойИнформации");
            childNode.put("Вид_Key", "e8824d2b-a731-11e6-bf18-20cf30c9e6ce");
            childNode.put("Период", "0001-01-01T00:00:00");
            childNode.put("Представление", "191187, Россия, г. Санкт-Петербург, наб. Реки Фонтанки, д. 8, стр. А");

            mapper.writeValue(outputStream, rootNode);

            return outputStream.toString();
        } catch (IOException ex) {
            return String.format("Sorry. had a error on during Object to String. ("+ex.toString()+")");
        }
    }


    public String testBuildJsonUnisender() {
        /* Массовый импорт и синхронизация контактов
         *  http://www.unisender.com/ru/help/api/importContacts */

        UniSender us = new UniSender();

        ObjectMapper mapper = new ObjectMapper();

        FieldData fd = new FieldData();
        fd.addFields(new String[]{"email", "email_list_ids"}); // Также можно использовать List

        fd.addValues(new String[]{"some@mail.com", "17129945"});
        fd.addValues(new String[]{"some2@mail.com", "17129945"});

        String jsonString = "нет";
        try {

            ImportContactsRequest ic = new ImportContactsRequest(fd);
            ImportContactsResponse icr = us.importContacts(ic);

            //jsonString = mapper.writeValueAsString(fd);
            jsonString = String.format(
                    "total:%s, inserted:%s, updated:%s, deleted:%s, new_emails:%s",
                    icr.getTotal(),
                    icr.getInserted(),
                    icr.getUpdated(),
                    icr.getDeleted(),
                    icr.getNewEmails()
            );
        } catch (UniSenderException e) {
            return e.getMessage();
        }
        return jsonString;

    }

}
