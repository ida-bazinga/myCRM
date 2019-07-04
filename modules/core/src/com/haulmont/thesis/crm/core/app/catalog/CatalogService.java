/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.catalog;

import com.google.common.io.BaseEncoding;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.haulmont.cuba.core.app.FileStorageService;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Configuration;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.thesis.crm.core.config.CrmConfig;
import com.haulmont.thesis.crm.entity.*;
import com.haulmont.workflow.core.entity.AttachmentType;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

@ManagedBean(ICatalogService.NAME)
public class CatalogService implements ICatalogService {

    @Inject
    private FileStorageService fileStorageService;
    @Inject
    private DataManager dataManager;

    private UUID roomId;
    private RoomSeatingType roomSeatingType;
    private List<RoomGeolocation> roomGeolocationList;
    private List<RoomException> roomExceptionList;

    private Gson gson = new GsonBuilder().create();

    private String stringJson, extId, errorMessage;
    private LogCatalog logCatalog;
    private String url_base;

    //protected Log log = LogFactory.getLog(CatalogService.class);

    public String initCatalogService() {
        this.stringJson = "";
        this.extId = "";
        this.errorMessage = "";
        this.url_base = AppBeans.get(Configuration.class).getConfig(CrmConfig.class).getCatalogUrl();
        this.logCatalog = getLog();

        if (this.logCatalog != null) {
            this.roomSeatingType = getRoomSeatingType();
            this.roomId = this.roomSeatingType.getRoom().getId();
            this.roomGeolocationList = getRoomGeolocationList();
            this.roomExceptionList = getRoomExceptionList();
            boolean isBuilderCatalogJson = builderCatalogJson();

            if (isBuilderCatalogJson) {
               boolean isConnectCatalogServer = connectCatalogServer();
               if(isConnectCatalogServer) {
                   updateStatusLogCatalog();
                   return "подключение прошло успешно!";
               } else {
                   this.errorMessage = "подключение не удалось!" + System.lineSeparator() + this.errorMessage ;
                   updateStatusLogCatalog();
                   return this.errorMessage;
               }
            } else {
                this.errorMessage = "json не построен!" + System.lineSeparator() + this.errorMessage;
                updateStatusLogCatalog();
                return this.errorMessage;
            }
        } else {
            return "в логе нет данных для отправки!";
        }
    }

    protected LogCatalog getLog() {
        LoadContext loadContext = new LoadContext(LogCatalog.class).setView("edit");
        loadContext.setQueryString("select e from crm$LogCatalog e where e.shortServiceOperationResults = 1 and e.entityName = 'crm$RoomSeatingType' order by e.startDate");
        return dataManager.load(loadContext);
    }

    protected RoomSeatingType getRoomSeatingType() {
        LoadContext loadContext = new LoadContext(RoomSeatingType.class).setView("edit");
        loadContext.setQueryString("select e from crm$RoomSeatingType e where e.id = :Id")
                .setParameter("Id", this.logCatalog.getEntityId());
        return dataManager.load(loadContext);
    }

    protected List<RoomGeolocation> getRoomGeolocationList() {
        LoadContext loadContext = new LoadContext(RoomGeolocation.class).setView("edit");
        loadContext.setQueryString("select e from crm$RoomGeolocation e where e.room.id = :roomId")
                .setParameter("roomId", roomId);
        return dataManager.loadList(loadContext);
    }

    protected List<RoomException> getRoomExceptionList() {
        LoadContext loadContext = new LoadContext(RoomException.class).setView("edit");
        loadContext.setQueryString("select e from crm$RoomException e where e.room.id = :roomId")
                .setParameter("roomId", roomId);
        return dataManager.loadList(loadContext);
    }

    protected List<RoomSeatingType> getRoomSeatingTypeException(UUID Id) {
        LoadContext loadContext = new LoadContext(RoomSeatingType.class).setView("edit");
        loadContext.setQueryString("select e from crm$RoomSeatingType e where e.room.id = :Id")
                .setParameter("Id", Id);
        return dataManager.loadList(loadContext);
    }

