/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.comparisonGraph;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.petecode.common.client.events.CloseEvent;
import com.petecode.common.client.events.CloseHandler;
import com.ted.commander.client.enums.ComparisonGraphType;
import com.ted.commander.client.places.ComparisonPlace;
import com.ted.commander.client.view.LoadingOverlay;
import com.ted.commander.client.widgets.graph.HistoryGraph;
import com.ted.commander.client.widgets.graph.TotemBarGraph;
import com.ted.commander.client.widgets.graph.formatter.DateFormatter;
import com.ted.commander.client.widgets.graph.formatter.ExportGraphTypeFormatter;
import com.ted.commander.client.widgets.graph.model.GraphAxis;
import com.ted.commander.client.widgets.graph.model.GraphDataPoint;
import com.ted.commander.client.widgets.graph.model.GraphDataPoints;
import com.ted.commander.client.widgets.toolbar.TitleBar;
import com.ted.commander.common.enums.ExportGraphType;
import com.ted.commander.common.enums.HistoryType;
import com.ted.commander.common.model.ComparisonQueryRequest;
import com.ted.commander.common.model.ComparisonQueryResponse;
import com.ted.commander.common.model.VirtualECC;
import com.ted.commander.common.model.history.*;
import com.vaadin.polymer.elemental.Event;
import com.vaadin.polymer.elemental.EventListener;
import com.vaadin.polymer.paper.element.PaperRadioGroupElement;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


public class ComparisonGraphViewImpl extends Composite implements ComparisonGraphView {

    static final Logger LOGGER = Logger.getLogger(ComparisonGraphViewImpl.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);
    Presenter presenter;

    @UiField
    TitleBar titleBar;

    @UiField
    DivElement titleField;

    @UiField
    PaperRadioGroupElement comparisonGraphTypeGroup;
    @UiField
    Element powerButton;
    @UiField
    Element peakVoltageButton;
    @UiField
    Element minVoltageButton;
    @UiField
    Element voltageButton;
    @UiField
    Element energyButton;
    @UiField
    SimplePanel graphPanel;
    @UiField
    Element demandCostField;


    public static final String COLORS[] = {
            "#008000", //green
            "#FFA500", // orange
            "#800080", //purple
            "#808000", //olive
            "#00FF00", //lime
            "#800000", //maroon
            "#00FFFF", //aqua
            "#008080", //team
            "#000080", //navy
            "#FF00FF", //fushua
            "#808080" //gray
    };
    private ComparisonQueryResponse response;

    private String getColor(String key, int index, int count) {
        if (key.equals("NET")) return HistoryGraph.NET_COLOR;
        if (key.equals("GENERATION")) return HistoryGraph.GEN_COLOR;
        if (key.equals("LOAD")) return HistoryGraph.LOAD_COLOR;

        if (index >= COLORS.length) {

            //TODO: replace this w/ a user lookup.
            int val = (index + 1) * (224 / count);
            String hex = Integer.toHexString(val);
            if (hex.length() == 1) hex = "0" + hex;
            return "#00" + hex + hex;
        }

        return COLORS[index];
    }


    ComparisonQueryRequest comparisonQueryRequest;
    List<History>[] historyListArray;
    String currencyCode;

    public ComparisonGraphViewImpl() {
        initWidget(defaultBinder.createAndBindUi(this));
        titleBar.setSelectedPlace(new ComparisonPlace(""));
        titleBar.addCloseHandler(new CloseHandler() {
            @Override
            public void onClose(CloseEvent event) {
                presenter.goTo(new ComparisonPlace(""));
            }
        });

        comparisonGraphTypeGroup.addEventListener("iron-select", new EventListener() {
            @Override
            public void handleEvent(Event event) {
                drawGraph();
            }
        });

    }

