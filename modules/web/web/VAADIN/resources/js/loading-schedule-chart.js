/**
 * @author Kirill Khoroshilov, 2019
 * Created on 10.02.2019.
 */

var loadingSchedule = loadingSchedule || {};
//todo add rows coll (rooms) as chart param
//  maincolor & addcolor as chart param
// how to move elements:
//     - http://www.petercollingridge.co.uk/tutorials/svg/interactive/dragging/
//     - https://css-tricks.com/creating-a-panning-effect-for-svg/
// how to resize elements
//     - https://bl.ocks.org/Herst/093ff9962405dd564ef58ad8af9544d0
loadingSchedule.Chart = function () {
    var version = "2.1.9";

    this.getVersion = function () {
        return version
    };

    var chart = this;

    var rootElement,
        startDate,
        endDate,
        chartData,
        chartDays,
        chartRows,
        chartCollisions,
        chartValues,
        rowLabelClickCallback,
        editCallback,
        barHeight = 10,
        textFontSize = 10;

    chart.setRootElement = function (element) {
        if (element !== undefined && element.tagName.toLowerCase() === "div" && element.hasAttribute('id')) {
            var idRootElement = element.getAttribute('id');
            rootElement = d3.select("div#" + idRootElement);
        } else {
            console.error();
            throw new Error("rootElement is not 'DIV' or id attr is not specified");
        }
    };

    chart.getStartDate = function () {
        return startDate;
    };

    chart.getEndDate = function () {
        return endDate;
    };

    chart.setChartDays = function (value) {
        chartDays = d3.nest()
            .key(function(d) { return d.date; })
            .rollup(function(values) { return values[0].isWorkDay})
            .map(value);

        var dates = d3.extent(chartDays.keys(), function (el) {
            return new Date(el);
        });

        startDate = new Date(dates[0]);
        endDate = new Date(dates[1]);
    };

    chart.setChartCollisionData = function (value) {
        chartCollisions = value;
    };

    chart.setChartRows = function (value) {
        var rootRows = value.filter(function (el) {
            return el.parentRow === undefined;
        });

        sortBy(rootRows, 'code');

        var i = 0;

        rootRows.forEach(function (row) {
            row.sortOrder = i;
            i++;
            var subRows = row.subRows;

            sortBy(subRows, 'code');

            subRows.forEach(function (row) {
                row.sortOrder = i;
                i++;
            })
        });

        value.sort(function (a, b) {
            return  a.sortOrder - b.sortOrder;
        });

        chartRows = value;
    };

    function sortBy(coll, fieldName) {
        coll.sort(function (a, b) {
            if (fieldName in a && fieldName in b && typeof a[fieldName] === 'string' && typeof b[fieldName] === 'string') {
                if (a[fieldName] > b[fieldName]) {
                    return 1;
                }
                if (a[fieldName] < b[fieldName]) {
                    return -1;
                }
            }
            return 0;
        });
    }

    chart.setBarHeight = function (value) {
        barHeight = value;
    };

    chart.setTextFontSize = function (value) {
        textFontSize = value;
    };

    chart.setRowLabelClickCallback = function (callback) {
        rowLabelClickCallback = callback;
    };

    chart.setOnEditCallback = function (callback) {
        editCallback = callback;
    };

    chart.setChartData = function (value) {
        chartData = value;
        buildRowValues();
    };

    function buildRowValues() {
        var result = d3.map();
        var rowY = 0.5;

        chartRows.forEach(function (row) {
            if (!row.visible) return;

            var parentRow = row.parentRow;
            row.rowY = rowY - 0.5;

            var rowValues = chartData.filter(function (el) {
                return el.row_id === row.id
            });

            if (row.subRows.length) {
                row.level = 1;
            } else {
                row.level = parentRow != null ? parentRow.level + 1 : 1;
            }
            result.set(row.id, getRowLines(rowValues));
        });

        chartValues = result;

        function getRowLines(rows) {
            rows.sort(function (a, b) {
                return a.d1 - b.d1;
            });

            var rowLines = d3.map();

            if (!rows.length) {
                rowLines.set(rowY, []);
                rowY += 1;
            }

            while (rows.length) {
                var forNewLine = [];
                setRowLine(rows, rowY, rowLines, forNewLine);
                rows = forNewLine;
                rowY += 1;
            }
            return rowLines;
        }

        function setRowLine(rows, y, map, forNewLine) {
            var line = [];
            rows.reverse();

            while (rows.length) {
                var item = rows.pop();

                if (line.some(findRowLineIntersection, item)) {
                    forNewLine.push(item);
                } else {
                    line.push(item);
                }
            }
            map.set(y, line);
        }

        function findRowLineIntersection(el, index, array) {
            var dayDiff = "isPhantom" in this && this.isPhantom ? 1 : 2;

            var d4 = moment(el.d4).add(dayDiff, 'd');
            return moment(this.d1).isSame(d4, 'day') || moment(this.d1).isBefore(d4);
        }
    }

    this.clearChart = function () {
        d3.selectAll('svg#schedule-chart')
            .remove();
    };

    //Based on
    // Brush & Zoom https://bl.ocks.org/mbostock/34f08d5e11952a80609169b7917d4172
    // Pan & Zoom Axes https://bl.ocks.org/mbostock/db6b4335bf1662b413e7968910104f0f
    // D3 scroll time scale https://codepen.io/bennekrouf/pen/jqvqNZ
    // D3 Axis Tick Tricks https://codepen.io/anon/pen/jvRwee
    this.refreshChart = function () {
        this.clearChart();

        var margin = {top: 40, right: 20, bottom: 20, left: 110},
            padding = {top: 5, right: 5, bottom: 5, left: 5};

        //ширина svg контейнера
        var width = rootElement.node().parentElement.parentElement.clientWidth;
        //высота svg контейнера
        var height = rootElement.node().parentElement.parentElement.clientHeight;
        var dayWidth = 20;
        var lineHeight = 70; //todo высота шрифта зависит от высоты строки, высота полосок тоже зависит.

        //ширина контейнера для отображения графика
        var visibleWidth = width - padding.left - padding.right;
        //высота контейнера для отображения графика
        var visibleHeight = height - padding.top - padding.bottom;

        var lineCount = d3.nest()
            .rollup(function (leaves) {
                return d3.sum(leaves, function (d) {
                    return d.size();
                })
            })
            .entries(chartValues.values());

        var daysCount = d3.timeDay.count(startDate, endDate) + 1; //moment(endDate).diff(moment(startDate), 'days') + 1;

        // длина оси X
        var xAxisLength = visibleWidth - margin.left - margin.right;
        var visibleDays = Math.floor(xAxisLength / dayWidth) > daysCount ? daysCount : Math.floor(xAxisLength / dayWidth);
        var invisibleDaysWidth = (daysCount - visibleDays) * dayWidth;

        var yAxisLengthMax = visibleHeight - margin.top - margin.bottom;

        var visibleLineCount = Math.floor(yAxisLengthMax / lineHeight) > lineCount ? lineCount : Math.floor(yAxisLengthMax / lineHeight);
        // длина оси Y
        var yAxisLength = visibleLineCount * lineHeight;
        var invisibleLinesHeight = (lineCount - visibleLineCount) * lineHeight;

        //Диапозон дат на видимой части шкалы
        var timeDomainStart = d3.timeHour.offset(startDate, -6);

        var timeDomainEnd = d3.timeHour.offset(startDate, visibleDays * 24 + 6);
        var timeDomainEnd2 = d3.timeHour.offset(endDate,24 + 6);

        // функция интерполяции времени на ось X
        var timeAxisScale = d3.scaleTime()
            .domain([timeDomainStart, timeDomainEnd])
            .range([0, xAxisLength]);

        var timeAxisScaleMax = d3.scaleTime()
            .domain([timeDomainStart, timeDomainEnd2])
            .range([0, xAxisLength]);

        // функция интерполяции линий на ось Y
        var yAxisScale = d3.scaleLinear()
            .domain([0, visibleLineCount])
            .range([0, yAxisLength]);

        var yAxisScaleMax = d3.scaleLinear()
            .domain([0, lineCount])
            .range([0, yAxisLength]);

        var zoom = d3.zoom()
        //степень увеличения
            .scaleExtent([1, 1])
            //ограничение зоны перемещения(прокрутки) по осям
            .translateExtent([[0, 0], [width + (timeAxisScale(d3.timeDay.offset(endDate)) - timeAxisScale(d3.timeMonth.offset(startDate))), height + invisibleLinesHeight]])
            .on("zoom", zoomed);

        var xBrush = d3.brushX()
            .extent([[0, 1], [xAxisLength, 14]])
            .on("brush", brushedX);

        var yBrush = d3.brushY()
            .extent([[1, 0], [14, yAxisLength]])
            .on("brush", brushedY);

//--------------------------------------------------------------------------------------------------------------

// Оси координат
        // Оси по X
        // Шкала для вертикальных линий сетки
        var dayTicksAxis = d3.axisTop(timeAxisScale)
            .ticks(d3.timeDay.every(1))
            .tickSize(-yAxisLength)
            .tickFormat('');

        // Шкала для номеров дней
        var dayLabelsAxis = d3.axisTop(timeAxisScale)
            .ticks(d3.timeHour, 12)
            .tickSize(0)
            .tickFormat(function (d) {
                var hours = d.getHours();
                if (hours === 12) {
                    var formatter = d3.timeFormat("%-d");
                    return formatter(d);
                }
                return null
            });

        // Шкала дней недели
        var weekdayLabelsAxis = d3.axisTop(timeAxisScale)
            .ticks(d3.timeHour, 12)
            .tickPadding(13)
            .tickSize(0)
            .tickFormat(function (d) {
                var hours = d.getHours();
                if (hours === 12) {
                    return moment(d).locale('ru').format('dd')
                }
                return null
            });

        //Шкала для рисок границ месяцев
        var monthTicksAxis = d3.axisTop(timeAxisScale)
            .ticks(d3.timeMonth.every(1))
            .tickSize(margin.top - padding.top * 2)
            .tickFormat('');

        // Названия для названий месяцев
        var monthLabelsAxis = d3.axisTop(timeAxisScale)
            .ticks(d3.timeDay.every(1))
            .tickSize(0)
            .tickPadding(23)
            .tickFormat(function (d) {
                if (d.getDate() === 15) {
                    return moment(d).locale('ru').format('MMMM YYYY')
                }
                return null;
            });

        // Оси по Y
        // Шкала для горизонтальных линий сетки
        var lineTicksAxis = d3.axisLeft(yAxisScale)
            .ticks(visibleLineCount)
            .tickSize(-xAxisLength)
            .tickFormat('');

        // Шкала названий для строк
        var lineLabelsAxis = d3.axisRight(yAxisScale)
            .ticks(visibleLineCount * 2)
            .tickSize(0)
            .tickPadding(-5)
            .tickFormat(function (d) {
                var row = chartRows.find(function (el, index, array) {
                    return el.rowY === d - 0.5;
                });
                return row != null ? row.name : null;
            });

//--------------------------------------------------------------------------------------------------------------

        var svg = rootElement
            .append("svg")
            .attr("id", "schedule-chart")
            .attr('viewBox', '0 0 ' + (width) + ' ' + (height))
            .attr("width", width)
            .attr("height", height)
            .attr("version", "1.1");

        // Define the div for the tooltip
        var div = d3.select("body")
            .append("div")
            .attr("class", "tooltip")
            .style("opacity", 0);

        svg.append("defs")
            .append("clipPath")
            .attr("id", "clip")
            .append("rect")
            .attr("width", xAxisLength + 1)
            .attr("height", yAxisLength + 1);

        var lg1 = svg.append("linearGradient")
            .attr("id", "linear-gradient-1");

        lg1.append("stop")
            .attr("offset", "0%")
            .attr("stop-color", "gold");

        lg1.append("stop")
            .attr("offset", "100%")
            .attr("stop-color", "teal");

        var coordinates = svg.append('g')
            .attr("id", "coordinates");

//Отрисовка координатных осей, названий и сетки

        // Оси по X
        var axisTransformValue = 'translate(' + (margin.left) + ', ' + (margin.top) + ')';

        //Вертикальные линий сетки
        var dayTicks = coordinates.append("g")
            .classed("axis", true)
            .classed("x-axis", true)
            .classed("days", true)
            .classed("grid-line", true)
            .attr('transform', axisTransformValue)
            .call(dayTicksAxis);

        // Метки номеров дней
        var dayLabels = coordinates.append("g")
            .classed("axis", true)
            .classed("x-axis", true)
            .classed("days", true)
            .classed("labeled", true)
            .attr('transform', axisTransformValue)
            .call(dayLabelsAxis);

        // Названия дня недели
        var weekdayLabels = coordinates.append("g")
            .classed("axis", true)
            .classed("x-axis", true)
            //.classed("days", true)
            .classed("weekdays", true)
            .classed("labeled", true)
            .attr('transform', axisTransformValue)
            .call(weekdayLabelsAxis);

        // Риски на границе месяцев
        var monthTicks = coordinates.append("g")
            .classed("axis", true)
            .classed("x-axis", true)
            .classed("months", true)
            .attr('transform', axisTransformValue)
            .call(monthTicksAxis);

        // Названия месяцев
        var monthLabels = coordinates.append("g")
            .classed("axis", true)
            .classed("x-axis", true)
            .classed("months", true)
            .classed("labeled", true)
            .attr('transform', axisTransformValue)
            .call(monthLabelsAxis)
            .call(holidaysRepaint);

        //  Оси по Y
        //Горизонтальные линий сетки
        var lineTicks = coordinates.append("g")
            .classed("axis", true)
            .classed("y-axis", true)
            .classed("grid-line", true)
            .attr('transform', axisTransformValue)
            .call(lineTicksAxis)
            .call(hideRowLine);

        //Название помещений
        var lineLabels = coordinates.append("g")
            .classed("axis", true)
            .classed("y-axis", true)
            .classed("labeled", true)
            .attr('transform', 'translate(' + (padding.left * 2) + ', ' + (margin.top) + ')')
            .call(lineLabelsAxis)
            .call(rowLabelRepaint);

        var g = svg.append('g')
            .attr("id", "values")
            .attr('transform', 'translate(' + (margin.left) + ', ' + (margin.top) + ')');

        g.append("rect")
            .attr('id', 'zoom')
            .attr("width", xAxisLength)
            .attr("height", yAxisLength)
            .call(zoom);

        var valuesGroup = g.append("g")
            .attr("id", "object-values");

        var collisionsGroup = g.append("g")
            .attr("id", "collisions");

        // Рамка вокруг зоны видимости
        svg.append("polyline")
            .attr('id', 'view')
            .attr("points", function () {
                var width = margin.left + xAxisLength;
                var height = margin.top + yAxisLength

                return margin.left - 1 + ',' + margin.top + ' ' + width + ',' + margin.top + ' ' + width + ',' + height + ' ' +
                    margin.left + ',' + height + ' ' + margin.left + ',' + margin.top
            });

// Значения

        //  полосы прокрутки
        // по горизонтали
        if (invisibleDaysWidth) {
            var xContext = svg.append('g')
                .attr('class', 'x-context')
                .attr('transform', 'translate(' + (margin.left - 2) + ',' + (padding.top + margin.top + yAxisLength) + ')');

            xContext.append("g")
                .classed('brush', true)
                .classed('x-brush', true)
                .attr('transform', 'translate(' + 2 + ',' + 0 + ')')
                .call(xBrush)
                .call(xBrush.move, [timeAxisScaleMax(timeDomainStart), timeAxisScaleMax(d3.timeDay.offset(timeDomainEnd, 1))]);

            d3.selectAll('g.x-brush rect.selection')
                .attr('rx', 2)
                .attr('cursor', 'ew-resize');
        }

        // по вертикали
        if (invisibleLinesHeight) {
            var yContext = svg.append('g')
                .attr('class', 'y-context')
                .attr('transform', 'translate(' + (padding.left + margin.left + xAxisLength) + ',' + (margin.top - 2) + ')');

            yContext.append("g")
                .classed('brush', true)
                .classed('y-brush', true)
                .attr('transform', 'translate(' + 0 + ',' + 2 + ')')
                .call(yBrush)
                .call(yBrush.move, [0, yAxisScaleMax(visibleLineCount)]);

            d3.selectAll('g.y-brush rect.selection')
                .attr('rx', 2)
                .attr('cursor', 'ns-resize');
        }

        d3.selectAll('g.brush rect.overlay')
            .attr('pointer-events', 'none');

        d3.selectAll('g.brush rect.handle')
            .remove();

        function refreshValues() {
            d3.selectAll('.object-value')
                .remove();



            chartValues.each(function (rowLines, roowId) {
                rowLines.each(function (value, key) {
                    var y = yAxisScale(key) - padding.top;

                    value.forEach(function (e) {
                        printLine(y, e);
                    })
                });
            });

            repaintCollision();
        }

        function printLine(y, r) {
            var dY = -18,
                leftPairArrows = String.fromCharCode(8647),
                rightPairArrows = String.fromCharCode(8649);

            if (!r.isPhantom && r.d1 <= endDate && r.d4 >= startDate) {
                var objectValueGroup = valuesGroup.append("g")
                    .attr("id", function () {
                        return r.id;
                    })
                    .classed("object-value", true)
                    .on("dblclick", function () {
                        var callback = editCallback;
                        callback(r);
                    });

                // Даты событий
                var dateGroup = objectValueGroup.append("g")
                    .classed("event-dates", true);

                var rd1 = r.d1 < startDate ? startDate : r.d1,
                    rd4 = r.d4 > endDate ? endDate : moment(r.d4).add(1, 'd'),
                    rd2, rd3;
                var x_d1 = timeAxisScale(rd1) + 1;

                // Начало монтажа
                if (r.d2 >= startDate) {
                    dateGroup.append("text")
                        .classed("event-inst-date", true)
                        .attr("x", x_d1)
                        .attr("y", y)
                        .attr("dy", dY)
                        .attr("text-anchor", "middle")
                        .attr("font-size", textFontSize)
                        .text(function () {
                            if (r.d1 < startDate) return leftPairArrows;
                            return moment(r.d1).isSame(r.d2, 'day') ? '' : r.d1.getDate();
                        });
                }

                // Начало проведения
                if (r.d3 >= startDate && r.d1 <= endDate) {
                    rd2 = r.d2 < startDate ? startDate : r.d2;

                    dateGroup.append("text")
                        .classed("text.event-start-date", true)
                        .attr("x", function () {
                            var x = timeAxisScale(r.d2 > endDate ? d3.timeDay.offset(endDate, 1): rd2);
                            if (moment(r.d2).isSame(r.d3, 'day')) {
                                x = (timeAxisScale(r.d2) + timeAxisScale(moment(r.d2).add(1, 'd'))) / 2;
                            } else if (moment(r.d1).isSame(endDate, 'day')){
                                x = timeAxisScale(d3.timeDay.offset(r.d1,1))
                            } else {
                                if (r.d1 > startDate && moment(moment(r.d1).add(1, 'd')).isSame(r.d2, 'day'))
                                    x += 4;
                            }
                            return x;
                        })
                        .attr("y", y)
                        .attr("dy", dY)
                        .attr("text-anchor", "middle")
                        .attr("font-size", textFontSize)
                        .text(function () {
                            if (r.d2 < startDate) return leftPairArrows;
                            if (r.d2 > endDate) return rightPairArrows;
                            return rd2.getDate();
                        });
                }

                // Окончание проведения
                if (r.d4 >= startDate && r.d2 <= endDate) {
                    rd3 = r.d3 >= endDate ? endDate : d3.timeDay.offset(r.d3, 1);

                    dateGroup.append("text")
                        .classed("event-end-date", true)
                        .attr("x", function () {
                            var x = timeAxisScale(r.d3 < startDate ? startDate : rd3);
                            if (r.d3 < endDate && !moment(r.d2).isSame(r.d3, 'day') && moment(moment(r.d3).add(1, 'd')).isSame(r.d4, 'day')){
                                x -= 4;
                            }
                            if (moment(r.d4).isSame(startDate,'day')) {
                                x += 4;
                            }

                            return x ;
                        })
                        .attr("y", y)
                        .attr("dy", dY)
                        .attr("text-anchor", "middle")
                        .attr("font-size", textFontSize)
                        .text(function () {
                            if (r.d3 < startDate) return leftPairArrows;
                            if (r.d3 > endDate) return rightPairArrows;
                            return moment(r.d2).isSame(r.d3, 'day') ? '' : r.d3.getDate();
                        });
                }

                // Окончание демонтажа
                if (r.d4 >= startDate && r.d3 <= endDate) {
                    dateGroup.append("text")
                        .classed("event-deinst-date", true)
                        .attr("x", timeAxisScale(rd4))
                        .attr("y", y)
                        .attr("dy", dY)
                        .attr("text-anchor", "middle")
                        .attr("font-size", textFontSize)
                        .text(function () {
                            if (r.d4 > endDate) return rightPairArrows;
                            return moment(r.d3).isSame(r.d4, 'day') ? '' : r.d4.getDate();
                        });
                }

                // Дни мероприятия
                var barGroup = objectValueGroup.append("g")
                    .classed("event-bars", true);

                dY += 2;

                // Монтаж
                if (r.d2 > startDate && r.d1 <= endDate) {
                    rd2 = d3.timeDay.offset((r.d2 >= endDate ? endDate : r.d2), 1);

                    if (rd2 >= rd1) {
                        barGroup.append("rect")
                            .classed("bar", true)
                            .classed("inst-bar", true)
                            .attr("x", x_d1)
                            .attr("y", y + dY)
                            .attr("width", timeAxisScale(rd2) - timeAxisScale(rd1))
                            .attr("height", barHeight)
                            .attr("fill", r.addColor);
                    }
                }
                if (r.d1 >= startDate && rd2 > rd1) {
                    barGroup.append("line")
                        .classed("black-line", true)
                        .classed("inst-line", true)
                        .attr("x1", x_d1)
                        .attr("y1", y + dY)
                        .attr("x2", x_d1)
                        .attr("y2", y + dY + barHeight);
                }

                // Проведение
                if (r.d3 >= startDate && r.d2 < endDate) {
                    rd2 = r.d2 < startDate ? startDate : r.d2;

                    barGroup.append("rect")
                        .classed("bar", true)
                        .classed("event-bar", true)
                        .attr("x", timeAxisScale(rd2))
                        .attr("y", y + dY)
                        .attr("width", timeAxisScale(rd3) - timeAxisScale(rd2) + 1)
                        .attr("height", barHeight)
                        .attr("fill", r.mainColor);
                }

                // Демонтаж
                if (r.d4 >= startDate && r.d3 < endDate) {
                    rd3 = r.d3 < startDate ? startDate : d3.timeDay.offset(r.d3, 1);

                    if (rd4 > rd3) {
                        barGroup.append("rect")
                            .classed("bar", true)
                            .classed("deinst-bar", true)
                            .attr("x", timeAxisScale(rd3))
                            .attr("y", y + dY)
                            .attr("width", timeAxisScale(rd4) - timeAxisScale(rd3))
                            .attr("height", barHeight)
                            .attr("fill", r.addColor);
                    }
                }

                if (r.d4 <= endDate && rd4 > rd3) {
                    barGroup.append("line")
                        .classed("black-line", true)
                        .classed("deinst-line", true)
                        .attr("x1", timeAxisScale(rd4))
                        .attr("y1", y + dY)
                        .attr("x2", timeAxisScale(rd4))
                        .attr("y2", y + dY + barHeight);
                }

                // Названия мероприятий
                var titleGroup = objectValueGroup.append("g")
                    .classed("event-title", true);

                var eventName = titleGroup.append("text")
                    .classed("event-name", true)
                    .attr("x", (timeAxisScale(rd1) + timeAxisScale(rd4)) / 2)
                    .attr("y", y + dY + textFontSize + barHeight)
                    .attr("text-anchor", "middle")
                    .attr("font-size", textFontSize)
                    .call(wrap, r.event.name_ru, timeAxisScale(rd4) - timeAxisScale(rd1), 2, 0)
                    .on("mouseover", function() {
                        div.transition()
                            .duration(200)
                            .style("opacity", .9);
                        div.html(r.event.name_ru)
                            .style("left", (d3.event.pageX) + "px")
                            .style("top", (d3.event.pageY - 30) + "px");
                    })
                    .on("mouseout", function(d) {
                        div.transition()
                            .duration(500)
                            .style("opacity", 0);
                    });

                //Дата опциона
                eventName.call(
                    wrap,
                    r.optionDate != null ? "Опцион " + moment(r.optionDate).format('DD.MM.YY') : '',
                    timeAxisScale(rd4) - timeAxisScale(rd1) + 3,
                    2,
                    2.0,
                    "red"
                );
            }

            //Фантомы для залов с изменяемой конфигурацией
            var dY = -16;
            if (r.isPhantom) {
                var phantomsGroup = valuesGroup.append("g")
                    .classed("object-value", true)
                    .classed("phantom-bars", true)
                    //todo disable
                    .on("dblclick", function () {
                        var callback = editCallback;
                        callback(r);
                    });

                if (r.d4 > startDate && r.d1 < endDate) {
                    var fsd = r.d1 < startDate ? startDate : r.d1,
                        fed = r.d4 > endDate ? endDate : moment(r.d4).add(1, 'd');

                    phantomsGroup.append("rect")
                        .classed("bar", true)
                        .classed("phantom-bar", true)
                        .attr("x", timeAxisScale(fsd))
                        .attr("y", y + dY)
                        .attr("width", timeAxisScale(fed) - timeAxisScale(fsd))
                        .attr("height", barHeight)
                        .attr("fill", r.mainColor);

                    phantomsGroup.append("text")
                        .classed("event-name", true)
                        .attr("x", (timeAxisScale(fsd) + timeAxisScale(fed)) / 2)
                        .attr("y", y )
                        .attr("dy", dY + 2 + textFontSize + barHeight)
                        .attr("text-anchor", "middle")
                        .attr("font-size", textFontSize);
                    //.text(function () {
                    //    return r.event.name_ru;
                    //})
                }
            }
        }

        // Коллизии мероприятий
        function repaintCollision() {
            d3.selectAll("ellipse.event-collision")
                .remove();

            function getValue(id) {
                return chartData.find(function (el, index, array) {
                    return  Array.isArray(el.entity_id) ? el.entity_id.includes(id) : el.entity_id === id;
                });
            }

            function getRy(v){
                var value = getValue(v.value_id);
                var collision = getValue(v.collision_id);
                var rowLines = chartValues.get(v.row_id);

                var dY = getLine(rowLines, value.entity_id).key;
                var collisionY = getLine(rowLines, collision.entity_id).key;

                return (yAxisScale(dY) + yAxisScale(collisionY))/2
            }

            function getRy2(v){
                var value = getValue(v.value_id);
                var collision = getValue(v.collision_id);
                var rowLines = chartValues.get(v.row_id);

                var dY = getLine(rowLines, value.entity_id).key;
                var collisionY = getLine(rowLines, collision.entity_id).key;

                return Math.abs(yAxisScale(dY) - yAxisScale(collisionY))/2
            }

            function getLine (lines, id) {
                return lines.entries().find(function (el, index, array) {
                    return el.value.find(function(e, i, a){
                        return e.entity_id.includes(id);
                    }) != null;
                });
            }

            collisionsGroup.selectAll("ellipse.event-collision")
                .data(chartCollisions)
                .enter()
                .append("ellipse")
                .attr("id", function (d) {
                    return d.id;
                } )
                .classed("event-collision", true);

            collisionsGroup.selectAll("ellipse.event-collision")
                .data(chartCollisions)
                .filter(function (d) {
                    return chartData.some(function (el, index, array) {
                        return Array.isArray(el.entity_id) ? el.entity_id.includes(d.value_id) : el.entity_id === d.value_id;
                    });
                })
                .attr("cx", function (d) {

                    var value = getValue(d.value_id);
                    var collision = getValue(d.collision_id);

                    var rd1 = value.d1 >= collision.d1 ? value.d1 : collision.d1;
                    var rd4 = value.d4 >= collision.d4 ? collision.d4 : value.d4;

                    rd1 = rd1 < startDate ? startDate : rd1;
                    rd4 = rd4 > endDate ? endDate : moment(rd4).add(1, 'd');

                    return (timeAxisScale(rd1) + timeAxisScale(rd4)) / 2;
                })
                .attr("cy", function (d) {
                    return getRy(d) - 16; //<= dY = -18 +2 (lines 644 & 750) func printLine()
                })
                .attr("rx", function () {
                    return 10;
                })
                .attr("ry", function (d) {
                    return getRy2(d) + barHeight;
                })
                .attr("fill", "none")
                .style("stroke", "firebrick")
                .style("stroke-width", "1px");

            collisionsGroup.selectAll("ellipse.event-collision")
                .data(chartCollisions)
                .exit()
                .remove();
        }

        function brushedX() {
            if (d3.event.sourceEvent && d3.event.sourceEvent.type === "zoom") return; // ignore brush-by-zoom
            var s = d3.event.selection || timeAxisScaleMax.range();
            timeAxisScale.domain(s.map(timeAxisScaleMax.invert, timeAxisScaleMax));

            dayTicks.call(dayTicksAxis);
            dayLabels.call(dayLabelsAxis);
            weekdayLabels.call(weekdayLabelsAxis);
            monthTicks.call(monthTicksAxis);
            monthLabels
                .call(monthLabelsAxis)
                .call(holidaysRepaint);

            svg.select("#zoom")
                .call(zoom.transform, d3.zoomIdentity
                    .scale(1)
                    .translate(-s[0], 0)
                );

            refreshValues();
            //valuesGroup.attr("transform", d3.event.transform);
        }

        function brushedY() {
            if (d3.event.sourceEvent && d3.event.sourceEvent.type === "zoom") return; // ignore brush-by-zoom
            var s = d3.event.selection || yAxisScaleMax.range();
            yAxisScale.domain(s.map(yAxisScaleMax.invert, yAxisScaleMax));

            // reload values focus.select(".area").attr("d", area);

            lineTicks
                .call(lineTicksAxis)
                .call(hideRowLine);
            lineLabels
                .call(lineLabelsAxis)
                .call(rowLabelRepaint);

            svg.select("#zoom")
                .call(zoom.transform, d3.zoomIdentity
                    .scale(1)
                    .translate(-s[0], 0)
                );

            refreshValues();
        }

        function zoomed() {
            if (d3.event.sourceEvent && d3.event.sourceEvent.type === "brush") return; // ignore zoom-by-brush

            dayTicks.call(dayTicksAxis.scale(d3.event.transform.rescaleX(timeAxisScale)));
            dayLabels.call(dayLabelsAxis.scale(d3.event.transform.rescaleX(timeAxisScale)));
            weekdayLabels.call(weekdayLabelsAxis.scale(d3.event.transform.rescaleX(timeAxisScale)));
            monthTicks.call(monthTicksAxis.scale(d3.event.transform.rescaleX(timeAxisScale)));
            monthLabels
                .call(monthLabelsAxis.scale(d3.event.transform.rescaleX(timeAxisScale)))
                .call(holidaysRepaint);

            lineTicks
                .call(lineTicksAxis.scale(d3.event.transform.rescaleY(yAxisScale)))
                .call(hideRowLine);
            lineLabels
                .call(lineLabelsAxis.scale(d3.event.transform.rescaleY(yAxisScale)))
                .call(rowLabelRepaint);

            //valuesGroup.attr("transform", d3.event.transform);

            var t = d3.event.transform;
            var sd = timeAxisScale.invert(Math.abs(t.x));

            xContext
                .select(".x-brush")
                .call(xBrush.move, [timeAxisScaleMax(sd), timeAxisScaleMax(d3.timeMonth.offset(sd))]);
        }

        function holidaysRepaint() {
            // Цвет текста дня на шкале (выходные - красные и рабочие - черные
            d3.selectAll('g.x-axis.days.labeled g.tick text')
                .style('fill', function (d) {
                    //todo get colors from settings
                    return chartDays.get(d3.timeHour.offset(d, -12)) ? 'black' : 'red';
                });

            // Цвет вертикалной полосы для дней (выходные - красные и рабочие - черные
            d3.selectAll('g.x-axis.days.grid-line g.tick rect')
                .remove();

            d3.selectAll('g.x-axis.days.grid-line g.tick')
                .append('rect')
                .attr("width", function (d) {
                    var next = d3.timeDay.offset(d, 0).setHours(24);

                    return timeAxisScale(next) - timeAxisScale(d.setHours(0));
                })
                .attr("height", yAxisLength)
                .style('fill', function (d) {
                    return chartDays.get(d) ? 'white' : 'tomato';
                })
                .style('fill-opacity', 0.1);
        }

        function rowLabelRepaint() {
            var rowLabels = d3.selectAll('g.y-axis.labeled g.tick text');

            rowLabels
                .filter(function (d) {
                    return chartRows.some(function (el, index, array) {
                        return el.rowY === d - 0.5;
                    })
                })
                .attr('dx', function (d) {
                    var row = chartRows.find(function (el, index, array) {
                        return el.rowY === d - 0.5;
                    });
                    return (row.level - 1) * padding.left * 2;
                });

            rowLabels
                .call(wrap, null, margin.left, 3, 0)
                .on("dblclick", function(d){
                    var callback = rowLabelClickCallback;
                    var row = chartRows.find(function (el, index, array) {
                        return el.rowY === d - 0.5;
                    });
                    callback(row.id);
                });
        }

        function hideRowLine() {
            d3.selectAll('g.y-axis.grid-line g.tick line')
                .filter(function (d) {
                    return !chartRows.some(function (el, index, array) {
                        return el.rowY === d;
                    })
                })
                .attr('x2', 0);
        }

        //based on https://bl.ocks.org/mbostock/7555321
        function wrap(text, str, width, maxLines, dy, fill) {
            text.each(function () {
                var textEl = d3.select(this),
                    words = (str != null ? str : textEl.text()).split(/\s+/).reverse(),
                    word,
                    line = [],
                    lineNumber = 0,
                    lineHeight = 1.0, // ems
                    xPos = textEl.attr("x"),
                    yPos = textEl.attr("y");

                if (str == null) {
                    textEl.text(null);
                }

                while (lineNumber < maxLines) {
                    var tspan = textEl
                        .append("tspan")
                        .attr("x", xPos)
                        .attr("y", yPos)
                        .attr("dy", function (){
                            return (lineNumber > 0 ? 1 : 0) + dy + "em"
                        })
                        .style("fill", fill);

                    var addWord = words.length > 0;

                    while (addWord) {
                        if (tspan.node().getComputedTextLength() < width) {
                            if (word = words.pop()) {
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

        function trimmer(tspan, width, margin) {
            var letters = tspan.text().split('');

            while (tspan.node().getComputedTextLength() > margin * 2 + width) {
                letters.pop();
                tspan.text(letters.join("") + "...")
            }
        }
    };
};