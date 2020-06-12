package com.ted.commander.client.view.energyPlan.rates;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.petecode.common.client.events.ItemChangedEvent;
import com.petecode.common.client.events.ItemChangedHandler;
import com.ted.commander.client.resources.WebStringResource;
import com.ted.commander.common.enums.PlanType;
import com.ted.commander.common.model.EnergyPlan;
import com.ted.commander.common.model.EnergyRate;

import java.util.List;
import java.util.logging.Logger;

public class EnergyRateRow extends Composite {

    static Logger LOGGER = Logger.getLogger(EnergyRateRow.class.toString());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);

    final HandlerManager handlerManager = new HandlerManager(this);

    @UiField
    Label rowLabel;
    @UiField
    HorizontalPanel cellPanel;

    public EnergyRateRow(final EnergyPlan energyPlan, final Integer tier, List<EnergyRate> energyRateList) {
        initWidget(defaultBinder.createAndBindUi(this));


        boolean showTierLabel = energyPlan.getPlanType().equals(PlanType.TIER) || energyPlan.getPlanType().equals(PlanType.TIERTOU);
        boolean hasTOU = energyPlan.getPlanType().equals(PlanType.TOU) || energyPlan.getPlanType().equals(PlanType.TIERTOU);

        rowLabel.setVisible(showTierLabel);
        if (showTierLabel) rowLabel.setText(WebStringResource.INSTANCE.tier() + " " + (tier + 1));

        if (hasTOU) {
            //Add multiple elements per TOU type
            for (EnergyRate energyRate : energyRateList) {
                energyRate.setEnergyPlanId(energyPlan.getId());
                EnergyRateCell energyRateCell = new EnergyRateCell(energyPlan, energyRate);
                energyRateCell.addChangeHandler(new ItemChangedHandler<EnergyPlan>() {
                    @Override
                    public void onChanged(ItemChangedEvent<EnergyPlan> event) {
                        handlerManager.fireEvent(event);
                    }
                });


                cellPanel.add(energyRateCell);
            }
        } else {
            EnergyRateCell energyRateCell = new EnergyRateCell(energyPlan, energyRateList.get(0));
            energyRateCell.addChangeHandler(new ItemChangedHandler<EnergyPlan>() {
                @Override
                public void onChanged(ItemChangedEvent<EnergyPlan> event) {
                    handlerManager.fireEvent(event);
                }
            });
            cellPanel.add(energyRateCell);
        }
    }

    public void addChangeHandler(ItemChangedHandler<EnergyPlan> itemChangedHandler) {
        handlerManager.addHandler(ItemChangedEvent.TYPE, itemChangedHandler);
    }

    //Skin mapping

    interface DefaultBinder extends UiBinder<Widget, EnergyRateRow> {
    }

}
