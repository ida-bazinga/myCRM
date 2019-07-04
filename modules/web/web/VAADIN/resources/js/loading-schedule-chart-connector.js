/**
 * @author Kirill Khoroshilov, 2018
 * Created on 10.12.2018.
 */

//https://vaadin.com/docs/v8/framework/gwt/gwt-javascript.html
//window.
com_haulmont_thesis_crm_web_gui_components_charts_scheduleChart_ScheduleChartJsComponent =
function(){
    var connector = this;
    var element = connector.getElement();
    var chartComponent = new loadingSchedule.Chart(element);

    element.setAttribute("id", "loadingScheduleChart");
    element.style.width = '100%';
    element.style.height = '100%';
    chartComponent.setRootElement(element);

    connector.onStateChange = function() {
        var state = connector.getState();

        if (state.data) {
            var chartData = JSON.parse(state.data,function (key, value) {
                var dateFields = ['d1', 'd2', 'd3', 'd4', 'optionDate', 'date'];
                if (value != null && dateFields.indexOf(key)+1) {
                    return new Date(value);
                }

                return value;
            });

            var chartRows = chartData.rows;
            chartRows.forEach(function (row) {
                //add subRows
                row.subRows = chartRows.filter(function (el) {
                    return el.type === row.type && el.parentId === row.id
                });

                //set parent row
                row.parentRow = chartRows.find(function (el, index, array) {
                    return el.type === row.type && el.id === row.parentId;
                });
            });

            chartData.values.forEach(function (value) {
                if (value.isEarlyInstallation && value.isLateDeinstallation){
                    value.event.name_ru = '(РМ/ПДМ) ' + value.event.name_ru
                }else if (value.isEarlyInstallation){
                    value.event.name_ru = '(РМ) ' + value.event.name_ru
                } else if (value.isLateDeinstallation){
                    value.event.name_ru = '(ПДМ) ' + value.event.name_ru
                }
            });

            chartComponent.setChartDays(chartData.columns);
            chartComponent.setChartRows(chartRows);
            chartComponent.setChartData(chartData.values);
            chartComponent.setChartCollisionData(chartData.collisions);

//          chartComponent.setBarHeight(10);
//          chartComponent.setTextFontSize(10);

            //var chartElement = document.createElement("div");
            //chartElement.setAttribute("id", "loadingScheduleChart");
            //element.appendChild(chartElement);


            //chartComponent.setRootElement(chartElement);

            chartComponent.setRowLabelClickCallback(function (uuid) {
                connector.openRoomEditor(uuid);
            });

            chartComponent.setOnEditCallback(function(item){
                connector.openEditor(item);
            });

            chartComponent.refreshChart();
        }
    };

    this.getChartContent = function () {
        var chartElement = document.getElementById("loadingScheduleChart");
        if (chartElement != null) {
            var value = utf8_to_b64(chartElement.innerHTML);
            connector.printChartContent(value);
        } else {
            connector.printChartContent("");
        }
    };

    function utf8_to_b64(str) {
        return window.btoa(unescape(encodeURIComponent(str)));
    }
};