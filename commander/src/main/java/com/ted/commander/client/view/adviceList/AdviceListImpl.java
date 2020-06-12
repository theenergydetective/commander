/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.adviceList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.petecode.common.client.events.ItemSelectedEvent;
import com.petecode.common.client.events.ItemSelectedHandler;
import com.ted.commander.client.places.DashboardPlace;
import com.ted.commander.client.widgets.AccountPicker;
import com.ted.commander.common.model.AccountMembership;
import com.ted.commander.common.model.AccountMemberships;
import com.ted.commander.common.model.Advice;
import com.vaadin.polymer.paper.widget.PaperFab;
import com.vaadin.polymer.paper.widget.PaperIconButton;

import java.util.List;
import java.util.logging.Logger;


public class AdviceListImpl extends Composite implements AdviceListView {

    static final Logger LOGGER = Logger.getLogger(AdviceListImpl.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);
    Presenter presenter;

    @UiField
    AccountPicker accountPicker;
    @UiField
    DivElement noAdviceField;
    @UiField
    PaperFab addButton;
    @UiField
    PaperIconButton backButton;
    @UiField
    VerticalPanel advicePanel;
    @UiField
    Element adviceDiv;

    ItemSelectedHandler<Advice> adviceItemSelectedHandler = new ItemSelectedHandler<Advice>() {
        @Override
        public void onSelected(ItemSelectedEvent<Advice> event) {
            presenter.selectAdvice(event.getItem());
        }
    };

    public AdviceListImpl() {
        initWidget(defaultBinder.createAndBindUi(this));

        addButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                presenter.createAdvice();
            }
        });

        backButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                presenter.goTo(new DashboardPlace(""));
            }
        });

        accountPicker.useTitle();

        accountPicker.addValueChangeHandler(new ValueChangeHandler<AccountMembership>() {
            @Override
            public void onValueChange(ValueChangeEvent<AccountMembership> valueChangeEvent) {
                presenter.selectAccount(valueChangeEvent.getValue());
            }
        });
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setAccountMemberships(AccountMemberships accountMemberships) {
        accountPicker.clear();
        for (AccountMembership accountMembership: accountMemberships.getAccountMemberships()){
            accountPicker.addItem(accountMembership);
        }
    }

    @Override
    public void setAccountMembership(AccountMembership accountMembership) {
        accountPicker.setValue(accountMembership, false);
    }

    @Override
    public void setAdvice(List<Advice> adviceList) {
        advicePanel.clear();
        if (adviceList.size() == 0){
            noAdviceField.getStyle().clearDisplay();
            adviceDiv.getStyle().setDisplay(Style.Display.NONE);
        } else {
            adviceDiv.getStyle().clearDisplay();
            noAdviceField.getStyle().setDisplay(Style.Display.NONE);

            boolean drawDivider = false;
            for (Advice advice: adviceList){
                AdviceTile adviceTile = new AdviceTile(advice, drawDivider );
                drawDivider = true;
                adviceTile.addItemSelectedHandler(adviceItemSelectedHandler);
                advicePanel.add(adviceTile);
            }
        }


    }

    @Override
    public void showCreateIcon(boolean b) {
        if (b) {
            addButton.getElement().getStyle().clearDisplay();
        } else {
            addButton.getElement().getStyle().setDisplay(Style.Display.NONE);
        }
    }

    interface DefaultBinder extends UiBinder<Widget, AdviceListImpl> {
    }


}
