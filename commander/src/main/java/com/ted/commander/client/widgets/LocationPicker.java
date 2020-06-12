package com.ted.commander.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.Style;
import com.google.gwt.uibinder.client.UiConstructor;
import com.petecode.common.client.events.ItemSelectedEvent;
import com.petecode.common.client.events.ItemSelectedHandler;
import com.petecode.common.client.widget.paper.PaperDropDown;
import com.ted.commander.client.widgets.dialogs.LocationSelectionPopup;
import com.ted.commander.common.model.AccountLocation;

import java.util.ArrayList;


public class LocationPicker extends PaperDropDown<AccountLocation> {

    final ArrayList<AccountLocation> accountLocationList = new ArrayList<AccountLocation>();



    private String popupWidth;
    private boolean readOnly = false;

    @UiConstructor
    public LocationPicker(String popupWidth) {
        super();
        setPopupWidth(popupWidth);
    }

    @Override
    public String formatDisplayValue(AccountLocation value) {
        if (value == null) return null;
        return value.getVirtualECC().getName();
    }

    @Override
    public void showPicker() {
        if (this.readOnly) return;

        if (accountLocationList.size() > 0) {
            GWT.runAsync(new RunAsyncCallback() {
                public void onFailure(Throwable throwable) {
                    GWT.log("Async Callback Failure");
                }

                public void onSuccess() {
                    final LocationSelectionPopup locationSelectionPopup = new LocationSelectionPopup(accountLocationList);
                    locationSelectionPopup.open();
                    locationSelectionPopup.addItemSelectedHandler(new ItemSelectedHandler<AccountLocation>() {
                        @Override
                        public void onSelected(ItemSelectedEvent<AccountLocation> itemSelectedEvent) {
                            GWT.log("SELECTED: " + itemSelectedEvent.getItem());
                            AccountLocation accountLocation = itemSelectedEvent.getItem();
                            setValue(accountLocation, true);
                            setFocus(true);
                        }
                    });
                }
            });
        }
    }

    public void addItem(AccountLocation accountLocation) {
        accountLocationList.add(accountLocation);
    }

    public void clear(){
        accountLocationList.clear();
    }

    public void setPopupWidth(String popupWidth) {
        this.popupWidth = popupWidth;
    }

    public String getPopupWidth() {
        return popupWidth;
    }


    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
        if (this.readOnly) {
            dropdownIcon.getStyle().setVisibility(Style.Visibility.HIDDEN);
        } else {
            dropdownIcon.getStyle().clearVisibility();
        }

    }
}
