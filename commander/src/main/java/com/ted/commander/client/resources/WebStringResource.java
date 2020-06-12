/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Constants;


public interface WebStringResource extends Constants {

    static WebStringResource INSTANCE = GWT.create(WebStringResource.class);

    String enterUsername();

    String password();

    String missingFieldError();

    String systemError();

    String authError();

    String requiredField();

    String email();

    String passwordMatchError();

    String emailMatchError();

    String passwordCriteriaError();

    String invalidEmailError();

    String duplicateUserError();

    String error();

    String activationKey();

    String accountName();

    String firstName();

    String middleName();

    String lastName();

    String phone();

    String companyName();

    String accountRole();

    String passAuthError();

    String accountRoleOwner();

    String accountRoleAdmin();

    String accountRoleECCEditor();

    String accountRoleReadOnly();

    String addUserTitle();

    String addUserText();

    String mtuTypeNet();

    String mtuTypeLoad();

    String mtuTypeGen();

    String mtuTypeSA();

    String mtuColumnType();

    String mtuColumnDesc();

    String mtuColumnID();

    String confirmDelete();

    String confirmDeleteMTU();

    String confirmDeleteVirtualECC();

    String mtuColumnPowerMult();

    String mtuColumnVoltageMult();

    String mtuColumnSelected();

    String confirmDeleteEnergyPlan();

    String seasonOutOfOrder();

    String season();

    String am();

    String pm();

    String defaultTOUNameSuperPeak();

    String defaultTOUNameMidPeak();

    String defaultTOUNamePeak();

    String defaultTOUNameOffPeak();

    String tier();

    String step();

    String taxQuestion();

    String surchargeQuestion();

    String fixedQuestion();

    String minimumQuestion();


    String missingTOULabel();

    String saving();

    String loading();

    String copyOf();

    String confirmEmail();

    String confirmPassword();


    String accountAdmin();

    String userProfile();

    String dashboard();

    String activateEccButton();

    String yourMTUList();

    String energyRates();

    String locations();

    String no();

    String yes();

    String sunday();

    String monday();

    String tuesday();

    String wednesday();

    String thursday();

    String friday();

    String saturday();

    String graphSettings();

    String changeEmail();

    String changePassword();

    String activationKeys();

    String costPerDay();

    String kwhConsumed();

    String averagePF();

    String peakDemand();

    String peakVoltage();

    String lowVoltage();

    String avgTemp();

    String cloudCoverage();

    String kwHGenerated();

    String touRate();

    String billingCycleStarting();

    String flat();

    String dailyDetail();

    String recentKW();

    String totalKwhForDay();

    String totalSpent();

    String powerFactor();

    String power();

    String cost();

    String powerFactorSmall();

    String voltage();

    String demand();

    String comparison();

    String logout();

    String accountSettings();

    String userSettings();

    String energyPlans();

    String averageSentence();

    String projKWH();

    String projCost();

    String mtdCost();

    String mtdKWH();

    String recent();

    String totalForDay();

    String peakForDay();

    String lowForDay();

    String averageForDay();

    String confirmDeleteMember();

    String spyder();

    String mtu();

    String newLocation();

    String locationsNeeded();

    String energy();

    String minuteGraphResolution();

    String hourGraphResolution();

    String dayGraphResolution();

    String monthGraphResolution();

    String yearGraphResolution();

    String graphing();

    String dataExport();

    String street1();

    String street2();

    String city();

    String state();

    String postal();

    String country();

    String pickLocation();

    String locationNET();

    String locationLOAD();

    String locationGENERATION();

    String dataPointMissing();

    String previousMonth();

    String nextMonth();

    String previousWeek();

    String nextWeek();

    String changeLocation();

    String changeOptions();

    String weather();

    String minuteGraph();

    String hourGraph();

    String dayGraph();

    String billingCycleGraph();

    String kwhNet();

    String kwhGenerated();

    String lowTemp();


    String peakTemp();

    String clouds();

    String windSpeed();

    String addEnergyPlan();

    String add();

    String energyPlanName();

    String utility();

    String seasonName();

    String seasonDate();

    String minutes();

    String delete();

    String temperature();

    String temperatureSymbol();

    String minMax();

    String demandCost();

    String defaultEnergyPlan();

    String noBillingCycleFound();

    String noMonitoringPointsBody();

    String noMonitoringPoints();

    String maintenance();

    String maintenanceBody();

    String advisor();

    String adviceName();

    String billing();
}
