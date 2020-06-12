/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.billing;


import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.petecode.common.client.model.DateRange;
import com.ted.commander.common.enums.DataExportFileType;
import com.ted.commander.common.enums.HistoryType;
import com.ted.commander.common.model.AccountLocation;

import java.util.List;


public interface BillingView extends IsWidget {

    void setLogo(ImageResource imageResource);

    void setPresenter(BillingView.Presenter presenter);

    HasValue<DateRange> getDateRangeField();
    HasValue<DataExportFileType> getDataExportFileType();
    List<AccountLocation> getLocationsForExport();
    HasValue<HistoryType> getHistoryType();

    void setLoadingVisible(boolean isVisible);
    void setLocationList(List<AccountLocation> accountLocationList);
    void setDownloadUrl(String url);
    void scrollToTop();
    void clearDataExportPoints();

    public interface Presenter extends com.ted.commander.client.presenter.Presenter {
        void doExport();
    }
}
