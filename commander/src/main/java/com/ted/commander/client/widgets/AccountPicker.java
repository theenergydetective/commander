package com.ted.commander.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.uibinder.client.UiConstructor;
import com.petecode.common.client.events.CloseEvent;
import com.petecode.common.client.events.CloseHandler;
import com.petecode.common.client.model.GroupedNameValuePair;
import com.petecode.common.client.model.NameValuePair;
import com.petecode.common.client.widget.paper.PaperDropDown;
import com.petecode.common.client.widget.paper.simplePicker.SimplePickerOverlay;
import com.ted.commander.common.model.AccountMembership;

import java.util.HashMap;


public class AccountPicker extends PaperDropDown<AccountMembership> {

    final GroupedNameValuePair pickerData = new GroupedNameValuePair();
    final HashMap<Long, AccountMembership> accountHashMap = new HashMap<Long, AccountMembership>();





    @UiConstructor
    public AccountPicker() {
        super();
    }

    public void useTitle(){
        underlineField.getStyle().setColor("#FFF");;
        underlineField.getStyle().setBackgroundColor("#FFF");;
        valueField.getStyle().setColor("#FFF");;
        labelField.getStyle().setColor("#FFF");;
        dropdownIcon.getStyle().setColor("#FFF");
    }

    @Override
    public String formatDisplayValue(AccountMembership value) {
        if (value == null) return null;
        return value.getAccount().getName();
    }



    @Override
    public void showPicker() {
        GWT.runAsync(new RunAsyncCallback() {
            @Override
            public void onFailure(Throwable throwable) {
                GWT.log("Async Callback Failure");
            }

            @Override
            public void onSuccess() {
                //Don't show if we have no data.
                if (pickerData.getGroupSize() > 0) {
                    String value = null;
                    if (valueChangeManager.getValue() != null)
                        value = valueChangeManager.getValue().getAccount().getId().toString();

                    final SimplePickerOverlay pickerOverlay = new SimplePickerOverlay(getText(), value, pickerData, false, false, false);

                    pickerOverlay.addCloseHandler(new CloseHandler() {
                        @Override
                        public void onClose(CloseEvent event) {
                            if (pickerOverlay.getValue() != null) {
                                if (!pickerOverlay.getValue().equals(valueChangeManager.getValue())) {
                                    String id = pickerOverlay.getValue();
                                    if (id != null) {
                                        Long accountId = Long.parseLong(id);
                                        setValue(accountHashMap.get(accountId), true);
                                        setFocus(true);
                                    }
                                }
                            }
                        }
                    });


                    int pw = getElement().getClientWidth();
                    if (pw < 320) pw = 320;
                    if (pw > 600) pw = 600;
                    pw -= 32;
                    pickerOverlay.setWidth(pw + "px");


                    pickerOverlay.setGlassEnabled(false);
                    pickerOverlay.setModal(true);

                    pickerOverlay.setPopupPosition(getAbsoluteLeft() - 20, getAbsoluteTop());
                    pickerOverlay.show();


                }
            }
        });

    }

    public void addItem(AccountMembership accountMembership) {
        accountHashMap.put(accountMembership.getAccount().getId(), accountMembership);
        NameValuePair group = new NameValuePair(accountMembership.getAccount().getName(), accountMembership.getAccount().getName());
        NameValuePair item = new NameValuePair(accountMembership.getAccount().getName(), accountMembership.getAccount().getId().toString());
        pickerData.addItem(group, item);
    }

    public void clear(){
        pickerData.clear();
    }


    
}
