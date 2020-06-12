/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.dashboard;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.ted.commander.client.callback.DefaultMethodCallback;
import com.ted.commander.client.clientFactory.ClientFactory;
import com.ted.commander.client.enums.CommanderFormats;
import com.ted.commander.client.events.PlaceRequestEvent;
import com.ted.commander.client.model.DashboardDateRange;
import com.ted.commander.client.model.DashboardOptions;
import com.ted.commander.client.places.ActivationKeysPlace;
import com.ted.commander.client.places.DailyDetailPlace;
import com.ted.commander.client.places.DashboardPlace;
import com.ted.commander.client.places.LocationListPlace;
import com.ted.commander.client.restInterface.RESTFactory;
import com.ted.commander.client.util.MathUtil;
import com.ted.commander.common.enums.AccountRole;
import com.ted.commander.common.enums.BillingCycleDataType;
import com.ted.commander.common.model.*;
import com.ted.commander.common.model.history.HistoryDay;
import com.ted.commander.common.model.history.WeatherHistory;
import org.fusesource.restygwt.client.Method;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class DashboardPresenter implements DashboardView.Presenter, IsWidget {

    static final Logger LOGGER = Logger.getLogger(DashboardPresenter.class.getName());

    final ClientFactory clientFactory;
    final DashboardView view;
    final DashboardPlace place;

    DashboardDateRange dashboardDateRange = new DashboardDateRange();
    DashboardResponse dashboardResponse;
    VirtualECC dashboardLocation;

    //Gradient Maps
    final HashMap<Integer, Double> minMap = new HashMap<Integer, Double>();
    final HashMap<Integer, Double> maxMap = new HashMap<Integer, Double>();

    //Create the calendar value Strings
    final HashMap<CalendarKey, List<String>> dataMap = new HashMap<CalendarKey, List<String>>();
    final HashMap<CalendarKey, Double> gradientMap = new HashMap<CalendarKey, Double>();
    final HashMap<CalendarKey, WeatherHistory> weatherHistoryHashMap = new HashMap<CalendarKey, WeatherHistory>();


    public DashboardPresenter(final ClientFactory clientFactory, DashboardPlace place) {
        LOGGER.fine("CREATING NEW DashboardPresenter ");
        this.clientFactory = clientFactory;
        this.place = place;
        view = clientFactory.getDashboardView();
        view.setPresenter(this);
        view.setLogo(clientFactory.getInstance().getLogo().getHeaderLogo());
        view.setLoadingVisible(true);

        dashboardLocation = clientFactory.getInstance().getDashboardLocation();
        if (place.getLocationId() == null && dashboardLocation != null){
            queryLocation(dashboardLocation.getId(), new DashboardDateRange(), clientFactory.getInstance().getDashboardOptions());
        } else if (dashboardLocation == null || !dashboardLocation.getId().equals(place.getLocationId())) {
            if (place.getLocationId() != null) {
                queryLocation(place.getLocationId(), new DashboardDateRange(), clientFactory.getInstance().getDashboardOptions());
            } else {
                queryLocation(0l, new DashboardDateRange(), clientFactory.getInstance().getDashboardOptions());
            }
        } else {
            queryLocation(dashboardLocation.getId(), new DashboardDateRange(), clientFactory.getInstance().getDashboardOptions());
        }
    }

    public void queryLocation(long locationId, DashboardDateRange dashboardDateRange, DashboardOptions dashboardOptions) {
        view.setLoadingVisible(true);

        String startDate = dashboardDateRange.getStartDateKey().getYear() + "-" +
                dashboardDateRange.getStartDateKey().getMonth() + "-" +
                dashboardDateRange.getStartDateKey().getDate();

        String endDate = dashboardDateRange.getEndDateKey().getYear() + "-" +
                dashboardDateRange.getEndDateKey().getMonth() + "-" +
                dashboardDateRange.getEndDateKey().getDate();

        this.dashboardDateRange = dashboardDateRange;

        String weather = "false";
        for (BillingCycleDataType billingCycleDataType : clientFactory.getInstance().getDashboardOptions().getGraphedOptions()) {
            if (billingCycleDataType.equals(BillingCycleDataType.PEAK_TEMP) ||
                    billingCycleDataType.equals(BillingCycleDataType.LOW_TEMP) ||
                    billingCycleDataType.equals(BillingCycleDataType.CLOUD_COVERAGE)) {
                weather="true";
                break;
            }
        }

        RESTFactory.getDashboardControllerClient(clientFactory).get(locationId, startDate, endDate, weather, new DefaultMethodCallback<DashboardResponse>() {
            @Override
            public void onSuccess(Method method, DashboardResponse dr) {
                dashboardResponse = dr;

                if (dashboardResponse.getVirtualECC() == null){
                    view.setLoadingVisible(false);
                    goTo(new LocationListPlace(""));
                } else {

                    if (dr.getHistoryDayList() == null || dr.getHistoryDayList().size() == 0) {
                        minMap.clear();
                        maxMap.clear();
                        dataMap.clear();

                        if (!dashboardResponse.isHasNetPoints()){
                            view.showNoNetError();
                        }
                        dashboardLocation = dashboardResponse.getVirtualECC();
                        clientFactory.getInstance().setDashboardLocation(dashboardLocation);


                    } else {
                        if (dashboardResponse.getVirtualECC() == null) {
                            newLocation();
                            return;
                        }
                        dashboardLocation = dashboardResponse.getVirtualECC();
                        clientFactory.getInstance().setDashboardLocation(dashboardLocation);


                        //Convert Data
                        setWeatherHistory(dashboardResponse.getWeatherHistoryList());
                        calcCalendarValues();
                    }


                    redrawCalendar();
                    view.setLoadingVisible(false);
                }
            }
        });
    }


    @Override
    public boolean isValid() {
        boolean valid = true;
        return valid;
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public void queryLocations() {
        RESTFactory.getVirtualECCService(clientFactory).getForAllAccounts(new DefaultMethodCallback<List<AccountLocation>>() {
            @Override
            public void onSuccess(Method method, List<AccountLocation> accountLocations) {
                view.showLocationSelector(accountLocations);
            }
        });
    }

    @Override
    public void queryOptions() {
        view.setOptionSelector(dashboardLocation, clientFactory.getInstance().getDashboardOptions());
    }


    public void newLocation() {
        LOGGER.fine("new Location Called");
        //Check to see if there are any MTU's posting. If there are, go to the new
        RESTFactory.getUserService(clientFactory).getAccountMemberships(clientFactory.getUser().getId(), new DefaultMethodCallback<AccountMemberships>() {
            @Override
            public void onSuccess(Method method, AccountMemberships accountMemberships) {
                for (AccountMembership accountMemberShip : accountMemberships.getAccountMemberships()) {
                    if (accountMemberShip.getAccountRole().equals(AccountRole.OWNER)) {
                        clientFactory.getInstance().setLastEditedAccountMembership(accountMemberShip);
                        RESTFactory.getMTUService(clientFactory).getMTUs(clientFactory.getInstance().getLastEditedAccountMembership().getAccount().getId(), 0, 0, "", "", new DefaultMethodCallback<List<MTU>>() {
                            @Override
                            public void onSuccess(Method method, List<MTU> mtus) {
                                view.setLoadingVisible(false);
                                if (mtus == null || mtus.size() == 0) {
                                    goTo(new ActivationKeysPlace(""));
                                    return;
                                } else {
                                    goTo(new LocationListPlace(""));
                                    return;
                                }
                            }
                        });
                    }
                }
            }
        });
    }


    @Override
    public void queryDailyDisplay(CalendarKey dailyDisplayKey) {
        goTo(new DailyDetailPlace(dashboardLocation.getId(), dailyDisplayKey));
    }


    private void setWeatherHistory(List<WeatherHistory> weatherHistoryList) {
        weatherHistoryHashMap.clear();
        //Set Weather
        for (BillingCycleDataType billingCycleDataType : clientFactory.getInstance().getDashboardOptions().getGraphedOptions()) {
            if (billingCycleDataType.equals(BillingCycleDataType.PEAK_TEMP) ||
                    billingCycleDataType.equals(BillingCycleDataType.LOW_TEMP) ||
                    billingCycleDataType.equals(BillingCycleDataType.CLOUD_COVERAGE)) {
                for (WeatherHistory weatherHistory : weatherHistoryList) {
                    weatherHistoryHashMap.put(weatherHistory.getCalendarKey(), weatherHistory);
                }
                break;
            }
        }
    }



    private void calcCalendarValues() {
        minMap.clear();
        maxMap.clear();
        dataMap.clear();
        gradientMap.clear();

        List<HistoryDay> historyDayList = dashboardResponse.getHistoryDayList();
        for (HistoryDay historyDay : historyDayList) {
            int billingCycleMonth = historyDay.getBillingCycleMonth();
            double value = getGradientValue(historyDay, weatherHistoryHashMap.get(historyDay.getCalendarKey()));
            gradientMap.put(historyDay.getCalendarKey(), value);
            if (!minMap.containsKey(billingCycleMonth)) {
                minMap.put(billingCycleMonth, Double.MAX_VALUE);
                maxMap.put(billingCycleMonth, Double.MIN_VALUE);
            }
            if (value > maxMap.get(billingCycleMonth)) {
                //LOGGER.fine(">>>>>>>SETTING MAX VALUE: " + billingCycleMonth + " V:" + value + "b: " + billingCycleMonth + " C:" + historyDay.getCalendarKey() + historyDay.getKey());
                maxMap.put(billingCycleMonth, value);
            }
            if (value < minMap.get(billingCycleMonth)) minMap.put(billingCycleMonth, value);

            List<String> dataValue = formatData(dashboardResponse.getEnergyPlan().getRateType(), historyDay, weatherHistoryHashMap.get(historyDay.getCalendarKey()));
            dataMap.put(historyDay.getCalendarKey(), dataValue);
        }
    }


    private void redrawCalendar() {

        //Only show it once.
//        if (clientFactory.getShowMaintenanceDialog()){
//            view.showMaintenanceDialog();
//            clientFactory.setShowMaintenanceDialog(false);
//        }


        view.onResize();
        view.clearCalendar();
        view.setDateRage(dashboardDateRange);
        view.setSummary(dashboardResponse);
        view.setOptions(clientFactory.getInstance().getDashboardOptions());



        view.setLocation(dashboardResponse.getVirtualECC());

        if (dashboardResponse != null && dashboardResponse.getCurrentBillingCycle() !=  null) {

            List<HistoryDay> historyDayList = dashboardResponse.getHistoryDayList();
            for (HistoryDay historyDay : historyDayList) {
                double gradientValue = 0.0;
                String color = "transparent";
                int billingCycleMonth = historyDay.getBillingCycleMonth();
                CalendarKey calendarKey = historyDay.getCalendarKey();
                if (clientFactory.getInstance().getDashboardOptions().getGradientOption() != null && gradientMap.get(calendarKey) != null) {
                    Double max = maxMap.get(billingCycleMonth);
                    Double v = gradientMap.get(calendarKey);
                    if (v != null) {
                        gradientValue = v / max;


                        switch (clientFactory.getInstance().getDashboardOptions().getGradientOption()){
                            case AVG_PF: {
                                if (gradientValue < .80) color = "red";
                                else if (gradientValue < .90) color = "yellow";
                                else color = "green";
                                break;
                            }
                            case PEAK_VOLTAGE:{
                                color = "red";
                                if (gradientValue < 1) gradientValue = 0;
                                break;
                            }
                            case LOW_VOLTAGE:{
                                color = "red";
                                Double min = minMap.get(billingCycleMonth);
                                if (MathUtil.doubleEquals(v,min,.01)){
                                    gradientValue = 1;
                                } else {
                                    gradientValue = 0;
                                }
                                break;
                            }

                            case KWH_GENERTED:{
                                //TED-122 & TED-31
                                if (gradientValue < .50) color = "gen50";
                                else if (gradientValue < .80) color = "gen80";
                                else color = "gen100";
                                break;
                            }
                            default:{
                                if (gradientValue < .80) color = "green";
                                else if (gradientValue < .90) color = "yellow";
                                else color = "red";
                            }
                        }
                    }
                }
                view.setCalendarValue(calendarKey, dataMap.get(calendarKey), gradientValue, color);
            }
        }
        view.setLoadingVisible(false);

    }

    private double getGradientValue(HistoryDay historyDay, WeatherHistory weatherHistory) {
        if (clientFactory.getInstance().getDashboardOptions().getGradientOption() == null) return 0;
        switch (clientFactory.getInstance().getDashboardOptions().getGradientOption()) {
            case COST_PER_DAY:
                return historyDay.getNetCost();
            case PEAK_TEMP:
                if (weatherHistory == null) return 0;
                return weatherHistory.getPeakTemperature();
            case LOW_TEMP:
                if (weatherHistory == null) return 0;
                return weatherHistory.getLowTemperature();
            case CLOUD_COVERAGE:
                if (weatherHistory == null) return 0;
                return weatherHistory.getClouds();
            case KWH_NET:
                return historyDay.getNet();
            case KWH_GENERTED:
                return Math.abs(historyDay.getGeneration());
            case KWH_CONSUMED:
                return historyDay.getLoad();
            case AVG_PF:
                return historyDay.getDemandPeak() / (double)historyDay.getPfSampleCount();
            case LOW_VOLTAGE:
                return historyDay.getMinVoltage();
            case PEAK_VOLTAGE:
                return historyDay.getPeakVoltage();
            case PEAK_DEMAND:
                return historyDay.getDemandPeak();
            default:
                return 0;
        }
    }


    private List<String> formatData(String rateType, HistoryDay dailyTotal, WeatherHistory weatherHistory) {
        ArrayList<String> stringArrayList = new ArrayList<String>();

        for (BillingCycleDataType billingCycleDataType : clientFactory.getInstance().getDashboardOptions().getGraphedOptions()) {
            switch (billingCycleDataType) {
                case PEAK_TEMP: {
                    if (weatherHistory != null && weatherHistory.getPeakTempTime() != null) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(CommanderFormats.TEMP_FORMAT.format(weatherHistory.getPeakTemperature()));
                        stringBuilder.append(" @ ");
                        Date date = new Date();
                        CalendarUtil.resetTime(date);

                        date.setHours(weatherHistory.getPeakTempTime().getHour());
                        date.setMinutes(weatherHistory.getPeakTempTime().getMin());
                        stringBuilder.append(CommanderFormats.PEAK_TIME_FORMAT.format(date));
                        stringArrayList.add(stringBuilder.toString());
                    }
                    break;
                }
                case LOW_TEMP: {
                    if (weatherHistory != null && weatherHistory.getLowTempTime() != null) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(CommanderFormats.TEMP_FORMAT.format(weatherHistory.getLowTemperature()));
                        stringBuilder.append(" @ ");
                        Date date = new Date();
                        CalendarUtil.resetTime(date);
                        date.setHours(weatherHistory.getLowTempTime().getHour());
                        date.setMinutes(weatherHistory.getLowTempTime().getMin());
                        stringBuilder.append(CommanderFormats.PEAK_TIME_FORMAT.format(date));
                        stringArrayList.add(stringBuilder.toString());

                    }
                    break;
                }
                case CLOUD_COVERAGE: {
                    if (weatherHistory != null) {
                        stringArrayList.add(CommanderFormats.PF_FORMAT.format(weatherHistory.getClouds()));
                    }
                    break;
                }
                case COST_PER_DAY:
                    stringArrayList.add(NumberFormat.getSimpleCurrencyFormat(rateType).format(dailyTotal.getNetCost()));
                    break;
                case KWH_NET:
                    stringArrayList.add(CommanderFormats.SHORT_KWH_FORMAT.format(dailyTotal.getNet() / 1000));
                    break;
                case KWH_CONSUMED:
                    stringArrayList.add(CommanderFormats.SHORT_KWH_FORMAT.format(dailyTotal.getLoad() / 1000));
                    break;
                case KWH_GENERTED:
                    stringArrayList.add(CommanderFormats.SHORT_KWH_FORMAT.format(Math.abs(dailyTotal.getGeneration()) / 1000));
                    break;
                case AVG_PF:
                    double pf = dailyTotal.getPfTotal();
                    pf /= (double)dailyTotal.getPfSampleCount();
                    pf /= 100.0;
                    stringArrayList.add(CommanderFormats.PF_FORMAT.format(pf));
                    break;
                case PEAK_DEMAND: {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(CommanderFormats.KW_FORMAT.format(dailyTotal.getDemandPeak() / 1000));
                    stringBuilder.append(" @ ");
                    Date date = new Date();
                    CalendarUtil.resetTime(date);
                    date.setHours(dailyTotal.getDemandPeakCalendarKey().getHour());
                    date.setMinutes(dailyTotal.getDemandPeakCalendarKey().getMin());
                    stringBuilder.append(CommanderFormats.PEAK_TIME_FORMAT.format(date));
                    stringArrayList.add(stringBuilder.toString());
                    break;
                }

                case PEAK_VOLTAGE: {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(CommanderFormats.VOLTAGE_FORMAT.format(dailyTotal.getPeakVoltage()));
                    stringBuilder.append(" @ ");
                    Date date = new Date();
                    CalendarUtil.resetTime(date);
                    date.setHours(dailyTotal.getPeakVoltageCalendarKey().getHour());
                    date.setMinutes(dailyTotal.getPeakVoltageCalendarKey().getMin());
                    stringBuilder.append(CommanderFormats.PEAK_TIME_FORMAT.format(date));
                    stringArrayList.add(stringBuilder.toString());
                    break;
                }

                case LOW_VOLTAGE: {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(CommanderFormats.VOLTAGE_FORMAT.format(dailyTotal.getMinVoltage()));
                    stringBuilder.append(" @ ");
                    Date date = new Date();
                    CalendarUtil.resetTime(date);
                    date.setHours(dailyTotal.getMinVoltageCalendarKey().getHour());
                    date.setMinutes(dailyTotal.getMinVoltageCalendarKey().getMin());
                    stringBuilder.append(CommanderFormats.PEAK_TIME_FORMAT.format(date));
                    stringArrayList.add(stringBuilder.toString());
                    break;
                }

            }
        }
        return stringArrayList;
    }


    @Override
    public void goTo(Place destinationPage) {
        clientFactory.getEventBus().fireEvent(new PlaceRequestEvent(destinationPage, new DashboardPlace(""), null));
    }

    @Override
    public void onResize() {
        if (dashboardResponse != null) {
            redrawCalendar();
        }
    }
}