    protected List<Image> imageAttachments() {
        List<Image> imageArrayList = new ArrayList<>();
        //room
        if (this.roomSeatingType.getRoom().getResourceAttachments() != null) {
           List<ResourceAttachment> resourceAttachmentList = this.roomSeatingType.getRoom().getResourceAttachments();
            for (ResourceAttachment resourceAttachment:resourceAttachmentList) {
                Image image = createImage (resourceAttachment.getAttachType(), resourceAttachment.getFile());
                if (image != null) {
                    imageArrayList.add(image);
                }
            }
        }
        //seatingType
        if (this.roomSeatingType.getRoomSeatingTypeAttachment() != null) {
            List<RoomSeatingTypeAttachment> roomSeatingTypeAttachmentList = this.roomSeatingType.getRoomSeatingTypeAttachment();
            for (RoomSeatingTypeAttachment roomSeatingTypeAttachment:roomSeatingTypeAttachmentList) {
                Image image = createImage (roomSeatingTypeAttachment.getAttachType(), roomSeatingTypeAttachment.getFile());
                if (image != null) {
                    imageArrayList.add(image);
                }
            }
        }
        return imageArrayList;
    }

    protected Image createImage(AttachmentType attachmentType, FileDescriptor fileDescriptor) {
        try {
            int codeInt = Integer.parseInt(attachmentType.getCode());
            String fileName = fileDescriptor.getName();
            byte[] bytes = fileStorageService.loadFile(fileDescriptor);
            //String fileData = new sun.misc.BASE64Encoder().encode(bytes);
            String fileData = BaseEncoding.base64().encode(bytes);

            return new Image(fileName, fileData, codeInt);
        } catch (Exception e) {
            return null;
        }
    }

    protected List<Integer> roomException() {
        List<Integer> rException = new ArrayList<>();
        for (RoomException roomException:this.roomExceptionList) {
            List<RoomSeatingType> RoomSeatingTypeList = getRoomSeatingTypeException(roomException.getRoomConflicting().getId());
            for (RoomSeatingType roomSeatingType:RoomSeatingTypeList) {
                rException.add(Integer.parseInt(roomSeatingType.getCode()));
            }
        }
        return rException;
    }


    protected Map<String, Object> roomGeolocation() {
        Map<String, Object> geolocation = new HashMap<>();
        for (RoomGeolocation roomGeolocation:roomGeolocationList) {
            if ("LOCATION".equals(roomGeolocation.getGeolocationType().getId())) {
                geolocation.put("location", new location(roomGeolocation.getLocationId().getId(),
                        roomGeolocation.getPointX(),
                        roomGeolocation.getPointY(),
                        roomGeolocation.getLength(),
                        roomGeolocation.getWidth()));
            }
            if ("MAP".equals(roomGeolocation.getGeolocationType().getId())) {
                geolocation.put("map", new map(roomGeolocation.getPointX(),
                        roomGeolocation.getPointY(),
                        roomGeolocation.getLength(),
                        roomGeolocation.getWidth()));
            }
        }
        return geolocation;
    }


    protected boolean builderCatalogJson() {
        try {
            CatalogJson catalogJson = new CatalogJson();
            catalogJson.setId(Integer.parseInt(this.roomSeatingType.getCode()));  //this.roomSeatingType.getCode();
            catalogJson.setName(this.roomSeatingType.getRoom().getName_ru()); //this.roomSeatingType.getRoom().getName_ru();
            catalogJson.setMinimalCapacity(this.roomSeatingType.getMinCapacity()); //this.roomSeatingType.getMinCapacity();
            catalogJson.setMaximumCapacity(this.roomSeatingType.getMaxCapacity()); //this.roomSeatingType.getMaxCapacity();
            catalogJson.setSeatingType(this.roomSeatingType.getSeatingType().getId()); //this.roomSeatingType .seatingType
            catalogJson.setSquare(this.roomSeatingType.getRoom().getTotalGrossArea().intValue()); //this.roomSeatingType.getRoom().getTotalGrossArea();
            catalogJson.setLength(this.roomSeatingType.getRoom().getLength()); //this.roomSeatingType.getRoom().getLength();
            catalogJson.setWidth(this.roomSeatingType.getRoom().getWidth()); //this.roomSeatingType.getRoom().getWidth();
            catalogJson.setDescription(this.roomSeatingType.getRoom().getComment_ru());
            List<Equipment> equipmentList = new ArrayList<>();
            equipmentList.add(new Equipment("Кресло черное", "10"));
            equipmentList.add(new Equipment("Круглый стол", "1"));
            catalogJson.setEquipment(equipmentList);
            catalogJson.setImage(imageAttachments());
            catalogJson.setException(roomException());
            catalogJson.setGeolocation(roomGeolocation());

            this.stringJson = gson.toJson(catalogJson);

            //log.warn("JSON : " + this.stringJson);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    protected boolean connectCatalogServer() {

        StringBuilder sb = new StringBuilder();
        HttpURLConnection connection = null;
        //String url_base = "http://efcatalog-dev.dwpr.ru/api/tezis/v1/configuration/change";
        String uniqueID = UUID.randomUUID().toString();
        String urlString = String.format("%s?rKey=%s", this.url_base, uniqueID);

        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection)url.openConnection();
            connection.addRequestProperty("Content-Type", "application/json");
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            connection.setDoInput(true);

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8);
            outputStreamWriter.write(this.stringJson);
            outputStreamWriter.flush();
            outputStreamWriter.close();

