/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.dashboard;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.IsWidget;
import com.ted.commander.client.model.DashboardDateRange;
import com.ted.commander.client.model.DashboardOptions;
import com.ted.commander.common.model.AccountLocation;
import com.ted.commander.common.model.CalendarKey;
import com.ted.commander.common.model.DashboardResponse;
import com.ted.commander.common.model.VirtualECC;

import java.util.List;

public interface DashboardView extends IsWidget {

    void setLogo(ImageResource imageResource);

    void setPresenter(DashboardView.Presenter presenter);


    void setDateRage(DashboardDateRange dashboardDateRange);

    void setLocation(VirtualECC location);

    void setOptions(DashboardOptions dashboardOptions);

    void clearCalendar();
    void setCalendarValue(CalendarKey key, List<String> value, Double gradient, String color);
    void setSummary(DashboardResponse dashboardResponse);


    void showLocationSelector(List<AccountLocation> accountLocationList);

    void setOptionSelector(VirtualECC virtualECC,DashboardOptions dashboardOptions);

    void setLoadingVisible(boolean visible);

    void onResize();

    void showMaintenanceDialog();

    void showNoNetError();

    void clearCalendarValues();



    public interface Presenter extends com.ted.commander.client.presenter.Presenter {
        void queryLocations();

        void queryOptions();

        void queryLocation(long locationId, DashboardDateRange dashboardDateRange, DashboardOptions dashboardOptions);

        void queryDailyDisplay(CalendarKey dailyDisplayKey);

    }


}
