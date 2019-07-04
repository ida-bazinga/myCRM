/**
 * @author Kirill Khoroshilov, 2018
 * Created on 10.12.2018.
 */
var loadingSchedule = loadingSchedule || {};

loadingSchedule.Chart = function (element) {
    var version = "0.4.0";

    this.getVersion = function(){return version};

    //todo refactor
// add param time format for parse date

    Date.prototype.addDays = function (days) {
        var date = new Date(this.valueOf());
        date.setDate(date.getDate() + days);
        return date;
    };

    Date.prototype.equals = function (day) {
        var date = new Date(this.valueOf());
        return date.getTime() === day.getTime();
    };

    function getMonthName(monthIndex) {
        var monthNames = ['январь', 'февраль', 'март', 'апрель', 'май', 'июнь', 'июль', 'август', 'сентябрь', 'октябрь', 'ноябрь', 'декабрь'];
        return monthNames[monthIndex];
    }

    function formatDate(date, shortYear) {
        shortYear = shortYear === undefined ? false : shortYear;
        var d = date.getDate();
        var m = date.getMonth() + 1; //Month from 0 to 11
        var y = date.getFullYear().toString();
        return '' + (d <= 9 ? '0' + d : d) + '.' + (m <= 9 ? '0' + m : m) + '.' + y.substring(y.length -(shortYear ? 2 : y.length));
    }

    function monthDiff(d1, d2) {
        var months;
        months = (d2.getFullYear() - d1.getFullYear()) * 12;
        months = months - d1.getMonth() + 1;
        months += d2.getMonth();
        return months <= 0 ? 0 : months;
    }

    // https://bl.ocks.org/mbostock/7555321
    function wrap(text, str, width, maxLines, dy, fill) {
        text.each(function() {
            var textEl = d3.select(this),
                words = str.split(/\s+/).reverse(),
                word,
                line = [],
                lineNumber = 0,
                lineHeight = 1.0, // ems
                xPos = textEl.attr("x"),
                yPos = textEl.attr("y");

            while (lineNumber < maxLines) {
                var tspan = textEl
                    .append("tspan")
                    .attr("x", xPos)
                    .attr("y", yPos)
                    .attr("dy", lineNumber * lineHeight + dy + "em")
                    .style("fill", fill);

                var addWord = words.length > 0;

                while (addWord) {
                    if (tspan.node().getComputedTextLength() < width) {
                        if ((word = words.pop())) {
                            line.push(word);
                            tspan.text(line.join(" "));
                            addWord = true;
                        } else {
                            addWord = false;
                        }
                    } else {
                        if (line.length > 1) {
                            word = line.pop();
                            word = words.push(word);
                            tspan.text(line.join(line.length > 1 ? " " : ""));
                        } else {
                            tspan.text(line.join(""));
                            trimmer(tspan, width, 12);
                        }
                        addWord = false;
                    }
                }
                lineNumber++;
                line = [];
            }
        });
    }

    function trimmer(tspan, width, margin){
        var letters = tspan.text().split('');

        while(tspan.node().getComputedTextLength() > margin * 2 + width){
            letters.pop();
            tspan.text(letters.join("") + "...")
        }
    }

    var chart = this;

    var svgWidth = 0,
        svgHeight = 0;

    this.setChartWidth = function (width) {
        svgWidth = width;
    };

    chart.getChartWidth = function () {
        return svgWidth;
    };

    chart.setChartHeight = function (height) {
        svgHeight = height;
    };

    chart.getChartHeight = function () {
        return svgHeight;
    };

    chart.xOffset = 120;
    chart.yOffset = 50;
    chart.padding = 4;
    chart.barHeight = 7;
    chart.eventFontSize = 11;
    chart.lineK = 1.5;

    //todo add scroll to now - 1Month
    // http://qaru.site/questions/609/jquery-scroll-to-element
    // https://www.abeautifulsite.net/smoothly-scroll-to-an-element-without-a-jquery-plugin-2
    // D3 Axis Tick Tricks https://codepen.io/anon/pen/jvRwee
    this.refreshChart = function (rootElement, startDate, endDate, objs, lines) {

        var idRootElement = rootElement.getAttribute('id');

        if (idRootElement == null) return;

        //todo date format param
        var parseDate = d3.timeParse("%d.%m.%Y"), //"%d.%m.%Y %H:%M:%S"),
            wk = 450,
            hk = 80,
            margin = 10;

        var monthCount = monthDiff(startDate, endDate);
        var width = (monthCount > 4 ? monthCount : 4) * wk + chart.xOffset + margin;

        var lineCount =  0;

        objs.forEach( function (obj) {
            function findByObj(line) {
                return line.room.id === obj.id;
            }

            var fL = lines.filter(findByObj);
            var maxL = fL.length > 0 ? d3.max(fL, function (r){return r.line;}) : 0;

            lineCount = (maxL > 0 ? maxL : lineCount + 1);
        });

        var height = lineCount * hk + chart.yOffset + margin;

        var rootEl = d3.select("div#" + idRootElement);
        rootEl.select("svg").remove();

        var svg = rootEl.append("svg")
           .attr("id", "schedule-chart")
           .attr("viewBox", "0 0 " + width + " " + height)
           .attr("width", width)
           .attr("height", height)
           .attr("version", "1.1");

        // длина оси X = ширина контейнера svg - отступ слева и справа
        var xAxisLength = width - chart.xOffset - margin;

        // длина оси Y = высота контейнера svg - отступ сверху и снизу
        var yAxisLength = height - chart.yOffset - margin;

        lines.forEach(function (d) {
            d.d1 = parseDate(d.d1);
            d.d2 = parseDate(d.d2);
            d.d3 = parseDate(d.d3);
            d.d4 = parseDate(d.d4);
            d.optionDate = parseDate(d.optionDate);
        });

        // функция интерполяции линий на ось Y
        var yAxisScale = d3.scaleLinear()
            .domain([0, lineCount * chart.lineK])
            .range([0, yAxisLength]);

        // функция интерполяции времени на ось X
        var timeAxisScale = d3.scaleTime()
            .domain([startDate, endDate])
            .range([0, xAxisLength]);

// Оси координат
    // Оси по Y
        var lineAxis = d3.axisLeft(yAxisScale)
            .ticks(lineCount * 4)
            .tickSize(0)
            .tickPadding(6)
            .tickFormat(function () {
                return null;
            });

    // Оси по X
        //Шкала месяцев для оси X
        var monthsTickmarksAxis = d3.axisTop(timeAxisScale)
            .ticks(d3.timeMonth.every(1))
            .tickFormat("")
            .tickSize(0)
            .tickPadding(6);

        // Названия месяцев для оси X
        var monthsLabelAxis = d3.axisTop(timeAxisScale)
            .ticks(d3.timeDay.every(1))
            .tickSize(0)
            .tickPadding(34)
            .tickFormat(function (d) {
                if (d.getDate() === 15) {
                    return getMonthName(d.getMonth()) + " " + d.getFullYear();
                }
                return null;
            });

        // Шкала дней для оси X
        var daysAxis = d3.axisTop(timeAxisScale)
            .ticks(d3.timeDay.every(1))
            .tickFormat(function (d) {
                if (d.getDate() % 2) {
                    var formatter = d3.timeFormat("%_d");
                    return formatter(d);
                }
                return null;
            })
            .tickSize(8)
            .tickPadding(6);

//Отрисовка координатных осей, названий и сетки
        var coordinatesGroup = svg.append("g")
            .attr("id", "coordinates");

    // Оси по Y
        coordinatesGroup.append("g")
            .classed("axis", true)
            .classed("y-axis", true)
            .attr("transform", "translate(" + chart.xOffset + ", " + chart.yOffset + ")")
            .call(lineAxis);

    // Оси по X
        // https://codepen.io/anon/pen/jvRwee
        coordinatesGroup.append("g")
            .classed("axis", true)
            .classed("x-axis", true)
            .classed("days", true)
            .classed("labeled", true)
            .style("position", "fixed")
            .attr("transform", "translate(" + chart.xOffset + ", " + chart.yOffset + ")")
            .call(daysAxis);

        coordinatesGroup.append("g")
            .classed("axis", true)
            .classed("months", true)
            .attr("transform", "translate(" + chart.xOffset + ", " + chart.yOffset + ")")
            .call(monthsTickmarksAxis);

        coordinatesGroup.append("g")
            .classed("axis", true)
            .classed("months", true)
            .classed("labeled", true)
            .attr("transform", "translate(" + chart.xOffset + ", " + chart.yOffset + ")")
            .call(monthsLabelAxis);

    // Горизонтальные линии сетки
        d3.selectAll("g.y-axis g.tick:first-of-type")
            .append("line")
            .classed("grid-line", true)
            .attr("x1", -(chart.xOffset))
            .attr("y1", 0)
            .attr("x2", 0)
            .attr("y2", 0);

    // Вертикальные линий сетки
        d3.selectAll("g.months g.tick")
            .append("line")
            .classed("grid-line", true)
            .attr("x1", 0)
            .attr("y1", 0)
            .attr("x2", 0)
            .attr("y2", yAxisLength);

// Мероприятия
        var valuesGroup = svg.append("g")
            .attr("id", "values");

        var yMin = 0,
            yMax = 0,
            leftPairArrows = String.fromCharCode(8647),
            rightPairArrows = String.fromCharCode(8649);

        objs.forEach( function (obj) {
            function findByObj(line){
                return line.room.id === obj.id;
            }

            var objectEventGroup =  valuesGroup.append("g")
                .attr("id", obj.id)
                .classed("object-info", true);

            var objectEventData = d3.nest()
                .key(function (d) {
                    return d.line;
                })
                .sortKeys(d3.ascending)
                .entries(lines.filter(findByObj));

            yMax = (objectEventData.length > 0 ? objectEventData.length : 1) * chart.lineK + yMax;

    // Название объекта на оси Y
            objectEventGroup.append("text")
                .classed("objects", true)
                .classed("labeled", true)
                .attr("x", timeAxisScale(startDate) + chart.padding)
                .attr("y", function(){
                    return yAxisScale((yMax + yMin) / 2) + chart.yOffset;
                })
                .call(wrap, obj.name_ru, chart.xOffset - chart.padding, 3, 0);  //todo calc dy

            var eventsGroup = objectEventGroup.append("g")
                .classed("object-events", true);

    // Мероприятие
            objectEventData.forEach(function (lines) {
                lines.values.forEach(function (e) {
                    if (e.d1 < endDate && e.d4 > startDate) {
                        var eventGroup = eventsGroup.append("g")
                            .attr("id", e.uuid)
                            .classed("event-info", true);

        // Даты мероприятия
                        var dateGroup = eventGroup.append("g")
                            .classed("event-dates", true);

                        var y = yAxisScale(chart.lineK *(e.line - 0.75)) + chart.yOffset; // -0.7 = -(0.5 + 0.2)

                        var d1 = e.d1 < startDate ? startDate : e.d1,
                            d4 = e.d4 > endDate ? endDate : e.d4.addDays(1),
                            d2, d3;
                        var x_d1= timeAxisScale(d1) + chart.xOffset;

            // Начало монтажа
                        if (e.d2 >= startDate){
                            dateGroup.append("text")
                                .classed("event-inst-date", true)
                                .attr("x", x_d1)
                                .attr("y", y - chart.padding)
                                .attr("text-anchor", "middle")
                                .attr("font-size", chart.eventFontSize)
                                .text(function () {
                                    if (e.d1 < startDate) return leftPairArrows;
                                    return e.d1.equals(e.d2) ? '' : e.d1.getDate();
                                });
                        }
            // Начало проведения
                        if (e.d3 >= startDate && e.d1 < endDate){
                            d2 = e.d2 < startDate ? startDate : e.d2;

                            dateGroup.append("text")
                                .classed("text.event-start-date", true)
                                .attr("x", function () {
                                    var x = timeAxisScale(e.d2 > endDate ? endDate : d2);
                                    if (e.d2.equals(e.d3)) {
                                        x = (timeAxisScale(e.d2) + timeAxisScale(e.d2.addDays(1))) / 2;
                                    } else {
                                        if (e.d1 > startDate && e.d1.addDays(1).equals(e.d2))
                                            x += chart.padding
                                    }
                                    return x + chart.xOffset;
                                })
                                .attr("y", y - chart.padding)
                                .attr("text-anchor", "middle")
                                .attr("font-size", chart.eventFontSize)
                                .text(function(){
                                    if (e.d2 < startDate) return leftPairArrows;
                                    if (e.d2 > endDate) return rightPairArrows;
                                    return d2.getDate();
                                });
                        }
            // Окончание проведения
                        if (e.d4 >= startDate && e.d2 <= endDate){
                            d3 = e.d3 >= endDate ? endDate : e.d3.addDays(1);

                            dateGroup.append("text")
                                .classed("event-end-date", true)
                                .attr("x", function () {
                                    var x = timeAxisScale(e.d3 < startDate ? startDate : d3);
                                    if (e.d3 < endDate && !e.d2.equals(e.d3) && e.d3.addDays(1).equals(e.d4))
                                        x -= chart.padding;

                                    return x + chart.xOffset;
                                })
                                .attr("y", y - chart.padding)
                                .attr("text-anchor", "middle")
                                .attr("font-size", chart.eventFontSize)
                                .text(function () {
                                    if (e.d3 < startDate) return leftPairArrows;
                                    if (e.d3 > endDate) return rightPairArrows;
                                    return e.d2.equals(e.d3) ? '' : e.d3.getDate();
                                });
                        }
            // Окончание демонтажа
                        if (e.d4 > startDate && e.d3 <= endDate){
                            dateGroup.append("text")
                                .classed("event-deinst-date", true)
                                .attr("x", timeAxisScale(d4) + chart.xOffset)
                                .attr("y", y - chart.padding)
                                .attr("text-anchor", "middle")
                                .attr("font-size", chart.eventFontSize)
                                .text(function () {
                                    if (e.d4 > endDate) return rightPairArrows;
                                    return e.d3.equals(e.d4) ? '' : e.d4.getDate();
                                });
                        }

        // Дни мероприятия
                        var barGroup = eventGroup.append("g")
                            .classed("event-bars", true);

            // Монтаж
                        if (e.d2 > startDate && e.d1 < endDate){
                            d2 = e.d2 >= endDate ? endDate : e.d2;

                            barGroup.append("rect")
                                .classed("bar", true)
                                .classed("inst-bar", true)
                                .attr("x", timeAxisScale(d1) + chart.xOffset)
                                .attr("y", y)
                                .attr("width", timeAxisScale(d2) - timeAxisScale(d1))
                                .attr("height", chart.barHeight)
                                .attr("fill", e.addColor);
                        }
                        if (e.d1 >= startDate) {
                            barGroup.append("line")
                                .classed("black-line", true)
                                .classed("inst-line", true)
                                .attr("x1", x_d1)
                                .attr("y1", y)
                                .attr("x2", x_d1)
                                .attr("y2", y + chart.barHeight);
                        }
            // Проведение
                        if (e.d3 >= startDate && e.d2 < endDate) {
                            d2 = e.d2 < startDate ? startDate : e.d2;

                            barGroup.append("rect")
                                .classed("bar", true)
                                .classed("event-bar", true)
                                .attr("x", timeAxisScale(d2) + chart.xOffset)
                                .attr("y", y)
                                .attr("width", timeAxisScale(d3) - timeAxisScale(d2) + 1)
                                .attr("height", chart.barHeight)
                                .attr("fill", e.mainColor);
                        }
            // Демонтаж
                        if (e.d4 > startDate && e.d3 < endDate){
                            d3 = e.d3 < startDate ? startDate : e.d3.addDays(1);

                            barGroup.append("rect")
                                .classed("bar", true)
                                .classed("deinst-bar", true)
                                .attr("x", timeAxisScale(d3) + chart.xOffset)
                                .attr("y", y)
                                .attr("width", timeAxisScale(d4) - timeAxisScale(d3))
                                .attr("height", chart.barHeight)
                                .attr("fill", e.addColor);
                        }

                        if (e.d4 <= endDate){
                            barGroup.append("line")
                                .classed("black-line", true)
                                .classed("deinst-line", true)
                                .attr("x1", timeAxisScale(d4) + chart.xOffset)
                                .attr("y1", y)
                                .attr("x2", timeAxisScale(d4) + chart.xOffset)
                                .attr("y2", y + chart.barHeight);
                        }

        // Названия мероприятий
                        var titleGroup = eventGroup.append("g")
                            .classed("event-title", true);

                        var eventName = titleGroup.append("text")
                            .classed("event-name", true)
                            .attr("x", (timeAxisScale(d1) + timeAxisScale(d4)) / 2 + chart.xOffset)
                            .attr("y", y + chart.eventFontSize + chart.barHeight)
                            .attr("text-anchor", "middle")
                            .attr("font-size", chart.eventFontSize)
                            .call(wrap, e.project.name_ru, timeAxisScale(d4) - timeAxisScale(d1), 2, 0);

        //Дата опциона
                        eventName.call(
                            wrap,
                            e.optionDate != null ? "Опцион " + formatDate(e.optionDate, true) : '',
                            timeAxisScale(d4) - timeAxisScale(d1) + 3,
                            2,
                            2.0,
                            "red"
                        );
                    }
                });
            });

    //Нижняя линия группы объекта
            objectEventGroup.append("g")
                .classed("axis", true)
                .classed("y-axis", true)
                .append("line")
                .classed("grid-line", true)
                .attr("opacity", 1)
                .attr("x1", 0)
                .attr("y1", yAxisScale(yMax) + chart.yOffset)
                .attr("x2", xAxisLength + chart.xOffset)
                .attr("y2", yAxisScale(yMax) + chart.yOffset);

            yMin = yMax;
        });

    // Коллизии мероприятий
        var collisions = valuesGroup.append("g")
            .attr("id", "collisions");

        function getRy(l1, l2){
            var y1 = chart.lineK * (l1 - 0.75);
            var y2 = chart.lineK * (l2 - 0.75);
            return yAxisScale(Math.abs(y1 - y2))/2;
        }

        collisions.selectAll("ellipse.event-collision")
            .data(lines)
            .enter()
            .filter(function (d) {
                return (d.d1 < endDate && d.d4 > startDate && d.collision)
            })
            .append("ellipse")
            .classed("event-collision", true)
            .attr("cx", function (d) {
                d.collision.d1 = parseDate(d.collision.d1);
                d.collision.d4 = parseDate(d.collision.d4);
                var d1 = d.d1 >= d.collision.d1 ? d.d1 : d.collision.d1;
                var d4 = d.d4 >= d.collision.d4 ? d.collision.d4 : d.d4;
                return (timeAxisScale(d1) + timeAxisScale(d4)) / 2 + chart.xOffset;
            })
            .attr("cy", function (d) {
                var baseLine = d.line > d.collision.line ? d.collision.line : d.line;
                var dy = getRy(d.line, d.collision.line);

                return yAxisScale(chart.lineK * (baseLine - 0.75)) + dy + 4 + chart.yOffset;
            })
            .attr("rx", function () {
                return 10;
            })
            .attr("ry", function (d) {
                return getRy(d.line, d.collision.line) + 6;
            })
            .attr("fill", "none")
            .style("stroke", "firebrick")
            .style("stroke-width", "1px");
    };
};