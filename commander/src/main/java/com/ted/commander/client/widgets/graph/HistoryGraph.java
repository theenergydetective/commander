package com.ted.commander.client.widgets.graph;

import com.github.gwtd3.api.D3;
import com.github.gwtd3.api.core.Selection;
import com.google.gwt.dom.client.Style;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.ted.commander.client.widgets.graph.model.GraphDataPoint;
import com.ted.commander.client.widgets.graph.model.GraphDataPoints;
import com.ted.commander.common.model.CalendarKey;

import java.util.Date;
import java.util.logging.Logger;


public abstract class HistoryGraph extends Composite {


    static final Logger LOGGER = Logger.getLogger(HistoryGraph.class.getName());

    public static final String NET_COLOR = "#0000FF";
    public static final String LOAD_COLOR = "#FF0000";
    public static final String GEN_COLOR = "#00FF00";
    public static final String WEATHER_COLOR = "#FBC02D";

    static final GraphResource graphCss = GraphResourceBundle.INSTANCE.graphCss();


    @UiField
    SimplePanel canvas;


    protected final int canvasWidth;
    protected final int canvasHeight;

    protected final int drawingAreaHeight;
    protected final int drawingAreaWidth;

    protected static final int leftMargin  = 60;
    protected static final int rightMargin = 60;
    protected static final int topMargin = 10;
    protected static final int bottomMargin = 40;

    protected Selection svg;




    protected HistoryGraph(int canvasWidth, int canvasHeight){
        this.canvasHeight = canvasHeight;
        this.canvasWidth = canvasWidth;
        drawingAreaHeight = canvasHeight - (topMargin + bottomMargin);
        drawingAreaWidth = canvasWidth - (leftMargin + rightMargin);
        LOGGER.fine("Graph Size: w:" + this.canvasWidth + " h:" + canvasHeight + " dw:" + drawingAreaWidth + " dh:" + drawingAreaHeight);
        graphCss.ensureInjected();
    }

    protected void postInit(){
        canvas.getElement().getStyle().setWidth((canvasWidth), Style.Unit.PX);
        canvas.getElement().getStyle().setHeight(canvasHeight, Style.Unit.PX);
        canvas.addStyleName(graphCss.graphBackground());

        LOGGER.fine("CREATING SVG");
        svg = D3
                .select(canvas)
                .append("svg:svg")
                .attr("class", graphCss.svg())
                .attr("width", canvasWidth)
                .attr("height", canvasHeight)
                .append("svg:g")
                //.attr("transform", "translate(0,0)")
                ;
        svg.append("svg:clipPath").attr("id", "clip").append("svg:rect").attr("width", canvasWidth).attr("height", canvasHeight);
    }


    protected void padArray(GraphDataPoints areaDataPoints, long tickInterval){
        GraphDataPoint finalPoint = areaDataPoints.get(areaDataPoints.size() - 1);
        Date nextInterval = new Date((finalPoint.getTimestamp() + tickInterval) * 1000);
        CalendarKey calendarKey = new CalendarKey(nextInterval);
        GraphDataPoint doublePoint = new GraphDataPoint(calendarKey, finalPoint.getValue());
        areaDataPoints.add(doublePoint);
    }








}
