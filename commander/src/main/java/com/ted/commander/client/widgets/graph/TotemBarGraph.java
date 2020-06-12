package com.ted.commander.client.widgets.graph;

import com.github.gwtd3.api.D3;
import com.github.gwtd3.api.arrays.Array;
import com.github.gwtd3.api.core.Selection;
import com.github.gwtd3.api.core.Value;
import com.github.gwtd3.api.functions.DatumFunction;
import com.github.gwtd3.api.scales.LinearScale;
import com.github.gwtd3.api.svg.Axis;
import com.github.gwtd3.api.time.TimeScale;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.ted.commander.client.widgets.graph.datumFunction.BarTimeDatumFunction;
import com.ted.commander.client.widgets.graph.datumFunction.BarValueDatumFunction;
import com.ted.commander.client.widgets.graph.formatter.DateFormatter;
import com.ted.commander.client.widgets.graph.model.CursorTextRow;
import com.ted.commander.client.widgets.graph.model.GraphAxis;
import com.ted.commander.client.widgets.graph.model.GraphDataPoint;
import com.ted.commander.client.widgets.graph.model.GraphDataPoints;
import com.ted.commander.common.model.CalendarKey;
import com.vaadin.polymer.elemental.Event;
import com.vaadin.polymer.elemental.EventListener;
import com.vaadin.polymer.paper.element.PaperSliderElement;

import java.util.*;
import java.util.logging.Logger;

/**
 * Created by pete on 3/13/2015.
 */
public class TotemBarGraph extends HistoryGraph {

    static final Logger LOGGER = Logger.getLogger(LineGraph.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);


    @UiField
    PaperSliderElement dateSlider;
    @UiField
    DivElement sliderDiv;

    int barCount = 0;

    final int tickInterval;

    final DateFormatter dateFormatter;
    final List<GraphAxis> graphAxisList;


    final CalendarKey minTime;
    final CalendarKey maxTime;




    int xDiv;
    TimeScale timeScaleX;
    final List<LinearScale> linearScaleY = new ArrayList<LinearScale>();
    LinearScale areaScaleY;

    CalendarKey currentStartDate;
    CalendarKey currentEndDate;

    Selection xAxis;
    Selection graph;


    long currentStartEpoch;
    long currentEndEpoch;

    int barWidth;
    int maxNumberOfRecords;

    final List<Integer> xTickValues = new ArrayList<Integer>();

    private static final int SLIDER_HEIGHT = 60;
    private Cursor cursor;

    Timer sliderTimer = new Timer() {
        @Override
        public void run() {
            changeStartDate(dateSlider.getImmediateValue());

        }
    };

