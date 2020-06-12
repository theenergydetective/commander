/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.billing;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.place.shared.Place;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.petecode.common.client.events.ItemSelectedEvent;
import com.petecode.common.client.events.ItemSelectedHandler;
import com.petecode.common.client.model.DateRange;
import com.petecode.common.client.widget.PaperCheckboxElementManager;
import com.ted.commander.client.places.ComparisonPlace;
import com.ted.commander.client.radioManager.DataExportFileTypeManager;
import com.ted.commander.client.radioManager.HistoryTypeRadioManager;
import com.ted.commander.client.view.LoadingOverlay;
import com.ted.commander.client.widgets.datePicker.DatePicker;
import com.ted.commander.client.widgets.dialogs.AlertDialog;
import com.ted.commander.client.widgets.toolbar.TitleBar;
import com.ted.commander.common.enums.DataExportFileType;
import com.ted.commander.common.enums.HistoryType;
import com.ted.commander.common.model.AccountLocation;
import com.vaadin.polymer.elemental.Event;
import com.vaadin.polymer.elemental.EventListener;
import com.vaadin.polymer.paper.element.PaperButtonElement;
import com.vaadin.polymer.paper.element.PaperRadioGroupElement;
import com.vaadin.polymer.paper.widget.PaperButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;


public class BillingViewImpl extends Composite implements BillingView {
    static final Logger LOGGER = Logger.getLogger(BillingViewImpl.class.getName());

    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);

    Presenter presenter;
    @UiField
    TitleBar titleBar;

    @UiField
    PaperRadioGroupElement dataExportTypeGroup;
    @UiField
    PaperButton exportButton;
    @UiField
    VerticalPanel dataPointPanel;
    @UiField
    Frame downloadFrame;
    @UiField
    DatePicker datePicker;
    @UiField
    PaperButtonElement allButton;
    @UiField
    PaperButtonElement clearButton;
    @UiField
    PaperRadioGroupElement historyTypeGroup;

    HistoryTypeRadioManager historyTypeRadioManager;

    final ArrayList<PaperCheckboxElementManager> paperCheckboxElementManagerArrayList = new ArrayList<PaperCheckboxElementManager>();
    final DataExportFileTypeManager dataExportFileTypeManager;

    public BillingViewImpl() {
        initWidget(defaultBinder.createAndBindUi(this));

        titleBar.setSelectedPlace(new ComparisonPlace(""));
        titleBar.addItemSelectedHandler(new ItemSelectedHandler<Place>() {
            @Override
            public void onSelected(ItemSelectedEvent<Place> event) {
                presenter.goTo(event.getItem());
            }
        });


        downloadFrame.setVisible(true);
        downloadFrame.setSize("1px", "1px");
        downloadFrame.getElement().getStyle().setDisplay(Style.Display.NONE);

        dataExportFileTypeManager = new DataExportFileTypeManager(dataExportTypeGroup);

        historyTypeRadioManager = new HistoryTypeRadioManager(historyTypeGroup);
        historyTypeRadioManager.addValueChangeHandler(new ValueChangeHandler<HistoryType>() {
            @Override
            public void onValueChange(ValueChangeEvent<HistoryType> valueChangeEvent) {
                datePicker.setHistoryType(valueChangeEvent.getValue());
            }
        });

        exportButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                boolean selected = false;
                for (int i=0; i < dataPointPanel.getWidgetCount(); i++){
                    BillingLocationRow billingLocationRow = (BillingLocationRow) dataPointPanel.getWidget(i);
                    if (billingLocationRow.getChecked()){
                        selected =true;
                        break;
                    }
                }

                if (!selected){
                    AlertDialog alertDialog = new AlertDialog();
                    alertDialog.open("Location Not Selected", "Please select at least one location.");
                } else {
                    presenter.doExport();
                }

            }
        });
        datePicker.setHistoryType(HistoryType.BILLING_CYCLE);

        dataExportFileTypeManager.setValue(DataExportFileType.CSV);
        dataExportTypeGroup.setSelected("CSV");

        allButton.addEventListener("click", new EventListener() {
            @Override
            public void handleEvent(Event event) {
                for (int i=0; i < dataPointPanel.getWidgetCount(); i++){
                    BillingLocationRow billingLocationRow = (BillingLocationRow) dataPointPanel.getWidget(i);
                    billingLocationRow.setChecked(true);
                }
            }
        });

        clearButton.addEventListener("click", new EventListener() {
            @Override
            public void handleEvent(Event event) {
                for (int i=0; i < dataPointPanel.getWidgetCount(); i++){
                    BillingLocationRow billingLocationRow = (BillingLocationRow) dataPointPanel.getWidget(i);
                    billingLocationRow.setChecked(false);
                }
            }
        });
    }

    @Override
    public void setLogo(ImageResource imageResource) {
//        logo.setResource(imageResource);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public HasValue<DateRange> getDateRangeField() {
        return datePicker;
    }


    @Override
    public HasValue<DataExportFileType> getDataExportFileType() {
        return dataExportFileTypeManager;
    }

    @Override
    public List<AccountLocation> getLocationsForExport() {
        List<AccountLocation> exportList = new ArrayList<AccountLocation>();
        for (int i=0; i < dataPointPanel.getWidgetCount(); i++){
            BillingLocationRow billingLocationRow = (BillingLocationRow) dataPointPanel.getWidget(i);
            if (billingLocationRow.getChecked()){
                exportList.add(billingLocationRow.getAccountLocation());
            }
        }
        return exportList;
    }

    @Override
    public HasValue<HistoryType> getHistoryType() {
        return historyTypeRadioManager;
    }


    @Override
    public void setLoadingVisible(boolean isVisible) {
        if (isVisible) LoadingOverlay.get().show();
        else LoadingOverlay.get().hide();

    }


    @Override
    public void setLocationList(List<AccountLocation> accountLocationList) {

        Collections.sort(accountLocationList, new Comparator<AccountLocation>(){
            @Override
            public int compare(AccountLocation o1, AccountLocation o2) {
                return o1.getVirtualECC().getName().compareTo(o2.getVirtualECC().getName());
            }
        });

        dataPointPanel.clear();
        for (int i=0; i < accountLocationList.size(); i++){
            AccountLocation accountLocation =accountLocationList.get(i);
            dataPointPanel.add(new BillingLocationRow(accountLocation));
        }
    }

    public final native String getUserAgent() /*-{
        return navigator.userAgent.toLowerCase();
    }-*/;

    private boolean isIE() {
        String ua = getUserAgent();
        if (ua.contains("edge")) return true;
        if (ua.contains("msie")) return true;
        if (ua.contains("firefox")) return false;
        if (ua.contains("safari")) return false;
        if (ua.contains("chrome")) return false;
        return true;
    }

    @Override
    public void setDownloadUrl(String url) {
        LOGGER.fine("Downloading: " + url + "   UA:" + getUserAgent());
        if (isIE()) {
            Window.open(url, "_blank", "");
        } else {
            downloadFrame.setUrl(url);
        }
    }

    @Override
    public void scrollToTop() {
        Window.scrollTo(0,0);
    }

    @Override
    public void clearDataExportPoints() {
    }

    interface DefaultBinder extends UiBinder<Widget, BillingViewImpl> {
    }


}

