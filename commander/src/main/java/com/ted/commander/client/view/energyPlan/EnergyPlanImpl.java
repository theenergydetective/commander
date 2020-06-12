/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.energyPlan;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.i18n.client.CurrencyData;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;
import com.petecode.common.client.events.ItemSelectedEvent;
import com.petecode.common.client.events.ItemSelectedHandler;
import com.petecode.common.client.widget.PaperCheckboxElementManager;
import com.petecode.common.client.widget.PaperSliderManager;
import com.petecode.common.client.widget.paper.HasValidation;
import com.petecode.common.client.widget.paper.PaperInputDecorator;
import com.petecode.common.client.widget.paper.autocomplete.AutoCompletePopup;
import com.petecode.common.client.widget.paper.autocomplete.AutoCompleteSuggestion;
import com.petecode.common.client.widget.paper.dialog.ConfirmDialog;
import com.ted.commander.client.radioManager.HolidayTypeRadioManager;
import com.ted.commander.client.radioManager.IntegerTypeRadioManager;
import com.ted.commander.client.radioManager.MeterReadCycleRadioManager;
import com.ted.commander.client.radioManager.PlanTypeRadioManager;
import com.ted.commander.client.resources.WebStringResource;
import com.ted.commander.client.view.energyPlan.additionalCharges.AdditionalChargePanel;
import com.ted.commander.client.view.energyPlan.demandCharges.DemandChargePanel;
import com.ted.commander.client.view.energyPlan.rates.EnergyRatePanel;
import com.ted.commander.client.view.energyPlan.tier.TierSeason;
import com.ted.commander.client.view.energyPlan.tou.TOUPanel;
import com.ted.commander.client.widgets.CurrencyPicker;
import com.ted.commander.common.enums.HolidayType;
import com.ted.commander.common.enums.MeterReadCycle;
import com.ted.commander.common.enums.PlanType;
import com.vaadin.polymer.paper.element.PaperCheckboxElement;
import com.vaadin.polymer.paper.element.PaperRadioGroupElement;
import com.vaadin.polymer.paper.widget.PaperIconButton;
import com.vaadin.polymer.paper.widget.PaperSlider;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


public class EnergyPlanImpl extends Composite implements EnergyPlanView {

    static final Logger LOGGER = Logger.getLogger(EnergyPlanImpl.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);
    Presenter presenter;
    @UiField
    PaperIconButton backButton;
    @UiField
    PaperInputDecorator utilityNameField;
    @UiField
    CurrencyPicker currencyPicker;
    @UiField
    PaperInputDecorator planNameField;
    @UiField
    PaperSlider daySlider;

    final PaperSliderManager daySliderManager;
    final PaperSliderManager tierSliderManager;
    final PaperSliderManager touSliderManager;

    @UiField
    PaperRadioGroupElement billingCycleGroup;
    @UiField
    PaperRadioGroupElement seasonNumberGroup;
    @UiField
    FlowPanel seasonFlowPanel;

    @UiField
    PaperRadioGroupElement planTypeGroup;
    @UiField
    DivElement tierDiv;
    @UiField
    DivElement touDiv;
    @UiField
    FlowPanel tierFlowPanel;
    @UiField
    PaperSlider tierSlider;
    @UiField
    FlowPanel touFlowPanel;
    @UiField
    PaperSlider touSlider;
    @UiField
    PaperCheckboxElement saturdayCB;
    PaperCheckboxElementManager saturdayCBManager;

    @UiField
    PaperCheckboxElement sundayCB;
    PaperCheckboxElementManager sundayCBManager;

    @UiField
    PaperCheckboxElement holidayCB;
    PaperCheckboxElementManager holidayCBManager;

    @UiField
    PaperRadioGroupElement holidyScheduleGroup;
    @UiField
    FlowPanel additionalChargePanel;
    @UiField
    FlowPanel demandChargePanel;
    @UiField
    FlowPanel energyRatesPanel;
    @UiField
    PaperIconButton deleteButton;

    final MeterReadCycleRadioManager meterReadCycleRadioManager;
    final IntegerTypeRadioManager seasonRadioManager;
    final PlanTypeRadioManager planTypeRadioManager;
    final HolidayTypeRadioManager holidayTypeRadioManager;

    Timer utilitySearchTimer = new Timer(){
        @Override
        public void run() {
            presenter.searchUtility(utilityNameField.getValue());
        }
    };

    AutoCompletePopup<String> utilityAutoCompletePopup;




