/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.energyPlan;

import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.petecode.common.client.widget.ValueChangeManager;
import com.ted.commander.client.clientFactory.ClientFactory;
import com.ted.commander.client.resources.WebStringResource;
import com.ted.commander.common.model.EnergyPlan;
import com.ted.commander.common.model.EnergyPlanSeason;

import java.util.Date;
import java.util.logging.Logger;

public class SeasonPresenter implements SeasonView.Presenter, IsWidget, HasValueChangeHandlers<EnergyPlanSeason> {

    static final Logger LOGGER = Logger.getLogger(SeasonPresenter.class.getName());

    final ClientFactory clientFactory;
    final SeasonView view;


    final EnergyPlan energyPlan;
    final EnergyPlanSeason energyPlanSeason;
    ValueChangeManager<EnergyPlanSeason> valueChangeManager = new ValueChangeManager<>();


    public SeasonPresenter(final ClientFactory clientFactory, final EnergyPlan energyPlan, final EnergyPlanSeason energyPlanSeason) {
        LOGGER.fine("CREATING NEW EnergyPlanListPresenter");
        this.clientFactory = clientFactory;
        this.energyPlan = energyPlan;
        this.energyPlanSeason = energyPlanSeason;
        view = clientFactory.getSeasonView();
        view.setPresenter(this);
        view.seasonName().setValue(energyPlanSeason.getSeasonName());

        Date date = new Date(1,1,114);
        date.setDate(energyPlanSeason.getSeasonDayOfMonth());
        date.setMonth(energyPlanSeason.getSeasonMonth());
        CalendarUtil.resetTime(date);

        view.seasonDate().setValue(date);

        valueChangeManager.setValue(energyPlanSeason, false);

        view.seasonName().addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> valueChangeEvent) {
                if (isValid()) {
                    energyPlanSeason.setSeasonName(view.seasonName().getValue());
                    valueChangeManager.setValue(energyPlanSeason, true);
                }

            }
        });

        view.seasonDate().addValueChangeHandler(new ValueChangeHandler<Date>() {
            @Override
            public void onValueChange(ValueChangeEvent<Date> valueChangeEvent) {
                if (isValid()) {
                    energyPlanSeason.setSeasonMonth(view.seasonDate().getValue().getMonth());
                    energyPlanSeason.setSeasonDayOfMonth(view.seasonDate().getValue().getDate());
                    valueChangeManager.setValue(energyPlanSeason, true);
                }
            }
        });
    }

    public boolean isValid() {
        boolean valid = true;
        view.seasonName().setInvalid("");
        if (view.seasonName().getValue().isEmpty()){
            view.seasonName().setInvalid(WebStringResource.INSTANCE.requiredField());
            valid = false;
        }
        return valid;
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }


    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<EnergyPlanSeason> valueChangeHandler) {
        return valueChangeManager.addValueChangeHandler(valueChangeHandler);
    }

    @Override
    public void fireEvent(GwtEvent<?> gwtEvent) {
        valueChangeManager.fireEvent(gwtEvent);
    }
}
