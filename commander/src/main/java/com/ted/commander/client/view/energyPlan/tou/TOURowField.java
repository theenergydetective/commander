package com.ted.commander.client.view.energyPlan.tou;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.petecode.common.client.events.ItemChangedEvent;
import com.petecode.common.client.events.ItemChangedHandler;
import com.ted.commander.client.widgets.timePicker.TimePicker;
import com.ted.commander.common.model.EnergyPlanTOU;

import java.util.logging.Logger;

public class TOURowField extends Composite {

    static Logger LOGGER = Logger.getLogger(TOURowField.class.toString());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);
    final HandlerManager handlerManager = new HandlerManager(this);
    final EnergyPlanTOU energyPlanTOU;
    @UiField
    HorizontalPanel peakPanel;
    @UiField
    TimePicker touEndField;
    @UiField
    TimePicker touStartField;


    ValueChangeHandler valueChangeHandler = new ValueChangeHandler() {
        @Override
        public void onValueChange(ValueChangeEvent valueChangeEvent) {
            energyPlanTOU.setTouStartHour(touStartField.getHour());
            energyPlanTOU.setTouStartMinute(touStartField.getMinute());
            energyPlanTOU.setTouEndHour(touEndField.getHour());
            energyPlanTOU.setTouEndMinute(touEndField.getMinute());
             handlerManager.fireEvent(new ItemChangedEvent<EnergyPlanTOU>(energyPlanTOU));
        }
    };

    public TOURowField(final EnergyPlanTOU energyPlanTOU) {

        initWidget(defaultBinder.createAndBindUi(this));
        this.energyPlanTOU = energyPlanTOU;
        touStartField.addChangeHandler(valueChangeHandler);
        touEndField.addChangeHandler(valueChangeHandler);

        refresh();
    }

    public void addChangeHandler(ItemChangedHandler<EnergyPlanTOU> handler) {
        handlerManager.addHandler(ItemChangedEvent.TYPE, handler);
    }

    public void refresh() {
        touStartField.setTime(energyPlanTOU.getTouStartHour(), energyPlanTOU.getTouStartMinute());
        touEndField.setTime(energyPlanTOU.getTouEndHour(), energyPlanTOU.getTouEndMinute());
    }

    //Skin mapping
    @UiTemplate("TOURowField.ui.xml")
    interface DefaultBinder extends UiBinder<Widget, TOURowField> {
    }
}
