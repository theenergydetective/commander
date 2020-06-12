package com.ted.commander.client.widgets.graph;

import com.github.gwtd3.api.D3;
import com.github.gwtd3.api.core.Selection;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.ted.commander.client.widgets.graph.model.PiePoint;

import java.util.List;
import java.util.logging.Logger;

/**
 * Created by pete on 3/13/2015.
 */
public class PieGraph extends Composite {

    static final Logger LOGGER = Logger.getLogger(PieGraph.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);

    static final GraphResource graphCss = GraphResourceBundle.INSTANCE.graphCss();

    @UiField
    SimplePanel canvas;
    @UiField
    VerticalPanel legendPanel;


    public PieGraph(List<PiePoint> piePointList, int canvasWidth, int canvasHeight, boolean drawInnerCircle) {
        initWidget(defaultBinder.createAndBindUi(this));
        graphCss.ensureInjected();


        FlowPanel svgPanel = new FlowPanel();
        canvas.clear();
        svgPanel.getElement().getStyle().setWidth((canvasWidth - 316), Style.Unit.PX);
        svgPanel.getElement().getStyle().setHeight(canvasHeight, Style.Unit.PX);
        canvas.add(svgPanel);
        canvas.getElement().getStyle().setWidth((canvasWidth - 316), Style.Unit.PX);
        canvas.getElement().getStyle().setHeight(canvasHeight, Style.Unit.PX);
        canvas.addStyleName(graphCss.graphBackground());

        int[] m = new int[]{10, 40, 40, 60};
        final int w = (canvasWidth - 316) - m[1] - m[3];
        int drawingAreaHeight = canvasHeight - m[0] - m[2];


        LOGGER.fine("CREATING SVG");
        final Selection svg = D3
                .select(svgPanel)
                .append("svg:svg")
                .attr("class", graphCss.svg())
                .attr("width", w + m[1] + m[3])
                .attr("height", drawingAreaHeight + m[0] + m[2])
                .append("svg:g")
                .attr("transform", "translate(" + (w + m[1] + m[3]) / 2 + "," + (drawingAreaHeight + m[0] + m[2]) / 2 + ")");


        svg.append("svg:clipPath").attr("id", "clip").append("svg:rect").attr("width", w).attr("height", drawingAreaHeight);


        String[] colors = new String[piePointList.size()];
        String[] ids = new String[piePointList.size()];
        String[] names = new String[piePointList.size()];
        String[] innerValueStrings = new String[piePointList.size()];
        Double[] innerValues = new Double[piePointList.size()];
        String[] outerValueStrings = new String[piePointList.size()];
        Double[] outerValues = new Double[piePointList.size()];


        int index = 0;
        for (PiePoint piePoint : piePointList) {
            ids[index] = piePoint.getId();
            names[index] = piePoint.getName();
            colors[index] = piePoint.getColor();
            innerValues[index] = piePoint.getInnerValue();
            outerValues[index] = piePoint.getOuterValue();
            outerValueStrings[index] = piePoint.getOuterString();
            innerValueStrings[index++] = piePoint.getInnerString();


        }

        for (int i = 0; i < names.length; i++) {
            legendPanel.add(new PieGraphLegendRow(names[i], innerValueStrings[i], outerValueStrings[i], colors[i]));
        }


        int outerWidth = w + m[1] + m[3];
        int outerHeight = drawingAreaHeight + m[0] + m[2];
        outerWidth -= 20;
        outerHeight -= 20;

        int innerWidth = (int) (outerHeight * .6);
        int innerHeight = (int) (outerHeight * .6);

        drawEnergyPie(graphCss.tooltip(), ".outer", svg, outerWidth, outerHeight, colors, ids, names, outerValues, outerValueStrings);
        if (drawInnerCircle) {
            drawEnergyPie(graphCss.tooltip(), ".inner", svg, innerWidth, innerHeight, colors, ids, names, innerValues, innerValueStrings);
        }



    }


    /**
     * Pie layout support not done yet in GWT-D3. We have to use JNSI for now.
     *
     * @param svg
     * @return
     */
    public static native void drawEnergyPie(String tooltipStyle, String field, Selection svg, int width, int height, String[] colors, String ids[], String names[], Double values[], String valueStrings[]) /*-{

        var div = $wnd.d3.select("body").append("div")
            .attr("class", tooltipStyle)
            .style("opacity", 0);

        var radius = Math.min(width, height) / 2;

        var color = $wnd.d3.scale.ordinal()
            .range(colors);

        var arc = $wnd.d3.svg.arc()
            .outerRadius(radius - 10)
            .innerRadius(0);

        var arcOver = $wnd.d3.svg.arc()
            .outerRadius(radius + 10);

        var data = new Array();

        for (i = 0; i < colors.length; i++) {
            var nvp = new Object();
            nvp.id = ids[i];
            nvp.name = names[i];
            nvp.value = values[i];
            nvp.valueString = valueStrings[i];
            data.push(nvp);
        }

        var pie = $wnd.d3.layout.pie()
            .sort(null)
            .value(function (d) {
                return d.value;
            });


        var g = svg.selectAll(field)
            .data(pie(data))
            .enter().append("g")
            .attr("class", "arc");

        g.append("path")
            .attr("d", arc)
            .style('stroke', 'white')
            .style('stroke-width', '2px')
            .style("fill", function (d) {
                return color(d.data.id);
            })
            .on("mouseenter", function (d) {
                $wnd.d3.select(this)
                    .attr("stroke", "white")
                    .transition()
                    .duration(250)
                    .attr("d", arcOver)
                    .attr("stroke-width", 6);

                div.text(d.data.name + ": " + d.data.valueString);

                div.transition()
                    .duration(500)
                    .style("opacity", 1);
            })
            .on("mousemove", function (d) {
                div.style("left", ($wnd.d3.event.pageX + 20 ) + "px")
                    .style("top", ($wnd.d3.event.pageY) + "px");

            })
            .on("mouseleave", function (d) {
                $wnd.d3.select(this).transition()
                    .attr("d", arc)
                    .attr("stroke", "none");

                div.transition()
                    .style("opacity", 0);
            });


    }-*/;


    interface DefaultBinder extends UiBinder<Widget, PieGraph> {
    }


}
