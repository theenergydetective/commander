package com.ted.commander.client.view.energyPlan;


import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.petecode.common.client.widget.paper.HasValidation;

import java.util.Date;


public interface SeasonView extends IsWidget {

    void setPresenter(Presenter presenter);

    HasValidation<String> seasonName();
    HasValue<Date> seasonDate();

    interface Presenter  {

    }
}
