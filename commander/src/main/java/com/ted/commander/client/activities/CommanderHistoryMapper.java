/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.activities;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;
import com.ted.commander.client.places.*;

/**
 * Created by pete on 10/17/2014.
 */
@WithTokenizers({
        NoPostPlace.Tokenizer.class,
        LoginPlace.Tokenizer.class,
        LogoutPlace.Tokenizer.class,
        JoinPlace.Tokenizer.class,
        JoinSuccessPlace.Tokenizer.class,
        DashboardPlace.Tokenizer.class,
        DailyDetailPlace.Tokenizer.class,
        LocationListPlace.Tokenizer.class,
        LocationEditPlace.Tokenizer.class,
        UserSettingsPlace.Tokenizer.class,
        EnergyPlanListPlace.Tokenizer.class,
        ComparisonPlace.Tokenizer.class,
        ComparisonGraphPlace.Tokenizer.class,
        ActivationKeysPlace.Tokenizer.class,
        AccountListPlace.Tokenizer.class,
        AccountPlace.Tokenizer.class,
        AccountMTUPlace.Tokenizer.class,
        LocationMTUPlace.Tokenizer.class,
        AccountMembershipPlace.Tokenizer.class,
        ExportPlace.Tokenizer.class,
        GraphingPlace.Tokenizer.class,
        GraphingOptionsPlace.Tokenizer.class,
        EnergyPlanListPlace.Tokenizer.class,
        EnergyPlanPlace.Tokenizer.class,
        ChangeEmailPlace.Tokenizer.class,
        ChangePasswordPlace.Tokenizer.class,
        ForgotPasswordPlace.Tokenizer.class,
        ResetPasswordPlace.Tokenizer.class,
        AdviceListPlace.Tokenizer.class,
        AdviceEditPlace.Tokenizer.class,
        BillingPlace.Tokenizer.class,
})
public interface CommanderHistoryMapper extends PlaceHistoryMapper {
}
