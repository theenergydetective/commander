/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.clientFactory;


import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.web.bindery.event.shared.EventBus;
import com.petecode.common.client.logging.ConsoleLoggerFactory;
import com.ted.commander.client.model.Instance;
import com.ted.commander.client.model.OAuthResponse;
import com.ted.commander.client.view.accountList.AccountListView;
import com.ted.commander.client.view.accountList.AccountListViewImpl;
import com.ted.commander.client.view.accountMTU.AccountMTUView;
import com.ted.commander.client.view.accountMTU.AccountMTUViewImpl;
import com.ted.commander.client.view.accountMembership.AccountMembershipView;
import com.ted.commander.client.view.accountMembership.AccountMembershipViewImpl;
import com.ted.commander.client.view.accountView.AccountView;
import com.ted.commander.client.view.accountView.AccountViewImpl;
import com.ted.commander.client.view.accountView.accountMembers.addMember.AddMemberView;
import com.ted.commander.client.view.accountView.accountMembers.addMember.AddMemberViewImpl;
import com.ted.commander.client.view.activationKeys.ActivationKeysView;
import com.ted.commander.client.view.activationKeys.ActivationKeysViewImpl;
import com.ted.commander.client.view.adviceEdit.AdviceEditImpl;
import com.ted.commander.client.view.adviceEdit.AdviceEditView;
import com.ted.commander.client.view.adviceList.AdviceListImpl;
import com.ted.commander.client.view.adviceList.AdviceListView;
import com.ted.commander.client.view.billing.BillingView;
import com.ted.commander.client.view.billing.BillingViewImpl;
import com.ted.commander.client.view.comparison.ComparisonView;
import com.ted.commander.client.view.comparison.ComparisonViewImpl;
import com.ted.commander.client.view.comparisonGraph.ComparisonGraphView;
import com.ted.commander.client.view.comparisonGraph.ComparisonGraphViewImpl;
import com.ted.commander.client.view.confirmEmail.ConfirmEmailView;
import com.ted.commander.client.view.confirmEmail.ConfirmEmailViewImpl;
import com.ted.commander.client.view.dailyDetail.DailyDetailView;
import com.ted.commander.client.view.dailyDetail.DailyDetailViewImpl;
import com.ted.commander.client.view.dashboard.DashboardView;
import com.ted.commander.client.view.dashboard.DashboardViewImpl;
import com.ted.commander.client.view.energyPlan.EnergyPlanImpl;
import com.ted.commander.client.view.energyPlan.EnergyPlanView;
import com.ted.commander.client.view.energyPlan.SeasonImpl;
import com.ted.commander.client.view.energyPlan.SeasonView;
import com.ted.commander.client.view.energyPlanList.EnergyPlanListImpl;
import com.ted.commander.client.view.energyPlanList.EnergyPlanListView;
import com.ted.commander.client.view.energyPlanList.EnergyPlanRowImpl;
import com.ted.commander.client.view.energyPlanList.EnergyPlanRowView;
import com.ted.commander.client.view.export.ExportView;
import com.ted.commander.client.view.export.ExportViewImpl;
import com.ted.commander.client.view.forgotPassword.ForgotPasswordView;
import com.ted.commander.client.view.forgotPassword.ForgotPasswordViewImpl;
import com.ted.commander.client.view.graphing.GraphingOptionsImpl;
import com.ted.commander.client.view.graphing.GraphingOptionsView;
import com.ted.commander.client.view.graphing.GraphingView;
import com.ted.commander.client.view.graphing.GraphingViewImpl;
import com.ted.commander.client.view.join.JoinSuccessView;
import com.ted.commander.client.view.join.JoinSuccessViewImpl;
import com.ted.commander.client.view.join.JoinView;
import com.ted.commander.client.view.join.JoinViewImpl;
import com.ted.commander.client.view.locationEdit.LocationEditView;
import com.ted.commander.client.view.locationEdit.LocationEditViewImpl;
import com.ted.commander.client.view.locationList.LocationListView;
import com.ted.commander.client.view.locationList.LocationListViewImpl;
import com.ted.commander.client.view.locationMTU.LocationMTUView;
import com.ted.commander.client.view.locationMTU.LocationMTUViewImpl;
import com.ted.commander.client.view.login.LoginView;
import com.ted.commander.client.view.login.LoginViewImpl;
import com.ted.commander.client.view.noPost.NoPostImpl;
import com.ted.commander.client.view.noPost.NoPostView;
import com.ted.commander.client.view.resetPassword.ResetPasswordView;
import com.ted.commander.client.view.resetPassword.ResetPasswordViewImpl;
import com.ted.commander.client.view.userSettings.ChangeEmailView;
import com.ted.commander.client.view.userSettings.ChangePasswordView;
import com.ted.commander.client.view.userSettings.UserSettingsView;
import com.ted.commander.client.view.userSettings.impl.ChangeEmailViewImpl;
import com.ted.commander.client.view.userSettings.impl.ChangePasswordViewImpl;
import com.ted.commander.client.view.userSettings.impl.UserSettingsViewImpl;
import com.ted.commander.common.enums.AccountRole;
import com.ted.commander.common.model.AccountMemberships;
import com.ted.commander.common.model.CalendarKey;
import com.ted.commander.common.model.EnergyPlan;
import com.ted.commander.common.model.User;
import com.ted.commander.common.model.export.GraphRequest;

