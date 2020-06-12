/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.export;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
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
import com.petecode.common.client.widget.paper.HasValidation;
import com.ted.commander.client.model.DataExportDataPoint;
import com.ted.commander.client.places.ComparisonPlace;
import com.ted.commander.client.radioManager.BooleanTypeRadioManager;
import com.ted.commander.client.radioManager.DataExportFileTypeManager;
import com.ted.commander.client.radioManager.HistoryTypeRadioManager;
import com.ted.commander.client.view.LoadingOverlay;
import com.ted.commander.client.widgets.LocationPicker;
import com.ted.commander.client.widgets.datePicker.DatePicker;
import com.ted.commander.client.widgets.toolbar.TitleBar;
import com.ted.commander.common.enums.DataExportFileType;
import com.ted.commander.common.enums.HistoryType;
import com.ted.commander.common.model.AccountLocation;
import com.vaadin.polymer.paper.element.PaperCheckboxElement;
import com.vaadin.polymer.paper.element.PaperRadioGroupElement;
import com.vaadin.polymer.paper.widget.PaperButton;
import com.vaadin.polymer.paper.widget.PaperCheckbox;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


public class ExportViewImpl extends Composite implements ExportView {
    static final Logger LOGGER = Logger.getLogger(ExportViewImpl.class.getName());

    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);

    Presenter presenter;
    @UiField
    TitleBar titleBar;

    @UiField
    LocationPicker locationPicker;
    @UiField
    PaperRadioGroupElement historyTypeGroup;
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
    DivElement subloadField;
    @UiField
    PaperRadioGroupElement subloadGroup;
    @UiField
    DivElement subloadDataPointField;
    @UiField
    VerticalPanel subloadDataPointPanel;
    @UiField
    PaperRadioGroupElement weatherGroup;
    @UiField
    PaperCheckboxElement allSubloadButton;
    @UiField
    DivElement demandCostDiv;
    @UiField
    PaperRadioGroupElement demandCostGroup;
    PaperCheckboxElementManager allSubloadButtonManager;
    final ArrayList<PaperCheckboxElementManager> paperCheckboxElementManagerArrayList = new ArrayList<PaperCheckboxElementManager>();

    final HistoryTypeRadioManager historyTypeRadioManager;
    final DataExportFileTypeManager dataExportFileTypeManager;
    final CheckBoxManager checkBoxManager;
    final CheckBoxManager subLoadCheckBoxManager;
    final BooleanTypeRadioManager subloadManager;
    final BooleanTypeRadioManager weatherManager;
    final BooleanTypeRadioManager demandCostManager;




    //Wrapper class.
    class CheckBoxManager extends ArrayList<DataExportDataPoint> {

        transient private final VerticalPanel verticalPanel;
        transient private final HandlerManager handlerManager = new HandlerManager(this);

        public CheckBoxManager(VerticalPanel verticalPanel) {
            super();
            this.verticalPanel = verticalPanel;
        }

        CheckBoxManager() {
            super();
            verticalPanel = null;
        }

        @Override
        public void clear() {
            super.clear();
            verticalPanel.clear();
            paperCheckboxElementManagerArrayList.clear();
        }

        public void checkAll(boolean checkValue){
            for (int i=0; i < verticalPanel.getWidgetCount(); i++){
                paperCheckboxElementManagerArrayList.get(i).setValue(checkValue);
            }
        }

        public void addValueChangeHandler(ValueChangeHandler<Boolean> handler){
            handlerManager.addHandler(ValueChangeEvent.getType(), handler);
        }

        @Override
        public boolean add(final DataExportDataPoint dataExportDataPoint) {

            PaperCheckbox paperCheckBox = new PaperCheckbox(dataExportDataPoint.getDescription());
            PaperCheckboxElementManager paperCheckboxElementManager = new PaperCheckboxElementManager(paperCheckBox.getPolymerElement());

            paperCheckBox.getElement().getStyle().setPadding(8, Style.Unit.PX);
            if (dataExportDataPoint.isSelected()) paperCheckboxElementManager.setValue(true, false);

            paperCheckboxElementManager.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                @Override
                public void onValueChange(ValueChangeEvent<Boolean> valueChangeEvent) {
                    dataExportDataPoint.setSelected(valueChangeEvent.getValue());
                    //handlerManager.fireEvent(valueChangeEvent);
                }
            });
            verticalPanel.add(paperCheckBox);
            paperCheckboxElementManagerArrayList.add(paperCheckboxElementManager);
            return super.add(dataExportDataPoint);
        }
    }


    public ExportViewImpl() {
        initWidget(defaultBinder.createAndBindUi(this));

        allSubloadButtonManager = new PaperCheckboxElementManager(allSubloadButton);
        allSubloadButtonManager.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> valueChangeEvent) {
                boolean clickValue = allSubloadButton.getChecked();
                subLoadCheckBoxManager.checkAll(clickValue);
            }
        });


        checkBoxManager = new CheckBoxManager(dataPointPanel);

        subLoadCheckBoxManager = new CheckBoxManager(subloadDataPointPanel);
        subLoadCheckBoxManager.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> valueChangeEvent) {
                if (allSubloadButtonManager.getValue() && !valueChangeEvent.getValue())
                    allSubloadButtonManager.setValue(false, false);
            }
        });

        subloadManager = new BooleanTypeRadioManager(subloadGroup);
        weatherManager = new BooleanTypeRadioManager(weatherGroup);
        demandCostManager = new BooleanTypeRadioManager(demandCostGroup);



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


        historyTypeRadioManager = new HistoryTypeRadioManager(historyTypeGroup);
        dataExportFileTypeManager = new DataExportFileTypeManager(dataExportTypeGroup);

        exportButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                presenter.doExport();
            }
        });

        locationPicker.addValueChangeHandler(new ValueChangeHandler<AccountLocation>() {
            @Override
            public void onValueChange(ValueChangeEvent<AccountLocation> valueChangeEvent) {
                presenter.changeLocation(valueChangeEvent.getValue());
            }
        });

        historyTypeRadioManager.addValueChangeHandler(new ValueChangeHandler<HistoryType>() {
            @Override
            public void onValueChange(ValueChangeEvent<HistoryType> valueChangeEvent) {
                datePicker.setHistoryType(valueChangeEvent.getValue());
                if (valueChangeEvent.getValue().equals(HistoryType.BILLING_CYCLE)){
                    demandCostDiv.getStyle().clearDisplay();
                } else {
                    demandCostField().setValue(false);
                    demandCostDiv.getStyle().setDisplay(Style.Display.NONE);
                }
            }
        });


        dataExportFileTypeManager.setValue(DataExportFileType.CSV);
        dataExportTypeGroup.setSelected("CSV");
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
    public HasValidation<AccountLocation> getLocationPicker() {
        return locationPicker;
    }

    @Override
    public HasValue<HistoryType> getHistoryType() {
        return historyTypeRadioManager;
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
    public List<DataExportDataPoint> getDataExportDataPoints() {
        return checkBoxManager;
    }

    @Override
    public List<DataExportDataPoint> getSubloadDataExportDataPoints() {
        return subLoadCheckBoxManager;
    }

    @Override
    public HasValue<Boolean> graphSubloadField() {
        return subloadManager;
    }

    @Override
    public HasValue<Boolean> allSubloadField() {
        return allSubloadButtonManager;
    }

    @Override
    public HasValue<Boolean> weatherField() {
        return weatherManager;
    }

    @Override
    public HasValue<Boolean> demandCostField() {
        return demandCostManager;
    }

    @Override
    public void setSubloadPanelVisibility(boolean visible) {
        subloadField.getStyle().setDisplay(visible? Style.Display.INLINE : Style.Display.NONE);
    }

    @Override
    public void setSubloadDataPointPanelVisibility(boolean visible) {
        subloadDataPointField.getStyle().setDisplay(visible? Style.Display.INLINE : Style.Display.NONE);
    }


    @Override
    public void setLoadingVisible(boolean isVisible) {
        if (isVisible) LoadingOverlay.get().show();
        else LoadingOverlay.get().hide();

    }


    @Override
    public void setLocationList(List<AccountLocation> accountLocationList) {
        for (AccountLocation accountLocation : accountLocationList) {
            locationPicker.addItem(accountLocation);
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
        subLoadCheckBoxManager.clear();
        subloadDataPointPanel.clear();
    }

    interface DefaultBinder extends UiBinder<Widget, ExportViewImpl> {
    }


}

