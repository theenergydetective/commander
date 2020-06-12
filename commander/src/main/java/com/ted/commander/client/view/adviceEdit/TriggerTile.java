/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.adviceEdit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.petecode.common.client.events.ItemSelectedEvent;
import com.petecode.common.client.events.ItemSelectedHandler;
import com.ted.commander.client.enums.CommanderFormats;
import com.ted.commander.client.util.CurrencyUtil;
import com.ted.commander.client.util.MathUtil;
import com.ted.commander.common.enums.HistoryType;
import com.ted.commander.common.enums.SendAtMostType;
import com.ted.commander.common.model.AdviceTrigger;
import com.ted.commander.common.model.EnergyPlan;
import com.vaadin.polymer.elemental.Event;
import com.vaadin.polymer.elemental.EventListener;
import com.vaadin.polymer.paper.element.PaperMaterialElement;

import java.util.logging.Logger;


public class TriggerTile extends Composite {

    static final Logger LOGGER = Logger.getLogger(TriggerTile.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);

    final HandlerManager handlerManager = new HandlerManager(this);
    @UiField
    PaperMaterialElement mainPanel;
    @UiField
    DivElement textField;

    static NumberFormat decimalFormat = NumberFormat.getFormat("0.0##");

    public TriggerTile(final AdviceTrigger trigger, final EnergyPlan energyPlan) {
        initWidget(defaultBinder.createAndBindUi(this));

        StringBuilder text = new StringBuilder();
        switch (trigger.getTriggerType()){
            case NEW_TOU_RATE:{
                text.append("send advice  ");
                text.append(insertHighlight(trigger.getMinutesBefore() + ""));
                text.append(" minutes before a new TOU Rate. ");
                break;
            }
            case RATE_CHANGE: {
                text.append("send advice when a rate change occurs. ");
                break;
            }
            case NEW_DEMAND_CHARGE: {
                text.append("send advice when a new demand charge occurs. Wait until ")
                        .append(insertHighlight(CommanderFormats.ADVICE_HOUR.format((double)trigger.getDelayMinutes()/60.0)))
                        .append(" hours have passed in the billing cycle ");
                if (!MathUtil.doubleEquals(0, trigger.getAmount(), .01)) {
                    text.append(" and a minimum amount of ");
                    text.append(insertHighlight(CurrencyUtil.format(energyPlan.getRateType(), trigger.getAmount())));
                    text.append(" has been reached");
                }
                text.append(". ");
                text.append(sendAtMost(trigger.getSendAtMost()));
                break;
            }

            case MONEY_SPENT: {
                text.append("send advice when money spent exceeds ");
                text.append(insertHighlight(CurrencyUtil.format(energyPlan.getRateType(), trigger.getAmount())));
                text.append(" since ");
                if (trigger.getSinceStart().equals(HistoryType.DAILY)) text.append(insertHighlight("Midnight. "));
                else text.append(insertHighlight("the Billing Cycle Start. "));
                text.append(sendAtMost(trigger.getSendAtMost()));
                break;
            }
            case ENERGY_CONSUMED: {
                text.append("send advice when NET energy consumed exceeds ");
                text.append(insertHighlight(decimalFormat.format(trigger.getAmount()) + " kWh"));
                text.append(" since ");
                if (trigger.getSinceStart().equals(HistoryType.DAILY)) text.append(insertHighlight("Midnight. "));
                else text.append(insertHighlight("the Billing Cycle Start. "));
                text.append(sendAtMost(trigger.getSendAtMost()));
                break;
            }
            case TEN_MINUTE_DEMAND: {
                text.append("send advice when 10-minute KW average exceeds ");
                text.append(insertHighlight(decimalFormat.format(trigger.getAmount()) + " kW"));
                text.append(". ");
                text.append(sendAtMost(trigger.getSendAtMost()));
                break;
            }
            case AVG_GENERATED: {
                text.append("send advice when real time power generated does not exceed ");
                text.append(insertHighlight(decimalFormat.format(trigger.getAmount()) + " kW"));
                text.append(" for ").append(insertHighlight(CommanderFormats.ADVICE_HOUR.format((double)trigger.getDelayMinutes()/60.0))).append(" hours. ");
                text.append(sendAtMost(trigger.getSendAtMost()));
                break;
            }
            case AVG_CONSUMED: {
                text.append("send advice when real time power consumed does not exceed ");
                text.append(insertHighlight(decimalFormat.format(trigger.getAmount()) + " kW"));
                text.append(" for ").append(insertHighlight(CommanderFormats.ADVICE_HOUR.format((double)trigger.getDelayMinutes()/60.0))).append(" hours. ");
                text.append(sendAtMost(trigger.getSendAtMost()));
                break;
            }
            case COMMANDER_NO_POST: {
                text.append("send advice when TED Commander has not received a post ");
                if (trigger.isAllMTUs()) text.append(" from all MTUs ");
                else text.append(" from any MTU ");
                text.append("for ").append(CommanderFormats.ADVICE_HOUR.format((double)trigger.getDelayMinutes()/60.0)).append(" hours. ");
                text.append(sendAtMost(trigger.getSendAtMost()));
                break;
            }
            case VOLTAGE_EXCEEDS: {
                text.append("send advice when the peak voltage exceeds ");
                text.append(insertHighlight((int)trigger.getAmount() + " V"));
                text.append(". ");
                text.append(sendAtMost(trigger.getSendAtMost()));
                break;
            }
            case VOLTAGE_DOES_NOT_EXCEED: {
                text.append("send advice when the minimum voltage does not exceed ");
                text.append(insertHighlight((int)trigger.getAmount() + " V"));
                text.append(". ");
                text.append(sendAtMost(trigger.getSendAtMost()));
                break;
            }
            case REAL_TIME_POWER: {
                text.append("send advice when the real time power exceeds ");
                text.append(insertHighlight(decimalFormat.format(trigger.getAmount()) + " kW"));
                text.append(". ");
                text.append(sendAtMost(trigger.getSendAtMost()));
                break;
            }
        }

        text.append(sendBetween(trigger));
        textField.setInnerHTML(text.toString());


        mainPanel.addEventListener("click", new EventListener() {
            @Override
            public void handleEvent(Event event) {
                handlerManager.fireEvent(new ItemSelectedEvent<AdviceTrigger>(trigger));
            }
        });

    }

