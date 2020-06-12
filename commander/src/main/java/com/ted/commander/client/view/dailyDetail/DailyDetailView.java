/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.dailyDetail;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.IsWidget;
import com.ted.commander.common.model.CalendarKey;
import com.ted.commander.common.model.DailySummary;
import com.ted.commander.common.model.EnergyPlan;

public interface DailyDetailView extends IsWidget {

    void setLogo(ImageResource imageResource);

    void setPresenter(DailyDetailView.Presenter presenter);

    void setSummary(EnergyPlan energyPlan, DailySummary summary);

    void setLoadingVisible(boolean visible);

    int getSelectedTab();

    public interface Presenter extends com.ted.commander.client.presenter.Presenter {

        public void query();

        public void query(CalendarKey calendarKey);
    }

    void onResize();

}
