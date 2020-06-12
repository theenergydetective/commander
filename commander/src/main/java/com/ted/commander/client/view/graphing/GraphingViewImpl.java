/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.graphing;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.place.shared.Place;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.petecode.common.client.events.ItemSelectedEvent;
import com.petecode.common.client.events.ItemSelectedHandler;
import com.petecode.common.client.widget.paper.PaperStyleManager;
import com.ted.commander.client.places.GraphingOptionsPlace;
import com.ted.commander.client.places.GraphingPlace;
import com.ted.commander.client.view.LoadingOverlay;
import com.ted.commander.client.widgets.graph.BarGraph;
import com.ted.commander.client.widgets.graph.LineGraph;
import com.ted.commander.client.widgets.graph.PieGraph;
import com.ted.commander.client.widgets.graph.formatter.DateFormatter;
import com.ted.commander.client.widgets.graph.model.GraphAxis;
import com.ted.commander.client.widgets.graph.model.GraphDataPoints;
import com.ted.commander.client.widgets.graph.model.PiePoint;
import com.ted.commander.client.widgets.toolbar.TitleBar;
import com.ted.commander.common.model.CalendarKey;
import com.vaadin.polymer.paper.element.PaperTabsElement;
import com.vaadin.polymer.paper.widget.PaperIconButton;

import java.util.List;
import java.util.logging.Logger;



public class GraphingViewImpl extends Composite implements GraphingView {
    static final Logger LOGGER = Logger.getLogger(GraphingViewImpl.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);

    GraphingView.Presenter presenter;
    @UiField
    PaperIconButton graphSettingsButton;
    @UiField
    DivElement locationNameField;
    @UiField
    DivElement locationCaptionField;

    @UiField
    SimplePanel graphPanel;

    @UiField
    TitleBar titleBar;
    @UiField
    PaperTabsElement paperTabSelector;

    CalendarKey currentStartDate = null;





    public GraphingViewImpl() {
        initWidget(defaultBinder.createAndBindUi(this));
        graphSettingsButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                presenter.goTo(new GraphingOptionsPlace(""));
            }
        });
        titleBar.setSelectedPlace(new GraphingPlace(""));
        titleBar.addItemSelectedHandler(new ItemSelectedHandler<Place>() {
            @Override
            public void onSelected(ItemSelectedEvent<Place> event) {
                presenter.goTo(event.getItem());
            }
        });

        paperTabSelector.addEventListener("iron-select", new com.vaadin.polymer.elemental.EventListener() {
            @Override
            public void handleEvent(com.vaadin.polymer.elemental.Event event) {
                presenter.changeTab(getSelectedTab());
            }
        });
    }

    
    @Override
    protected void onAttach() {
        super.onAttach();
    }


    @Override
    public void setLogo(ImageResource imageResource) {
    }

    @Override
    public void setLocationName(String text) {
        locationNameField.setInnerText(text);
    }

    @Override
    public void setDateRange(String text) {
        locationCaptionField.setInnerText(text);
    }

    @Override
    public int getSelectedTab() {
        return Integer.parseInt(paperTabSelector.getSelected());
    }


    @Override
    public void setLoadingVisible(boolean isVisible) {
        if (isVisible) LoadingOverlay.get().show();
        else LoadingOverlay.get().hide();
    }

    @Override
    public void setPresenter(GraphingView.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void drawLineGraph(CalendarKey minTime, CalendarKey maxTime, long tickInterval, DateFormatter dateFormatter, List<GraphAxis> graphAxisList, GraphAxis areaAxis, GraphDataPoints areaDataPoints) {
        graphPanel.clear();
        int maxHeight = Window.getClientHeight();
        maxHeight -= graphPanel.getAbsoluteTop();
        maxHeight -= PaperStyleManager.getDP(16);
        maxHeight -= PaperStyleManager.getDP(16);
        graphPanel.getElement().getStyle().setHeight(maxHeight, Style.Unit.PX);
        int width = graphPanel.getElement().getClientWidth();
        int numTicks = 8; //TODO: Reduce for small screens

        if (tickInterval > 1) {
            long totalIntervals = (maxTime.toLocalEpoch() - minTime.toLocalEpoch()) / tickInterval;
            if (totalIntervals < numTicks) numTicks = (int) totalIntervals;
        } else {
            //Count the billing cycle months
            CalendarKey minDate = null;
            CalendarKey maxDate = null;
            //Find the absolute min and max date for the range.
            for (GraphAxis graphAxis : graphAxisList){
                for (GraphDataPoints graphDataPoints: graphAxis.getGraphDataPointsList()) {
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
            maxDate.addMonth(1);
            int months = minDate.monthDiff(maxDate);
            if (months == 0) months = 1;
            if (months < numTicks){
                numTicks = months;
            }
        }

        if (numTicks == 0) numTicks = 8;

        graphPanel.add(new LineGraph(width, maxHeight, minTime, maxTime, tickInterval, numTicks,  dateFormatter, graphAxisList, areaAxis, areaDataPoints));
    }



    @Override
    public void drawPie(List<PiePoint> piePointList) {
        graphPanel.clear();
        int maxHeight = Window.getClientHeight();
        maxHeight -= graphPanel.getAbsoluteTop();
        maxHeight -= PaperStyleManager.getDP(16);
        maxHeight -= PaperStyleManager.getDP(16);

        graphPanel.getElement().getStyle().setHeight(maxHeight, Style.Unit.PX);

        int width = graphPanel.getElement().getClientWidth();
        graphPanel.add(new PieGraph(piePointList, width, maxHeight, true));

    }

    @Override
    public void drawBarGraph(CalendarKey minTime, CalendarKey maxTime, long tickInterval, DateFormatter dateFormatter, List<GraphAxis> graphAxisList, GraphAxis areaAxis, GraphDataPoints areaDataPoints) {
        graphPanel.clear();
        int maxHeight = Window.getClientHeight();
        maxHeight -= graphPanel.getAbsoluteTop();
        maxHeight -= PaperStyleManager.getDP(16);
        maxHeight -= PaperStyleManager.getDP(16);
        graphPanel.getElement().getStyle().setHeight(maxHeight, Style.Unit.PX);
        int width = graphPanel.getElement().getClientWidth();

        if (currentStartDate == null) {
            currentStartDate = minTime;
        }

        //Recreate the graph on the specified start date.
        BarGraph barGraph = new BarGraph(width, maxHeight, minTime, maxTime, (int) tickInterval, dateFormatter, graphAxisList, areaAxis, areaDataPoints);
        barGraph.changeStartDate(currentStartDate);

        graphPanel.add(barGraph);

    }




    interface DefaultBinder extends UiBinder<Widget, GraphingViewImpl> {
    }

}
