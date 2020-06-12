/*
 * Copyright (c) 2014. The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.energyPlan.tou;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.petecode.common.client.events.ItemChangedEvent;
import com.petecode.common.client.events.ItemChangedHandler;
import com.ted.commander.client.resources.WebStringResource;
import com.ted.commander.common.enums.TOUPeakType;
import com.ted.commander.common.model.EnergyPlan;
import com.ted.commander.common.model.EnergyPlanSeason;
import com.ted.commander.common.model.EnergyPlanTOULevel;

import java.util.logging.Logger;


public class TOUPanel extends Composite {

    static final Logger LOGGER = Logger.getLogger(TOUPanel.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);

    private final HandlerManager handlerManager = new HandlerManager(this);


    @UiField
    FlowPanel contentPanel;

    EnergyPlan energyPlan;

    public TOUPanel(EnergyPlan energyPlan) {
        initWidget(defaultBinder.createAndBindUi(this));
        setEnabled(false);
        setActiveEnergyPlan(energyPlan);
    }


    public void setActiveEnergyPlan(final EnergyPlan energyPlan) {
        setEnabled(false);
        this.energyPlan = energyPlan;

        contentPanel.clear();

        if (energyPlan.getNumberTOU() ==1) energyPlan.setNumberTOU(2);

        while (energyPlan.getTouLevels().size() < energyPlan.getNumberTOU()) {
            EnergyPlanTOULevel energyPlanTOULevel = new EnergyPlanTOULevel();
            int peakTypeOrdinal = energyPlan.getTouLevels().size();
            energyPlanTOULevel.setPeakType(TOUPeakType.values()[peakTypeOrdinal]);
            switch (energyPlanTOULevel.getPeakType()) {
                case OFF_PEAK:
                    energyPlanTOULevel.setTouLevelName(WebStringResource.INSTANCE.defaultTOUNameOffPeak());
                    break;
                case PEAK:
                    energyPlanTOULevel.setTouLevelName(WebStringResource.INSTANCE.defaultTOUNamePeak());
                    break;
                case MID_PEAK:
                    energyPlanTOULevel.setTouLevelName(WebStringResource.INSTANCE.defaultTOUNameMidPeak());
                    break;
                case SUPER_PEAK:
                    energyPlanTOULevel.setTouLevelName(WebStringResource.INSTANCE.defaultTOUNameSuperPeak());
                    break;
            }
            energyPlan.getTouLevels().add(energyPlanTOULevel);
        }

        for (EnergyPlanSeason energyPlanSeason : energyPlan.getSeasonList()) {
            TOUSeason touSeason = new TOUSeason(energyPlan, energyPlanSeason);
            contentPanel.add(touSeason);
            touSeason.addChangeHandler(new ItemChangedHandler<EnergyPlan>() {
                @Override
                public void onChanged(ItemChangedEvent<EnergyPlan> event) {

                    handlerManager.fireEvent(event);
                }
            });
        }
        setEnabled(true);

    }

    public boolean isValid() {
        boolean valid = true;
        for (int i = 0; i < contentPanel.getWidgetCount(); i++)
            if (!((TOUSeason) contentPanel.getWidget(i)).isValid()) {
                return false;
            }
        return valid;
    }

    public void setEnabled(boolean enabled) {

        for (int i = 0; i < contentPanel.getWidgetCount(); i++)
            ((TOUSeason) contentPanel.getWidget(i)).setEnabled(enabled);
    }

    public void addChangeHandler(ItemChangedHandler<EnergyPlan> handler) {
        handlerManager.addHandler(ItemChangedEvent.TYPE, handler);
    }

    interface DefaultBinder extends UiBinder<Widget, TOUPanel> {
    }


}

