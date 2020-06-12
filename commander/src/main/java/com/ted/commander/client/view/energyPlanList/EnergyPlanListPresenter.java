/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.energyPlanList;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.ted.commander.client.callback.DefaultMethodCallback;
import com.ted.commander.client.clientFactory.ClientFactory;
import com.ted.commander.client.events.PlaceRequestEvent;
import com.ted.commander.client.places.EnergyPlanListPlace;
import com.ted.commander.client.places.EnergyPlanPlace;
import com.ted.commander.client.resources.WebStringResource;
import com.ted.commander.client.restInterface.RESTFactory;
import com.ted.commander.common.model.*;
import org.fusesource.restygwt.client.Method;

import java.util.List;
import java.util.logging.Logger;

public class EnergyPlanListPresenter implements EnergyPlanListView.Presenter, IsWidget {

    static final Logger LOGGER = Logger.getLogger(EnergyPlanListPresenter.class.getName());

    final ClientFactory clientFactory;
    final EnergyPlanListView view;
    final EnergyPlanListPlace place;

    WebStringResource stringRes = WebStringResource.INSTANCE;

    AccountMembership selectedAccount = null;

    public EnergyPlanListPresenter(ClientFactory clientFactory, final EnergyPlanListPlace place) {
        LOGGER.fine("CREATING NEW EnergyPlanListPresenter");
        this.clientFactory = clientFactory;
        this.place = place;
        view = clientFactory.getEnergyPlanListView();
        view.setPresenter(this);




        //Get Account Memberships
        RESTFactory.getUserService(clientFactory).getAccountMemberships(clientFactory.getUser().getId(), new DefaultMethodCallback<AccountMemberships>() {
            @Override
            public void onSuccess(Method method, AccountMemberships accountMemberships) {
                view.setAccountMemberships(accountMemberships);
                LOGGER.fine("USING DEFAULT ACCOUNT: " + place.getAccountId());
                if (place.getAccountId() != null){
                    for (AccountMembership accountMembership : accountMemberships.getAccountMemberships()){
                        if (accountMembership.getAccount().getId().equals(place.getAccountId())){
                            selectAccount(accountMembership);
                            break;
                        }
                    }
                } else {
                    selectAccount(accountMemberships.getAccountMemberships().get(0));

                }
            }
        });
    }


    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public void goTo(Place place) {
        if (isValid()) {
            clientFactory.getEventBus().fireEvent(new PlaceRequestEvent(place, this.place, null));
        }
    }

    @Override
    public void onResize() {
        view.onResize();
    }


    @Override
    public void selectAccount(AccountMembership accountMembership) {
        selectedAccount = accountMembership;
        view.setAccountMembership(accountMembership);
        //Refresh energy plan list


        LOGGER.fine("UPDATING ACCOUNT: " + selectedAccount);
        RESTFactory.getEnergyPlanService(clientFactory).getKeysForAccount(selectedAccount.getAccount().getId(), new DefaultMethodCallback<List<EnergyPlanKey>>() {
            @Override
            public void onSuccess(Method method, List<EnergyPlanKey> energyPlanKeys) {
                LOGGER.fine("FOUND PLANS:" + energyPlanKeys.size());
                view.clearEnergyPlans();
                for (EnergyPlanKey energyPlanKey: energyPlanKeys){
                    EnergyPlanRowPresenter energyPlanRowPresenter = new EnergyPlanRowPresenter(clientFactory, energyPlanKey);
                    view.addEnergyPlan(energyPlanRowPresenter);
                }
            }
        });


    }

    @Override
    public void selectEnergyPlan(EnergyPlanKey energyPlanKey) {
        goTo(new EnergyPlanPlace(energyPlanKey.getId()));
    }

    @Override
    public void addEnergyPlan() {
        EnergyPlan energyPlan= new EnergyPlan();
        energyPlan.setAccountId(selectedAccount.getAccount().getId());

        RESTFactory.getEnergyPlanService(clientFactory).create(energyPlan, new DefaultMethodCallback<RESTPostResponse>() {
            @Override
            public void onSuccess(Method method, RESTPostResponse restPostResponse) {
                goTo(new EnergyPlanPlace(restPostResponse.getId()));
            }
        });
    }
}
