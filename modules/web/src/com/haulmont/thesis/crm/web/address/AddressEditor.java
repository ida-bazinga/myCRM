package com.haulmont.thesis.crm.web.address;

import com.haulmont.charts.gui.components.map.MapViewer;
import com.haulmont.charts.gui.map.model.GeoPoint;
import com.haulmont.charts.gui.map.model.Marker;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.ValueListener;
import com.haulmont.thesis.crm.core.app.bp.Work1C;
import com.haulmont.thesis.crm.core.app.dadata.DaData;
import com.haulmont.thesis.crm.core.app.dadata.entity.Address;
import com.haulmont.thesis.crm.core.config.CrmConfig;
import com.haulmont.thesis.crm.entity.*;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

public class AddressEditor<T extends com.haulmont.thesis.crm.entity.Address> extends AbstractEditor <T> {

    //public static final String BANK_ICON = "VAADIN/resources/images/bpmn-is-role-assigned.png";
    @Inject
    private MapViewer mapViewer;

    private Marker marker;

    @Inject
    private TextField addressDadata;
    @Inject
    private DataManager dataManager;
    @Inject
    private Work1C work1C;
    @Inject
    private CrmConfig config;
    @Inject
    private SearchPickerField country;
    @Inject
    private TextField name_ru;
    @Inject
    private GroupBoxLayout groupSearchAddress, groupFirst, groupSecond;
    @Inject
    private HBoxLayout hboxRegion, hboxRegionDistrict, hboxCity, hboxAddress_ru, hboxParentObject, hboxZip, hboxOkato, hboxComment_ru;

    @Named("mainDs")
    protected Datasource<com.haulmont.thesis.crm.entity.Address> mainDs;

    private DaData daData;



