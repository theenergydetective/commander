package com.ted.commander.client.widgets.graph;

import com.github.gwtd3.api.D3;
import com.github.gwtd3.api.arrays.Array;
import com.github.gwtd3.api.core.Value;
import com.github.gwtd3.api.functions.DatumFunction;
import com.github.gwtd3.api.scales.LinearScale;
import com.github.gwtd3.api.svg.Axis;
import com.github.gwtd3.api.svg.Line;
import com.github.gwtd3.api.time.TimeScale;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.ted.commander.client.widgets.graph.datumFunction.LineTimeDatumFunction;
import com.ted.commander.client.widgets.graph.datumFunction.LineValueDatumFunction;
import com.ted.commander.client.widgets.graph.formatter.DateFormatter;
import com.ted.commander.client.widgets.graph.model.CursorTextRow;
import com.ted.commander.client.widgets.graph.model.GraphAxis;
import com.ted.commander.client.widgets.graph.model.GraphDataPoint;
import com.ted.commander.client.widgets.graph.model.GraphDataPoints;
import com.ted.commander.common.model.CalendarKey;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by pete on 3/13/2015.
 */
public class LineGraph extends HistoryGraph {

    static final Logger LOGGER = Logger.getLogger(LineGraph.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);


    TimeScale timeScaleX;

    final List<LinearScale> linearScaleY = new ArrayList<LinearScale>();
    LinearScale areaScaleY;

    Cursor cursor;

    long minEpoch;
    long maxEpoch;
    int maxNumberOfRecord;
    int xDiv;


