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
import com.ted.commander.client.resources.WebStringResource;

import java.util.HashMap;


public class AMPMPicker extends PaperDropDown<String> {

    final GroupedNameValuePair pickerData = new GroupedNameValuePair();
    final HashMap<String, String> dataHashMap = new HashMap<String, String>();

    @UiConstructor
    public AMPMPicker() {
        super();
        NameValuePair group = new NameValuePair("", "");

        dataHashMap.put("AM", "AM");
        dataHashMap.put("PM", "PM");
        pickerData.addItem(group, new NameValuePair(formatDisplayValue("AM"), WebStringResource.INSTANCE.am()));
        pickerData.addItem(group, new NameValuePair(formatDisplayValue("PM"), WebStringResource.INSTANCE.pm()));
    }


    @Override
    public String formatDisplayValue(String val) {
        return val;

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
                    if (valueChangeManager.getValue() != null) value = valueChangeManager.getValue().toString();

                    final SimplePickerOverlay pickerOverlay = new SimplePickerOverlay(getText(), value, pickerData, false, false, false);

                    pickerOverlay.addCloseHandler(new CloseHandler() {
                        @Override
                        public void onClose(CloseEvent event) {
                            if (pickerOverlay.getValue() != null) {
                                if (!pickerOverlay.getValue().equals(valueChangeManager.getValue())) {
                                    String id = pickerOverlay.getValue();
                                    if (id != null) {
                                        setValue(dataHashMap.get(id), true);
                                        setFocus(true);
                                    }
                                }
                            }
                        }
                    });


                    pickerOverlay.setWidth("150px");

                    pickerOverlay.setGlassEnabled(false);
                    pickerOverlay.setModal(true);
                    pickerOverlay.setPopupPosition(getAbsoluteLeft() - 20, getAbsoluteTop());
                    pickerOverlay.show();


                }
            }
        });

    }


    public void clear(){
        pickerData.clear();
    }


}
