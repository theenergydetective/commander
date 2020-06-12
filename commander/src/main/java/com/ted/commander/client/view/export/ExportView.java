/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.export;


import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.petecode.common.client.model.DateRange;
import com.petecode.common.client.widget.paper.HasValidation;
import com.ted.commander.client.model.DataExportDataPoint;
import com.ted.commander.common.enums.DataExportFileType;
import com.ted.commander.common.enums.HistoryType;
import com.ted.commander.common.model.AccountLocation;

import java.util.List;


public interface ExportView extends IsWidget {

    void setLogo(ImageResource imageResource);

    void setPresenter(ExportView.Presenter presenter);

    HasValidation<AccountLocation> getLocationPicker();
    HasValue<HistoryType> getHistoryType();
    HasValue<DateRange> getDateRangeField();
    HasValue<DataExportFileType> getDataExportFileType();
    List<DataExportDataPoint> getDataExportDataPoints();

    List<DataExportDataPoint> getSubloadDataExportDataPoints();
    HasValue<Boolean> graphSubloadField();
    HasValue<Boolean> allSubloadField();
    HasValue<Boolean> weatherField();
    HasValue<Boolean> demandCostField();

    void setSubloadPanelVisibility(boolean visible);
    void setSubloadDataPointPanelVisibility(boolean visible);




    void setLoadingVisible(boolean isVisible);
    void setLocationList(List<AccountLocation> accountLocationList);
    void setDownloadUrl(String url);

    void scrollToTop();

    void clearDataExportPoints();


    public interface Presenter extends com.ted.commander.client.presenter.Presenter {
        void doExport();
        void changeLocation(AccountLocation accountLocation);
    }
}
