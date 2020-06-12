/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.adviceEdit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.petecode.common.client.events.*;
import com.ted.commander.client.view.adviceEdit.triggerTiles.*;
import com.ted.commander.common.enums.TriggerType;
import com.ted.commander.common.model.Advice;
import com.ted.commander.common.model.AdviceTrigger;
import com.ted.commander.common.model.EnergyPlan;
import com.vaadin.polymer.elemental.Event;
import com.vaadin.polymer.elemental.EventListener;
import com.vaadin.polymer.paper.element.PaperButtonElement;
import com.vaadin.polymer.paper.element.PaperMaterialElement;

import java.util.logging.Logger;


public class TriggerDialog extends PopupPanel {
    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
    final HandlerManager handlerManager = new HandlerManager(this);
    static final Logger LOGGER = Logger.getLogger(AdviceEditPresenter.class.getName());


    @UiField
    PaperButtonElement cancelButton;
    @UiField
    PaperButtonElement deleteButton;
    @UiField
    PaperButtonElement addButton;
    @UiField
    PaperButtonElement saveButton;

    @UiField
    SimplePanel triggerPanel;
    @UiField
    ListBox triggerTypeListBox;
    @UiField
    HTMLPanel mainPanel;

    @UiField
    PaperMaterialElement dialog;


    final AdviceTrigger adviceTrigger;
    final EnergyPlan energyPlan;


    public TriggerDialog(Advice advice, final AdviceTrigger at, boolean canEdit, EnergyPlan energyPlan) {
        setWidget(uiBinder.createAndBindUi(this));
        setAnimationEnabled(true);
        setAnimationType(PopupPanel.AnimationType.CENTER);
        setModal(true);
        setGlassEnabled(true);
        Style glassStyle = getGlassElement().getStyle();
        glassStyle.setProperty("width", "100%");
        glassStyle.setProperty("height", "100%");
        glassStyle.setProperty("backgroundColor", "#000");
        glassStyle.setProperty("opacity", "0.45");
        glassStyle.setProperty("mozOpacity", "0.45");
        glassStyle.setProperty("filter", " alpha(opacity=45)");
        dialog.getStyle().setProperty("zIndex", "2147483647");

        this.energyPlan = energyPlan;
        if (at == null) {
            this.adviceTrigger = new AdviceTrigger();
            this.adviceTrigger.setAdviceId(advice.getId());
        } else {
            this.adviceTrigger = at;
        }

        cancelButton.addEventListener("click", new EventListener() {
            @Override
            public void handleEvent(Event event) {
                hide();
            }
        });


        boolean showAdd = this.adviceTrigger.getId()==null || this.adviceTrigger.getId().equals(0l);

        addButton.getStyle().setProperty("display", showAdd?"":"none");
        addButton.addEventListener("click", new EventListener() {
            @Override
            public void handleEvent(Event event) {
                if (((TriggerPanel)triggerPanel.getWidget()).validate()){
                    ((TriggerPanel)triggerPanel.getWidget()).update();
                    handlerManager.fireEvent(new ItemCreatedEvent<AdviceTrigger>(adviceTrigger));
                }
            }
        });


        deleteButton.getStyle().setProperty("display", !showAdd?"":"none");
        deleteButton.addEventListener("click", new EventListener() {
            @Override
            public void handleEvent(Event event) {
                handlerManager.fireEvent(new ItemDeletedEvent<AdviceTrigger>(adviceTrigger));
            }
        });

        saveButton.getStyle().setProperty("display", !showAdd?"":"none");
        saveButton.addEventListener("click", new EventListener() {
            @Override
            public void handleEvent(Event event) {
                if (((TriggerPanel)triggerPanel.getWidget()).validate()){
                    ((TriggerPanel)triggerPanel.getWidget()).update();
                    handlerManager.fireEvent(new ItemChangedEvent<AdviceTrigger>(adviceTrigger));
                }
            }
        });





        triggerTypeListBox.addItem("When a rate change occurs", TriggerType.RATE_CHANGE.ordinal() + "");
        triggerTypeListBox.addItem("Before a new TOU Rate", TriggerType.NEW_TOU_RATE.ordinal() + "");
        triggerTypeListBox.addItem("When a new demand charge is reached", TriggerType.NEW_DEMAND_CHARGE.ordinal() + "");
        triggerTypeListBox.addItem("When money spent", TriggerType.MONEY_SPENT.ordinal() + "");
        triggerTypeListBox.addItem("When real time power", TriggerType.REAL_TIME_POWER.ordinal() + "");
        triggerTypeListBox.addItem("When NET energy consumed", TriggerType.ENERGY_CONSUMED.ordinal() + "");
        triggerTypeListBox.addItem("When 10-minute KW average", TriggerType.TEN_MINUTE_DEMAND.ordinal() + "");
        triggerTypeListBox.addItem("When real time power generated", TriggerType.AVG_GENERATED.ordinal() + "");
        triggerTypeListBox.addItem("When real time power does NOT exceed", TriggerType.AVG_CONSUMED.ordinal() + "");
        triggerTypeListBox.addItem("When peak voltage", TriggerType.VOLTAGE_EXCEEDS.ordinal() + "");
        triggerTypeListBox.addItem("When minimum voltage", TriggerType.VOLTAGE_DOES_NOT_EXCEED.ordinal() + "");
        triggerTypeListBox.addItem("When Commander has not received a post", TriggerType.COMMANDER_NO_POST.ordinal() + "");

        for (int i=0; i < triggerTypeListBox.getItemCount(); i++){
            if (triggerTypeListBox.getValue(i).equals(adviceTrigger.getTriggerType().ordinal() + "")){
                triggerTypeListBox.setSelectedIndex(i);
                break;
            }
        }






        dialog.addEventListener("change", new EventListener() {
            @Override
            public void handleEvent(Event event) {
                String value = triggerTypeListBox.getValue(triggerTypeListBox.getSelectedIndex());
                changeTrigger(TriggerType.values()[Integer.parseInt(value)], false);
                validate();
            }
        });
        String value = triggerTypeListBox.getValue(triggerTypeListBox.getSelectedIndex());
        changeTrigger(TriggerType.values()[Integer.parseInt(value)], true);
    }