import java.util.Date;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * Client factory for desktop
 */
public class ClientFactoryImpl implements ClientFactory {
    private static Logger LOGGER = ConsoleLoggerFactory.getLogger(ClientFactoryImpl.class);
    final Instance instance = Instance.load();
    OAuthResponse oAuthResponse = null;
    private final EventBus eventBus = new SimpleEventBus();
    private final PlaceController placeController = new PlaceController(eventBus);


    protected AccountMemberships accountMemberships;
    protected HashMap<Long, EnergyPlan> energyPlanCache = new HashMap<Long, EnergyPlan>();
    CalendarKey dailyDisplayKey;
    ResizeHandler resizeHandler;


    private User user;


    GraphRequest graphRequest = null;

    public ClientFactoryImpl() {

    }

    @Override
    public Instance getInstance() {
        return instance;
    }

    @Override
    public OAuthResponse getOAuthResponse() {
        return oAuthResponse;
    }

    @Override
    public void setOAuthResponse(OAuthResponse oAuthResponse) {
        this.oAuthResponse = oAuthResponse;
        if (this.oAuthResponse != null) {
            long expiresEpoch = (new Date()).getTime() + (this.oAuthResponse.getExpires_in() * 1000);
            this.oAuthResponse.setExpiresEpoch(expiresEpoch);
            LOGGER.fine("ACCESS TOKEN EXPIRATION SET TO " + new Date(this.oAuthResponse.getExpiresEpoch()));
        } else {
            LOGGER.fine("CLEARED oAuthResponse");
        }
    }

    @Override
    public EventBus getEventBus() {
        return eventBus;
    }

    @Override
    public PlaceController getController() {
        return placeController;
    }

    @Override
    public LoginView getLoginView() {
            return new LoginViewImpl();
    }

    @Override
    public JoinView getJoinView() {
            return new JoinViewImpl();
    }

    @Override
    public JoinSuccessView getJoinSuccessView() {
            return new JoinSuccessViewImpl();
    }

    @Override
    public DashboardView getDashboardView() {
            return new DashboardViewImpl();
    }

    @Override
    public DailyDetailView getDailyDetailView() {
            return new DailyDetailViewImpl();
    }

    @Override
    public AccountListView getAccountListView() {
            return new AccountListViewImpl();
    }



    @Override
    public UserSettingsView getUserSettingsView() {
            return new UserSettingsViewImpl();
    }

