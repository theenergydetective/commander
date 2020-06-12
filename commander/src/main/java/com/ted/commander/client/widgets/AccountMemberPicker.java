package com.ted.commander.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.Style;
import com.google.gwt.uibinder.client.UiConstructor;
import com.petecode.common.client.events.CloseEvent;
import com.petecode.common.client.events.CloseHandler;
import com.petecode.common.client.model.GroupedNameValuePair;
import com.petecode.common.client.model.NameValuePair;
import com.petecode.common.client.widget.paper.PaperDropDown;
import com.petecode.common.client.widget.paper.simplePicker.SimplePickerOverlay;
import com.ted.commander.common.model.AccountMember;

import java.util.HashMap;


public class AccountMemberPicker extends PaperDropDown<AccountMember> {

    final GroupedNameValuePair pickerData = new GroupedNameValuePair();
    final HashMap<Long, AccountMember> accountMemberHashMap = new HashMap<Long, AccountMember>();


    private String popupWidth;
    private boolean readOnly = false;

    @UiConstructor
    public AccountMemberPicker(String popupWidth) {
        super();
        setPopupWidth(popupWidth);
    }

    @Override
    public String formatDisplayValue(AccountMember value) {
        if (value == null) return null;
        return value.getUser().getFormattedName();
    }




    @Override
    public void showPicker() {
        if (this.readOnly) return;

        GWT.runAsync(new RunAsyncCallback() {
            @Override
            public void onFailure(Throwable throwable) {

            }

            @Override
            public void onSuccess() {
                //Don't show if we have no data.
                if (pickerData.getGroupSize() > 0) {
                    String selectedId = null;
                    if (valueChangeManager.getValue() != null) selectedId = valueChangeManager.getValue().getUser().getId().toString();
                    final SimplePickerOverlay pickerOverlay = new SimplePickerOverlay(getText(), selectedId, pickerData);
                    pickerOverlay.addCloseHandler(new CloseHandler() {
                        @Override
                        public void onClose(CloseEvent event) {
                            if (!pickerOverlay.getValue().equals(valueChangeManager.getValue())) {
                                String id = pickerOverlay.getValue();
                                if (id != null){
                                    Long userId = Long.parseLong(id);
                                    setValue(accountMemberHashMap.get(userId), true);
                                    setFocus(true);
                                }

                            }
                        }
                    });
                    pickerOverlay.setWidth(popupWidth);
                    pickerOverlay.setGlassEnabled(true);
                    pickerOverlay.setModal(true);
                    pickerOverlay.center();
                    pickerOverlay.show();
                }
            }
        });
    }

    public void addItem(AccountMember accountMember) {
        accountMemberHashMap.put(accountMember.getUser().getId(), accountMember);
        NameValuePair group = new NameValuePair(accountMember.getUser().getUsername(), accountMember.getUser().getUsername());
        NameValuePair item = new NameValuePair(accountMember.getUser().getFormattedName(), accountMember.getUser().getId().toString());
        pickerData.addItem(group, item);
    }

    public void clear(){
        pickerData.clear();
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