    public TotemBarGraph(final int canvasWidth, final int canvasHeight, final CalendarKey minTime, final CalendarKey maxTime, final int tickInterval, final DateFormatter dateFormatter, final List<GraphAxis> graphAxisList) {
        super(canvasWidth, canvasHeight - SLIDER_HEIGHT);
        initWidget(defaultBinder.createAndBindUi(this));
        postInit();

        this.tickInterval = tickInterval;

        this.dateFormatter = dateFormatter;
        this.graphAxisList = graphAxisList;
        this.minTime = minTime;
        this.maxTime = maxTime;

        barCount = 1;

        //Calculate the number of units we can draw ont he screen.
        int totalNumberOfRecords = (int)Math.ceil(((double)maxTime.toLocalEpoch() - (double)minTime.toLocalEpoch()) / (double)tickInterval);
        if (tickInterval == 1){
            totalNumberOfRecords = maxTime.monthDiff(minTime) - 1;
        }


        //Calculate the optimal bar width. The smallest width is 10 pixes.
        double bw = Math.floor(drawingAreaWidth / ((totalNumberOfRecords * (barCount + 1))));
        if (bw < 10) bw = 10;
        barWidth = (int)bw;

        int entryWidth = (barCount * barWidth) + barWidth;
        maxNumberOfRecords = drawingAreaWidth / entryWidth;
        if (maxNumberOfRecords < 1) maxNumberOfRecords = 1;


        double pages = Math.ceil((double)totalNumberOfRecords / (double)maxNumberOfRecords);
        LOGGER.info("We can draw " + maxNumberOfRecords + " entries per page. There are " + pages + " pages. The total record count is " + totalNumberOfRecords);

        if (pages > 0) {
            dateSlider.setMin(1.0);
            dateSlider.setMax(totalNumberOfRecords - maxNumberOfRecords);
            dateSlider.setExpand(true);
            dateSlider.setImmediateValue(0);
            //dateSlider.setPin(true);

            dateSlider.addEventListener("immediate-value-change", new EventListener() {
                @Override
                public void handleEvent(Event event) {
                    sliderTimer.cancel();
                    sliderTimer.schedule(250);
                }
            });

            dateSlider.addEventListener("value-change", new EventListener() {
                @Override
                public void handleEvent(Event event) {
                    sliderTimer.cancel();
                    sliderTimer.schedule(250);
                }
            });

        }

        //Hide the slider if we only have one page
        if (pages <= 1) sliderDiv.getStyle().setDisplay(Style.Display.NONE);
        else sliderDiv.getStyle().clearDisplay();


        //Set up static portions of the graph
        drawYAxis();


        xAxis = svg.append("svg:g").attr("id", "x-axis");
        graph = svg.append("svg:g").attr("id", "graph");

        //Set up Cursor
        setupCursor();


        timeScaleX = D3.time().scale().range(0, drawingAreaWidth);
        timeScaleX.domain(Array.fromInts(0, maxNumberOfRecords));



        int numberTicks = 8;
        if (numberTicks > maxNumberOfRecords) numberTicks = maxNumberOfRecords;
        if (numberTicks == 0) numberTicks = 1;
        xDiv = (int) (maxNumberOfRecords / numberTicks);

        int xTickValue = 0;
        while (xTickValue < maxNumberOfRecords){
            xTickValues.add(xTickValue);
            xTickValue += xDiv;
        }

        LOGGER.fine("INIT COMPLETE: " +  numberTicks);

    }

    private void changeStartDate(Double val){
        long startEpoch = minTime.toLocalEpoch() + (long) ((val-1) * tickInterval);
        long endEpoch = startEpoch + (maxNumberOfRecords * tickInterval);

        //Max sure we don't go past the end.
        if (endEpoch > maxTime.toLocalEpoch()) {
            endEpoch = maxTime.toLocalEpoch();
            startEpoch = endEpoch - (maxNumberOfRecords * tickInterval);
        }


        changeStartDate(new CalendarKey(new Date((startEpoch + tickInterval) * 1000)), true);
    }

    public void changeStartDate(CalendarKey currentStartDate){
        changeStartDate(currentStartDate, false);
    }

    public void changeStartDate(CalendarKey currentStartDate, boolean fireEvent){

        this.currentStartDate = currentStartDate;
        currentStartEpoch = currentStartDate.toLocalEpoch();

        if (tickInterval == 1){
            currentEndDate = currentStartDate.addMonth(maxNumberOfRecords);
            currentEndEpoch = currentEndDate.toLocalEpoch();
        } else {
            currentEndEpoch = currentStartEpoch + (maxNumberOfRecords * tickInterval);
            currentEndDate = new CalendarKey(new Date(currentEndEpoch * 1000));
        }

        LOGGER.fine("Date range being drawn is " + currentStartDate + " -to- " + currentEndDate);
        redrawGraph();
    }