            int HttpResult = connection.getResponseCode();
           // String result = connection.getResponseMessage();

            if(HttpResult == HttpURLConnection.HTTP_OK){
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        connection.getInputStream(),"utf-8"));
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();

                System.out.println(""+sb.toString());
                JsonParser jsonParser = new JsonParser();
                JsonElement jsonElement = jsonParser.parse(sb.toString());
                this.extId = jsonElement.getAsJsonObject().get("rKey").getAsString();
                return true;

            }else{
                //System.out.println(connection.getResponseMessage());
                this.extId = uniqueID;
                this.errorMessage = String.format("ResponseCode: %s; ResponseMessage: %s", HttpResult, connection.getResponseMessage());
                return false;
            }

        } catch (IOException e) {
            this.extId = uniqueID;
            this.errorMessage = e.getMessage();
            return false;
        }finally{
            if(connection != null) {
                connection.disconnect();
            }
        }
    }

    protected void updateStatusLogCatalog() {
        ServiceOperationResultsEnum operationResultsEnum = "".equals(this.errorMessage) ?
                ServiceOperationResultsEnum.success : ServiceOperationResultsEnum.err;
        this.logCatalog.setShortServiceOperationResults(operationResultsEnum);
        this.logCatalog.setError(this.errorMessage);
        this.logCatalog.setExtId(this.extId);
        dataManager.commit(this.logCatalog);
    }

}


class CatalogJson {
    private int id;
    private String name;
    private int minimalCapacity;
    private int maximumCapacity;
    private int seatingType;
    private int square;
    private int length;
    private int width;
    private String description;
    private List<Equipment> equipment = new ArrayList<>();
    private List<Image> image = new ArrayList<>();
    private List<Integer> exception = new ArrayList<>();
    private Object geolocation;

    public int getId() {
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public int getMinimalCapacity() {
        return minimalCapacity;
    }

    public void setMinimalCapacity(int minimalCapacity){
        this.minimalCapacity = minimalCapacity;
    }

    public int getMaximumCapacity() {
        return maximumCapacity;
    }

    public void setMaximumCapacity(int maximumCapacity){
        this.maximumCapacity = maximumCapacity;
    }

    public int getSeatingType() {
        return seatingType;
    }

    public void setSeatingType(int seatingType){
        this.seatingType = seatingType;
    }

    public int getSquare() {
        return square;
    }

    public void setSquare(int square){
        this.square = square;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length){
        this.length = length;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width){
        this.width = width;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public List<Equipment> getEquipment() {
        return equipment;
    }

    public void setEquipment(List<Equipment> equipment){
        this.equipment = equipment;
    }

    public List<Image> getImage() {
        return image;
    }

    public void setImage(List<Image> image){
        this.image = image;
    }

    public List<Integer> getException() {
        return exception;
    }

    public void setException(List<Integer> exception){
        this.exception = exception;
    }

    public Object getGeolocation() {
        return geolocation;
    }

    public void setGeolocation(Object geolocation){
        this.geolocation = geolocation;
    }

}

class Equipment {
    private String name;
    private String value;

    public Equipment(String name, String value) {
        this.name = name;
        this.value = value;
    }
}

class Image {
    private String fileName;
    private String fileData;
    private int typeId;

    public Image(String fileName, String fileData, int typeId) {
        this.fileName = fileName;
        this.fileData = fileData;
        this.typeId = typeId;
    }
}

class location {
    private int locationId;
    private int pointX;
    private int pointY;
    private int length;
    private int width;

    public location(int locationId, int pointX, int pointY, int length, int width) {
        this.locationId = locationId;
        this.pointX = pointX;
        this.pointY = pointY;
        this.length = length;
        this.width = width;
    }
}

class map {
    private int pointX;
    private int pointY;
    private int length;
    private int width;

    public map(int pointX, int pointY, int length, int width) {
        this.pointX = pointX;
        this.pointY = pointY;
        this.length = length;
        this.width = width;
    }
}