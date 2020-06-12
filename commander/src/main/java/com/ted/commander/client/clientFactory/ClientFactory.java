/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.clientFactory;


import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.place.shared.PlaceController;
import com.google.web.bindery.event.shared.EventBus;
import com.ted.commander.client.model.Instance;
import com.ted.commander.client.model.OAuthResponse;
import com.ted.commander.client.view.accountList.AccountListView;
import com.ted.commander.client.view.accountMTU.AccountMTUView;
import com.ted.commander.client.view.accountMembership.AccountMembershipView;
import com.ted.commander.client.view.accountView.AccountView;
import com.ted.commander.client.view.accountView.accountMembers.addMember.AddMemberView;
import com.ted.commander.client.view.activationKeys.ActivationKeysView;
import com.ted.commander.client.view.adviceEdit.AdviceEditView;
import com.ted.commander.client.view.adviceList.AdviceListView;
import com.ted.commander.client.view.billing.BillingView;
import com.ted.commander.client.view.comparison.ComparisonView;
import com.ted.commander.client.view.comparisonGraph.ComparisonGraphView;
import com.ted.commander.client.view.confirmEmail.ConfirmEmailView;
import com.ted.commander.client.view.dailyDetail.DailyDetailView;
import com.ted.commander.client.view.dashboard.DashboardView;
import com.ted.commander.client.view.energyPlan.EnergyPlanView;
import com.ted.commander.client.view.energyPlan.SeasonView;
import com.ted.commander.client.view.energyPlanList.EnergyPlanListView;
import com.ted.commander.client.view.energyPlanList.EnergyPlanRowView;
import com.ted.commander.client.view.export.ExportView;
import com.ted.commander.client.view.forgotPassword.ForgotPasswordView;
import com.ted.commander.client.view.graphing.GraphingOptionsView;
import com.ted.commander.client.view.graphing.GraphingView;
import com.ted.commander.client.view.join.JoinSuccessView;
import com.ted.commander.client.view.join.JoinView;
import com.ted.commander.client.view.locationEdit.LocationEditView;
import com.ted.commander.client.view.locationList.LocationListView;
import com.ted.commander.client.view.locationMTU.LocationMTUView;
import com.ted.commander.client.view.login.LoginView;
import com.ted.commander.client.view.noPost.NoPostView;
import com.ted.commander.client.view.resetPassword.ResetPasswordView;
import com.ted.commander.client.view.userSettings.ChangeEmailView;
import com.ted.commander.client.view.userSettings.ChangePasswordView;
import com.ted.commander.client.view.userSettings.UserSettingsView;
import com.ted.commander.common.enums.AccountRole;
import com.ted.commander.common.model.User;
import com.ted.commander.common.model.export.GraphRequest;

public interface ClientFactory {
    Instance getInstance();
    OAuthResponse getOAuthResponse();
    void setOAuthResponse(OAuthResponse oAuthResponse);

    User getUser();

    void setUser(User user);

    EventBus getEventBus();

    PlaceController getController();

    LoginView getLoginView();

    JoinView getJoinView();

    JoinSuccessView getJoinSuccessView();

    DashboardView getDashboardView();

    DailyDetailView getDailyDetailView();

    AccountListView getAccountListView();




    UserSettingsView getUserSettingsView();

    ChangeEmailView getChangeEmailView();

    ChangePasswordView getChangePasswordView();

    ActivationKeysView getActivationKeysView();

    ComparisonView getComparisonView();

    AccountMTUView getAccountMTUView();

    AccountView getAccountView(AccountRole accountRole);

    AddMemberView getAddMemberView();

    AccountMembershipView getAccountMembershipView();

    LocationListView getLocationListView();

    LocationEditView getLocationEditView();

    LocationEditView getReadOnlyLocationEditView();

    LocationMTUView getLocationMTUView();



    ComparisonGraphView getComparisonGraphView();


    ConfirmEmailView getConfirmEmailView();

    GraphingView getGraphingView();

    ExportView getExportView();

    BillingView getBillingView();


    GraphingOptionsView getGraphingOptionsView();

    GraphRequest getGraphRequest();
    void setGraphRequest(GraphRequest graphRequest);

    void onResize(ResizeEvent resizeEvent);

    EnergyPlanListView getEnergyPlanListView();

    EnergyPlanRowView getEnergyPlanRowView();

    void clearInstance();

    void setResizeHandler(ResizeHandler o);

    EnergyPlanView getEnergyPlanView();

    SeasonView getSeasonView();

    ForgotPasswordView getForgotPasswordView();

    ResetPasswordView getResetPasswordView();

    NoPostView getNoPostView();


    boolean getShowMaintenanceDialog();
    void setShowMaintenanceDialog(boolean v);

    AdviceEditView getAdviceEditView();

    AdviceListView getAdviceListView();
}
