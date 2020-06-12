package com.ted.commander.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.i18n.client.CurrencyData;
import com.google.gwt.i18n.client.CurrencyList;
import com.google.gwt.uibinder.client.UiConstructor;
import com.petecode.common.client.events.CloseEvent;
import com.petecode.common.client.events.CloseHandler;
import com.petecode.common.client.model.GroupedNameValuePair;
import com.petecode.common.client.model.NameValuePair;
import com.petecode.common.client.widget.paper.PaperDropDown;
import com.petecode.common.client.widget.paper.simplePicker.SimplePickerOverlay;

import java.util.HashMap;


public class CurrencyPicker extends PaperDropDown<CurrencyData> {

    final GroupedNameValuePair pickerData = new GroupedNameValuePair();
    final HashMap<String, CurrencyData> dataHashMap = new HashMap<String, CurrencyData>();





    @UiConstructor
    public CurrencyPicker() {
        super();

        //Add currency types
        CurrencyData defaultCurrency = CurrencyList.get().getDefault();
        for (CurrencyData currencyData : CurrencyList.get()) {
            if (!currencyData.equals(defaultCurrency) && !currencyData.isDeprecated()) {
                dataHashMap.put(currencyData.getCurrencyCode(), currencyData);
                NameValuePair group = new NameValuePair("", "");
                NameValuePair item = new NameValuePair(formatDisplayValue(currencyData), currencyData.getCurrencyCode());
                pickerData.addItem(group, item);
            }
        }
    }


    @Override
    public String formatDisplayValue(CurrencyData currencyData) {
        if (currencyData == null) return null;
        return CurrencyList.get().lookupName(currencyData.getCurrencyCode()) + " (" + currencyData.getSimpleCurrencySymbol() + ")";
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
                    if (valueChangeManager.getValue() != null) value = valueChangeManager.getValue().getCurrencyCode();

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

                    int pw = getElement().getClientWidth();
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


    public void clear(){
        pickerData.clear();
    }


    
}