    @Override
    public void setLogo(ImageResource imageResource) {
        titleBar.setLogo(imageResource);
    }


    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;

    }

    @Override
    public void setLoadingVisible(boolean visible) {
        if (visible) LoadingOverlay.get().show();
        else LoadingOverlay.get().hide();

    }

    public void setMode(HistoryType mode) {
        demandCostField.getStyle().setDisplay(Style.Display.NONE);
        if (mode.equals(HistoryType.MINUTE)) {
            powerButton.getStyle().clearDisplay();
            energyButton.getStyle().setDisplay(Style.Display.NONE);
            voltageButton.getStyle().clearDisplay();
            peakVoltageButton.getStyle().setDisplay(Style.Display.NONE);
            minVoltageButton.getStyle().setProperty("display", "none");
            comparisonGraphTypeGroup.setSelected("POWER");


        } else {
            powerButton.getStyle().setDisplay(Style.Display.NONE);
            energyButton.getStyle().clearDisplay();
            voltageButton.getStyle().setDisplay(Style.Display.NONE);
            peakVoltageButton.getStyle().clearDisplay();
            minVoltageButton.getStyle().clearDisplay();
            comparisonGraphTypeGroup.setSelected("ENERGY");
            if (mode.equals(HistoryType.BILLING_CYCLE)) demandCostField.getStyle().clearDisplay();
        }
    }

    @Override
    public void setTitleField(String txt) {
        titleField.setInnerText(txt);
    }

    @Override
    public void drawGraph(ComparisonQueryRequest comparisonQueryRequest, ComparisonQueryResponse response, List<History>[] historyListArray, String currencyCode) {
        this.comparisonQueryRequest = comparisonQueryRequest;
        this.historyListArray = historyListArray;
        this.currencyCode = currencyCode;
        this.response = response;

        //Size and place chart here.


        drawGraph();

    }

    public Double getPower(History history) {
        return ((HistoryNetGenLoad) history).getNet();
    }

    public Double getCost(History history) {
        switch (comparisonQueryRequest.getHistoryType()) {
            case BILLING_CYCLE:
                HistoryBillingCycle historyBillingCycle = (HistoryBillingCycle)history;
                return historyBillingCycle.getNetCost() + historyBillingCycle.getFixedCharge();
            default:
                return ((HistoryCost) history).getNetCost();
        }

    }

    public Double getMinVoltage(History history) {
        switch (comparisonQueryRequest.getHistoryType()) {
            case MINUTE:
                return ((HistoryMinute) history).getVoltageTotal() / ((HistoryMinute) history).getMtuCount();
            default:
                return ((HistoryVoltage) history).getMinVoltage();
        }
    }

    public Double getPeakVoltage(History history) {
        switch (comparisonQueryRequest.getHistoryType()) {
            case MINUTE:
                return ((HistoryMinute) history).getVoltageTotal() / ((HistoryMinute) history).getMtuCount();
            default:
                return ((HistoryVoltage) history).getPeakVoltage();
        }

    }

    public Double getDemand(History history) {
        switch (comparisonQueryRequest.getHistoryType()) {
            case MINUTE:
                return ((HistoryMinute) history).getDemandPeak();
            default:
                return ((HistoryDemandPeak) history).getDemandPeak();
        }
    }

    public Double getDemandCost(History history) {
        HistoryBillingCycle historyBillingCycle = (HistoryBillingCycle) history;
        return historyBillingCycle.getDemandCost();
    }

    public Double getPowerFactor(History history) {
        HistoryPowerFactor hpf = (HistoryPowerFactor) history;
        double pf = hpf.getPfTotal() / hpf.getPfSampleCount();
        pf /= 100.0;
        return pf;
    }

    public ExportGraphTypeFormatter getFormatter() {



        switch (comparisonGraphTypeGroup.getSelected()) {
            case "COST": {
                return new ExportGraphTypeFormatter(comparisonQueryRequest.getHistoryType(), ExportGraphType.COST, currencyCode);
            }

            case "MIN_VOLTAGE":
            case "VOLTAGE":
            case "PEAK_VOLTAGE": {
                return new ExportGraphTypeFormatter(comparisonQueryRequest.getHistoryType(), ExportGraphType.VOLTAGE, currencyCode);
            }

            case "DEMAND": {
                return new ExportGraphTypeFormatter(comparisonQueryRequest.getHistoryType(),ExportGraphType.DEMAND, currencyCode);
            }

            case "POWER_FACTOR": {
                return new ExportGraphTypeFormatter(comparisonQueryRequest.getHistoryType(),ExportGraphType.POWER_FACTOR, currencyCode);
            }
            case "DEMAND_COST": {
                return new ExportGraphTypeFormatter(comparisonQueryRequest.getHistoryType(),ExportGraphType.DEMAND_COST, currencyCode);
            }
            default:
                return new ExportGraphTypeFormatter(comparisonQueryRequest.getHistoryType(),ExportGraphType.ENERGY, currencyCode);
        }
    }

    public GraphDataPoint convertToDataPoint(History history) {
        switch (comparisonGraphTypeGroup.getSelected()) {
            case "COST": {
                return new GraphDataPoint(history.getCalendarKey(), getCost(history));
            }
            case "MIN_VOLTAGE": {
                return new GraphDataPoint(history.getCalendarKey(), getMinVoltage(history));
            }

            case "VOLTAGE":
            case "PEAK_VOLTAGE": {
                return new GraphDataPoint(history.getCalendarKey(), getPeakVoltage(history));
            }

            case "DEMAND": {
                return new GraphDataPoint(history.getCalendarKey(), getDemand(history));
            }

            case "POWER_FACTOR": {
                return new GraphDataPoint(history.getCalendarKey(), getPowerFactor(history));
            }

            case "DEMAND_COST": {
                return new GraphDataPoint(history.getCalendarKey(), getDemandCost(history));
            }
            default: {
                return new GraphDataPoint(history.getCalendarKey(), getPower(history));
            }
        }
    }

    public void drawGraph() {
        LOGGER.fine("DRAWING GRAPH: " + comparisonGraphTypeGroup.getSelected());
        ExportGraphTypeFormatter exportGraphTypeFormatter = getFormatter();
        int index = 0;
        List<GraphDataPoints> graphDataPointsList = new ArrayList<GraphDataPoints>();

        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;

        for (List<History> historyList : historyListArray) {
            VirtualECC location = comparisonQueryRequest.getLocationList().get(index++);
            String color = getColor(location.getName(), index, comparisonQueryRequest.getLocationList().size());
            GraphDataPoints graphDataPoints = new GraphDataPoints(color, location.getName(), exportGraphTypeFormatter);
            for (History history : historyList) {
                GraphDataPoint graphDataPoint = convertToDataPoint(history);
                if (graphDataPoint.getValue() > max) max = graphDataPoint.getValue();
                if (graphDataPoint.getValue() < min) min = graphDataPoint.getValue();
                graphDataPoints.add(graphDataPoint);
            }
            graphDataPointsList.add(graphDataPoints);
        }

        if (max == Double.MAX_VALUE) {
            max = 0;
            min = 0;
        }
        if (min > 0) min = 0;
        if (max < 0) max = 0;


        GraphAxis graphAxis = new GraphAxis(min, max, comparisonGraphTypeGroup.getSelected(), "#000", exportGraphTypeFormatter, graphDataPointsList);
        List<GraphAxis> graphAxisList = new ArrayList<GraphAxis>();
        graphAxisList.add(graphAxis);

        int canvasHeight = Window.getClientHeight() - 200;
        int canvasWidth = (int) ((Window.getClientWidth() * .98) - 64);

        graphPanel.getElement().getStyle().setWidth(canvasWidth, Style.Unit.PX);
        graphPanel.getElement().getStyle().setHeight(canvasHeight, Style.Unit.PX);
        graphPanel.clear();

        TotemBarGraph totemBarGraph = new TotemBarGraph(
                canvasWidth,
                canvasHeight,
                response.getStartCalendarKey(),
                response.getEndCalendarKey(),
                getTickInterval(comparisonQueryRequest.getHistoryType()),
                new DateFormatter(comparisonQueryRequest.getHistoryType()),
                graphAxisList);

        graphPanel.add(totemBarGraph);
        totemBarGraph.changeStartDate(comparisonQueryRequest.getStartDate());
        setLoadingVisible(false);
    }


    private int getTickInterval(HistoryType historyType) {
        switch (historyType) {
            case BILLING_CYCLE:
                return 1;
            case DAILY:
                return 86400;
            case HOURLY:
                return 3600;
            default:
                return 60;
        }
    }

    @Override
    public void setGraphDataType(ComparisonGraphType power) {

    }


    //Skin mapping

    interface DefaultBinder extends UiBinder<Widget, ComparisonGraphViewImpl> {
    }


}
