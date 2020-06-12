/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.energyPlan;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.CurrencyList;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.petecode.common.client.events.ItemChangedEvent;
import com.petecode.common.client.events.ItemChangedHandler;
import com.ted.commander.client.callback.DefaultMethodCallback;
import com.ted.commander.client.clientFactory.ClientFactory;
import com.ted.commander.client.events.PlaceRequestEvent;
import com.ted.commander.client.places.EnergyPlanListPlace;
import com.ted.commander.client.places.EnergyPlanPlace;
import com.ted.commander.client.resources.WebStringResource;
import com.ted.commander.client.restInterface.RESTFactory;
import com.ted.commander.client.view.energyPlan.additionalCharges.AdditionalChargePanel;
import com.ted.commander.client.view.energyPlan.demandCharges.DemandChargePanel;
import com.ted.commander.client.view.energyPlan.rates.EnergyRatePanel;
import com.ted.commander.client.view.energyPlan.tier.TierSeason;
import com.ted.commander.client.view.energyPlan.tou.TOUPanel;
import com.ted.commander.common.enums.AdditionalChargeType;
import com.ted.commander.common.enums.HolidayType;
import com.ted.commander.common.enums.PlanType;
import com.ted.commander.common.model.EnergyPlan;
import com.ted.commander.common.model.EnergyPlanSeason;
import org.fusesource.restygwt.client.Method;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public class EnergyPlanPresenter implements EnergyPlanView.Presenter, IsWidget {

    static final Logger LOGGER = Logger.getLogger(EnergyPlanPresenter.class.getName());

    final ClientFactory clientFactory;
    final EnergyPlanView view;
    final EnergyPlanPlace place;

    Long energyPlanId = null;
    EnergyPlan energyPlan;

    Timer saveTimer = new Timer(){

        @Override
        public void run() {
            RESTFactory.getEnergyPlanService(clientFactory).update(energyPlan, new DefaultMethodCallback<Void>() {
                @Override
                public void onSuccess(Method method, Void aVoid) {
                    refreshView();
                }
            });
        }
    };

    ValueChangeHandler valueChangeHandler = new ValueChangeHandler() {
        @Override
        public void onValueChange(ValueChangeEvent valueChangeEvent) {
            LOGGER.fine("VALUE CHANGE DETECTED");

            if (view.planName().getValue().isEmpty()) energyPlan.setDescription("Energy Plan");
            energyPlan.setDescription(view.planName().getValue());
            energyPlan.setRateType(view.currencyType().getValue().getCurrencyCode());
            energyPlan.setUtilityName(view.utilityName().getValue());
            energyPlan.setMeterReadDate(view.meterReadDate().getValue());
            energyPlan.setMeterReadCycle(view.meterReadCycle().getValue());
            energyPlan.setNumberSeasons(view.numberSeasons().getValue());
            energyPlan.setPlanType(view.planType().getValue());
            energyPlan.setNumberTier(view.numberTiers().getValue());
            energyPlan.setNumberTOU(view.numberTOU().getValue());
            energyPlan.setTouApplicableSaturday(view.touSaturday().getValue());
            energyPlan.setTouApplicableSunday(view.touSunday().getValue());
            energyPlan.setTouApplicableHoliday(view.touHoliday().getValue());
            energyPlan.setHolidayScheduleId((long) view.holidaySchedule().getValue().ordinal());

            //Add Debounce between saves
            saveTimer.cancel();
//            saveTimer.schedule(500);
        }
    };

    public EnergyPlanPresenter(final ClientFactory clientFactory, final EnergyPlanPlace place) {
        LOGGER.fine("CREATING NEW EnergyPlanListPresenter");
        this.clientFactory = clientFactory;
        this.place = place;
        view = clientFactory.getEnergyPlanView();
        view.setPresenter(this);


        energyPlanId = place.getEnergyPlanId();

        view.planName().addValueChangeHandler(valueChangeHandler);
        view.currencyType().addValueChangeHandler(valueChangeHandler);
        view.utilityName().addValueChangeHandler(valueChangeHandler);
        view.meterReadDate().addValueChangeHandler(valueChangeHandler);
        view.meterReadCycle().addValueChangeHandler(valueChangeHandler);
        view.numberSeasons().addValueChangeHandler(valueChangeHandler);
        view.planType().addValueChangeHandler(valueChangeHandler);
        view.numberTiers().addValueChangeHandler(valueChangeHandler);
        view.numberTOU().addValueChangeHandler(valueChangeHandler);
        view.touSaturday().addValueChangeHandler(valueChangeHandler);
        view.touSunday().addValueChangeHandler(valueChangeHandler);
        view.touHoliday().addValueChangeHandler(valueChangeHandler);
        view.holidaySchedule().addValueChangeHandler(valueChangeHandler);



        RESTFactory.getEnergyPlanService(clientFactory).get(energyPlanId, new DefaultMethodCallback<EnergyPlan>() {
            @Override
            public void onSuccess(Method method, EnergyPlan value) {
                energyPlan = value;

                view.planName().setValue(energyPlan.getDescription());

                if (energyPlan.getRateType() == null || energyPlan.getRateType().isEmpty()) energyPlan.setRateType(CurrencyList.get().getDefault().getCurrencyCode());
                view.currencyType().setValue(CurrencyList.get().lookup(energyPlan.getRateType()));
                view.utilityName().setValue(energyPlan.getUtilityName());
                view.meterReadDate().setValue(energyPlan.getMeterReadDate());
                view.meterReadCycle().setValue(energyPlan.getMeterReadCycle());
                view.numberSeasons().setValue(energyPlan.getNumberSeasons());
                view.planType().setValue(energyPlan.getPlanType());
                view.numberTiers().setValue(energyPlan.getNumberTier());
                view.numberTOU().setValue(energyPlan.getNumberTOU());
                view.touSaturday().setValue(energyPlan.getTouApplicableSaturday());
                view.touSunday().setValue(energyPlan.getTouApplicableSunday());
                view.touHoliday().setValue(energyPlan.getTouApplicableHoliday());
                view.holidaySchedule().setValue(HolidayType.values()[energyPlan.getHolidayScheduleId().intValue()]);
                refreshView();
            }
        });


    }


    private void balanceSeasons() {
        if (energyPlan.getSeasonList().size() > energyPlan.getNumberSeasons()) {
            //Remove a season
            energyPlan.getSeasonList().remove(energyPlan.getSeasonList().size() - 1);
            balanceSeasons();
        } else if (energyPlan.getSeasonList().size() < energyPlan.getNumberSeasons()) {
            //Add a season
            EnergyPlanSeason energyPlanSeason = new EnergyPlanSeason();
            energyPlanSeason.setId(energyPlan.getSeasonList().size());
            switch (energyPlanSeason.getId()) {
                case 0:
                    energyPlanSeason.setSeasonDayOfMonth(15);
                    energyPlanSeason.setSeasonMonth(2);
                    break;
                case 1:
                    energyPlanSeason.setSeasonDayOfMonth(15);
                    energyPlanSeason.setSeasonMonth(5);
                    break;
                case 2:
                    energyPlanSeason.setSeasonDayOfMonth(15);
                    energyPlanSeason.setSeasonMonth(8);
                    break;
                case 3:
                    energyPlanSeason.setSeasonDayOfMonth(15);
                    energyPlanSeason.setSeasonMonth(11);
                    break;
                default:
                    energyPlanSeason.setSeasonDayOfMonth(15);
                    energyPlanSeason.setSeasonMonth(2);
                    break;
            }
            energyPlanSeason.setSeasonName(WebStringResource.INSTANCE.season() + " " + (energyPlanSeason.getId() + 1));
            energyPlan.getSeasonList().add(energyPlanSeason);
            balanceSeasons();
        } else {
            //balanced
            LOGGER.fine("SEASONS BALANCED");

            //Adjust the dates
            Date lastDate = null;
            for(EnergyPlanSeason season: energyPlan.getSeasonList()){
                Date seasonDate = new Date(1,1,114);
                seasonDate.setDate(season.getSeasonDayOfMonth());
                seasonDate.setMonth(season.getSeasonMonth());
                CalendarUtil.resetTime(seasonDate);
                if (lastDate == null) lastDate = new Date(seasonDate.getTime());
                else {
                    if (seasonDate.before(lastDate) || seasonDate.equals(lastDate)){
                        LOGGER.warning("Adjusting season date: " + seasonDate + " is before or on " + lastDate);
                        seasonDate = new Date(lastDate.getTime());
                        CalendarUtil.addDaysToDate(seasonDate, 1);
                        season.setSeasonMonth(seasonDate.getMonth());
                        season.setSeasonDayOfMonth(seasonDate.getDate());
                    }
                    lastDate = new Date(seasonDate.getTime());
                }
            }
        }
    }

    Timer updateTimer = new Timer(){
        @Override
        public void run() {
            LOGGER.severe("CALLING UPDATE");
            RESTFactory.getEnergyPlanService(clientFactory).update(energyPlan, new DefaultMethodCallback<Void>() {
                @Override
                public void onSuccess(Method method, Void aVoid) {
                    refreshView();
                }
            });
        }
    };




    ItemChangedHandler<EnergyPlan> energyPlanItemChangedHandler = new ItemChangedHandler<EnergyPlan>() {
        @Override
        public void onChanged(ItemChangedEvent<EnergyPlan> event) {
            LOGGER.fine("---Updating Energy Plan " + (new Date()).getTime());
            updateTimer.cancel();
            updateTimer.schedule(200);
        }
    };

    private void refreshView() {
        //LOGGER.fine("REFRESH VIEW: " +  energyPlan.getSeasonList().get(0).getEnergyRateList().size());
        balanceSeasons();

        view.showSeasons(view.numberSeasons().getValue() > 1);
        view.clearSeasons();

        //Do the plan type update
        view.showTier(view.planType().getValue() == PlanType.TIER || view.planType().getValue() == PlanType.TIERTOU);
        view.showTOU(view.planType().getValue() == PlanType.TOU || view.planType().getValue() == PlanType.TIERTOU);


        for(EnergyPlanSeason season: energyPlan.getSeasonList()){
            SeasonPresenter seasonPresenter = new SeasonPresenter(clientFactory, energyPlan, season);
            seasonPresenter.addValueChangeHandler(valueChangeHandler);
            view.addSeason(seasonPresenter);
            TierSeason tierSeason = new TierSeason(clientFactory.getInstance(), energyPlan, season);
            tierSeason.addChangeHandler(energyPlanItemChangedHandler);
            view.addTierSeason(tierSeason);
        }

        AdditionalChargePanel surchargePanel = new AdditionalChargePanel(AdditionalChargeType.SURCHARGE, energyPlan);
        AdditionalChargePanel fixedPanel = new AdditionalChargePanel(AdditionalChargeType.FIXED, energyPlan);
        AdditionalChargePanel minumumPanel = new AdditionalChargePanel(AdditionalChargeType.MINIMUM, energyPlan);
        AdditionalChargePanel taxPanel = new AdditionalChargePanel(AdditionalChargeType.TAX, energyPlan);


        DemandChargePanel demandChargePanel = new DemandChargePanel(energyPlan);
        EnergyRatePanel energyRatePanel = new EnergyRatePanel(energyPlan);

        surchargePanel.addChangeHandler(energyPlanItemChangedHandler);
        fixedPanel.addChangeHandler(energyPlanItemChangedHandler);
        minumumPanel.addChangeHandler(energyPlanItemChangedHandler);
        taxPanel.addChangeHandler(energyPlanItemChangedHandler);
        demandChargePanel.addChangeHandler(energyPlanItemChangedHandler);
        energyRatePanel.addChangeHandler(energyPlanItemChangedHandler);


        if (energyPlan.getPlanType() == PlanType.TOU || energyPlan.getPlanType().equals(PlanType.TIERTOU)) {
            TOUPanel touPanel = new TOUPanel(energyPlan);
            touPanel.addChangeHandler(energyPlanItemChangedHandler);
            view.addTOUPanel(touPanel);
        }



        view.addAdditionalCharge(surchargePanel);
        view.addAdditionalCharge(fixedPanel);
        view.addAdditionalCharge(minumumPanel);
        view.addAdditionalCharge(taxPanel);
        view.addDemandCharge(demandChargePanel);
        view.addEnergyRates(energyRatePanel);


    }

    @Override
    public boolean isValid() {
        boolean valid = true;
        view.planName().setInvalid("");
        view.currencyType().setInvalid("");
        view.utilityName().setInvalid("");

        if (view.planName().getValue().isEmpty()){
            view.planName().setInvalid(WebStringResource.INSTANCE.requiredField());
            valid = false;
        }

        if (view.currencyType() == null){
            view.currencyType().setInvalid(WebStringResource.INSTANCE.requiredField());
            valid = false;
        }


        if (view.utilityName().getValue().isEmpty()){
            view.utilityName().setInvalid(WebStringResource.INSTANCE.requiredField());
            valid = false;
        }


        return valid;

    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public void goTo(Place place) {

        clientFactory.getEventBus().fireEvent(new PlaceRequestEvent(place, this.place, null));

    }

    @Override
    public void onResize() {
        view.onResize();
    }


    @Override
    public void searchUtility(String value) {
        RESTFactory.getResourceService(clientFactory).findUtilities(value, 10, new DefaultMethodCallback<List<String>>() {
            @Override
            public void onSuccess(Method method, List<String> strings) {
                view.setUtilitySuggestions(strings);
            }
        });
    }

    @Override
    public void delete() {
        LOGGER.fine("---DELETING " + energyPlan);
        RESTFactory.getEnergyPlanService(clientFactory).delete(energyPlan.getId(), new DefaultMethodCallback<Void>() {
            @Override
            public void onSuccess(Method method, Void aVoid) {
                goTo(new EnergyPlanListPlace(energyPlan.getAccountId()));
            }
        });
    }

    @Override
    public void back() {
        //TODO: Show saving dialog
        if (isValid()){
            RESTFactory.getEnergyPlanService(clientFactory).update(energyPlan, new DefaultMethodCallback<Void>() {
                @Override
                public void onSuccess(Method method, Void aVoid) {
                    goTo(new EnergyPlanListPlace(energyPlan.getAccountId()));
                }
            });
        }
    }
}