    public EnergyPlanImpl() {
        initWidget(defaultBinder.createAndBindUi(this));

        saturdayCBManager = new PaperCheckboxElementManager(saturdayCB);
        sundayCBManager = new PaperCheckboxElementManager(sundayCB);
        holidayCBManager = new PaperCheckboxElementManager(holidayCB);

        daySliderManager = new PaperSliderManager(daySlider);
        tierSliderManager = new PaperSliderManager(tierSlider);
        touSliderManager = new PaperSliderManager(touSlider);

        meterReadCycleRadioManager = new MeterReadCycleRadioManager(billingCycleGroup);
        seasonRadioManager = new IntegerTypeRadioManager(seasonNumberGroup);
        planTypeRadioManager = new PlanTypeRadioManager(planTypeGroup);
        holidayTypeRadioManager = new HolidayTypeRadioManager(holidyScheduleGroup);

        utilityAutoCompletePopup = new AutoCompletePopup(utilityNameField);

        utilityAutoCompletePopup.addItemSelectedHandler(new ItemSelectedHandler<AutoCompleteSuggestion<String>>() {
            @Override
            public void onSelected(ItemSelectedEvent<AutoCompleteSuggestion<String>> event) {
                LOGGER.fine(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>UTILITY SELECTED!!!!!");
                utilityNameField.setValue(event.getItem().getValue(), false);
            }
        });

//        utilityNameField.addDomHandler(new BlurHandler() {
//            @Override
//            public void onBlur(BlurEvent blurEvent) {
//                LOGGER.fine(">>>>>>>>>>>>>>> ON BLUR!!!!!!!!!!!!!");
//                utilityAutoCompletePopup.hide();
//            }
//        }, BlurEvent.getType());

        utilityNameField.addDomHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent keyUpEvent) {
                LOGGER.fine("S>>>>>>>>>>>>>>>>>>>>>>>EARCH VALUE:" + utilityNameField.getValue());
                utilitySearchTimer.cancel();
                utilitySearchTimer.schedule(250);
            }
        }, KeyUpEvent.getType());


        backButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                presenter.back();
            }
        });

        deleteButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                ConfirmDialog confirmDialog = new ConfirmDialog(WebStringResource.INSTANCE.confirmDelete(), WebStringResource.INSTANCE.confirmDeleteEnergyPlan());
                confirmDialog.addConfirmHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent clickEvent) {
                        presenter.delete();
                    }
                });
                confirmDialog.center();
            }
        });
    }


    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public HasValidation<String> planName() {
        return planNameField;
    }

    @Override
    public HasValidation<CurrencyData> currencyType() {
        return currencyPicker;
    }

    @Override
    public HasValidation<String> utilityName() {
        return utilityNameField;
    }

    @Override
    public HasValue<Integer> meterReadDate() {

        return daySliderManager;
    }

    @Override
    public HasValue<MeterReadCycle> meterReadCycle() {
        return meterReadCycleRadioManager;
    }

    @Override
    public HasValue<Integer> numberSeasons() {
        return seasonRadioManager;
    }

    @Override
    public HasValue<PlanType> planType() {
        return planTypeRadioManager;
    }

    @Override
    public HasValue<Integer> numberTiers() {
        return tierSliderManager;
    }

    @Override
    public HasValue<Integer> numberTOU() {
        return touSliderManager;
    }

    @Override
    public HasValue<Boolean> touSaturday() {
        return saturdayCBManager;
    }

    @Override
    public HasValue<Boolean> touSunday() {
        return sundayCBManager;
    }

    @Override
    public HasValue<Boolean> touHoliday() {
        return holidayCBManager;
    }

    @Override
    public HasValue<HolidayType> holidaySchedule() {
        return holidayTypeRadioManager;
    }


    @Override
    protected void onAttach() {
        super.onAttach();
        onResize();
    }

    @Override
    public void onResize() {
    }

    @Override
    public void setUtilitySuggestions(List<String> utilityList) {
        List<AutoCompleteSuggestion<String>> autoCompleteSuggestionList = new ArrayList<>();

        for (String utility: utilityList){
            autoCompleteSuggestionList.add(new AutoCompleteSuggestion<String>(utility, utility));
        }

        if (!utilityAutoCompletePopup.isShowing()){
            utilityAutoCompletePopup.show();
        }
        utilityAutoCompletePopup.refreshSuggestions(autoCompleteSuggestionList);
    }

    @Override
    public void clearSeasons() {
        seasonFlowPanel.clear();
        tierFlowPanel.clear();
        touFlowPanel.clear();
        this.additionalChargePanel.clear();
        this.demandChargePanel.clear();
        this.energyRatesPanel.clear();
        this.touFlowPanel.clear();
    }

    @Override
    public void addSeason(SeasonPresenter seasonPresenter) {
        seasonFlowPanel.add(seasonPresenter);
    }

    @Override
    public void showSeasons(boolean b) {
        if (b) seasonFlowPanel.getElement().getStyle().clearDisplay();
        else seasonFlowPanel.getElement().getStyle().setDisplay(Style.Display.NONE);
    }

    @Override
    public void showTier(boolean b) {
        if (b) tierDiv.getStyle().clearDisplay();
        else tierDiv.getStyle().setDisplay(Style.Display.NONE);

    }

    @Override
    public void showTOU(boolean b) {
        if (b) touDiv.getStyle().clearDisplay();
        else touDiv.getStyle().setDisplay(Style.Display.NONE);
    }

    @Override
    public void addTierSeason(TierSeason tierSeason) {
        this.tierFlowPanel.add(tierSeason);
    }

    @Override
    public void addAdditionalCharge(AdditionalChargePanel additionalChargePanel) {
        this.additionalChargePanel.add(additionalChargePanel);
    }

    @Override
    public void addDemandCharge(DemandChargePanel demandChargePanel) {
        this.demandChargePanel.add(demandChargePanel);
    }

    @Override
    public void addEnergyRates(EnergyRatePanel energyRatePanel) {
        this.energyRatesPanel.add(energyRatePanel);
    }

    @Override
    public void addTOUPanel(TOUPanel touPanel) {
        touFlowPanel.add(touPanel);
    }


    //Skin mapping
    interface DefaultBinder extends UiBinder<Widget, EnergyPlanImpl> {
    }


}
