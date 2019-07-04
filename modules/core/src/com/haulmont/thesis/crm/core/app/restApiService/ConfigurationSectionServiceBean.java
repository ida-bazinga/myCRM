/*
 * Copyright (c) 2018 com.haulmont.thesis.crm.core.app
 */
package com.haulmont.thesis.crm.core.app.restApiService;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Query;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.thesis.crm.core.app.restApiService.myClass.ConfigurationSelection;
import com.haulmont.thesis.crm.core.app.restApiService.myClass.ConfigurationSelectionResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


/**
 * @author d.ivanov
 */
@Service(ConfigurationSectionService.NAME)
public class ConfigurationSectionServiceBean implements ConfigurationSectionService {


    @Inject
    protected Persistence persistence;

    protected String stringStartDate;
    protected String stringEndDate;
    protected String rkey;
    protected int seatingTypeId;
    protected int capacity;
    protected int square;
    protected Metadata metadata;
    protected Log log = LogFactory.getLog(ConfigurationSectionServiceBean.class);



    public ConfigurationSelectionResult configurationSearchService(String param) {

        this.metadata = AppBeans.get(Metadata.NAME);
        JsonParser jsonParser = new JsonParser();
        JsonElement json = jsonParser.parse(param);

        this.rkey = json.getAsJsonObject().get("rkey").getAsString();
        this.stringStartDate = json.getAsJsonObject().get("startDate").getAsString();
        this.stringEndDate = json.getAsJsonObject().get("endDate").getAsString();
        this.seatingTypeId = json.getAsJsonObject().get("seatingTypeId").getAsInt();
        this.capacity = json.getAsJsonObject().get("capacity").getAsInt();
        this.square = json.getAsJsonObject().get("square").getAsInt();

        ConfigurationSelectionResult configurationSelectionResult = metadata.create(ConfigurationSelectionResult.class);
        configurationSelectionResult.setRkey(this.rkey);
        configurationSelectionResult.setConfigurationSelection(congigurationList());

        log.warn("ConfigurationSectionServiceBean: request method configurationSearchService");

        return configurationSelectionResult;
    }


    protected List<ConfigurationSelection> congigurationList() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date d1 = new Date(sdf.parse(this.stringStartDate).getTime());
            Date d2 = new Date(sdf.parse(this.stringEndDate).getTime());

            Transaction tx = persistence.createTransaction();

            EntityManager em = persistence.getEntityManager();
            String sqlString = "{call getConfigurationSelection(?1,?2,?3,?4,?5)}";
            Query query = em.createNativeQuery(sqlString);
            query.setParameter(1, d1)
                    .setParameter(2, d2)
                    .setParameter(3, this.capacity)
                    .setParameter(4, this.seatingTypeId)
                    .setParameter(5, this.square);
            List list = query.getResultList();

            List<ConfigurationSelection> configurationSelectionArrayList = new ArrayList<>();
            for (Iterator it = list.iterator(); it.hasNext(); ) {
                Object[] row = (Object[]) it.next();
                int code = Integer.parseInt(row[0].toString());
                int statusId = Integer.parseInt(row[1].toString());
                String text = row[2] != null ? (String) row[2] : "";
                ConfigurationSelection configurationSelection = metadata.create(ConfigurationSelection.class);
                configurationSelection.setCode(code);
                configurationSelection.setStatusId(statusId);
                configurationSelection.setText(text);
                configurationSelectionArrayList.add(configurationSelection);
            }
            tx.commit();
            return configurationSelectionArrayList;
        } catch (Exception e) {
            log.error("ConfigurationSectionServiceBean: " + e.getMessage());
            return null;
        }
    }



/*
 ConfigurationSelection configurationSelection = new ConfigurationSelection(code, statusId, text);
String jsonConfigurationSelectionResult = "";
try {
    ConfigurationSelectionResult configurationSelectionResult = new ConfigurationSelectionResult(this.rkey, congigurationList());
    ObjectMapper mapper = new ObjectMapper();
    jsonConfigurationSelectionResult = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(configurationSelectionResult);
} catch (JsonProcessingException e) {
    jsonConfigurationSelectionResult = e.getMessage();
}
*/
/*
class ConfigurationSelectionResult {
    private String rkey;
    private List<ConfigurationSelection> configuration = new ArrayList<>();

    public ConfigurationSelectionResult(){}

    public ConfigurationSelectionResult(String rkey, List<ConfigurationSelection> configuration){
        this.rkey = rkey;
        this.configuration = configuration;
    }

    public String getRkey() {
        return rkey;
    }

    public void setRkey (String rkey) {
        this.rkey = rkey;
    }

    public List<ConfigurationSelection> getConfigurationSelection() {
        return configuration;
    }

    public void setConfigurationSelection (List<ConfigurationSelection> configuration) {
        this.configuration = configuration;
    }
}

class ConfigurationSelection {
    private int id;
    private int statusId;
    private String text;

    public ConfigurationSelection() {}

    public ConfigurationSelection(int id, int statusId, String text) {
        this.id = id;
        this.statusId = statusId;
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public void setId (int id) {
        this.id = id;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId (int statusId) {
        this.statusId = statusId;
    }

    public String getText() {
        return text;
    }

    public void setText (String text) {
        this.text = text;
    }
}
*/

}