/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.comparisonGraph;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.IsWidget;
import com.ted.commander.client.enums.ComparisonGraphType;
import com.ted.commander.common.enums.HistoryType;
import com.ted.commander.common.model.ComparisonQueryRequest;
import com.ted.commander.common.model.ComparisonQueryResponse;
import com.ted.commander.common.model.history.History;

import java.util.List;

public interface ComparisonGraphView extends IsWidget {

    void setLogo(ImageResource imageResource);

    void setPresenter(ComparisonGraphView.Presenter presenter);

    void setLoadingVisible(boolean v);

    void setMode(HistoryType minuteMode);

    void setTitleField(String txt);


    void drawGraph(ComparisonQueryRequest comparisonQueryRequest, ComparisonQueryResponse response, List<History>[] historyList, String currencyCode);

    void drawGraph();

    void setGraphDataType(ComparisonGraphType power);



    public interface Presenter extends com.ted.commander.client.presenter.Presenter {


    }

}
