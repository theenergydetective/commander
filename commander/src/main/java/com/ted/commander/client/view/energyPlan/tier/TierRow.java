package com.ted.commander.client.view.energyPlan.tier;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.petecode.common.client.events.ItemChangedEvent;
import com.petecode.common.client.events.ItemChangedHandler;
import com.ted.commander.client.model.Instance;
import com.ted.commander.client.widgets.NumericTextBox;
import com.ted.commander.common.model.EnergyPlan;
import com.ted.commander.common.model.EnergyPlanTier;

import java.util.logging.Logger;

public class TierRow extends Composite {

    static Logger LOGGER = Logger.getLogger(TierRow.class.toString());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);

    final HandlerManager handlerManager = new HandlerManager(this);
    final EnergyPlanTier energyPlanTier;
    final EnergyPlan energyPlan;
    @UiField
    NumericTextBox toBox;
    @UiField
    DivElement fromLabel;
    @UiField
    DivElement tierLabel;
    NumberFormat numberFormat = NumberFormat.getFormat("0");

    public TierRow(final Instance instance, final EnergyPlan energyPlan, final EnergyPlanTier energyPlanTier) {
        switch (instance.getSkin()) {
            default:
                initWidget(defaultBinder.createAndBindUi(this));
        }

        this.energyPlanTier = energyPlanTier;
        this.energyPlan = energyPlan;

        tierLabel.setInnerText("Tier " + (energyPlanTier.getId() + 1));
        if (energyPlanTier.getId() == (energyPlan.getNumberTier() - 1)) toBox.setVisible(false);

        toBox.setValue(energyPlanTier.getKwh() / 1000.0);

        toBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent changeEvent) {
                energyPlanTier.setKwh((long) (toBox.getDoubleValue() * 1000.0));
                handlerManager.fireEvent(new ItemChangedEvent<EnergyPlanTier>(energyPlanTier));
            }
        });
    }

    public long getValue() {
        return energyPlanTier.getKwh();
    }

    public void addChangeHandler(ItemChangedHandler<EnergyPlanTier> handler) {
        handlerManager.addHandler(ItemChangedEvent.TYPE, handler);
    }

    public void updateMinValue(long newMin) {
        fromLabel.setInnerText(numberFormat.format((float) newMin / 1000.0));


        if (energyPlanTier.getKwh() <= newMin) {
            energyPlanTier.setKwh(newMin + 1);
            toBox.setValue(energyPlanTier.getKwh() / 1000.0);
        } else {
            toBox.setMinValue(((float) (newMin + 1)) / 1000.0);
        }
    }

    interface MyUiBinder extends UiBinder<Widget, TierRow> {
    }


    //Skin mapping
    interface DefaultBinder extends UiBinder<Widget, TierRow> {
    }


}
