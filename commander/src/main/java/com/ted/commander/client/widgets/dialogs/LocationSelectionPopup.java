/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.widgets.dialogs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.petecode.common.client.events.ItemSelectedEvent;
import com.petecode.common.client.events.ItemSelectedHandler;
import com.ted.commander.client.widgets.AccountPicker;
import com.ted.commander.client.widgets.ClickableLocationRow;
import com.ted.commander.common.model.Account;
import com.ted.commander.common.model.AccountLocation;
import com.ted.commander.common.model.AccountMembership;

import java.util.*;
import java.util.logging.Logger;


public class LocationSelectionPopup extends PopupPanel {
    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
    final HandlerManager handlerManager = new HandlerManager(this);

    static final Logger LOGGER = Logger.getLogger(LocationSelectionPopup.class.getName());



    @UiField
    HTMLPanel htmlPanel;
    @UiField
    DivElement dialogDiv;
    @UiField
    AccountPicker accountPicker;
    private AccountLocation selectedLocation;

    final List<AccountLocation> accountLocationList;
    final HashMap<Long, AccountMembership> membershipHashMap;



    @UiField
    VerticalPanel locationVP;


    public LocationSelectionPopup(final List<AccountLocation> accountLocationList) {
        super(true);
        setWidget(uiBinder.createAndBindUi(this));



        this.accountLocationList = accountLocationList;
        membershipHashMap = new HashMap<Long, AccountMembership>();
        AccountMembership allAccounts = new AccountMembership();
        allAccounts.setAccount(new Account());
        allAccounts.getAccount().setId(0l);
        allAccounts.getAccount().setName("All Accounts");
        membershipHashMap.put(0l, allAccounts);
        accountPicker.addItem(allAccounts);
        accountPicker.setValue(allAccounts);

        List<AccountMembership> accountMembershipList =new ArrayList<AccountMembership>();
        for (AccountLocation  accountLocation: accountLocationList){
            if (membershipHashMap.get(accountLocation.getVirtualECC().getAccountId()) == null) {
                AccountMembership accountMembership = new AccountMembership();
                accountMembership.setAccount(new Account());
                accountMembership.getAccount().setName(accountLocation.getAccountName());
                accountMembership.getAccount().setId(accountLocation.getVirtualECC().getAccountId());
                membershipHashMap.put(accountMembership.getAccount().getId(), accountMembership);
                accountPicker.addItem(accountMembership);
            }
        }

        accountPicker.addValueChangeHandler(new ValueChangeHandler<AccountMembership>() {
            @Override
            public void onValueChange(ValueChangeEvent<AccountMembership> valueChangeEvent) {
                updateLocationList(valueChangeEvent.getValue());
            }
        });

        updateLocationList(accountPicker.getValue());

        setAnimationEnabled(true);
        setAnimationType(AnimationType.CENTER);
        setModal(true);
        setGlassEnabled(true);
        Style glassStyle = getGlassElement().getStyle();
        glassStyle.setProperty("width", "100%");
        glassStyle.setProperty("height", "100%");
        glassStyle.setProperty("backgroundColor", "#000");
        glassStyle.setProperty("opacity", "0.45");
        glassStyle.setProperty("mozOpacity", "0.45");
        glassStyle.setProperty("filter", " alpha(opacity=45)");
        dialogDiv.getStyle().setZIndex(2147483647);


    }

    ItemSelectedHandler<AccountLocation> selectedHandler = new ItemSelectedHandler<AccountLocation>() {
        @Override
        public void onSelected(ItemSelectedEvent<AccountLocation> itemSelectedEvent) {
            handlerManager.fireEvent(itemSelectedEvent);
            hide();
        }
    };

    private void updateLocationList(AccountMembership value) {
        LOGGER.info("Update Location List: " + value.getAccount().getName());
        List<AccountLocation> filteredList = new ArrayList<AccountLocation>();

        //Add locations
        for (AccountLocation accountLocation: accountLocationList){
            if (value.getAccount().getId() == 0L || value.getAccount().getId().equals(accountLocation.getVirtualECC().getAccountId())){
                filteredList.add(accountLocation);
            }
        }

        //Sort Locations
        Collections.sort(filteredList, new Comparator<AccountLocation>() {
            @Override
            public int compare(AccountLocation o1, AccountLocation o2) {
                return o1.getVirtualECC().getName().compareTo(o2.getVirtualECC().getName());
            }
        });

        locationVP.clear();
        for (AccountLocation accountLocation: filteredList){

            ClickableLocationRow clickableLocationRow = new ClickableLocationRow(accountLocation);
            locationVP.add(clickableLocationRow);
            clickableLocationRow.addItemSelectedHandler(selectedHandler);
        }

    }

    @Override
    protected void onAttach() {
        super.onAttach();
        GWT.log(">>>>>>>>>>>>> ON ATTACH");
    }

    public void open() {
        this.center();
        this.show();
    }

    public HandlerRegistration addItemSelectedHandler(ItemSelectedHandler<AccountLocation> handler){
        return handlerManager.addHandler(ItemSelectedEvent.TYPE, handler);
    }

    interface MyUiBinder extends UiBinder<Widget, LocationSelectionPopup> {
    }

}
