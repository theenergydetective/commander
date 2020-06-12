package com.ted.commander.client.widgets.graph;

import com.github.gwtd3.api.core.Selection;
import com.ted.commander.client.widgets.graph.model.CursorTextRow;

import java.util.List;


public class Cursor {

    final Selection cursorLine;
    final Selection cursorBox;
    final Selection cursorTextContainer;
    final Selection cursorBoxContainer;
    final Selection parent;
    final Selection[] cursorText = new Selection[50];
    final int leftMargin;
    static final GraphResource graphCss = GraphResourceBundle.INSTANCE.graphCss();

    static final int TEXT_HEIGHT = 24;

    public Cursor(Selection parent, int leftMargin, int drawingAreaHeight){
        this.parent = parent;
        this.leftMargin = leftMargin;
        graphCss.ensureInjected();


        cursorLine = parent.append("rect")
                .attr("x", leftMargin)
                .attr("y", -20)
                .attr("width", 1)
                .attr("height", drawingAreaHeight + 20)
                .attr("fill", "#00796B")
                .style("display", "none");

        cursorBoxContainer = parent.append("g");


        cursorBox = cursorBoxContainer.append("rect")
                .attr("x", leftMargin)
                .attr("y", -11)
                .attr("width", 100)
                .attr("height", 100)
                .attr("opacity", .85)
                .attr("rx", 5)         // set the x corner curve radius
                .attr("ry", 5)        // set the y corner curve radius
                .attr("fill", "#E0F2F1")
                .style("display", "none");

        cursorTextContainer = cursorBoxContainer.append("g");

    }


    protected void updateCursorText(List<CursorTextRow> cursorTextRows, long xPos, boolean drawLeft) {
        cursorLine.attr("transform", "translate(" + xPos + "," + 0 + ")");

        cursorTextContainer.selectAll("*").remove();
        cursorTextContainer.html("");


        int textLength = 0;
        int textHeight = TEXT_HEIGHT;




        for (CursorTextRow cursorTextRow : cursorTextRows){
            Selection cursorText = cursorTextContainer.append("text")
                    .attr("class", graphCss.cursorText())
                    .style("stroke", "transparent")
                    .text(cursorTextRow.getLabel())
                    .attr("fill", cursorTextRow.getColor())
                    .attr("transform", "translate(" + (leftMargin + 16) + "," + textHeight + ")");
            textHeight += TEXT_HEIGHT;
            int l = calcLength(cursorText);
            if (l > textLength) textLength = l;
        }

        cursorBox.attr("width", textLength + 32);
        cursorBox.attr("height", textHeight + "px");
        if (drawLeft) {
            cursorBoxContainer.attr("transform", "translate(" + ((xPos - 8) - (textLength + 32)) + "," + 0 + ")");
        } else {
            cursorBoxContainer.attr("transform", "translate(" + (xPos + 8) + "," + 0 + ")");

        }

        if (xPos > 0){
            cursorBox.style("display", "");
            cursorLine.style("display", "");
        }


    }



    public static native int calcLength(Selection s) /*-{
        return s.node().getComputedTextLength();
    }-*/;


    public static native int calcHight(Selection s) /*-{
        return s.node().getBBox().height;
    }-*/;




}
