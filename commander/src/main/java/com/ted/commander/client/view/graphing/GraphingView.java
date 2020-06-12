/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.graphing;


import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.IsWidget;
import com.ted.commander.client.widgets.graph.formatter.DateFormatter;
import com.ted.commander.client.widgets.graph.model.GraphAxis;
import com.ted.commander.client.widgets.graph.model.GraphDataPoints;
import com.ted.commander.client.widgets.graph.model.PiePoint;
import com.ted.commander.common.model.CalendarKey;

import java.util.List;

public interface GraphingView extends IsWidget {

    void setLogo(ImageResource imageResource);

    void setLocationName(String text);
    void setDateRange(String text);
    int getSelectedTab();

    void setLoadingVisible(boolean isVisible);
    void setPresenter(GraphingView.Presenter presenter);
    void drawLineGraph(CalendarKey minTime, CalendarKey maxTime, long tickInterval, DateFormatter dateFormatter,  List<GraphAxis> axisList, GraphAxis areaAxis, GraphDataPoints areaDataPoints);

    void drawPie(List<PiePoint> piePointListo);

    void drawBarGraph(CalendarKey startDate, CalendarKey endDate, long tickValue, DateFormatter dateFormatter, List<GraphAxis> graphAxisList, GraphAxis areaGraphAxis, GraphDataPoints areaGraphDataPoints);

    public interface Presenter extends com.ted.commander.client.presenter.Presenter {
       void changeTab(int selected);
    }
}
