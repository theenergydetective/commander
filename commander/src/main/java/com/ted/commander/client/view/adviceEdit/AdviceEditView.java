/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.adviceEdit;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.ted.commander.common.model.AccountLocation;
import com.ted.commander.common.model.AdviceRecipient;
import com.ted.commander.common.model.AdviceTrigger;
import com.ted.commander.common.model.EnergyPlan;
import com.vaadin.polymer.paper.widget.PaperInput;

import java.util.List;

public interface AdviceEditView extends IsWidget {


    void setLocationList(List<AccountLocation> locationList);
    HasValue<AccountLocation> locationPicker();
    PaperInput adviceName();


    void setRecipients(List<AdviceRecipient> recipients);
    void setTriggers(List<AdviceTrigger> triggers);

    void setPresenter(AdviceEditView.Presenter presenter);

    void showToast();

    void setReadOnly(boolean isReadOnly);



    public interface Presenter extends com.ted.commander.client.presenter.Presenter {

        void deleteAdvice();

        void goBack();

        void editRecipient(AdviceRecipient item);
        void createRecipient();

        void createTrigger();
        void editTrigger(AdviceTrigger item);



        EnergyPlan getEnergyPlan();

    }


}