    private boolean validate() {
        if (triggerPanel.getWidget() != null){
            ((TriggerPanel)triggerPanel.getWidget()).validate();
        }
        return true;
    }


    private void changeTrigger(TriggerType triggerType, boolean force) {
        if (force || !triggerType.equals(adviceTrigger.getTriggerType())) {
            adviceTrigger.setTriggerType(triggerType);
            triggerPanel.clear();
            switch (triggerType) {
                case NEW_TOU_RATE:
                    triggerPanel.setWidget(new NewTOURateTile(adviceTrigger, energyPlan));
                    break;
                case RATE_CHANGE:
                    triggerPanel.setWidget(new RateChangeTile(adviceTrigger));
                    break;
                case NEW_DEMAND_CHARGE:
                    triggerPanel.setWidget(new NewDemandChargeTile(adviceTrigger, energyPlan));
                    break;
                case MONEY_SPENT:
                    triggerPanel.setWidget(new MoneySpentTile(adviceTrigger, energyPlan));
                    break;
                case ENERGY_CONSUMED:
                    triggerPanel.setWidget(new EnergyConsumedTile(adviceTrigger, energyPlan));
                    break;
                case TEN_MINUTE_DEMAND:
                    triggerPanel.setWidget(new TenMinuteDemandTile(adviceTrigger, energyPlan));
                    break;
                case AVG_GENERATED:
                    triggerPanel.setWidget(new TenMinuteGeneratedTile(adviceTrigger));
                    break;
                case AVG_CONSUMED:
                    triggerPanel.setWidget(new TenMinuteConsumedTile(adviceTrigger));
                    break;
                case COMMANDER_NO_POST:
                    triggerPanel.setWidget(new CommanderNoPostTile(adviceTrigger));
                    break;
                case VOLTAGE_EXCEEDS:
                    triggerPanel.setWidget(new VoltageExceedsTile(adviceTrigger, energyPlan));
                    break;
                case VOLTAGE_DOES_NOT_EXCEED:
                    triggerPanel.setWidget(new VoltageDoesNotExceedTile(adviceTrigger, energyPlan));
                    break;
                case REAL_TIME_POWER:
                    triggerPanel.setWidget(new RealTimePowerTile(adviceTrigger, energyPlan));
                    break;

            }
        }
    }


    public void open() {
        center();
        show();

    }

    public void close() {
        hide();
    }

    interface MyUiBinder extends UiBinder<Widget, TriggerDialog> {
    }

    public HandlerRegistration addCreateHandler(ItemCreatedHandler<AdviceTrigger> handler){
        return handlerManager.addHandler(ItemCreatedEvent.TYPE, handler);
    }

    public HandlerRegistration addDeletedHandler(ItemDeletedHandler<AdviceTrigger> handler){
        return handlerManager.addHandler(ItemDeletedEvent.TYPE, handler);
    }

    public HandlerRegistration addChangeHandler(ItemChangedHandler<AdviceTrigger> handler){
        return handlerManager.addHandler(ItemChangedEvent.TYPE, handler);
    }


}
