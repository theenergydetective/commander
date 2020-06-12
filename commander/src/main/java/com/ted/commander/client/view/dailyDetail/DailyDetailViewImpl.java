/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.dailyDetail;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.petecode.common.client.events.CloseEvent;
import com.petecode.common.client.events.CloseHandler;
import com.petecode.common.client.widget.paper.PaperStyleManager;
import com.ted.commander.client.enums.CommanderFormats;
import com.ted.commander.client.places.DashboardPlace;
import com.ted.commander.client.resources.WebStringResource;
import com.ted.commander.client.view.LoadingOverlay;
import com.ted.commander.client.widgets.graph.BarGraph;
import com.ted.commander.client.widgets.graph.HistoryGraph;
import com.ted.commander.client.widgets.graph.LineGraph;
import com.ted.commander.client.widgets.graph.PieGraph;
import com.ted.commander.client.widgets.graph.formatter.DateFormatter;
import com.ted.commander.client.widgets.graph.formatter.ExportGraphTypeFormatter;
import com.ted.commander.client.widgets.graph.formatter.GraphLineTypeFormatter;
import com.ted.commander.client.widgets.graph.model.GraphAxis;
import com.ted.commander.client.widgets.graph.model.GraphDataPoint;
import com.ted.commander.client.widgets.graph.model.GraphDataPoints;
import com.ted.commander.client.widgets.graph.model.PiePoint;
import com.ted.commander.client.widgets.toolbar.TitleBar;
import com.ted.commander.common.enums.ExportGraphType;
import com.ted.commander.common.enums.GraphLineType;
import com.ted.commander.common.enums.HistoryType;
import com.ted.commander.common.enums.VirtualECCType;
import com.ted.commander.common.model.CalendarKey;
import com.ted.commander.common.model.DailySummary;
import com.ted.commander.common.model.EnergyPlan;
import com.ted.commander.common.model.history.HistoryHour;
import com.ted.commander.common.model.history.HistoryMTUHour;
import com.ted.commander.common.model.history.WeatherHistory;
import com.vaadin.polymer.paper.element.PaperTabsElement;
import com.vaadin.polymer.paper.widget.PaperIconButton;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;


public class DailyDetailViewImpl extends Composite implements DailyDetailView {

    static final Logger LOGGER = Logger.getLogger(DailyDetailViewImpl.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);

    Presenter presenter;

    @UiField
    TitleBar titleBar;
    @UiField
    PaperTabsElement displayTabElement;
    @UiField
    PaperIconButton prevDayButton;
    @UiField
    PaperIconButton nextDayButton;
    @UiField
    DivElement dayLabel;
    @UiField
    SimplePanel graphPanel;
    @UiField
    Element materialField;


    DailySummary summary = null;
    EnergyPlan energyPlan = null;

