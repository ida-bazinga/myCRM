package com.haulmont.thesis.crm.web.gui.components.charts.scheduleChart;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.UuidProvider;
import com.haulmont.reports.entity.Report;
import com.haulmont.reports.gui.ReportGuiManager;
import com.haulmont.thesis.crm.gui.components.charts.ScheduleChart;
import com.vaadin.annotations.JavaScript;
import com.vaadin.annotations.StyleSheet;
import com.vaadin.ui.AbstractJavaScriptComponent;
import com.vaadin.ui.JavaScriptFunction;
import elemental.json.JsonArray;
import elemental.json.impl.JreJsonObject;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Kirill Khoroshilov, 2018
 * Created on 10.12.2018.
 */

@JavaScript({"vaadin://resources/js/d3.v5.min.js", "vaadin://resources/js/moment-with-locales.min.js","vaadin://resources/js/loading-schedule-chart.js",
        "vaadin://resources/js/loading-schedule-chart-connector.js"})
@StyleSheet({"vaadin://resources/css/loading-schedule-chart.css"})
public class ScheduleChartJsComponent extends AbstractJavaScriptComponent{

    private Log log = LogFactory.getLog(getClass());

    protected ScheduleChart.ChartItemClickListener itemClickListener;

    public void setItemClickListener(ScheduleChart.ChartItemClickListener listener) {
        itemClickListener = listener;
    }

    public ScheduleChartJsComponent() {
        addFunction("printChartContent", new JavaScriptFunction() {
            @Override
            public void call(JsonArray arguments) throws org.json.JSONException {
                if (arguments != null) {
                    String value = arguments.getString(0);
                    if (value != null) {
                        try {
                            byte[] bytes = Base64.decodeBase64( value.getBytes(StandardCharsets.UTF_8) );
                            String content = new String(bytes);

                            Report report = loadReport();
                            if (report != null) {
                                Map<String, Object> reportParam = new HashMap<>();
//                                reportParam.put("startDate", getStartDate());
//                                reportParam.put("endDate", getEndDate());
                                reportParam.put("content", content);

                                AppBeans.get(ReportGuiManager.class).printReport(report, reportParam, null);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            log.error(e.getMessage(), e);
                        }
                    }
                }
            }
        });

        addFunction("openEditor", new JavaScriptFunction() {
            @Override
            public void call(JsonArray arguments) {
                if (arguments != null) {
                    try {
                        JreJsonObject obj = (JreJsonObject)arguments.getObject(0);
                        Map<String, Object> map = (Map<String, Object>)obj.getObject();
                    //{"addColor":"pink","edit_entity_id":"9901f97d-bf65-6ca3-c17a-2cbdb85c3cf8","entity_id":"0afbba07-b13c-1c25-4a15-8d4f7ec6c0a2",
                    //        "d1":"2019-04-28T21:00:00.000Z","isPhantom":false,"d2":"2019-04-30T21:00:00.000Z","metaClassName":"crm$BookingEvent",
                    //        "d3":"2019-05-04T21:00:00.000Z","d4":"2019-05-05T21:00:00.000Z","optionDate":"2019-08-31T21:00:00.000Z","mainColor":
                    //         "red", "id":"290417ab-87b0-e0cb-0c20-83ff28f8960e","row_id":"2055bbbb-14b0-f7cc-507c-f12d29c868f6",
                    //        "event":{"id":"48c7499a-b6ed-be36-c939-8d5ab426dc97","name_ru":"JOKER 2019"}}

                        Boolean isPhantom = (Boolean)map.get("isPhantom");

                        if (itemClickListener != null && !isPhantom){
                            String metaClassName = map.get("metaClassName").toString();
                            String entityId = map.get("edit_entity_id").toString();

                            itemClickListener.onClick(metaClassName, UuidProvider.fromString(entityId));
                        }

                    } catch (Exception e) {
                        log.error(e.getLocalizedMessage(), e);
                    }
                }
            }
        });

        addFunction("openRoomEditor", new JavaScriptFunction() {
            @Override
            public void call(JsonArray arguments) {
                if (arguments != null) {

                }
            }
        });
    }

    public void setData(String value) {
        getState().data = value;
    }

    @Override
    protected ScheduleChartJsComponentState getState() {
        return (ScheduleChartJsComponentState)super.getState();
    }

    //todo report code as param or Report as param
    protected Report loadReport() {
        LoadContext loadContext = new LoadContext(Report.class)
                .setView("report.edit");
        loadContext.setQueryString("select r from report$Report r where r.code = :code")
                .setParameter("code", "loadingScheduleChart");
        return AppBeans.get(DataManager.class).load(loadContext);
    }

    public void printChart(){
        callFunction("getChartContent");
    }
}


