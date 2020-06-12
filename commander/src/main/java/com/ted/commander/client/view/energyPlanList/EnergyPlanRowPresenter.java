
package com.ted.commander.client.view.energyPlanList;


import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.petecode.common.client.events.ItemSelectedEvent;
import com.petecode.common.client.events.ItemSelectedHandler;
import com.petecode.common.client.logging.ConsoleLoggerFactory;
import com.ted.commander.client.clientFactory.ClientFactory;
import com.ted.commander.common.model.EnergyPlanKey;

import java.util.logging.Logger;


public class EnergyPlanRowPresenter implements EnergyPlanRowView.Presenter, IsWidget {

    static final Logger LOGGER = ConsoleLoggerFactory.getLogger(EnergyPlanRowPresenter.class);

    final ClientFactory clientFactory;
    final EnergyPlanRowView view;
    final EnergyPlanKey energyPlanKey;

    HandlerManager handlerManager = new HandlerManager(this);

    public EnergyPlanKey getEnergyPlanKey() {
        return energyPlanKey;
    }

    public EnergyPlanRowPresenter(final ClientFactory clientFactory, final EnergyPlanKey energyPlanKey) {
        this.clientFactory = clientFactory;
        this.energyPlanKey = energyPlanKey;
        this.view = clientFactory.getEnergyPlanRowView();
        view.setPresenter(this);
        view.setName(energyPlanKey.getDescription());
        view.setDescription(energyPlanKey.getPlanType() + " " + energyPlanKey.getUtilityName());
    }


    @Override
    public Widget asWidget() {
        return view.asWidget();
    }


    @Override
    public void select() {
        LOGGER.fine("Firing selected event " + energyPlanKey);
        handlerManager.fireEvent(new ItemSelectedEvent<EnergyPlanKey>(energyPlanKey));
    }

    @Override
    public HandlerRegistration addItemSelectedHandler(ItemSelectedHandler<EnergyPlanKey> handler){
        return handlerManager.addHandler(ItemSelectedEvent.TYPE, handler);
    }


}
