package com.ted.commander.client.widgets.graph;

import com.github.gwtd3.api.D3;
import com.github.gwtd3.api.arrays.Array;
import com.github.gwtd3.api.core.Selection;
import com.github.gwtd3.api.core.Value;
import com.github.gwtd3.api.functions.DatumFunction;
import com.github.gwtd3.api.scales.LinearScale;
import com.github.gwtd3.api.svg.Axis;
import com.github.gwtd3.api.svg.Line;
import com.github.gwtd3.api.time.TimeScale;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.petecode.common.client.events.ItemSelectedEvent;
import com.petecode.common.client.events.ItemSelectedHandler;
import com.ted.commander.client.widgets.graph.datumFunction.BarTimeDatumFunction;
import com.ted.commander.client.widgets.graph.datumFunction.BarValueDatumFunction;
import com.ted.commander.client.widgets.graph.datumFunction.LineValueDatumFunction;
import com.ted.commander.client.widgets.graph.formatter.DateFormatter;
import com.ted.commander.client.widgets.graph.model.CursorTextRow;
import com.ted.commander.client.widgets.graph.model.GraphAxis;
import com.ted.commander.client.widgets.graph.model.GraphDataPoint;
import com.ted.commander.client.widgets.graph.model.GraphDataPoints;
import com.ted.commander.common.model.CalendarKey;
import com.vaadin.polymer.elemental.Event;
import com.vaadin.polymer.elemental.EventListener;
import com.vaadin.polymer.paper.element.PaperSliderElement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by pete on 3/13/2015.
 */
public class BarGraph extends HistoryGraph {

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
    final GraphAxis areaGraphAxis;
    final GraphDataPoints areaDataPoints;

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
    Selection areaFill;
    Selection areaLine;


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

