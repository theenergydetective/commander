
package com.ted.commander.client.view.energyPlanList;


import com.google.gwt.user.client.ui.IsWidget;
import com.ted.commander.common.model.AccountMembership;
import com.ted.commander.common.model.AccountMemberships;
import com.ted.commander.common.model.EnergyPlanKey;

public interface EnergyPlanListView extends IsWidget {

    void setPresenter(Presenter presenter);

    void onResize();

    void setAccountMemberships(AccountMemberships accountMemberships);

    void setAccountMembership(AccountMembership accountMembership);

    void clearEnergyPlans();

    void addEnergyPlan(EnergyPlanRowPresenter energyPlanRo);

    public interface Presenter extends com.ted.commander.client.presenter.Presenter {
        void selectAccount(AccountMembership accountMembership);
        void selectEnergyPlan(EnergyPlanKey energyPlanKey);
        void addEnergyPlan();

    }
}