    public LineGraph(final int canvasWidth, final int canvasHeight, final CalendarKey minTime, final CalendarKey maxTime, final long tickInterval, final long numberTicks, final DateFormatter dateFormatter, final List<GraphAxis> graphAxisList, final GraphAxis areaGraphAxis, final GraphDataPoints areaDataPoints) {
        super(canvasWidth, canvasHeight);
        initWidget(defaultBinder.createAndBindUi(this));
        postInit();
        //Set up X-Axis

        if (tickInterval == 1) {
            //Billing Cycle Handler
            CalendarKey minDate = null;
            CalendarKey maxDate = null;
            for (GraphAxis graphAxis : graphAxisList) {
                for (GraphDataPoints graphDataPoints : graphAxis.getGraphDataPointsList()) {
                    CalendarKey seriesMinDate = graphDataPoints.get(0).getCalendarKey();
                    CalendarKey seriesMaxDate = graphDataPoints.get(graphDataPoints.size() - 1).getCalendarKey();
                    if (minDate == null) {
                        minDate = seriesMinDate;
                        maxDate = seriesMaxDate;
                        continue;
                    }
                    if (seriesMinDate.toLocalEpoch() < minDate.toLocalEpoch()) minDate = seriesMinDate;
                    if (seriesMaxDate.toLocalEpoch() > maxDate.toLocalEpoch()) maxDate = seriesMaxDate;
                }
            }
            int months = minDate.monthDiff(maxDate);
            if (months == 0) months = 1;

            maxDate.addMonth(1);
            minEpoch = minDate.toLocalEpoch();
            maxEpoch = maxDate.toLocalEpoch();
            maxNumberOfRecord = minDate.monthDiff(maxDate);
            xDiv = (int) (maxNumberOfRecord / numberTicks);


        } else {
            //All other types
            minEpoch = minTime.toLocalEpoch();
            maxEpoch = (maxTime.toLocalEpoch() + (86400)); //Add the end of the day
            maxNumberOfRecord = (int) ((maxEpoch - minEpoch) / tickInterval);
            xDiv = (int) (maxNumberOfRecord / numberTicks);
        }

        timeScaleX = D3.time().scale().range(0, drawingAreaWidth);
        timeScaleX.domain(Array.fromInts(0, maxNumberOfRecord));

        List<Integer> xTickValues = new ArrayList<Integer>();
        int xTickValue = 0;
        while (xTickValue < maxNumberOfRecord) {
            xTickValues.add(xTickValue);
            xTickValue += xDiv;
        }

        final Axis xTimeAxis = D3.svg().axis()
                .scale(timeScaleX) //All of these are based on the same time axis
                .tickSize(-(drawingAreaHeight + 8))
                        //.ticks(8)
                .tickValues(xTickValues)
                .tickFormat(new DatumFunction<String>() {
                    @Override
                    public String apply(Element element, Value value, int i) {
                        if (tickInterval > 1) {
                            long epoch = minEpoch + (value.asLong() * tickInterval);
                            if (epoch == minEpoch) return "";
                            return dateFormatter.format(epoch);
                        } else {
                            Date date = new Date(minEpoch * 1000);
                            CalendarUtil.addMonthsToDate(date, value.asInt());
                            long epoch = date.getTime() / 1000;
                            if (epoch == minEpoch) return "";
                            return dateFormatter.format(epoch);
                        }
                    }
                });

        svg.append("svg:g")
                .attr("class", graphCss.x() + " " + graphCss.axis())
                .attr("transform", "translate(" + leftMargin + "," + (drawingAreaHeight + 8) + ")")
                .call(xTimeAxis);

        //Set up the y-axis
        int axisCount = 0;
        for (final GraphAxis graphAxis : graphAxisList) {
            final LinearScale linearScale = D3.scale.linear().range(drawingAreaHeight, 1);
            linearScale.domain(Array.fromDoubles(graphAxis.getMinValue(), graphAxis.getMaxValue()));
            linearScaleY.add(linearScale);

            final Axis yAxis = D3.svg().axis()
                    .scale(linearScale)
                    .orient((axisCount == 0) ? Axis.Orientation.LEFT : Axis.Orientation.RIGHT)
                    .tickSize((axisCount == 0) ? -drawingAreaWidth : 0)
                    .tickFormat(new DatumFunction<String>() {
                        @Override
                        public String apply(Element element, Value value, int i) {
                            return graphAxis.getGraphFormatter().format(value);
                        }
                    });

            svg.append("svg:g")
                    .attr("class", graphCss.y() + " " + graphCss.axis())
                    .attr("transform", "translate(" + leftMargin + ",0)")
                    .call(yAxis);
            axisCount++;
        }


        StringBuilder fillPath = null;
        StringBuilder linePath = null;

        //Do the Area fill
        if (areaDataPoints != null && areaDataPoints.size() > 0) {
            if (areaGraphAxis != null) {
                LOGGER.fine("Drawing Area Axis");
                //Draw the axis
                areaScaleY = D3.scale.linear().range(drawingAreaHeight, 1);
                areaScaleY.domain(Array.fromDoubles(areaGraphAxis.getMinValue(), areaGraphAxis.getMaxValue()));
                final Axis areaAxis = D3.svg().axis()
                        .scale(areaScaleY)
                        .orient(Axis.Orientation.RIGHT)
                        .tickSize(-5)
                        .tickFormat(new DatumFunction<String>() {
                            @Override
                            public String apply(Element element, Value value, int i) {
                                return areaGraphAxis.getGraphFormatter().format(value);
                            }
                        });

                svg.append("svg:g")
                        .attr("class", graphCss.y() + " " + graphCss.axis())
                        .attr("transform", "translate(" + (drawingAreaWidth + leftMargin) + ",0)")
                        .attr("color", areaGraphAxis.getColor())
                        .attr("stroke", areaGraphAxis.getColor())
                        .attr("fill", areaGraphAxis.getColor())
                        .call(areaAxis);
            }

            LOGGER.fine("Drawing Area Dill");
            //Draw the fill
            padArray(areaDataPoints, tickInterval);

            LineTimeDatumFunction lineTimeDatumFunction = new LineTimeDatumFunction(leftMargin, timeScaleX, minEpoch, tickInterval);
            LinearScale fillScale = areaScaleY;
            if (fillScale == null) fillScale = linearScaleY.get(0);
            final Line fill = D3.svg().line()
                    .interpolate(Line.InterpolationMode.STEP_AFTER)
                    .x(lineTimeDatumFunction)
                    .y(new LineValueDatumFunction(fillScale));


            //Add a before and after for the graph data point
            fillPath = new StringBuilder(fill.generate(areaDataPoints.getGraphDataPointsList()));
            linePath = new StringBuilder(fillPath); //Copy the path so we don't have to recalculate it.

            //Close out the path
            fillPath.append("V").append(drawingAreaHeight);
            fillPath.append("H").append(lineTimeDatumFunction.apply(areaDataPoints.get(0), 0));
            fillPath.append("Z");


            svg.append("svg:path").attr("class", graphCss.weatherFill())
                    .attr("fill", areaDataPoints.getColor())
                    .attr("stroke", areaDataPoints.getColor())
                    .attr("clip-path", "url(#clip)")
                    .attr("d", fillPath.toString());

        }


        axisCount = 0;
        for (final GraphAxis graphAxis : graphAxisList) {
            //Draw Lines
            for (GraphDataPoints graphDataPoints : graphAxis.getGraphDataPointsList()) {
                //Double the last entry so the line is drawn
                padArray(graphDataPoints, tickInterval);
                final Line line = D3.svg().line()
                        .interpolate(Line.InterpolationMode.STEP_AFTER)
                        .x(new LineTimeDatumFunction(leftMargin, timeScaleX, minEpoch, tickInterval))
                        .y(new LineValueDatumFunction(linearScaleY.get(axisCount)));

                svg.append("svg:path")
                        .attr("clip-path", "url(#clip)")
                        .attr("color", graphDataPoints.getColor())
                        .attr("stroke", graphDataPoints.getColor())
                        .attr("stroke-width", "2px")
                        .attr("fill", "transparent")
                        .attr("d", line.generate(graphDataPoints.getGraphDataPointsList()));
            }
            axisCount++;
        }

        LOGGER.fine("DRAWING AREA LINE");

        //Do the area line
        if (areaDataPoints != null && linePath != null){
            svg.append("svg:path").attr("class", graphCss.weatherLine())
                    .attr("color", areaDataPoints.getColor())
                    .attr("stroke", areaDataPoints.getColor())
                    .attr("clip-path", "url(#clip)")
                    .attr("d", linePath.toString());
        }


        //Set up Cursor
        cursor = new Cursor(svg, leftMargin, drawingAreaHeight);

        //Set up the hovering rectangle last
        svg.append("rect")
                .attr("width", drawingAreaWidth)
                .attr("height", drawingAreaHeight)
                .attr("transform", "translate(" + leftMargin + ",0)")
                .attr("class", graphCss.overlay())
                .on("mousemove", new DatumFunction<Void>() {
                    @Override
                    public Void apply(Element element, Value value, int i) {

                        double mx = D3.mouseX(svg.node()) - leftMargin;

                        List<CursorTextRow> cursorText = new ArrayList<CursorTextRow>();
                        long epoch = 0;
                        if (tickInterval > 1) {
                            double pixelsPerUnit = (double) drawingAreaWidth / (double) maxNumberOfRecord;
                            pixelsPerUnit = mx / pixelsPerUnit;
                            int offset = (int) pixelsPerUnit * (int) tickInterval;
                            epoch = minEpoch + offset;

                            //Adjust epoch based on interval
                            switch ((int) tickInterval) {
                                case 60: //Minute
                                {
                                    Date minDate = new Date(epoch * 1000);
                                    minDate.setSeconds(0);
                                    epoch = minDate.getTime() / 1000;
                                    break;
                                }
                                case 3600: //Hour
                                {
                                    Date hourDate = new Date(epoch * 1000);
                                    hourDate.setSeconds(0);
                                    hourDate.setMinutes(0);
                                    epoch = hourDate.getTime() / 1000;
                                    break;
                                }
                                default: {
                                    //set hours to 0
                                    Date dayDate = new Date(epoch * 1000);
                                    if (tickInterval == 1) dayDate.setDate(1);
                                    CalendarUtil.resetTime(dayDate);
                                    epoch = dayDate.getTime() / 1000;
                                    break;
                                }
                            }
                        } else {

                            double pixelsPerUnit = (double) drawingAreaWidth / (double) maxNumberOfRecord;
                            pixelsPerUnit = mx / pixelsPerUnit;
                            Date date = new Date(minEpoch * 1000);
                            CalendarUtil.addMonthsToDate(date, (int) pixelsPerUnit);
                            epoch = date.getTime() / 1000;
                        }

                        cursorText.add(new CursorTextRow(dateFormatter.format(epoch), "#000000"));

                        //Look up individual values.
                        for (GraphAxis graphAxis : graphAxisList) {
                            for (GraphDataPoints graphDataPoints : graphAxis.getGraphDataPointsList()) {
                                StringBuilder label = new StringBuilder(graphDataPoints.getDescription()).append(": ");
                                GraphDataPoint graphDataPoint = graphDataPoints.findForTime(epoch);
                                if (graphDataPoint != null) {
                                    label.append(graphAxis.getGraphFormatter().format(graphDataPoint.getValue()));
                                }
                                cursorText.add(new CursorTextRow(label.toString(), graphDataPoints.getColor()));
                            }
                        }

                        if (areaDataPoints != null && areaDataPoints.size() > 0) {
                            StringBuilder areaLabel = new StringBuilder(areaDataPoints.getDescription()).append(": ");
                            GraphDataPoint graphDataPoint = areaDataPoints.findForTime(epoch);
                            if (graphDataPoint != null) {
                                if (areaGraphAxis != null) {
                                    areaLabel.append(areaGraphAxis.getGraphFormatter().format(graphDataPoint.getValue()));
                                } else {
                                    areaLabel.append(graphAxisList.get(0).getGraphFormatter().format(graphDataPoint.getValue()));
                                }
                            }
                            cursorText.add(new CursorTextRow(areaLabel.toString(), areaDataPoints.getColor()));
                        }

                        //Move the cursor
                        if (mx < (drawingAreaWidth / 2)) {
                            //Resize it to the right
                            cursor.updateCursorText(cursorText, (int) mx, false);
                        } else {
                            cursor.updateCursorText(cursorText, (int) mx, true);
                        }

                        return null;
                    }
                });


    }


    interface DefaultBinder extends UiBinder<Widget, LineGraph> {
    }


}