    CalendarKey currentDate = null;
    DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_FULL);
    private NumberFormat currencyFormat;


    public DailyDetailViewImpl() {
        initWidget(defaultBinder.createAndBindUi(this));


        nextDayButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                if (currentDate != null) {
                    presenter.query(currentDate.addDay(1));
                }
            }
        });
        prevDayButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                if (currentDate != null) {
                    presenter.query(currentDate.addDay(-1));
                }
            }
        });


        displayTabElement.addEventListener("iron-select", new com.vaadin.polymer.elemental.EventListener() {
            @Override
            public void handleEvent(com.vaadin.polymer.elemental.Event event) {
                refreshGraphPanel();
            }
        });


        titleBar.addCloseHandler(new CloseHandler() {
            @Override
            public void onClose(CloseEvent event) {
                presenter.goTo(new DashboardPlace(""));
            }
        });


    }

    @Override
    public void setLogo(ImageResource imageResource) {
        //titleBar.setLogo(imageResource);
    }


    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }


    public void setDate(CalendarKey dailyDate) {
        this.currentDate = dailyDate;
        Date date = new Date();
        date.setYear(dailyDate.getYear() - 1900);
        date.setMonth(dailyDate.getMonth());
        date.setDate(dailyDate.getDate());
        CalendarUtil.resetTime(date);
        dayLabel.setInnerText(dateTimeFormat.format(date));
    }

    @Override
    public void setSummary(EnergyPlan energyPlan, DailySummary summary) {
        this.summary = summary;
        this.energyPlan = energyPlan;
        this.currencyFormat = NumberFormat.getSimpleCurrencyFormat(energyPlan.getRateType());
        setDate(summary.getDailyDate());
        refreshGraphPanel();
    }


    @Override
    public void setLoadingVisible(boolean visible) {
        if (visible) LoadingOverlay.get().show();
        else LoadingOverlay.get().hide();
    }

    @Override
    public void onResize() {
        refreshGraphPanel();
    }

    private String getColor(String key, int index, int count) {
        if (key.equals("NET")) return HistoryGraph.NET_COLOR;
        if (key.equals("GENERATION")) return HistoryGraph.GEN_COLOR;
        if (key.equals("LOAD")) return HistoryGraph.LOAD_COLOR;

        //TODO: replace this w/ a user lookup.
        int val = (index + 1) * (224 / count);
        String hex = Integer.toHexString(val);
        if (hex.length() == 1) hex = "0" + hex;
        return "#00" + hex + hex;
    }



    public GraphAxis getEnergyAxis(boolean gen, boolean standAlone){
        ExportGraphTypeFormatter graphFormatter = new ExportGraphTypeFormatter(HistoryType.HOURLY, ExportGraphType.ENERGY, energyPlan.getRateType());
        List<GraphDataPoints> energyGraphDataPoints = new ArrayList<GraphDataPoints>();
        double minValue = Double.MAX_VALUE;
        double maxValue = Double.MIN_VALUE;

        GraphDataPoints netDataPoints = new GraphDataPoints(getColor("NET",0,0), WebStringResource.INSTANCE.locationNET(), graphFormatter);

        int index = 1;
        for (HistoryHour historyHour: summary.getHourlyData()){
            if (historyHour.getNet() < minValue) minValue = historyHour.getNet();
            if (historyHour.getNet() > maxValue) maxValue = historyHour.getNet();
            netDataPoints.add(new GraphDataPoint(historyHour.getCalendarKey(), historyHour.getNet()));
        }
        energyGraphDataPoints.add(netDataPoints);


        if (gen) {
            GraphDataPoints genDataPoints = new GraphDataPoints(getColor("GENERATION", 0, 0), WebStringResource.INSTANCE.locationGENERATION(), graphFormatter);
            for (HistoryHour historyHour : summary.getHourlyData()) {
                if (historyHour.getGeneration() < minValue) minValue = historyHour.getGeneration();
                if (historyHour.getGeneration() > maxValue) maxValue = historyHour.getGeneration();
                genDataPoints.add(new GraphDataPoint(historyHour.getCalendarKey(), Math.abs(historyHour.getGeneration())));
            }
            index++;
            energyGraphDataPoints.add(genDataPoints);

            GraphDataPoints loadDataPoints = new GraphDataPoints(getColor("LOAD", 0, 0), WebStringResource.INSTANCE.locationLOAD(), graphFormatter);
            for (HistoryHour historyHour : summary.getHourlyData()) {
                if (historyHour.getLoad() < minValue) minValue = historyHour.getLoad();
                if (historyHour.getLoad() > maxValue) maxValue = historyHour.getLoad();
                loadDataPoints.add(new GraphDataPoint(historyHour.getCalendarKey(), historyHour.getLoad()));
            }
            energyGraphDataPoints.add(loadDataPoints);
            index++;
        }

        int total = summary.getStandAloneData().size() - index;

        if (standAlone){
            for (String mtuHex : summary.getStandAloneData().keySet()) {
                GraphDataPoints dataPoints = new GraphDataPoints(getColor(mtuHex, index++, total), summary.getMtuDescription().get(mtuHex), graphFormatter);
                List<HistoryMTUHour> historyDataPointList = summary.getStandAloneData().get(mtuHex);
                for (HistoryMTUHour historyDataPoint : historyDataPointList) {
                    if (historyDataPoint.getEnergy() < minValue) minValue = historyDataPoint.getEnergy();
                    if (historyDataPoint.getEnergy() > maxValue) maxValue = historyDataPoint.getEnergy();
                    dataPoints.add(new GraphDataPoint(historyDataPoint.getCalendarKey(), historyDataPoint.getEnergy()));
                }
                energyGraphDataPoints.add(dataPoints);
                index++;
            }
        }

        //Zero out the lines
        if (minValue > 0) minValue = 0;
        if (maxValue < 0) maxValue = 0;


        return new GraphAxis(minValue, maxValue, "Energy", "#000",  graphFormatter, energyGraphDataPoints);
    }



    private String formatPieString(NumberFormat numberFormat, Double value){
        if (value < 0) {
            return "(GEN) " + numberFormat.format(value/1000.0);
        } else {
            return numberFormat.format(value/1000.0);
        }
    }

    private String formatPieCostString(NumberFormat numberFormat, Double value){
        if (value < 0) {
            return "(GEN) " + numberFormat.format(value);
        } else {
            return numberFormat.format(value);
        }
    }


    public List<PiePoint> getPiePoints(){

        List<PiePoint> piePointList = new ArrayList<PiePoint>();


        int colorIndex = 0;
        int totalColors = 0;

        for (String key : summary.getStandAloneData().keySet()){
            totalColors++;
        }

        for (String key : summary.getStandAloneData().keySet()){
            List<HistoryMTUHour> HistoryDataPointList =  summary.getStandAloneData().get(key);

            double energyTotal = 0;
            double costTotal = 0;

            for (HistoryMTUHour historyMTUHour: HistoryDataPointList){
                    energyTotal += historyMTUHour.getEnergy();
                    costTotal += historyMTUHour.getCost();
            }


                piePointList.add(new PiePoint(key,
                        summary.getMtuDescription().get(key),
                        energyTotal,
                        formatPieString(CommanderFormats.SHORT_KWH_FORMAT, energyTotal),
                        costTotal,
                        formatPieCostString(currencyFormat, costTotal),
                        getColor(key, colorIndex++, totalColors)));

        }
        return piePointList;
    }

    GraphAxis getWeatherAxis(boolean isSolar){
        List<GraphDataPoints> graphDataPoints = new ArrayList<GraphDataPoints>();

        GraphLineTypeFormatter graphLineTypeFormatter = new GraphLineTypeFormatter(isSolar ?GraphLineType.CLOUD_COVERAGE:GraphLineType.TEMPERATURE, summary.isMetric(), "");
        GraphDataPoints weatherDataPoints = new GraphDataPoints(HistoryGraph.WEATHER_COLOR, isSolar?WebStringResource.INSTANCE.cloudCoverage():WebStringResource.INSTANCE.temperature(), graphLineTypeFormatter);
        graphDataPoints.add(weatherDataPoints);

        for (WeatherHistory weatherHistory : summary.getWeatherGraphPoints()){
            if (isSolar){
                weatherDataPoints.add(new GraphDataPoint(weatherHistory.getCalendarKey(), (double)weatherHistory.getClouds()));
            } else {
                weatherDataPoints.add(new GraphDataPoint(weatherHistory.getCalendarKey(), weatherHistory.getTemp()));
            }
        }

        return new GraphAxis(
                weatherDataPoints.getMinValue(),
                weatherDataPoints.getMaxValue(),
                WebStringResource.INSTANCE.temperature(),
                weatherDataPoints.getColor(),
                weatherDataPoints.getFormatter(),
                graphDataPoints);
    }

    void refreshGraphPanel(){
        LOGGER.fine("REFRESH GRAPH PANEL " + getSelectedTab());


        int maxHeight = Window.getClientHeight();
        maxHeight -= graphPanel.getAbsoluteTop();
        maxHeight -= PaperStyleManager.getDP(16);
        maxHeight -= PaperStyleManager.getDP(16);
        //graphPanel.getElement().getStyle().setHeight(maxHeight, Style.Unit.PX);
        int width =  (int)(Window.getClientWidth() * .98) - 32;
        materialField.getStyle().setWidth(Window.getClientWidth(), Style.Unit.PX);

        graphPanel.clear();


        switch (getSelectedTab()){
            case 0:{
                int aw = Window.getClientWidth();
                if (aw > 800) aw = 800;
                materialField.getStyle().setWidth(aw, Style.Unit.PX);
                graphPanel.add(new DailyAveragesPage(energyPlan, summary, currencyFormat));
                break;
            }
            case 1:{
                materialField.getStyle().setWidth(Window.getClientWidth(), Style.Unit.PX);
                GraphAxis weatherAxis = getWeatherAxis(!summary.getVirtualECCType().equals(VirtualECCType.NET_ONLY));
                List<GraphAxis> graphAxisList = new ArrayList<GraphAxis>();
                graphAxisList.add(getEnergyAxis(!summary.getVirtualECCType().equals(VirtualECCType.NET_ONLY), false));
                graphPanel.add(new LineGraph(width - 40, maxHeight - 16, summary.getDailyDate(), summary.getDailyDate(), 3600, 6, new DateFormatter(HistoryType.HOURLY), graphAxisList, weatherAxis, weatherAxis.getGraphDataPointsList().get(0)));
                break;
            }
            case 2:{
                materialField.getStyle().setWidth(Window.getClientWidth(), Style.Unit.PX);
                GraphAxis weatherAxis = getWeatherAxis(!summary.getVirtualECCType().equals(VirtualECCType.NET_ONLY));
                List<GraphAxis> graphAxisList = new ArrayList<GraphAxis>();
                graphAxisList.add(getEnergyAxis(!summary.getVirtualECCType().equals(VirtualECCType.NET_ONLY), summary.getVirtualECCType().equals(VirtualECCType.NET_ONLY)));
                BarGraph barGraph = new BarGraph(width, maxHeight, summary.getDailyDate(), summary.getDailyDate().addDay(1), 3600, new DateFormatter(HistoryType.HOURLY), graphAxisList, weatherAxis, weatherAxis.getGraphDataPointsList().get(0));
                barGraph.changeStartDate(summary.getDailyDate());
                graphPanel.add(barGraph);
                break;
            }
            case 3: {
                materialField.getStyle().setWidth(Window.getClientWidth(), Style.Unit.PX);
                List<PiePoint> piePointList = getPiePoints();
                if (piePointList.size() > 0){
                    graphPanel.add(new PieGraph(piePointList, width, maxHeight, true));
                } else {
                    graphPanel.add(new DailyDetailViewNoPie());
                }
            }
        }
    }


    @Override
    public int getSelectedTab() {
        return Integer.parseInt(displayTabElement.getSelected());
    }


    interface DefaultBinder extends UiBinder<Widget, DailyDetailViewImpl> {
    }


}
