/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.adviceEdit.triggerTiles;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.ted.commander.client.util.CurrencyUtil;
import com.ted.commander.common.enums.HistoryType;
import com.ted.commander.common.enums.SendAtMostType;
import com.ted.commander.common.model.AdviceTrigger;
import com.ted.commander.common.model.EnergyPlan;
import com.vaadin.polymer.paper.element.PaperInputElement;

import java.util.logging.Logger;


public class MoneySpentTile extends Composite implements TriggerPanel {

    static final Logger LOGGER = Logger.getLogger(MoneySpentTile.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);


    @UiField(provided = true)
    AlarmRangeWidget alarmRateWidget;


    @UiField
    ListBox atMostField;
    @UiField
    ListBox sinceField;
    @UiField
    PaperInputElement exceedsField;

    final EnergyPlan energyPlan;
    final AdviceTrigger adviceTrigger;

    public MoneySpentTile(final AdviceTrigger adviceTrigger, final EnergyPlan energyPlan) {
        alarmRateWidget = new AlarmRangeWidget(adviceTrigger);
        initWidget(defaultBinder.createAndBindUi(this));
        this.energyPlan = energyPlan;
        this.adviceTrigger = adviceTrigger;

        exceedsField.setLabel("Exceeds (" + CurrencyUtil.getCurrencySymbol(energyPlan.getRateType()) + ")");

        exceedsField.addEventListener("change", new RangeEventListener(0, 9999999, exceedsField));

        atMostField.setSelectedIndex(adviceTrigger.getSendAtMost().ordinal());


        if (adviceTrigger.getSinceStart().equals(HistoryType.DAILY)) sinceField.setSelectedIndex(0);
        else sinceField.setSelectedIndex(1);

        int amt = (int) adviceTrigger.getAmount();
        exceedsField.setValue(amt + "");

    }

    @Override
    public boolean validate() {
        alarmRateWidget.validate();

        exceedsField.setInvalid(false);

        if (exceedsField.getValue().trim().length() == 0) {
                exceedsField.setInvalid(true);
                exceedsField.setErrorMessage("Required");
        } else {
            try {
                Integer.parseInt(exceedsField.getValue());
            } catch (Exception ex){
                exceedsField.setInvalid(true);
                exceedsField.setErrorMessage("Must be a whole number");
            }
        }
        return alarmRateWidget.validate() && !exceedsField.getInvalid();
    }

    @Override
    public void update() {
        exceedsField.setInvalid(false);
        alarmRateWidget.update();
        adviceTrigger.setSendAtMost(SendAtMostType.values()[atMostField.getSelectedIndex()]);
        if (sinceField.getSelectedIndex() == 0) adviceTrigger.setSinceStart(HistoryType.DAILY);
        else adviceTrigger.setSinceStart(HistoryType.BILLING_CYCLE);
        adviceTrigger.setAmount(Integer.parseInt(exceedsField.getValue()));
    }

    interface DefaultBinder extends UiBinder<Widget, MoneySpentTile> {
    }


}