    private void redrawXAxis(){
        LOGGER.fine("SETTING UP X SCALE: " + currentStartDate);
        final Axis xTimeAxis = D3.svg().axis()
                .scale(timeScaleX) //All of these are based on the same time axis
                .tickSize(-(drawingAreaHeight + 8))
                        //.ticks(8)
                .tickValues(xTickValues)
                .tickFormat(new DatumFunction<String>() {
                    @Override
                    public String apply(Element element, Value value, int i) {
                        if (tickInterval > 1) {
                            long epoch = currentStartEpoch + (value.asLong() * tickInterval);
                            if (epoch == currentStartEpoch) return "";
                            return dateFormatter.format(epoch);
                        } else {
                            Date date = new Date(currentStartEpoch * 1000);
                            CalendarUtil.addMonthsToDate(date, value.asInt());
                            long epoch = date.getTime() /1000;
                            if (epoch == currentStartEpoch) return "";
                            return dateFormatter.format(epoch);
                        }
                    }
                });

        xAxis.html("");
        xAxis.selectAll("*").remove();
        xAxis.append("svg:g")
                .attr("class", graphCss.x() + " " + graphCss.axis())
                .attr("transform", "translate(" + leftMargin + "," + (drawingAreaHeight + 8) + ")")
                .call(xTimeAxis);
    }



    private boolean isInRange(long gdpEpoch){
        return (gdpEpoch >= currentStartEpoch && gdpEpoch <= currentEndEpoch);
    }

    private void redrawGraph(){

        redrawXAxis();
        redrawBars();

        if (lastCursor >= 0) {
            updateCursor(lastCursor);
        }

    }

    class TotemGraphDatapoint extends GraphDataPoint {
        final String color;

        public TotemGraphDatapoint(GraphDataPoint graphDataPoint, String color){
            this(graphDataPoint.getCalendarKey(), graphDataPoint.getValue(), color);
        }

        public TotemGraphDatapoint(CalendarKey calendarKey, Double value, String color) {
            super(calendarKey, value);
            this.color = color;
        }

        public String getColor() {
            return color;
        }
    }


    public class TotemComparator implements Comparator<TotemGraphDatapoint> {
        @Override
        public int compare(TotemGraphDatapoint o1, TotemGraphDatapoint o2) {
            return o2.getValue().compareTo(o1.getValue());
        }
    }

