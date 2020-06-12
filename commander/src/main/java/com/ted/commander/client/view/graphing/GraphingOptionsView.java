/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.graphing;


import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.petecode.common.client.model.DateRange;
import com.petecode.common.client.widget.paper.HasValidation;
import com.ted.commander.client.model.DataExportDataPoint;
import com.ted.commander.common.enums.ExportGraphType;
import com.ted.commander.common.enums.GraphLineType;
import com.ted.commander.common.enums.HistoryType;
import com.ted.commander.common.model.AccountLocation;

import java.util.List;


public interface GraphingOptionsView extends IsWidget {

    void setLogo(ImageResource imageResource);

    void setPresenter(GraphingOptionsView.Presenter presenter);

    HasValidation<AccountLocation> getLocationPicker();
    HasValue<HistoryType> getHistoryType();
    HasValue<DateRange> getDateRangeField();
    HasValue<ExportGraphType> getExportGraphType();
    HasValue<GraphLineType> getGraphLineType();
    List<DataExportDataPoint> getDataExportDataPoints();
    List<DataExportDataPoint> getSubloadDataExportDataPoints();
    HasValue<Boolean> graphSubloadField();

    void setNoneRadioFieldVisible(boolean visible);
    void setNetRadioFieldVisible(boolean visible);
    void setGenRadioFieldVisible(boolean visible);
    void setLoadRadioFieldVisible(boolean visible);
    void setWeatherRadioFieldVisible(boolean visible);

    HasValue<Boolean> allSubloadField();

    void setSubloadPanelVisibility(boolean visible);
    void setSubloadDataPointPanelVisibility(boolean visible);


    void setLoadingVisible(boolean isVisible);
    void setLocationList(List<AccountLocation> accountLocationList);
    void scrollToTop();

    public interface Presenter extends com.ted.commander.client.presenter.Presenter {
        void doGraph();
        void changeLocation(AccountLocation accountLocation);
        void cancel();
    }
}
