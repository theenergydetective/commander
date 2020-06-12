/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.comparison;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.petecode.common.client.model.DateRange;
import com.ted.commander.common.enums.HistoryType;
import com.ted.commander.common.model.AccountLocation;
import com.ted.commander.common.model.VirtualECC;

import java.util.List;

public interface ComparisonView extends IsWidget {

    void setLogo(ImageResource imageResource);

    void setPresenter(ComparisonView.Presenter presenter);

    void setLoadingVisible(boolean visible);

    public void showLocationSelector(List<AccountLocation> accountLocationList);

    public void setLocations(List<VirtualECC> accountLocationList);

    HasValue<DateRange> datePicker();

    HasValue<HistoryType> getUnitOfTime();


    public interface Presenter extends com.ted.commander.client.presenter.Presenter {
        void queryLocations();

        void addLocation(VirtualECC accountLocation);

        void removeLocation(VirtualECC accountLocation);

        void generate();
    }

}