    private void redrawBars() {

        graph.selectAll("*").remove();
        graph.html("");


        LOGGER.fine("Sorting Totem Bars");


        HashMap<Integer, List<TotemGraphDatapoint>> pointMap = new HashMap<Integer, List<TotemGraphDatapoint>>();


        int axisCount = 0;
        BarValueDatumFunction barValueDatumFunction = new BarValueDatumFunction(linearScaleY.get(axisCount));

        for (final GraphAxis graphAxis : graphAxisList) {
            //Draw Lines


            for (GraphDataPoints graphDataPoints : graphAxis.getGraphDataPointsList()) {
                //Double the last entry so the line is drawn
                int index = 0;

                BarTimeDatumFunction barTimeDatumFunction = new BarTimeDatumFunction(leftMargin, timeScaleX, 0, currentStartEpoch, tickInterval);
                for (GraphDataPoint graphDataPoint : graphDataPoints) {
                    //Only draw the range we care about.
                    long gdpEpoch = graphDataPoint.getCalendarKey().toLocalEpoch();
                    if (isInRange(gdpEpoch)) {
                        double barX = barTimeDatumFunction.apply(graphDataPoint, index++);

                        List<TotemGraphDatapoint> totemGraphDatapointList = pointMap.get((int) barX);
                        if (totemGraphDatapointList == null) {
                            totemGraphDatapointList = new ArrayList<TotemGraphDatapoint>();
                            pointMap.put((int) barX, totemGraphDatapointList);
                        }
                        totemGraphDatapointList.add(new TotemGraphDatapoint(graphDataPoint, graphDataPoints.getColor()));
                    }
                }
            }
            axisCount++;
        }

        List<Integer> epochTimes = new ArrayList<Integer>();
        epochTimes.addAll(pointMap.keySet());
        Collections.sort(epochTimes, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1.compareTo(o2);
            }
        });


        TotemComparator totemComparator = new TotemComparator();
        double zeroLine = barValueDatumFunction.apply(0.0, 0);
        for (Integer epochTime : epochTimes) {
            if (epochTime >= (leftMargin + drawingAreaWidth)) continue;
            List<TotemGraphDatapoint> totemGraphDatapointList = pointMap.get(epochTime);
            Collections.sort(totemGraphDatapointList, totemComparator);
            for (TotemGraphDatapoint totemGraphDatapoint : totemGraphDatapointList) {
                double barX = epochTime;
                double barY = barValueDatumFunction.apply(totemGraphDatapoint.getValue(), epochTime);
                boolean isNegative = totemGraphDatapoint.getValue() < 0;
                if (isNegative) {
                    graph.append("rect")
                            .attr("x", barX)
                            .attr("y", zeroLine)
                            .attr("width", barWidth)
                            .attr("height", barY - zeroLine)
                            .attr("opacity", 1)
                            .attr("fill", totemGraphDatapoint.getColor());
                } else {
                    graph.append("rect")
                            .attr("x", barX)
                            .attr("y", barY)
                            .attr("width", barWidth)
                            .attr("height", zeroLine - barY)
                            .attr("opacity", 1)
                            .attr("fill", totemGraphDatapoint.getColor());
                }
            }
        }
    }



    interface DefaultBinder extends UiBinder<Widget, TotemBarGraph> {
    }


    private void drawYAxis(){
        LOGGER.fine("Setting up Y Axis");
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
        }

    }


    private void setupCursor(){
        cursor = new Cursor(svg, leftMargin, drawingAreaHeight);

        svg.append("rect")
                .attr("width", drawingAreaWidth)
                .attr("height", drawingAreaHeight)
                .attr("transform", "translate(" + leftMargin + ",0)")
                .attr("class", graphCss.overlay())
                .on("mousemove", new DatumFunction<Void>() {
                    @Override
                    public Void apply(Element element, Value value, int i) {
                        double mx = D3.mouseX(svg.node()) - leftMargin;
                        updateCursor(mx);
                        return null;
                    }
                });
    }

    double lastCursor = -1;

    private void updateCursor(double mx){
        lastCursor = mx;

        List<CursorTextRow> cursorText = new ArrayList<CursorTextRow>();
        long epoch = 0;
        if (tickInterval > 1) {
            double pixelsPerUnit = (double) drawingAreaWidth / (double) maxNumberOfRecords;
            pixelsPerUnit = mx / pixelsPerUnit;
            int offset = (int) pixelsPerUnit * (int) tickInterval;
            epoch = currentStartEpoch + offset;

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
            double pixelsPerUnit = (double) drawingAreaWidth / (double) maxNumberOfRecords;
            pixelsPerUnit = mx / pixelsPerUnit;
            Date date = new Date(currentStartEpoch * 1000);
            CalendarUtil.addMonthsToDate(date, (int) pixelsPerUnit);
            epoch = date.getTime() / 1000;
        }

        cursorText.add(new CursorTextRow(dateFormatter.format(epoch), "#000000"));

        //Look up individual values.
        for (GraphAxis graphAxis : graphAxisList) {
            for (GraphDataPoints graphDataPoints : graphAxis.getGraphDataPointsList()) {
                StringBuilder label = new StringBuilder(graphDataPoints.getDescription()).append(": ");
                GraphDataPoint graphDataPoint = graphDataPoints.findForTime(epoch, true);
                if (graphDataPoint != null) {
                    label.append(graphAxis.getGraphFormatter().format(graphDataPoint.getValue()));
                    cursorText.add(new CursorTextRow(label.toString(), graphDataPoints.getColor()));
                }

            }
        }

        //Move the cursor
        if (mx < (drawingAreaWidth / 2)) {
            //Resize it to the right
            cursor.updateCursorText(cursorText, (int) mx, false);
        } else {
            cursor.updateCursorText(cursorText, (int) mx, true);
        }

    }


}