    public BarGraph(final int canvasWidth, final int canvasHeight, final CalendarKey minTime, final CalendarKey maxTime, final int tickInterval, final DateFormatter dateFormatter, final List<GraphAxis> graphAxisList, final GraphAxis areaGraphAxis, final GraphDataPoints areaDataPoints) {
        super(canvasWidth, canvasHeight - SLIDER_HEIGHT);
        initWidget(defaultBinder.createAndBindUi(this));
        postInit();




        this.tickInterval = tickInterval;

        this.dateFormatter = dateFormatter;
        this.graphAxisList = graphAxisList;
        this.areaGraphAxis = areaGraphAxis;
        this.areaDataPoints = areaDataPoints;
        this.minTime = minTime;
        this.maxTime = maxTime;


        //Calculate the number of bars we have.
        for (GraphAxis graphAxis : graphAxisList) {
            barCount += graphAxis.getGraphDataPointsList().size();

//            for (GraphDataPoints graphDataPoints : graphAxis.getGraphDataPointsList()){
//                padArray(graphDataPoints, tickInterval);
//            }
        }



        LOGGER.info("Using " + barCount + " bars per date entry");


        //Calculate the number of units we can draw ont he screen.

        int totalNumberOfRecords = (int)Math.ceil(((double)maxTime.toLocalEpoch() - (double)minTime.toLocalEpoch()) / (double)tickInterval);
        if (tickInterval == 1){
            totalNumberOfRecords = maxTime.monthDiff(minTime);
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
        drawYAreaAxis();

        xAxis = svg.append("svg:g").attr("id", "x-axis");
        areaFill = svg.append("svg:g").attr("id", "areaFill");
        graph = svg.append("svg:g").attr("id", "graph");
        areaLine = svg.append("svg:g").attr("id", "areaLine");
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
        long maxTimeEndEpoch = maxTime.toLocalEpoch();

        if (endEpoch > maxTimeEndEpoch) {
            endEpoch = maxTimeEndEpoch;
            startEpoch = endEpoch - (maxNumberOfRecords * tickInterval);
        }


        changeStartDate(new CalendarKey(new Date((startEpoch + tickInterval) * 1000)), true);
    }

    public void changeStartDate(CalendarKey currentStartDate){
        changeStartDate(currentStartDate, false);
    }

    public void changeStartDate(CalendarKey currentStartDate, boolean fireEvent){

        this.currentStartDate = currentStartDate;
        if (fireEvent) {
            handlerManager.fireEvent(new ItemSelectedEvent<CalendarKey>(currentStartDate));
        }
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

    private void redrawAreaFill(){
        areaFill.html("");
        areaFill.selectAll("*").remove();
        areaLine.html("");
        areaLine.selectAll("*").remove();

        StringBuilder fillPath;
        StringBuilder linePath = null;

        if (areaDataPoints != null && areaDataPoints.size() > 0) {

            LOGGER.fine("SETTING UP AREA FILL");
            //Draw the fill
            BarTimeDatumFunction lineTimeDatumFunction = new BarTimeDatumFunction(leftMargin, timeScaleX, 0, currentStartEpoch, tickInterval);

            LinearScale fillScale = areaScaleY;
            if (fillScale == null) fillScale = linearScaleY.get(0);
            final Line fill = D3.svg().line()
                    .interpolate(Line.InterpolationMode.STEP_AFTER)
                    .x(lineTimeDatumFunction)
                    .y(new LineValueDatumFunction(fillScale));

            //Filter out the dates
            List<GraphDataPoint> areaPoints = new ArrayList<>();

            for (GraphDataPoint graphDataPoint : areaDataPoints) {
                long gdpEpoch = graphDataPoint.getCalendarKey().toLocalEpoch();
                if (isInRange(gdpEpoch)){
                    areaPoints.add(graphDataPoint);
                }
            }

            if (areaPoints.size() == 0) return;
            GraphDataPoint finalPoint = areaPoints.get(areaPoints.size() - 1);
            Date nextInterval = new Date((finalPoint.getCalendarKey().toLocalEpoch() + tickInterval) * 1000);
            CalendarKey calendarKey = new CalendarKey(nextInterval);
            GraphDataPoint doublePoint = new GraphDataPoint(calendarKey, finalPoint.getValue());
            if (isInRange(doublePoint.getCalendarKey().toLocalEpoch())) {
                areaPoints.add(doublePoint);
            }


            //Add a before and after for the graph data point
            fillPath = new StringBuilder(fill.generate(areaPoints));
            linePath = new StringBuilder(fillPath.toString()); //Copy the path so we don't have to recalculate it.

            //Close out the path
            fillPath.append("V").append(drawingAreaHeight);
            fillPath.append("H").append(lineTimeDatumFunction.apply(areaPoints.get(0), 0));
            fillPath.append("Z");


            areaFill.append("svg:path").attr("class", graphCss.weatherFill())
                    .attr("fill", areaDataPoints.getColor())
                    .attr("stroke", areaDataPoints.getColor())
                    .attr("clip-path", "url(#clip)")
                    .attr("d", fillPath.toString());


            areaLine.append("svg:path").attr("class", graphCss.weatherLine())
                    .attr("color", areaDataPoints.getColor())
                    .attr("stroke", areaDataPoints.getColor())
                    .attr("clip-path", "url(#clip)")
                    .attr("d", linePath.toString());

        }
    }

    private boolean isInRange(long gdpEpoch){
        return (gdpEpoch >= currentStartEpoch && gdpEpoch <= currentEndEpoch);
    }

    private void redrawGraph(){

        redrawXAxis();
        redrawAreaFill();
        redrawBars();

        if (lastCursor >= 0) {
            updateCursor(lastCursor);
        }

    }

    private void redrawBars(){

        graph.selectAll("*").remove();
        graph.html("");


        LOGGER.fine("DRAWING Y BARS");
        //Draw Y Graph
        //Set up the y-axis
        int axisCount = 0;
        for (final GraphAxis graphAxis : graphAxisList) {

            //Draw Lines

            int offset = 0;
            BarValueDatumFunction barValueDatumFunction = new BarValueDatumFunction(linearScaleY.get(axisCount));


            for (GraphDataPoints graphDataPoints : graphAxis.getGraphDataPointsList()) {
                //Double the last entry so the line is drawn
                //LOGGER.fine("DRAWING Y BARS:" + graphDataPoints);
                int index = 0;
                double zeroLine = barValueDatumFunction.apply(0.0, index);
                BarTimeDatumFunction barTimeDatumFunction = new BarTimeDatumFunction(leftMargin, timeScaleX, offset, currentStartEpoch, tickInterval);



                for (GraphDataPoint graphDataPoint: graphDataPoints){
                    //Only draw the range we care about.
                    long gdpEpoch = graphDataPoint.getCalendarKey().toLocalEpoch();
                    if (isInRange(gdpEpoch)) {
                        double barX = barTimeDatumFunction.apply(graphDataPoint, index);
                        if (barX >= (leftMargin +drawingAreaWidth)) continue;
                        double barY = barValueDatumFunction.apply(graphDataPoint, index++);



                        boolean isNegative = graphDataPoint.getValue() < 0;
                        if (isNegative){
                            graph.append("rect")
                                    .attr("x", barX)
                                    .attr("y", zeroLine)
                                    .attr("width", barWidth)
                                    .attr("height", barY-zeroLine)
                                    .attr("opacity", 1)
                                    .attr("fill", graphDataPoints.getColor());


                        } else {
                            graph.append("rect")
                                    .attr("x", barX)
                                    .attr("y", barY)
                                    .attr("width", barWidth)
                                    .attr("height", zeroLine-barY)
                                    .attr("opacity", 1)
                                    .attr("fill", graphDataPoints.getColor());
                        }
                    }



                }

                offset += barWidth;

            }

            axisCount++;
        }

    }


    interface DefaultBinder extends UiBinder<Widget, BarGraph> {
    }

    final HandlerManager handlerManager = new HandlerManager(this);
    public void addStartDateChangedHandler(ItemSelectedHandler<CalendarKey> handler ){
        handlerManager.addHandler(ItemSelectedEvent.TYPE, handler);
    }

    private void drawYAreaAxis(){
        if (areaDataPoints != null && areaDataPoints.size() > 0) {
            if (areaGraphAxis != null) {
                LOGGER.fine("SETTING UP AREA SCALE");
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
                        .attr("fill", areaGraphAxis.getColor())
                        .attr("stroke", "transparent")
                        .call(areaAxis);
            }
        }
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
            double pixelsPerUnit = (double) drawingAreaWidth / (double) (maxNumberOfRecords);
            pixelsPerUnit = mx / pixelsPerUnit;
            Date date = new Date(currentStartEpoch * 1000);
            date.setDate(1);
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

    }


}
