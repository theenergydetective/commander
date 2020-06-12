package com.ted.commander.client.view.energyPlan.demandCharges;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.i18n.client.CurrencyData;
import com.google.gwt.i18n.client.CurrencyList;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.ted.commander.common.enums.DemandPlanType;
import com.ted.commander.common.enums.TOUPeakType;
import com.ted.commander.common.model.DemandChargeTOU;
import com.ted.commander.common.model.DemandChargeTier;
import com.ted.commander.common.model.EnergyPlan;
import com.ted.commander.common.model.EnergyPlanSeason;

import java.util.logging.Logger;

public class DemandChargeSeason extends Composite {

    static Logger LOGGER = Logger.getLogger(DemandChargeSeason.class.toString());
    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
    final HandlerManager handlerManager = new HandlerManager(this);
    final EnergyPlan energyPlan;
    final EnergyPlanSeason energyPlanSeason;
    @UiField
    CaptionPanel captionField;
    @UiField
    DivElement costHeader;
    @UiField
    DivElement stepHeader;
    @UiField
    VerticalPanel contentPanel;

    public DemandChargeSeason(EnergyPlan energyPlan, EnergyPlanSeason energyPlanSeason, ValueChangeHandler valueChangeHandler) {
        LOGGER.fine("ADDING DEMAND CHARGE SEASON");
        this.energyPlan = energyPlan;
        this.energyPlanSeason = energyPlanSeason;

        initWidget(uiBinder.createAndBindUi(this));
        captionField.setCaptionText(energyPlanSeason.getSeasonName());

        if (energyPlan.getDemandPlanType().equals(DemandPlanType.TIERED) || energyPlan.getDemandPlanType().equals(DemandPlanType.TIERED_PEAK)) {
            stepHeader.getStyle().clearDisplay();
        }

        CurrencyData currency = CurrencyList.get().lookup(energyPlan.getRateType());
        costHeader.setInnerText(currency.getSimpleCurrencySymbol() + "/kWh");


        if (!energyPlan.getDemandUseActivePower()) {
            stepHeader.setInnerText(stepHeader.getInnerText().replace("kW", "kVA"));
            costHeader.setInnerText(costHeader.getInnerText().replace("kW", "kVA"));
        }

        if (energyPlan.getDemandPlanType().equals(DemandPlanType.TOU)) {
            //Draw TOW
            LOGGER.fine("DRAWING TOU");
            for (int tou = 0; tou < energyPlan.getNumberTOU(); tou++) {
                if (energyPlanSeason.getDemandChargeTOUList().size() < energyPlan.getNumberTOU()) {
                    DemandChargeTOU demandChargeTOU = new DemandChargeTOU();
                    demandChargeTOU.setSeasonId(energyPlanSeason.getId());
                    demandChargeTOU.setPeakType(TOUPeakType.values()[tou]);
                    energyPlanSeason.getDemandChargeTOUList().add(demandChargeTOU);
                }
                DemandChargeTOU demandChargeTOU = energyPlanSeason.getDemandChargeTOUList().get(tou);
                DemandChargeTOURow row = new DemandChargeTOURow(energyPlan, demandChargeTOU);
                row.addValueChangeHandler(valueChangeHandler);
                contentPanel.add(row);
            }
        } else {
            LOGGER.fine("DRAWING TIERS");
            //Draw Step
            for (int step = 0; step < energyPlan.getNumberDemandSteps(); step++) {
                if (energyPlanSeason.getDemandChargeTierList().size() < energyPlan.getNumberDemandSteps()) {
                    DemandChargeTier demandChargeTier = new DemandChargeTier();
                    demandChargeTier.setId(energyPlanSeason.getDemandChargeTierList().size());
                    demandChargeTier.setSeasonId(energyPlanSeason.getId());
                    energyPlanSeason.getDemandChargeTierList().add(demandChargeTier);
                }
                DemandChargeTier demandChargeTier = energyPlanSeason.getDemandChargeTierList().get(step);
                contentPanel.add(new DemandChargeRow(energyPlan, demandChargeTier));
            }
        }

    }

    public void setEnabled(boolean enabled) {


    }



    interface MyUiBinder extends UiBinder<Widget, DemandChargeSeason> {
    }


}