    private String sendBetween(AdviceTrigger trigger){
        StringBuilder text = new StringBuilder();
        if (trigger.getStartTime() != trigger.getEndTime()) {
            text.append("Limit alerts between ");
            text.append(formatTime(trigger.getStartTime()));
            text.append(" and ");
            text.append(formatTime(trigger.getEndTime()));
        }
        return text.toString();
    }

    private String sendAtMost(SendAtMostType sendAtMostType){
        StringBuilder stringBuilder = new StringBuilder("Send at most once per ");
        switch(sendAtMostType){
            case MINUTE: stringBuilder.append(insertHighlight("minute")); break;
            case HOURLY: stringBuilder.append(insertHighlight("hour")); break;
            case DAILY: stringBuilder.append(insertHighlight("day")); break;
            case BILLING_CYCLE: stringBuilder.append(insertHighlight("billing cycle")); break;
        }
        stringBuilder.append(". ");
        return stringBuilder.toString();
    }

    private String formatTime(int m) {
        if (m==0) return insertHighlight("Midnight");
        if (m==720) return insertHighlight("Noon");
        if (m==1440) return insertHighlight("Next AM");

        int hour = m / 60;
        int min = m % 60;
        boolean isAm = m < 720;
        if (hour > 12) hour -= 12;
        if (hour == 0) hour = 12;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(hour).append(":");
        if (min < 10) stringBuilder.append("0");
        stringBuilder.append(min);
        if (isAm) stringBuilder.append(" AM");
        else stringBuilder.append(" PM");

        return insertHighlight(stringBuilder.toString());
    }

    String insertHighlight(String text){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<span style='color: #2962FF'>");
        stringBuilder.append(text);
        stringBuilder.append("</span>");
        return stringBuilder.toString();
    }


    public HandlerRegistration addItemSelectedHandler(ItemSelectedHandler<AdviceTrigger> selectedHandler) {
        return handlerManager.addHandler(ItemSelectedEvent.TYPE, selectedHandler);
    }

    interface DefaultBinder extends UiBinder<Widget, TriggerTile> {
    }


}