    @Override
    public ChangeEmailView getChangeEmailView() {
            return new ChangeEmailViewImpl();
    }

    @Override
    public ChangePasswordView getChangePasswordView() {
            return new ChangePasswordViewImpl();
    }

    @Override
    public ActivationKeysView getActivationKeysView() {
            return new ActivationKeysViewImpl();
    }

    @Override
    public ComparisonView getComparisonView() {
            return new ComparisonViewImpl();
    }

    @Override
    public AccountMTUView getAccountMTUView() {
        return new AccountMTUViewImpl();
    }

    @Override
    public AccountView getAccountView(AccountRole accountRole) {
        return  new AccountViewImpl(accountRole);
    }

    @Override
    public AddMemberView getAddMemberView() {
        return new AddMemberViewImpl();
    }

    @Override
    public AccountMembershipView getAccountMembershipView() {
            return new AccountMembershipViewImpl();
    }

    @Override
    public LocationListView getLocationListView() {
        return new LocationListViewImpl();
    }

    @Override
    public LocationEditView getLocationEditView() {
        return new LocationEditViewImpl(false);
    }

    @Override
    public LocationEditView getReadOnlyLocationEditView() {
        return new LocationEditViewImpl(true);
    }

    @Override
    public LocationMTUView getLocationMTUView() {
            return new LocationMTUViewImpl();
    }


    @Override
    public ComparisonGraphView getComparisonGraphView() {
        return new ComparisonGraphViewImpl();
    }


    @Override
    public ConfirmEmailView getConfirmEmailView() {
        return new ConfirmEmailViewImpl();
    }


    @Override
    public GraphingView getGraphingView() {
            return new GraphingViewImpl();
    }


    @Override
    public ExportView getExportView() {
        return new ExportViewImpl();
    }

    @Override
    public BillingView getBillingView() {
        return new BillingViewImpl();
    }

    @Override
    public GraphingOptionsView getGraphingOptionsView() {
        return new GraphingOptionsImpl();
    }


    @Override
    public User getUser() {
        return user;
    }

    @Override
    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public GraphRequest getGraphRequest() {
        return graphRequest;
    }

    @Override
    public void setGraphRequest(GraphRequest graphRequest) {
        this.graphRequest = graphRequest;
    }


    @Override
    public void onResize(ResizeEvent resizeEvent) {
        if (resizeHandler != null) resizeHandler.onResize(resizeEvent);
    }

    @Override
    public void setResizeHandler(ResizeHandler resizeHandler) {
        this.resizeHandler = resizeHandler;
    }

    @Override
    public EnergyPlanView getEnergyPlanView() {
        return new EnergyPlanImpl();
    }

    @Override
    public SeasonView getSeasonView() {
        return new SeasonImpl();
    }

    @Override
    public ForgotPasswordView getForgotPasswordView() {
        return new ForgotPasswordViewImpl();
    }

    @Override
    public ResetPasswordView getResetPasswordView() {
        return new ResetPasswordViewImpl();
    }

    @Override
    public NoPostView getNoPostView() {
        return new NoPostImpl();
    }

    boolean showMaintenanceDialog = true;

    @Override
    public boolean getShowMaintenanceDialog() {
        return showMaintenanceDialog;
    }

    @Override
    public void setShowMaintenanceDialog(boolean v) {
        showMaintenanceDialog = v;
    }

    @Override
    public AdviceEditView getAdviceEditView() {
        return new AdviceEditImpl();
    }

    @Override
    public AdviceListView getAdviceListView() {
        return new AdviceListImpl();
    }


    @Override
    public EnergyPlanListView getEnergyPlanListView() {
        return new EnergyPlanListImpl();
    }

    @Override
    public EnergyPlanRowView getEnergyPlanRowView() {
        return new EnergyPlanRowImpl();
    }

    @Override
    public void clearInstance() {
        Instance.clear(instance);
        setUser(null);
        setOAuthResponse(null);
    }


}


