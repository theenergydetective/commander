/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.graphing;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.ted.commander.client.clientFactory.ClientFactory;
import com.ted.commander.client.enums.CommanderFormats;
import com.ted.commander.client.events.PlaceRequestEvent;
import com.ted.commander.client.places.GraphingOptionsPlace;
import com.ted.commander.client.places.GraphingPlace;
import com.ted.commander.client.resources.WebStringResource;
import com.ted.commander.client.restInterface.RESTFactory;
import com.ted.commander.client.widgets.graph.HistoryGraph;
import com.ted.commander.client.widgets.graph.formatter.DateFormatter;
import com.ted.commander.client.widgets.graph.formatter.ExportGraphTypeFormatter;
import com.ted.commander.client.widgets.graph.formatter.GraphFormatter;
import com.ted.commander.client.widgets.graph.formatter.GraphLineTypeFormatter;
import com.ted.commander.client.widgets.graph.model.GraphAxis;
import com.ted.commander.client.widgets.graph.model.GraphDataPoint;
import com.ted.commander.client.widgets.graph.model.GraphDataPoints;
import com.ted.commander.client.widgets.graph.model.PiePoint;
import com.ted.commander.common.enums.GraphLineType;
import com.ted.commander.common.enums.HistoryType;
import com.ted.commander.common.model.CalendarKey;
import com.ted.commander.common.model.VirtualECCMTU;
import com.ted.commander.common.model.export.GraphRequest;
import com.ted.commander.common.model.export.GraphResponse;
import com.ted.commander.common.model.export.HistoryDataPoint;
import com.ted.commander.common.model.history.WeatherHistory;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class GraphingPresenter implements GraphingView.Presenter, IsWidget {

    static final Logger LOGGER = Logger.getLogger(GraphingPresenter.class.getName());

    final ClientFactory clientFactory;
    final GraphingView view;
    final GraphingPlace place;

    static final DateTimeFormat dtf = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_MEDIUM);
    static final DateTimeFormat myf = DateTimeFormat.getFormat("MMM yyyy");
    static final DateTimeFormat dmyf = DateTimeFormat.getFormat("MMM dd yyyy");
    final HashMap<String, String> mtuNameMap = new HashMap<>();
    GraphResponse lastResponse;

    boolean isMetric = false;

    //Used to handle a resize event
    Timer refreshTimer = new Timer() {
        @Override
        public void run() {
            changeTab(view.getSelectedTab());
        }
    };


    public GraphingPresenter(final ClientFactory clientFactory, GraphingPlace place) {
        LOGGER.fine("CREATING NEW GraphingPresenter ");
        this.clientFactory = clientFactory;
        this.place = place;
        view = clientFactory.getGraphingView();
        view.setPresenter(this);
        view.setLogo(clientFactory.getInstance().getLogo().getHeaderLogo());

        GraphRequest graphRequest = clientFactory.getGraphRequest();

        if (graphRequest == null) {
            goTo(new GraphingOptionsPlace(""));
        } else {
            view.setLoadingVisible(true);
            RESTFactory.getGraphClient(clientFactory).request(graphRequest, graphRequest.getVirtualECCId(), graphRequest.getHistoryType(), new MethodCallback<GraphResponse>() {
                @Override
                public void onFailure(Method method, Throwable throwable) {
                    view.setLoadingVisible(false);
                    goTo(new GraphingOptionsPlace(""));
                }

                public void onSuccess(Method method, GraphResponse graphResponse) {
                    LOGGER.info("Graph Response " + graphResponse);
                    lastResponse = graphResponse;

                    view.setLocationName(graphResponse.getVirtualECC().getName());
                    String country = graphResponse.getVirtualECC().getCountry();
                    if (country != null && country.trim().length() > 0 && !"US".equals(country.trim().toUpperCase())) {
                        isMetric = true;
                    }

                    view.setDateRange(generateCaption());
                    mapMTUNames(graphResponse.getVirtualECCMTUList());

                    if (clientFactory.getGraphRequest().getGraphLineType().equals(GraphLineType.NET))
                        clientFactory.getGraphRequest().setExportNet(true);
                    if (clientFactory.getGraphRequest().getGraphLineType().equals(GraphLineType.LOAD))
                        clientFactory.getGraphRequest().setExportLoad(true);
                    if (clientFactory.getGraphRequest().getGraphLineType().equals(GraphLineType.GENERATION))
                        clientFactory.getGraphRequest().setExportGeneration(true);
                    if (clientFactory.getGraphRequest().getHistoryType().equals(HistoryType.BILLING_CYCLE) &&
                            clientFactory.getGraphRequest().getGraphLineType().equals(GraphLineType.DEMAND_COST)) {
                        LOGGER.fine("FLAGGING DEMAND COST");
                        clientFactory.getGraphRequest().setExportDemandCost(true);
                    }
                    changeTab(view.getSelectedTab());
                    view.setLoadingVisible(false);
                }
            });
        }
    }

    private String generateCaption() {
        StringBuilder caption = new StringBuilder();

        switch (clientFactory.getGraphRequest().getHistoryType()) {
            case MINUTE:
                caption.append(WebStringResource.INSTANCE.minuteGraph());
                break;
            case HOURLY:
                caption.append(WebStringResource.INSTANCE.hourGraph());
                break;
            case DAILY:
                caption.append(WebStringResource.INSTANCE.dayGraph());
                break;
            case BILLING_CYCLE:
                caption.append(WebStringResource.INSTANCE.billingCycleGraph());
                break;

        }
        Date startDate = new Date(clientFactory.getGraphRequest().getStartDate().getYear() - 1900, clientFactory.getGraphRequest().getStartDate().getMonth(), clientFactory.getGraphRequest().getStartDate().getDate());
        Date endDate = new Date(clientFactory.getGraphRequest().getEndDate().getYear() - 1900, clientFactory.getGraphRequest().getEndDate().getMonth(), clientFactory.getGraphRequest().getEndDate().getDate());
        if (startDate.equals(endDate)){
            switch (clientFactory.getGraphRequest().getHistoryType()) {
                case MINUTE:
                case HOURLY:
                case DAILY:
                    caption.append(" : ").append(dmyf.format(startDate));
                    break;
                case BILLING_CYCLE:
                    caption.append(" : ").append(myf.format(startDate));
                    break;
            }
        } else {
            caption.append(" : ").append(dtf.format(startDate)).append(" - ").append(dtf.format(endDate));
        }

        return caption.toString();
    }


    @Override
    public boolean isValid() {
        boolean valid = true;
        return valid;
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }


    @Override
    public void goTo(Place destinationPage) {
        clientFactory.getEventBus().fireEvent(new PlaceRequestEvent(destinationPage, place, null));
    }

    @Override
    public void onResize() {
        refreshTimer.cancel();
        refreshTimer.schedule(250);
    }

    public void mapMTUNames(List<VirtualECCMTU> virtualECCMTUList) {
        //Reset the mtu name map
        mtuNameMap.clear();
        for (VirtualECCMTU virtualECCMTU : virtualECCMTUList) {
            mtuNameMap.put(virtualECCMTU.getMtuId().toString(), virtualECCMTU.getMtuDescription());
        }
    }


    private String getName(String key) {
        if (key.equals("NET")) return WebStringResource.INSTANCE.locationNET();
        if (key.equals("GENERATION")) return WebStringResource.INSTANCE.locationGENERATION();
        if (key.equals("LOAD")) return WebStringResource.INSTANCE.locationLOAD();
        return mtuNameMap.get(key);
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

    @Override
    public void changeTab(int selected) {

        //TODO: Replace w/ user defined colors
        int colorIndex = 0;
        int colorCount = 0;
        for (String key : lastResponse.getDataMap().keySet()) {
            if (!key.equals("LOAD") && !key.equals("GENERATION") && !key.equals("LOAD")) colorCount++;
        }

        List<GraphDataPoints> graphDataPointsList = new ArrayList<GraphDataPoints>();
        double minValue = Double.MAX_VALUE;
        double maxValue = Double.MIN_VALUE;

        GraphDataPoints netDataPoints = null;
        GraphDataPoints loadDataPoints = null;
        GraphDataPoints genDataPoints = null;

        long tickValue;
        switch (clientFactory.getGraphRequest().getHistoryType()) {
            case MINUTE:
                tickValue = 60;
                break;
            case HOURLY:
                tickValue = 3600;
                break;
            case DAILY:
                tickValue = 86400;
                break;
            default:
                tickValue = 1;
        }

        GraphFormatter graphFormatter = new ExportGraphTypeFormatter(clientFactory.getGraphRequest().getHistoryType(), clientFactory.getGraphRequest().getExportGraphType(), lastResponse.getCurrencyCode());

        List<HistoryDataPoint> demandCostHistory = null;

        for (String key : lastResponse.getDataMap().keySet()) {
            String color = getColor(key, colorIndex++, colorCount);
            String name = getName(key);

            GraphDataPoints graphDataPoints = new GraphDataPoints(color, name, graphFormatter);
            boolean isGen = false;
            switch (key) {
                case "NET":
                    netDataPoints = graphDataPoints;
                    break;
                case "LOAD":
                    loadDataPoints = graphDataPoints;
                    break;
                case "GENERATION":
                    genDataPoints = graphDataPoints;
                    isGen = true;
                    break;
                case "DEMAND_COST":
                    demandCostHistory = lastResponse.getDataMap().get(key);
                    break;
                default:
                    graphDataPointsList.add(graphDataPoints);
            }


            List<HistoryDataPoint> dataPoints = lastResponse.getDataMap().get(key);
            for (HistoryDataPoint dataPoint : dataPoints) {
                Double value;


                if (key.equals("DEMAND_COST")) {
                    value = dataPoint.getCostValue();
                } else {
                    switch (clientFactory.getGraphRequest().getExportGraphType()) {
                        case COST:
                            value = dataPoint.getCostValue();
                            break;
                        case DEMAND:
                            value = dataPoint.getDemandValue();
                            break;
                        default: //ENERGY
                            value = dataPoint.getEnergyValue();
                    }
                    if (isGen) value = Math.abs(value);
                }


                if (value < minValue) minValue = value;
                if (value > maxValue) maxValue = value;

                if (clientFactory.getGraphRequest().getHistoryType().equals(HistoryType.BILLING_CYCLE)) {
                    CalendarKey billingCycleCalendarKey = new CalendarKey(dataPoint.getCalendarKey().getYear(), dataPoint.getCalendarKey().getMonth(), 1);
                    graphDataPoints.add(new GraphDataPoint(billingCycleCalendarKey, value));
                } else {
                    graphDataPoints.add(new GraphDataPoint(dataPoint.getCalendarKey(), value));
                }
            }
        }


        //Zero out the lines
        if (minValue > 0) minValue = 0;
        if (maxValue < 0) maxValue = 0;

        switch (selected) {
            case 2: {
                drawLineGraph(graphDataPointsList, graphFormatter, minValue, maxValue, netDataPoints, loadDataPoints, genDataPoints, demandCostHistory, tickValue);
                break;
            }
            case 0:
                drawBarGraph(graphDataPointsList, graphFormatter, minValue, maxValue, netDataPoints, loadDataPoints, genDataPoints, demandCostHistory, tickValue);
                break;

            case 1:
                drawPieGraph();

                break;
        }
    }

    private void drawPieGraph() {

        final HashMap<String, String> mtuNameMap = new HashMap<>();
        final HashMap<String, Double> energyTotals = new HashMap<>();
        final HashMap<String, Double> costTotals = new HashMap<>();

        for (VirtualECCMTU virtualECCMTU : lastResponse.getVirtualECCMTUList()) {
            mtuNameMap.put(virtualECCMTU.getMtuId().toString(), virtualECCMTU.getMtuDescription());
        }

        //Calculate totals
        for (String key : lastResponse.getDataMap().keySet()) {
            if (key.equals("DEMAND_COST")) continue; //Not showable here.

            List<HistoryDataPoint> HistoryDataPointList = lastResponse.getDataMap().get(key);

            double energyTotal = 0;
            double costTotal = 0;

            for (HistoryDataPoint HistoryDataPoint : HistoryDataPointList) {
                if (clientFactory.getGraphRequest().getHistoryType().equals(HistoryType.MINUTE)) {
                    energyTotal += (HistoryDataPoint.getEnergyValue() / 60.0);
                    costTotal += (HistoryDataPoint.getCostValue() / 60.0);
                } else {
                    energyTotal += HistoryDataPoint.getEnergyValue();
                    costTotal += HistoryDataPoint.getCostValue();
                }
            }

            if (key.equals("GENERATION")) {
                energyTotal = Math.abs(energyTotal);
                costTotal = Math.abs(costTotal);
            }

            energyTotals.put(key, energyTotal);
            costTotals.put(key, costTotal);


        }


        final NumberFormat currencyFormat = NumberFormat.getSimpleCurrencyFormat(lastResponse.getCurrencyCode());


        List<PiePoint> piePointList = new ArrayList<PiePoint>();
        int index = 0;
        if (costTotals.keySet().contains("NET")) {
            piePointList.add(new PiePoint("NET",
                    "NET",
                    energyTotals.get("NET"),
                    formatPieString(CommanderFormats.SHORT_KWH_FORMAT, energyTotals.get("NET")),
                    costTotals.get("NET"),
                    formatPieCostString(currencyFormat, costTotals.get("NET")),
                    getColor("NET", 0, 0)
            ));
            index++;
        }

        if (costTotals.keySet().contains("GENERATION")) {
            piePointList.add(new PiePoint("GEN",
                    "GEN",
                    energyTotals.get("GENERATION"),
                    formatPieString(CommanderFormats.SHORT_KWH_FORMAT, energyTotals.get("GENERATION")),
                    costTotals.get("GENERATION"),
                    formatPieCostString(currencyFormat, costTotals.get("GENERATION")),
                    getColor("GENERATION", 0, 0)
            ));
            index++;
        }


        if (costTotals.keySet().contains("LOAD")) {
            piePointList.add(new PiePoint("LOAD",
                    "LOAD",
                    energyTotals.get("LOAD"),
                    formatPieString(CommanderFormats.SHORT_KWH_FORMAT, energyTotals.get("LOAD")),
                    costTotals.get("LOAD"),
                    formatPieCostString(currencyFormat, costTotals.get("LOAD")),
                    getColor("LOAD", 0, 0)
            ));
            index++;
        }


        int count = costTotals.size() - index;
        final int divider = 224 / (costTotals.keySet().size());
        LOGGER.fine("divider " + divider);
        for (String key : costTotals.keySet()) {
            if (key.equals("NET")) continue;
            if (key.equals("LOAD")) continue;
            if (key.equals("GENERATION")) continue;

            piePointList.add(new PiePoint(key,
                    mtuNameMap.get(key),
                    energyTotals.get(key),
                    formatPieString(CommanderFormats.SHORT_KWH_FORMAT, energyTotals.get(key)),
                    costTotals.get(key),
                    formatPieCostString(currencyFormat, costTotals.get(key)),
                    getColor(key, index++, count)
            ));
        }


        view.drawPie(piePointList);

    }

    private String formatPieString(NumberFormat numberFormat, Double value) {
        if (value < 0) {
            return "(GEN) " + numberFormat.format(value / 1000.0);
        } else {
            return numberFormat.format(value / 1000.0);
        }
    }

    private String formatPieCostString(NumberFormat numberFormat, Double value) {
        if (value < 0) {
            return "(GEN) " + numberFormat.format(value);
        } else {
            return numberFormat.format(value);
        }
    }


    private void drawBarGraph(List<GraphDataPoints> graphDataPointsList, GraphFormatter graphFormatter, double minValue, double maxValue, GraphDataPoints netDataPoints, GraphDataPoints loadDataPoints, GraphDataPoints genDataPoints, List<HistoryDataPoint> demandCostDataPoints, long tickValue) {
        //Create Y Axis
        List<GraphAxis> graphAxisList = new ArrayList<GraphAxis>();

        String axisLabel;
        switch (clientFactory.getGraphRequest().getExportGraphType()) {
            case COST:
                axisLabel = WebStringResource.INSTANCE.cost();
                break;
            case DEMAND:
                axisLabel = WebStringResource.INSTANCE.demand();
                break;
            default: //ENERGY
                axisLabel = WebStringResource.INSTANCE.energy();
        }


        //Create Lines
        List<GraphDataPoints> lines = new ArrayList<GraphDataPoints>();
        if (netDataPoints != null && !clientFactory.getGraphRequest().getGraphLineType().equals(GraphLineType.NET))
            lines.add(netDataPoints);
        if (loadDataPoints != null && !clientFactory.getGraphRequest().getGraphLineType().equals(GraphLineType.LOAD))
            lines.add(loadDataPoints);
        if (genDataPoints != null && !clientFactory.getGraphRequest().getGraphLineType().equals(GraphLineType.GENERATION))
            lines.add(genDataPoints);
//        if (demandCostDataPoints != null && !clientFactory.getGraphRequest().getGraphLineType().equals(GraphLineType.DEMAND_COST))
//            lines.add(genDataPoints);

        lines.addAll(graphDataPointsList);


        //Add the primary data points
        GraphAxis graphAxis = new GraphAxis(minValue, maxValue, axisLabel, "#000000", graphFormatter, lines); //Black by default.
        graphAxisList.add(graphAxis);

        GraphAxis areaGraphAxis = null;
        GraphDataPoints areaGraphDataPoints = null;
        switch (clientFactory.getGraphRequest().getGraphLineType()) {
            case NET:
                areaGraphAxis = null;
                areaGraphDataPoints = netDataPoints;
                break;
            case LOAD:
                areaGraphAxis = null;
                areaGraphDataPoints = loadDataPoints;
                break;
            case GENERATION:
                areaGraphAxis = null;
                areaGraphDataPoints = genDataPoints;
                break;
            case TEMPERATURE: {
                areaGraphDataPoints = new GraphDataPoints(HistoryGraph.WEATHER_COLOR, WebStringResource.INSTANCE.temperature(), new GraphLineTypeFormatter(clientFactory.getGraphRequest().getGraphLineType(), isMetric, lastResponse.getCurrencyCode()));
                if (lastResponse.getWeatherHistory() != null && lastResponse.getWeatherHistory().size() > 0) {
                    for (WeatherHistory weatherHistory : lastResponse.getWeatherHistory()) {
                        areaGraphDataPoints.add(new GraphDataPoint(weatherHistory.getCalendarKey(), weatherHistory.getTemp()));
                    }
                }
                List<GraphDataPoints> areaGraphDataPointsList = new ArrayList<GraphDataPoints>();
                areaGraphDataPointsList.add(areaGraphDataPoints);


                areaGraphAxis = new GraphAxis(
                        areaGraphDataPoints.getMinValue(),
                        areaGraphDataPoints.getMaxValue(),
                        WebStringResource.INSTANCE.temperature(),
                        areaGraphDataPoints.getColor(),
                        areaGraphDataPoints.getFormatter(),
                        areaGraphDataPointsList);
                break;
            }
            case CLOUD_COVERAGE: {
                areaGraphDataPoints = new GraphDataPoints(HistoryGraph.WEATHER_COLOR, WebStringResource.INSTANCE.cloudCoverage(), new GraphLineTypeFormatter(clientFactory.getGraphRequest().getGraphLineType(), isMetric, lastResponse.getCurrencyCode()));
                if (lastResponse.getWeatherHistory() != null && lastResponse.getWeatherHistory().size() > 0) {
                    for (WeatherHistory weatherHistory : lastResponse.getWeatherHistory()) {
                        areaGraphDataPoints.add(new GraphDataPoint(weatherHistory.getCalendarKey(), (double) weatherHistory.getClouds()));
                    }
                }
                List<GraphDataPoints> areaGraphDataPointsList = new ArrayList<GraphDataPoints>();
                areaGraphDataPointsList.add(areaGraphDataPoints);

                areaGraphAxis = new GraphAxis(0.0, 100.0,
                        WebStringResource.INSTANCE.cloudCoverage(),
                        areaGraphDataPoints.getColor(),
                        areaGraphDataPoints.getFormatter(),
                        areaGraphDataPointsList);
                break;
            }

            case WIND_SPEED: {
                areaGraphDataPoints = new GraphDataPoints(HistoryGraph.WEATHER_COLOR, WebStringResource.INSTANCE.windSpeed(), new GraphLineTypeFormatter(clientFactory.getGraphRequest().getGraphLineType(), isMetric, lastResponse.getCurrencyCode()));
                if (lastResponse.getWeatherHistory() != null && lastResponse.getWeatherHistory().size() > 0) {
                    for (WeatherHistory weatherHistory : lastResponse.getWeatherHistory()) {
                        areaGraphDataPoints.add(new GraphDataPoint(weatherHistory.getCalendarKey(), (double) weatherHistory.getWindspeed()));
                    }
                }
                List<GraphDataPoints> areaGraphDataPointsList = new ArrayList<GraphDataPoints>();
                areaGraphDataPointsList.add(areaGraphDataPoints);

                areaGraphAxis = new GraphAxis(
                        0.0,
                        areaGraphDataPoints.getMaxValue(),
                        WebStringResource.INSTANCE.windSpeed(),
                        areaGraphDataPoints.getColor(),
                        areaGraphDataPoints.getFormatter(),
                        areaGraphDataPointsList);
                break;
            }
            case DEMAND_COST: {
                areaGraphDataPoints = new GraphDataPoints(HistoryGraph.WEATHER_COLOR, WebStringResource.INSTANCE.demandCost(), new GraphLineTypeFormatter(GraphLineType.DEMAND_COST, isMetric, lastResponse.getCurrencyCode()));
                if (demandCostDataPoints != null && demandCostDataPoints.size() > 0) {
                    for (HistoryDataPoint demandHistory : demandCostDataPoints) {
                        CalendarKey calendarKey = demandHistory.getCalendarKey();
                        calendarKey.setDate(1);
                        GraphDataPoint graphDataPoint = new GraphDataPoint(calendarKey, demandHistory.getCostValue());
                        areaGraphDataPoints.add(graphDataPoint);
                    }
                }

                List<GraphDataPoints> areaGraphDataPointsList = new ArrayList<GraphDataPoints>();
                areaGraphDataPointsList.add(areaGraphDataPoints);

                double minDemandCost = areaGraphDataPoints.getMinValue();
                if (minDemandCost > 0 && areaGraphDataPoints.getMaxValue() > 0) minDemandCost = 0;

                areaGraphAxis = new GraphAxis(
                        minDemandCost,
                        areaGraphDataPoints.getMaxValue(),
                        WebStringResource.INSTANCE.demandCost(),
                        areaGraphDataPoints.getColor(),
                        areaGraphDataPoints.getFormatter(),
                        areaGraphDataPointsList);
                break;
            }

            default:
                //None;
        }


        view.drawBarGraph(lastResponse.getStartCalendarKey(),
                lastResponse.getEndCalendarKey(),
                tickValue,
                new DateFormatter(clientFactory.getGraphRequest().getHistoryType()),
                graphAxisList, areaGraphAxis, areaGraphDataPoints);

    }


    private void drawLineGraph(List<GraphDataPoints> graphDataPointsList, GraphFormatter graphFormatter, double minValue, double maxValue, GraphDataPoints netDataPoints, GraphDataPoints loadDataPoints, GraphDataPoints genDataPoints, List<HistoryDataPoint> demandCostDataPoints, long tickValue) {
        //Create Y Axis
        List<GraphAxis> graphAxisList = new ArrayList<GraphAxis>();

        String axisLabel;
        switch (clientFactory.getGraphRequest().getExportGraphType()) {
            case COST:
                axisLabel = WebStringResource.INSTANCE.cost();
                break;
            case DEMAND:
                axisLabel = WebStringResource.INSTANCE.demand();
                break;
            default: //ENERGY
                axisLabel = WebStringResource.INSTANCE.energy();
        }


        //Create Lines
        List<GraphDataPoints> lines = new ArrayList<GraphDataPoints>();
        if (netDataPoints != null && !clientFactory.getGraphRequest().getGraphLineType().equals(GraphLineType.NET))
            lines.add(netDataPoints);
        if (loadDataPoints != null && !clientFactory.getGraphRequest().getGraphLineType().equals(GraphLineType.LOAD))
            lines.add(loadDataPoints);
        if (genDataPoints != null && !clientFactory.getGraphRequest().getGraphLineType().equals(GraphLineType.GENERATION))
            lines.add(genDataPoints);
        lines.addAll(graphDataPointsList);

        //Add the primary data points
        GraphAxis graphAxis = new GraphAxis(minValue, maxValue, axisLabel, "#000000", graphFormatter, lines); //Black by default.
        graphAxisList.add(graphAxis);

        GraphAxis areaGraphAxis = null;
        GraphDataPoints areaGraphDataPoints = null;
        switch (clientFactory.getGraphRequest().getGraphLineType()) {
            case NET:
                areaGraphAxis = null;
                areaGraphDataPoints = netDataPoints;
                break;
            case LOAD:
                areaGraphAxis = null;
                areaGraphDataPoints = loadDataPoints;
                break;
            case GENERATION:
                areaGraphAxis = null;
                areaGraphDataPoints = genDataPoints;
                break;
            case TEMPERATURE: {
                areaGraphDataPoints = new GraphDataPoints(HistoryGraph.WEATHER_COLOR, WebStringResource.INSTANCE.temperature(), new GraphLineTypeFormatter(clientFactory.getGraphRequest().getGraphLineType(), isMetric, lastResponse.getCurrencyCode()));
                if (lastResponse.getWeatherHistory() != null && lastResponse.getWeatherHistory().size() > 0) {
                    for (WeatherHistory weatherHistory : lastResponse.getWeatherHistory()) {
                        areaGraphDataPoints.add(new GraphDataPoint(weatherHistory.getCalendarKey(), weatherHistory.getTemp()));
                    }
                }
                List<GraphDataPoints> areaGraphDataPointsList = new ArrayList<GraphDataPoints>();
                areaGraphDataPointsList.add(areaGraphDataPoints);


                areaGraphAxis = new GraphAxis(
                        areaGraphDataPoints.getMinValue(),
                        areaGraphDataPoints.getMaxValue(),
                        WebStringResource.INSTANCE.temperature(),
                        areaGraphDataPoints.getColor(),
                        areaGraphDataPoints.getFormatter(),
                        areaGraphDataPointsList);
                break;
            }
            case CLOUD_COVERAGE: {
                areaGraphDataPoints = new GraphDataPoints(HistoryGraph.WEATHER_COLOR, WebStringResource.INSTANCE.cloudCoverage(), new GraphLineTypeFormatter(clientFactory.getGraphRequest().getGraphLineType(), isMetric, lastResponse.getCurrencyCode()));
                if (lastResponse.getWeatherHistory() != null && lastResponse.getWeatherHistory().size() > 0) {
                    for (WeatherHistory weatherHistory : lastResponse.getWeatherHistory()) {
                        areaGraphDataPoints.add(new GraphDataPoint(weatherHistory.getCalendarKey(), (double) weatherHistory.getClouds()));
                    }
                }
                List<GraphDataPoints> areaGraphDataPointsList = new ArrayList<GraphDataPoints>();
                areaGraphDataPointsList.add(areaGraphDataPoints);

                areaGraphAxis = new GraphAxis(0.0, 100.0,
                        WebStringResource.INSTANCE.cloudCoverage(),
                        areaGraphDataPoints.getColor(),
                        areaGraphDataPoints.getFormatter(),
                        areaGraphDataPointsList);
                break;
            }

            case WIND_SPEED: {
                areaGraphDataPoints = new GraphDataPoints(HistoryGraph.WEATHER_COLOR, WebStringResource.INSTANCE.windSpeed(), new GraphLineTypeFormatter(clientFactory.getGraphRequest().getGraphLineType(), isMetric, lastResponse.getCurrencyCode()));
                if (lastResponse.getWeatherHistory() != null && lastResponse.getWeatherHistory().size() > 0) {
                    for (WeatherHistory weatherHistory : lastResponse.getWeatherHistory()) {
                        areaGraphDataPoints.add(new GraphDataPoint(weatherHistory.getCalendarKey(), (double) weatherHistory.getWindspeed()));
                    }
                }
                List<GraphDataPoints> areaGraphDataPointsList = new ArrayList<GraphDataPoints>();
                areaGraphDataPointsList.add(areaGraphDataPoints);

                areaGraphAxis = new GraphAxis(
                        0.0,
                        areaGraphDataPoints.getMaxValue(),
                        WebStringResource.INSTANCE.windSpeed(),
                        areaGraphDataPoints.getColor(),
                        areaGraphDataPoints.getFormatter(),
                        areaGraphDataPointsList);
                break;
            }
            case DEMAND_COST: {
                areaGraphDataPoints = new GraphDataPoints(HistoryGraph.WEATHER_COLOR, WebStringResource.INSTANCE.demandCost(), new GraphLineTypeFormatter(GraphLineType.DEMAND_COST, isMetric, lastResponse.getCurrencyCode()));
                if (demandCostDataPoints != null && demandCostDataPoints.size() > 0) {
                    for (HistoryDataPoint demandHistory : demandCostDataPoints) {
                        CalendarKey calendarKey = demandHistory.getCalendarKey();
                        calendarKey.setDate(1);
                        GraphDataPoint graphDataPoint = new GraphDataPoint(calendarKey, demandHistory.getCostValue());
                        areaGraphDataPoints.add(graphDataPoint);
                    }
                }
                List<GraphDataPoints> areaGraphDataPointsList = new ArrayList<GraphDataPoints>();
                areaGraphDataPointsList.add(areaGraphDataPoints);

                double minDemandCost = areaGraphDataPoints.getMinValue();
                if (minDemandCost > 0 && areaGraphDataPoints.getMaxValue() > 0) minDemandCost = 0;


                areaGraphAxis = new GraphAxis(
                        minDemandCost,
                        areaGraphDataPoints.getMaxValue(),
                        WebStringResource.INSTANCE.windSpeed(),
                        areaGraphDataPoints.getColor(),
                        areaGraphDataPoints.getFormatter(),
                        areaGraphDataPointsList);
                break;
            }

            default:
                //None;
        }


        view.drawLineGraph(clientFactory.getGraphRequest().getStartDate(),
                clientFactory.getGraphRequest().getEndDate(),
                tickValue,
                new DateFormatter(clientFactory.getGraphRequest().getHistoryType()),
                graphAxisList, areaGraphAxis, areaGraphDataPoints);

    }
}
