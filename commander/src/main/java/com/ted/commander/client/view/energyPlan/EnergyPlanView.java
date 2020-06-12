
package com.ted.commander.client.view.energyPlan;


import com.google.gwt.i18n.client.CurrencyData;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.petecode.common.client.widget.paper.HasValidation;
import com.ted.commander.client.view.energyPlan.additionalCharges.AdditionalChargePanel;
import com.ted.commander.client.view.energyPlan.demandCharges.DemandChargePanel;
import com.ted.commander.client.view.energyPlan.rates.EnergyRatePanel;
import com.ted.commander.client.view.energyPlan.tier.TierSeason;
import com.ted.commander.client.view.energyPlan.tou.TOUPanel;
import com.ted.commander.common.enums.HolidayType;
import com.ted.commander.common.enums.MeterReadCycle;
import com.ted.commander.common.enums.PlanType;

import java.util.List;

public interface EnergyPlanView extends IsWidget {

    void setPresenter(Presenter presenter);

    HasValidation<String> planName();
    HasValidation<CurrencyData> currencyType();
    HasValidation<String> utilityName();
    HasValue<Integer> meterReadDate();
    HasValue<MeterReadCycle> meterReadCycle();
    HasValue<Integer> numberSeasons();
    HasValue<PlanType> planType();
    HasValue<Integer> numberTiers();
    HasValue<Integer> numberTOU();
    HasValue<Boolean> touSaturday();
    HasValue<Boolean> touSunday();
    HasValue<Boolean> touHoliday();
    HasValue<HolidayType> holidaySchedule();

    void onResize();

    public void setUtilitySuggestions(List<String> utilityList);

    void clearSeasons();

    void addSeason(SeasonPresenter seasonPresenter);

    void showSeasons(boolean b);
    void showTier(boolean b);
    void showTOU(boolean b);

    void addTierSeason(TierSeason tierSeason);

    void addAdditionalCharge(AdditionalChargePanel additionalChargePanel);

    void addDemandCharge(DemandChargePanel demandChargePanel);

    void addEnergyRates(EnergyRatePanel energyRatePanel);

    void addTOUPanel(TOUPanel touPanel);



    public interface Presenter extends com.ted.commander.client.presenter.Presenter {

        void searchUtility(String value);
        void delete();
        void back();
    }


}