    //todo проверять страну Если Россия прятать поля, а название собирать из англ наименований.

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        getDialogParams().setWidth(700).setHeight(750).setResizable(true);
    }

    @Override
    protected void postInit() {
        initComponentVisible();
        country.addListener(new ValueListener<Object>() {
            @Override
            public void valueChanged(Object source, String property, Object prevValue, Object value) {
                initComponentVisible();
            }
        });

        country.setSearchNotifications(new SearchField.SearchNotifications() {
            @Override
            public void notFoundSuggestions(String filterString) {
                showNotification("Введенное значение не найдено: " + filterString, NotificationType.TRAY);
            }

            @Override
            public void needMinSearchStringLength(String filterString, int minSearchStringLength) {
                showNotification("Minimum length of search string is " + minSearchStringLength, NotificationType.TRAY);
            }
        });

        markerMap();
    }

    private void initComponentVisible() {
        String nameRu = "Россия";
        if (getItem().getCountry() != null) {
            nameRu = getItem().getCountry().getName_ru();
        }
        else {
            country.setValue(getCountry("Россия"));
        }
        if("Россия".equals(nameRu)) {
            ExternalSystem row = config.getExternelSystemDaDataStandart();
            if (row != null) {
                daData = new DaData(row.getLogin(), row.getPassword(), row.getConnectionString());
            }
            componentVisible(true);
        }
        else {
            componentVisible(false);
        }
    }

    private void initGrpoupVisible(boolean isBool) {
        groupSearchAddress.setVisible(isBool);
        groupFirst.setVisible(true);
        groupSecond.setVisible(true);
        name_ru.setEditable(!isBool);
    }

    private void componentVisible(boolean isBool) {
        initGrpoupVisible(isBool);
        hboxRegion.setVisible(isBool);
        hboxRegionDistrict.setVisible(isBool);
        hboxCity.setVisible(isBool);
        hboxAddress_ru.setVisible(isBool);
        hboxParentObject.setVisible(isBool);
        hboxZip.setVisible(true);
        hboxOkato.setVisible(isBool);
        hboxComment_ru.setVisible(isBool);
    }

    public void cleanAddress() throws Exception {

        if (daData == null)
        {
            String txtMesage = "Отсутствуют настройки для работы с сервисом DaData";
            showNotification(getMessage(txtMesage), NotificationType.HUMANIZED);
            return;
        }

        if ("".equals(addressDadata.getValue().toString().trim()))
        {
            String txtMesage = "Заполните адрес для поиска!";
            showNotification(getMessage(txtMesage), NotificationType.HUMANIZED);
            return;
        }

        setAddressDadataParams();
    }

    private void setAddressDadataParams() throws Exception {
        com.haulmont.thesis.crm.entity.Address item = getItem();
        final Address address = daData.cleanAddress(addressDadata.getValue().toString());
        //решили каждый раз создавать новый адресс!!!
        item.setCode(address.getFiasId());
        item.setAddress_ru(getStreetAndHouse(address));
        item.setOkato(address.getOkato());
        item.setZip(address.getPostalCode());
        item.setCountry(getCountry(address.getCountry()));
        item.setGeoLat(address.getGeoLat());
        item.setGeoLon(address.getGeoLon());
        String city = address.getCity() != null && address.getCityType() != null ? String.format("%s. %s", address.getCityType(), address.getCity()) : address.getCity();
        String region = address.getRegion();
        if (city != null && region != null) {
            item.setRegion(getRegion(region));
            if (!city.equals(region)) {
                item.setCity(getCity(city));
            }
        }
        if (city == null || region == null) {
            if (city != null) {
                item.setCity(getCity(city));
            }
            if (region != null) {
                item.setRegion(getRegion(region));
            }
        }
        item.setRegionDistrict(getRegionDistrict(address.getArea()));
        //item.setComment_ru(address.getSource());
        markerMap();
    }

    private Country getCountry(String name) {
        LoadContext loadContext = new LoadContext(Country.class)
                .setView("_local");
        name = "(?i)%" + name + "%";
        loadContext.setQueryString("select e from crm$Country e where e.name_ru like :name")
                .setParameter("name", name);
        return dataManager.load(loadContext);
    }

    private City getCity(String name) {
        LoadContext loadContext = new LoadContext(City.class)
                .setView("_local");
        loadContext.setQueryString("select e from crm$City e where e.fullName_ru = :name")
                .setParameter("name", name);
        return dataManager.load(loadContext);
    }

    private Region getRegion(String name) {
        LoadContext loadContext = new LoadContext(Region.class)
                .setView("_local");
        name = "(?i)%" + name + "%";
        loadContext.setQueryString("select e from crm$Region e where e.name_ru like :name")
                .setParameter("name", name);
        return dataManager.load(loadContext);
    }

    private RegionDistrict getRegionDistrict(String name) {
        LoadContext loadContext = new LoadContext(RegionDistrict.class)
                .setView("_local");
        name = "(?i)%" + name + "%";
        loadContext.setQueryString("select e from crm$RegionDistrict e where e.name_ru like :name")
                .setParameter("name", name);
        return dataManager.load(loadContext);
    }

    private String getStreetAndHouse(Address address) {
        StringBuilder addressBilder = new StringBuilder();
        if (work1C.validate(address.getStreetType())) {
            addressBilder.append(address.getStreetType());
            addressBilder.append(" ");
        }
        if (work1C.validate(address.getStreet())) {
            addressBilder.append(address.getStreet());
        }
        if (work1C.validate(address.getHouseType())) {
            addressBilder.append(", ");
            addressBilder.append(address.getHouseType());
            addressBilder.append(" ");
        }
        if (work1C.validate(address.getHouse())) {
            addressBilder.append(address.getHouse());
        }
        if (work1C.validate(address.getBlockType())) {
            addressBilder.append(", ");
            addressBilder.append(address.getBlockType());
            addressBilder.append(" ");
        }
        if (work1C.validate(address.getBlock())) {
            addressBilder.append(address.getBlock());
        }
        if (work1C.validate(address.getFlatTypeFull())) {
            addressBilder.append(", ");
            addressBilder.append(address.getFlatType());
            addressBilder.append(" ");
        }
        if (work1C.validate(address.getFlat())) {
            addressBilder.append(address.getFlat());
        }
        if (work1C.validate(address.getUnparsedParts())) {
            addressBilder.append(", ");
            addressBilder.append(address.getUnparsedParts().replace(",", ""));
        }
        return addressBilder.toString();
    }

    /*
    private com.haulmont.thesis.crm.entity.Address getAddress(String code) {
        LoadContext loadContext = new LoadContext(com.haulmont.thesis.crm.entity.Address.class)
                .setView("browse");
        loadContext.setQueryString("select e from crm$Address e where e.code = :code")
                .setParameter("code", code);
        return dataManager.load(loadContext);
    }
    */

    private void markerMap() {
        final com.haulmont.thesis.crm.entity.Address item = getItem();

        if (work1C.validate(item.getGeoLat(), item.getGeoLon())) {
            mapViewer.clearMarkers();
            double latitude = item.getGeoLat();
            double longitude = item.getGeoLon();
            marker = mapViewer.createMarker("My place", mapViewer.createGeoPoint(latitude, longitude), true);
            marker.setPosition(mapViewer.createGeoPoint(latitude, longitude));
            mapViewer.addMarker(marker);
            GeoPoint center = mapViewer.createGeoPoint(latitude, longitude);
            mapViewer.setCenter(center);
            mapViewer.setZoom(10);
        }
    }



}