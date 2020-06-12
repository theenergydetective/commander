/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.graphing;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.petecode.common.client.events.CloseEvent;
import com.petecode.common.client.events.CloseHandler;
import com.petecode.common.client.model.DateRange;
import com.petecode.common.client.widget.PaperCheckboxElementManager;
import com.petecode.common.client.widget.paper.HasValidation;
import com.ted.commander.client.model.DataExportDataPoint;
import com.ted.commander.client.radioManager.BooleanTypeRadioManager;
import com.ted.commander.client.radioManager.ExportGraphTypeRadioManager;
import com.ted.commander.client.radioManager.GraphLineTypeRadioManager;
import com.ted.commander.client.radioManager.HistoryTypeRadioManager;
import com.ted.commander.client.view.LoadingOverlay;
import com.ted.commander.client.widgets.LocationPicker;
import com.ted.commander.client.widgets.datePicker.DatePicker;
import com.ted.commander.client.widgets.toolbar.TitleBar;
import com.ted.commander.common.enums.ExportGraphType;
import com.ted.commander.common.enums.GraphLineType;
import com.ted.commander.common.enums.HistoryType;
import com.ted.commander.common.model.AccountLocation;
import com.vaadin.polymer.paper.element.PaperCheckboxElement;
import com.vaadin.polymer.paper.element.PaperRadioButtonElement;
import com.vaadin.polymer.paper.element.PaperRadioGroupElement;
import com.vaadin.polymer.paper.widget.PaperButton;
import com.vaadin.polymer.paper.widget.PaperCheckbox;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


public class GraphingOptionsImpl extends Composite implements GraphingOptionsView {
    static final Logger LOGGER = Logger.getLogger(GraphingOptionsImpl.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);
    final HistoryTypeRadioManager historyTypeRadioManager;
    final ExportGraphTypeRadioManager exportGraphTypeRadioManager;
    final CheckBoxManager checkBoxManager;
    final CheckBoxManager subLoadCheckBoxManager;
    final GraphLineTypeRadioManager graphLineTypeRadioManager;
    final BooleanTypeRadioManager subloadManager;
    final ArrayList<PaperCheckboxElementManager> paperCheckboxElementManagerArrayList = new ArrayList<PaperCheckboxElementManager>();

    Presenter presenter;
    @UiField
    PaperButton cancelButton;

    @UiField
    LocationPicker locationPicker;
    @UiField
    PaperRadioGroupElement historyTypeGroup;
    @UiField
    PaperRadioGroupElement graphTypeGroup;
    @UiField
    DatePicker datePicker;
    @UiField
    PaperButton exportButton;
    @UiField
    VerticalPanel dataPointPanel;
    @UiField
    TitleBar titleBar;
    @UiField
    PaperRadioButtonElement noneField;
    @UiField
    PaperRadioButtonElement netField;
    @UiField
    PaperRadioButtonElement generationField;
    @UiField
    PaperRadioButtonElement tempField;
    @UiField
    PaperRadioGroupElement graphLineTypeGroup;
    @UiField
    PaperRadioButtonElement loadField;
    @UiField
    VerticalPanel subloadDataPointPanel;
    @UiField
    PaperRadioGroupElement subloadGroup;

    @UiField
    DivElement subloadField;


    @UiField
    PaperCheckboxElement allSubloadButton;
    PaperCheckboxElementManager allSubloadButtonManager;

    @UiField
    HTMLPanel mainPanel;
    @UiField
    DivElement subloadDataPointField;
    @UiField
    Element demandCostRadio;


    public GraphingOptionsImpl() {

        initWidget(defaultBinder.createAndBindUi(this));

        allSubloadButtonManager = new PaperCheckboxElementManager(allSubloadButton);

        allSubloadButtonManager.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> valueChangeEvent) {
                boolean clickValue = allSubloadButtonManager.getValue();
                subLoadCheckBoxManager.checkAll(clickValue);
            }
        });

        demandCostRadio.getStyle().setDisplay(Style.Display.NONE);

        historyTypeRadioManager = new HistoryTypeRadioManager(historyTypeGroup);
        exportGraphTypeRadioManager = new ExportGraphTypeRadioManager(graphTypeGroup);
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



        graphLineTypeRadioManager = new GraphLineTypeRadioManager(graphLineTypeGroup);

        titleBar.addCloseHandler(new CloseHandler() {
            @Override
            public void onClose(CloseEvent event) {
                presenter.cancel();
            }
        });

        cancelButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                presenter.cancel();
            }
        });


        exportButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                presenter.doGraph();
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

                if (historyTypeRadioManager.getValue().equals(HistoryType.BILLING_CYCLE)){
                    demandCostRadio.getStyle().clearDisplay();
                } else {
                    demandCostRadio.getStyle().setDisplay(Style.Display.NONE);
                }
            }
        });
    }

    @Override
    public void setLogo(ImageResource imageResource) {
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
    public HasValue<ExportGraphType> getExportGraphType() {
        return exportGraphTypeRadioManager;
    }

    @Override
    public HasValue<GraphLineType> getGraphLineType() {
        return graphLineTypeRadioManager;
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

    private void setVisible(boolean isVisible, PaperRadioButtonElement paperRadioButtonElement){
        if (isVisible){
            paperRadioButtonElement.getStyle().setProperty("display", "inline");
        } else {
            paperRadioButtonElement.getStyle().setProperty("display", "none");
        }
    }
    @Override
    public void setNoneRadioFieldVisible(boolean visible) {
        setVisible(visible, noneField);

    }

    @Override
    public void setNetRadioFieldVisible(boolean visible) {
        setVisible(visible, netField);

    }

    @Override
    public void setGenRadioFieldVisible(boolean visible) {
        setVisible(visible, generationField);
    }

    @Override
    public void setLoadRadioFieldVisible(boolean visible) {
        setVisible(visible, loadField);

    }

    @Override
    public void setWeatherRadioFieldVisible(boolean visible) {
        setVisible(visible, tempField);
    }

    @Override
    public HasValue<Boolean> allSubloadField() {
        return allSubloadButtonManager;
    }

    @Override
    public void setSubloadPanelVisibility(boolean visible) {
        if (visible){
            subloadField.getStyle().clearDisplay();
        } else {
            subloadField.getStyle().setDisplay(Style.Display.NONE);
        }
    }

    @Override
    public void setSubloadDataPointPanelVisibility(boolean visible) {
        if (visible){
            subloadDataPointField.getStyle().clearDisplay();
        } else {
            subloadDataPointField.getStyle().setDisplay(Style.Display.NONE);
        }
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

    @Override
    public void scrollToTop() {
        Window.scrollTo(0,0);
    }

    interface DefaultBinder extends UiBinder<Widget, GraphingOptionsImpl> {
    }

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
            paperCheckboxElementManagerArrayList.clear();;
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
            //paperCheckBox.setAriaLabel();
            paperCheckBox.getElement().getStyle().setPadding(8, Style.Unit.PX);
            if (dataExportDataPoint.isSelected()) {
                paperCheckBox.setChecked(true);
            }

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

}
