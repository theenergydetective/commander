/*
 * Copyright (c) 2014. The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.energyPlan.additionalCharges;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.petecode.common.client.events.ItemChangedEvent;
import com.petecode.common.client.events.ItemChangedHandler;
import com.petecode.common.client.widget.PaperCheckboxElementManager;
import com.ted.commander.client.resources.WebStringResource;
import com.ted.commander.common.enums.AdditionalChargeType;
import com.ted.commander.common.model.AdditionalCharge;
import com.ted.commander.common.model.EnergyPlan;
import com.ted.commander.common.model.EnergyPlanSeason;
import com.vaadin.polymer.paper.element.PaperCheckboxElement;

import java.util.logging.Logger;


public class AdditionalChargePanel extends Composite {

    static final Logger LOGGER = Logger.getLogger(AdditionalChargePanel.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);
    final AdditionalChargeType additionalChargeType;

    private final HandlerManager handlerManager = new HandlerManager(this);
    @UiField
    FlowPanel contentPanel;
    @UiField
    PaperCheckboxElement questionCB;

    PaperCheckboxElementManager questionCBManager;

    @UiField
    DivElement questionField;
    EnergyPlan energyPlan;


    public AdditionalChargePanel(final AdditionalChargeType additionalChargeType, EnergyPlan energyPlan) {
        this.additionalChargeType = additionalChargeType;
        initWidget(defaultBinder.createAndBindUi(this));
        getElement().getStyle().setProperty("float", "left");
        getElement().getStyle().setMargin(8, Style.Unit.PX);

        questionCBManager = new PaperCheckboxElementManager(questionCB);

        setEnabled(false);
        setActiveEnergyPlan(energyPlan);
    }


    public void setActiveEnergyPlan(final EnergyPlan energyPlan) {
        setEnabled(false);
        this.energyPlan = energyPlan;
        contentPanel.clear();

        questionCBManager.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> booleanValueChangeEvent) {
                switch (additionalChargeType) {
                    case SURCHARGE:
                        energyPlan.setSurcharge(questionCBManager.getValue());
                        break;
                    case FIXED:
                        energyPlan.setFixed(questionCBManager.getValue());
                        break;
                    case MINIMUM:
                        energyPlan.setMinimum(questionCBManager.getValue());
                        break;
                    case TAX:
                        energyPlan.setTaxes(questionCBManager.getValue());
                        break;
                }
                handlerManager.fireEvent(new ItemChangedEvent<EnergyPlan>(energyPlan));
                refreshCharges();
            }
        });

        switch (additionalChargeType) {
            case SURCHARGE:
                questionField.setInnerText(WebStringResource.INSTANCE.surchargeQuestion());
                questionCBManager.setValue(energyPlan.isSurcharge());
                break;
            case FIXED:
                questionField.setInnerText(WebStringResource.INSTANCE.fixedQuestion());
                questionCBManager.setValue(energyPlan.isFixed());
                break;
            case MINIMUM:
                questionField.setInnerText(WebStringResource.INSTANCE.minimumQuestion());
                questionCBManager.setValue(energyPlan.isMinimum());
                break;
            case TAX:
                questionField.setInnerText(WebStringResource.INSTANCE.taxQuestion());
                questionCBManager.setValue(energyPlan.isTaxes());
                break;
        }


        refreshCharges();
        setEnabled(true);
    }

    AdditionalCharge findCharge(EnergyPlanSeason energyPlanSeason) {
        for (AdditionalCharge additionalCharge : energyPlanSeason.getAdditionalChargeList()) {
            if (additionalCharge.getAdditionalChargeType().equals(additionalChargeType)) return additionalCharge;
        }
        return null;
    }

    public boolean isValid() {
        boolean valid = true;
        return valid;
    }

    public void setEnabled(boolean enabled) {

    }

    private void refreshCharges() {
        contentPanel.clear();
        if (questionCBManager.getValue()) {
            for (EnergyPlanSeason season : energyPlan.getSeasonList()) {
                AdditionalCharge additionalCharge = findCharge(season);
                if (additionalCharge == null) {
                    additionalCharge = new AdditionalCharge();
                    additionalCharge.setAdditionalChargeType(additionalChargeType);
                    additionalCharge.setSeasonId(season.getId());
                    season.getAdditionalChargeList().add(additionalCharge);
                }

                AdditionalChargeCell additionalChargeCell = new AdditionalChargeCell(energyPlan, season, additionalCharge);
                additionalChargeCell.addChangeHandler(new ItemChangedHandler<EnergyPlan>() {
                    @Override
                    public void onChanged(ItemChangedEvent<EnergyPlan> event) {
                        handlerManager.fireEvent(event);;
                    }
                });
                contentPanel.add(additionalChargeCell);
            }
        }
    }

    public void addChangeHandler(ItemChangedHandler<EnergyPlan> energyPlanItemChangedHandler) {
        handlerManager.addHandler(ItemChangedEvent.TYPE, energyPlanItemChangedHandler);
    }


    //Skin mapping
    @UiTemplate("AdditionalChargePanel.ui.xml")
    interface DefaultBinder extends UiBinder<Widget, AdditionalChargePanel> {
    }


}

