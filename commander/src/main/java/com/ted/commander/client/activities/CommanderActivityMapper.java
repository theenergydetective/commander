/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.activities;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import com.ted.commander.client.analytics.GoogleAnalytics;
import com.ted.commander.client.clientFactory.ClientFactory;
import com.ted.commander.client.places.*;

import java.util.logging.Logger;

/**
 * Created by pete on 10/17/2014.
 */
public class CommanderActivityMapper implements ActivityMapper {
    static final Logger LOGGER = Logger.getLogger(CommanderActivityMapper.class.getName());

    private ClientFactory clientFactory;

    public CommanderActivityMapper(ClientFactory clientFactory) {
        super();
        this.clientFactory = clientFactory;
    }

    @Override
    public Activity getActivity(Place place) {


        if (place instanceof EnergyPlanListPlace) {
            GoogleAnalytics.trackPageview("EnergyPlanListPlace");
            return new EnergyPlanListActivity((EnergyPlanListPlace) place, clientFactory);
        }


        if (place instanceof EnergyPlanPlace) {
            GoogleAnalytics.trackPageview("EnergyPlanPlace");
            return new EnergyPlanActivity((EnergyPlanPlace) place, clientFactory);
        }

        if (place instanceof LoginPlace) {
            GoogleAnalytics.trackPageview("LoginPlace");
            return new LoginActivity((LoginPlace) place, clientFactory);
        }
        if (place instanceof JoinPlace) {
            GoogleAnalytics.trackPageview("JoinPlace");
            return new JoinActivity((JoinPlace) place, clientFactory);
        }
        if (place instanceof ResetPasswordPlace) {
            GoogleAnalytics.trackPageview("ResetPasswordPlace");
            return new ResetPasswordActivity((ResetPasswordPlace) place, clientFactory);
        }
        if (place instanceof ForgotPasswordPlace) {
            GoogleAnalytics.trackPageview("ForgotPasswordPlace");
            return new ForgotPasswordActivity((ForgotPasswordPlace) place, clientFactory);
        }
        if (place instanceof JoinSuccessPlace) {
            GoogleAnalytics.trackPageview("JoinSuccessActivity");
            return new JoinSuccessActivity((JoinSuccessPlace) place, clientFactory);
        }

        if (place instanceof NoPostPlace) {
            GoogleAnalytics.trackPageview("NoPostPlace");
            return new NoPostActivity((NoPostPlace) place, clientFactory);
        }
        if (place instanceof DashboardPlace) {
            GoogleAnalytics.trackPageview("DashboardPlace");
            return new DashboardActivity((DashboardPlace) place, clientFactory);
        }

        if (place instanceof DailyDetailPlace) {
            GoogleAnalytics.trackPageview("DailyDetailPlace");
            return new DailyDetailActivity((DailyDetailPlace) place, clientFactory);
        }

        if (place instanceof LocationListPlace) {
            GoogleAnalytics.trackPageview("LocationListPlace");
            return new LocationListActivity((LocationListPlace) place, clientFactory);
        }
        if (place instanceof LocationEditPlace) {
            GoogleAnalytics.trackPageview("LocationEditPlace");
            return new LocationEditActivity((LocationEditPlace) place, clientFactory);
        }


        if (place instanceof UserSettingsPlace) {
            GoogleAnalytics.trackPageview("UserSettingsPlace");
            return new UserSettingsActivity((UserSettingsPlace) place, clientFactory);
        }
        if (place instanceof LogoutPlace) {
            GoogleAnalytics.trackPageview("LogoutPlace");
            return new LogoutActivity((LogoutPlace) place, clientFactory);
        }

        if (place instanceof ComparisonPlace) {
            GoogleAnalytics.trackPageview("ComparisonPlace");
            return new ComparisonActivity((ComparisonPlace) place, clientFactory);
        }
        if (place instanceof ComparisonGraphPlace) {
            GoogleAnalytics.trackPageview("ComparisonGraphPlace");
            return new ComparisonGraphActivity((ComparisonGraphPlace) place, clientFactory);
        }

        if (place instanceof ActivationKeysPlace) {
            GoogleAnalytics.trackPageview("ActivationKeysPlace");
            return new ActivationKeysActivity((ActivationKeysPlace) place, clientFactory);
        }


        if (place instanceof ChangeEmailPlace) {
            GoogleAnalytics.trackPageview("ChangeEmailPlace");
            return new ChangeEmailActivity((ChangeEmailPlace) place, clientFactory);
        }
        if (place instanceof ChangePasswordPlace) {
            GoogleAnalytics.trackPageview("ChangePasswordPlace");
            return new ChangePasswordActivity((ChangePasswordPlace) place, clientFactory);
        }

        if (place instanceof AccountPlace) {
            GoogleAnalytics.trackPageview("AccountPlace");
            return new AccountActivity((AccountPlace) place, clientFactory);
        }
        if (place instanceof AccountListPlace) {
            GoogleAnalytics.trackPageview("AccountListPlace");
            return new AccountListActivity((AccountListPlace) place, clientFactory);
        }
        if (place instanceof AccountMembershipPlace) {
            GoogleAnalytics.trackPageview("AccountMembershipPlace");
            return new AccountMembershipActivity((AccountMembershipPlace) place, clientFactory);
        }
        if (place instanceof AccountMTUPlace) {
            GoogleAnalytics.trackPageview("AccountMTUPlace");
            return new AccountMTUActivity((AccountMTUPlace) place, clientFactory);
        }
        if (place instanceof LocationMTUPlace) {
            GoogleAnalytics.trackPageview("LocationMTUPlace");
            return new LocationMTUActivity((LocationMTUPlace) place, clientFactory);
        }
        if (place instanceof ConfirmEmailPlace) {
            GoogleAnalytics.trackPageview("ConfirmEmailPlace");
            return new ConfirmEmailActivity((ConfirmEmailPlace) place, clientFactory);
        }
        if (place instanceof GraphingPlace) {
            GoogleAnalytics.trackPageview("GraphingPlace");
            return new GraphingActivity((GraphingPlace) place, clientFactory);
        }
        if (place instanceof GraphingOptionsPlace) {
            GoogleAnalytics.trackPageview("GraphingOptionsPlace");
            return new GraphingOptionsActivity((GraphingOptionsPlace) place, clientFactory);
        }

        if (place instanceof BillingPlace) {
            GoogleAnalytics.trackPageview("BillingPlace");
            return new BillingActivity((BillingPlace) place, clientFactory);
        }

        if (place instanceof ExportPlace) {
            GoogleAnalytics.trackPageview("ExportAExportPlacectivity");
            return new ExportActivity((ExportPlace) place, clientFactory);
        }

        if (place instanceof AdviceListPlace) {
            GoogleAnalytics.trackPageview("AdviceListPlace");
            return new AdviceListActivity((AdviceListPlace) place, clientFactory);
        }
        if (place instanceof AdviceEditPlace) {
            GoogleAnalytics.trackPageview("AdviceEditPlace");
            return new AdviceEditActivity((AdviceEditPlace) place, clientFactory);
        }


        GoogleAnalytics.trackPageview("Invalid Place: " + place.getClass().getName());
        LOGGER.warning("Invalid Place: " + place.getClass().getName());
        return null;
    }

}
