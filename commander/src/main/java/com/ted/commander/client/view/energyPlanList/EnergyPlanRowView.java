package com.ted.commander.client.view.energyPlanList;


import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.IsWidget;
import com.petecode.common.client.events.ItemSelectedHandler;
import com.ted.commander.common.model.EnergyPlanKey;


public interface EnergyPlanRowView extends IsWidget {

    void setPresenter(Presenter presenter);
    void setName(String name);
    void setDescription(String description);


    interface Presenter  {
        void select();
        HandlerRegistration addItemSelectedHandler(ItemSelectedHandler<EnergyPlanKey> handler);
    }
}
