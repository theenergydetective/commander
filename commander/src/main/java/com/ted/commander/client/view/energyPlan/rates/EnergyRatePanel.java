/*
 * Copyright (c) 2014. The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.energyPlan.rates;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.petecode.common.client.events.ItemChangedEvent;
import com.petecode.common.client.events.ItemChangedHandler;
import com.ted.commander.common.model.EnergyPlan;
import com.ted.commander.common.model.EnergyPlanSeason;

import java.util.logging.Logger;


public class EnergyRatePanel extends Composite {

    static final Logger LOGGER = Logger.getLogger(EnergyRatePanel.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);
    private final HandlerManager handlerManager = new HandlerManager(this);

    @UiField
    FlowPanel contentPanel;
    EnergyPlan energyPlan;

    public EnergyRatePanel(EnergyPlan energyPlan) {
        initWidget(defaultBinder.createAndBindUi(this));
        setEnabled(false);
        setActiveEnergyPlan(energyPlan);
    }


    public void setActiveEnergyPlan(final EnergyPlan energyPlan) {
        setEnabled(false);
        this.energyPlan = energyPlan;

        contentPanel.clear();

        if (energyPlan != null) {
            for (EnergyPlanSeason energyPlanSeason : energyPlan.getSeasonList()) {
                EnergyRateSeason energyRateSeason = new EnergyRateSeason(energyPlan, energyPlanSeason);
                energyRateSeason.addChangeHandler(new ItemChangedHandler<EnergyPlan>(){
                    @Override
                    public void onChanged(ItemChangedEvent<EnergyPlan> event) {
                        handlerManager.fireEvent(event);
                    }
                });
                contentPanel.add(energyRateSeason);
            }
        }
        setEnabled(true);
    }

    public boolean isValid() {
        boolean valid = true;
        return valid;
    }

    public void setEnabled(boolean enabled) {
        for (int i = 0; i < contentPanel.getWidgetCount(); i++)
            ((EnergyRateSeason) contentPanel.getWidget(i)).setEnabled(enabled);
    }

    public void addChangeHandler(ItemChangedHandler<EnergyPlan> energyPlanItemChangedHandler) {
        handlerManager.addHandler(ItemChangedEvent.TYPE, energyPlanItemChangedHandler);
    }


    //Skin mapping

    interface DefaultBinder extends UiBinder<Widget, EnergyRatePanel> {
    }


}

