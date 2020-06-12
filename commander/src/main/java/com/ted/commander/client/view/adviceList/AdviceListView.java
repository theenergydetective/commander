/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.adviceList;

import com.google.gwt.user.client.ui.IsWidget;
import com.ted.commander.common.model.AccountMembership;
import com.ted.commander.common.model.AccountMemberships;
import com.ted.commander.common.model.Advice;

import java.util.List;

public interface AdviceListView extends IsWidget {

    void setPresenter(AdviceListView.Presenter presenter);
    void setAccountMemberships(AccountMemberships accountMemberships);
    void setAccountMembership(AccountMembership accountMembership);
    void setAdvice(List<Advice> adviceList);
    void showCreateIcon(boolean b);


    public interface Presenter extends com.ted.commander.client.presenter.Presenter {
        void selectAccount(AccountMembership accountMembership);
        void createAdvice();
        void selectAdvice(Advice item);
    }


}
