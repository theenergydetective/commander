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
import java.util.logging.Logger;


public class DemandAverageTimePicker extends PaperDropDown<Integer> {

    final GroupedNameValuePair pickerData = new GroupedNameValuePair();
    final HashMap<String, Integer> dataHashMap = new HashMap<String, Integer>();

    static final Logger LOGGER = Logger.getLogger(DemandAverageTimePicker.class.getName());



    @UiConstructor
    public DemandAverageTimePicker() {
        super();
        dataHashMap.put("5", 5);
        dataHashMap.put("15", 15);
        dataHashMap.put("20", 20);
        dataHashMap.put("30", 30);

        NameValuePair group = new NameValuePair("", "");
        pickerData.addItem(group, new NameValuePair(formatDisplayValue(5), "5"));
        pickerData.addItem(group, new NameValuePair(formatDisplayValue(15), "15"));
        pickerData.addItem(group, new NameValuePair(formatDisplayValue(20), "20"));
        pickerData.addItem(group, new NameValuePair(formatDisplayValue(30), "30"));
    }


    @Override
    public String formatDisplayValue(Integer minutes) {
        return minutes + " " + WebStringResource.INSTANCE.minutes();
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
                                        LOGGER.fine("VALUE: " + dataHashMap.get(id));
                                        setValue(dataHashMap.get(id), true);
                                        setFocus(true);
                                    }
                                }
                            }
                        }
                    });


                    pickerOverlay.setWidth("240 px");

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
