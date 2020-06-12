package com.ted.commander.client.view.energyPlan.tou;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.petecode.common.client.events.ItemChangedEvent;
import com.petecode.common.client.events.ItemChangedHandler;
import com.petecode.common.client.widget.paper.PaperInputDecorator;
import com.ted.commander.client.resources.WebStringResource;
import com.ted.commander.common.enums.TOUPeakType;
import com.ted.commander.common.model.EnergyPlan;
import com.ted.commander.common.model.EnergyPlanSeason;
import com.ted.commander.common.model.EnergyPlanTOU;
import com.ted.commander.common.model.EnergyPlanTOULevel;

import java.util.logging.Logger;

public class TOURow extends Composite {

    static Logger LOGGER = Logger.getLogger(TOURow.class.toString());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);
    final HandlerManager handlerManager = new HandlerManager(this);
    final EnergyPlan energyPlan;
    final EnergyPlanSeason energyPlanSeason;
    final EnergyPlanTOU amEnergyPlanTOU;
    final EnergyPlanTOU pmEnergyPlanTOU;
    @UiField
    PaperInputDecorator touLabel;
    @UiField
    DivElement offPeakPanel;

    @UiField(provided = true)
    TOURowField amTOU;
    @UiField(provided = true)
    TOURowField pmTOU;
    @UiField
    DivElement peakPanel;
    ItemChangedHandler<EnergyPlanTOU> touLevelChangeHandler = new ItemChangedHandler<EnergyPlanTOU>() {
        @Override
        public void onChanged(ItemChangedEvent<EnergyPlanTOU> event) {

            balanceTOU();
            handlerManager.fireEvent(new ItemChangedEvent<EnergyPlan>(energyPlan));


        }
    };
    private boolean valid;


    public TOURow( final EnergyPlan energyPlan, final EnergyPlanSeason energyPlanSeason, final EnergyPlanTOU amEnergyPlanTOU, final EnergyPlanTOU pmEnergyPlanTOU) {
        amTOU = new TOURowField(amEnergyPlanTOU);
        pmTOU = new TOURowField(pmEnergyPlanTOU);
        amTOU.addChangeHandler(touLevelChangeHandler);
        pmTOU.addChangeHandler(touLevelChangeHandler);


        initWidget(defaultBinder.createAndBindUi(this));

        this.energyPlan = energyPlan;
        this.energyPlanSeason = energyPlanSeason;
        this.amEnergyPlanTOU = amEnergyPlanTOU;
        this.pmEnergyPlanTOU = pmEnergyPlanTOU;

        touLabel.setValue(energyPlan.getTouLevels().get(amEnergyPlanTOU.getPeakType().ordinal()).getTouLevelName());
        touLabel.setDisabled((energyPlanSeason.getId() != 0));
        touLabel.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> stringValueChangeEvent) {
                if (touLabel.getValue().trim().length() > 0) {
                    EnergyPlanTOULevel energyPlanTOULevel = energyPlan.getTouLevels().get(amEnergyPlanTOU.getPeakType().ordinal());
                    energyPlanTOULevel.setTouLevelName(touLabel.getValue());
                    handlerManager.fireEvent(new ItemChangedEvent<EnergyPlanTOULevel>(energyPlanTOULevel));
                } else {
                    EnergyPlanTOULevel energyPlanTOULevel = energyPlan.getTouLevels().get(amEnergyPlanTOU.getPeakType().ordinal());
                    touLabel.setValue(energyPlanTOULevel.getTouLevelName());
                }
            }
        });


        balanceTOU();

        boolean peakVisible = !amEnergyPlanTOU.getPeakType().equals(TOUPeakType.OFF_PEAK);

        if (peakVisible) {
            peakPanel.getStyle().clearDisplay();
            offPeakPanel.getStyle().setDisplay(Style.Display.NONE);
        } else {
            offPeakPanel.getStyle().clearDisplay();
            peakPanel.getStyle().setDisplay(Style.Display.NONE);
        }
    }

    public void addChangeHandler(ItemChangedHandler<EnergyPlan> handler) {
        handlerManager.addHandler(ItemChangedEvent.TYPE, handler);
    }



    private void balanceTOU() {

        amTOU.energyPlanTOU.balance();
        pmTOU.energyPlanTOU.balance();
        if (pmTOU.energyPlanTOU.before(amTOU.energyPlanTOU)){
            pmTOU.energyPlanTOU.forceAfter(amTOU.energyPlanTOU);
        }
        amTOU.refresh();
        pmTOU.refresh();

    }

    public boolean isValid() {
        if (energyPlanSeason.getId() > 0) return true;
        touLabel.setInvalid("");
        if (touLabel.getValue().trim().length() == 0) {
            touLabel.setValue(WebStringResource.INSTANCE.requiredField());
            return false;
        }
        return true;

    }

    //Skin mapping
    interface DefaultBinder extends UiBinder<Widget, TOURow> {
    }
}
