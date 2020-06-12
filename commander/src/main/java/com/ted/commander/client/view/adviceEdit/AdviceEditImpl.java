/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.adviceEdit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.petecode.common.client.events.ItemSelectedEvent;
import com.petecode.common.client.events.ItemSelectedHandler;
import com.ted.commander.client.widgets.LocationPicker;
import com.ted.commander.common.model.AccountLocation;
import com.ted.commander.common.model.AdviceRecipient;
import com.ted.commander.common.model.AdviceTrigger;
import com.vaadin.polymer.elemental.Event;
import com.vaadin.polymer.elemental.EventListener;
import com.vaadin.polymer.paper.element.PaperIconButtonElement;
import com.vaadin.polymer.paper.element.PaperToastElement;
import com.vaadin.polymer.paper.widget.PaperIconButton;
import com.vaadin.polymer.paper.widget.PaperInput;

import java.util.List;
import java.util.logging.Logger;


public class AdviceEditImpl extends Composite implements AdviceEditView {

    static final Logger LOGGER = Logger.getLogger(AdviceEditImpl.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);
    Presenter presenter;

    @UiField
    PaperIconButton deleteButton;
    @UiField
    PaperIconButton backButton;
    @UiField
    LocationPicker locationPicker;
    @UiField
    PaperInput adviceNameField;
    @UiField
    PaperToastElement toast;
    @UiField
    DivElement noRecipientsField;
    @UiField
    DivElement noTriggersField;
    @UiField
    VerticalPanel recipientPanel;

    @UiField
    PaperIconButtonElement addRecipientIcon;
    @UiField
    PaperIconButtonElement addTriggerIcon;
    @UiField
    DivElement recipientDiv;
    @UiField
    DivElement triggerDiv;
    @UiField
    VerticalPanel triggerPanel;


    ItemSelectedHandler<AdviceRecipient> recipientItemSelectedHandler = new ItemSelectedHandler<AdviceRecipient>() {
        @Override
        public void onSelected(ItemSelectedEvent<AdviceRecipient> event) {
            presenter.editRecipient(event.getItem());
        }
    };

    ItemSelectedHandler<AdviceTrigger> triggerItemSelectedHandler = new ItemSelectedHandler<AdviceTrigger>() {
        @Override
        public void onSelected(ItemSelectedEvent<AdviceTrigger> event) {
            presenter.editTrigger(event.getItem());
        }
    };

    public AdviceEditImpl() {
        initWidget(defaultBinder.createAndBindUi(this));

        deleteButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                presenter.deleteAdvice();
            }
        });


        backButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                presenter.goBack();
            }
        });

        addRecipientIcon.addEventListener("click", new EventListener() {
            @Override
            public void handleEvent(Event event) {
                presenter.createRecipient();
            }
        });


        addTriggerIcon.addEventListener("click", new EventListener() {
            @Override
            public void handleEvent(Event event) {
                presenter.createTrigger();
            }
        });
    }


    @Override
    public void setLocationList(List<AccountLocation> locationList) {
        LOGGER.fine("SETTING LOCATION LIST: " + locationList.size());
        for (AccountLocation accountLocation: locationList) {
            LOGGER.fine("ADDING ITEM: " + accountLocation);
            locationPicker.addItem(accountLocation);
        }
    }

    @Override
    public HasValue<AccountLocation> locationPicker() {
        return this.locationPicker;
    }

    @Override
    public PaperInput adviceName() {
        return this.adviceNameField;
    }

    @Override
    public void setRecipients(List<AdviceRecipient> recipients) {
        recipientPanel.clear();
        if (recipients.size() == 0) {
            noRecipientsField.getStyle().clearDisplay();
        } else {
            noRecipientsField.getStyle().setDisplay(Style.Display.NONE);

            for (AdviceRecipient recipient: recipients) {
                RecipientTile recipientTile = new RecipientTile(recipient);
                recipientTile.addItemSelectedHandler(recipientItemSelectedHandler);
                recipientPanel.add(recipientTile);
            }

        }
    }

    @Override
    public void setTriggers(List<AdviceTrigger> triggers) {
        triggerPanel.clear();
        if (triggers.size() == 0) {
            noTriggersField.getStyle().clearDisplay();
        } else {
            noTriggersField.getStyle().setDisplay(Style.Display.NONE);
        }

        for (AdviceTrigger trigger: triggers) {
            TriggerTile triggerTile = new TriggerTile(trigger, presenter.getEnergyPlan());
            triggerTile.addItemSelectedHandler(triggerItemSelectedHandler);
            triggerPanel.add(triggerTile);
        }

    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;

    }

    @Override
    public void showToast() {
        toast.setDuration(1000.0);
        toast.show();
    }

    @Override
    public void setReadOnly(boolean isReadOnly) {
        locationPicker.setReadOnly(isReadOnly);
        adviceName().setReadonly(isReadOnly);

        if (isReadOnly){
            deleteButton.getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);
            recipientDiv.getStyle().setVisibility(Style.Visibility.HIDDEN);
            triggerDiv.getStyle().setVisibility(Style.Visibility.HIDDEN);
        } else {
            deleteButton.getElement().getStyle().clearVisibility();
            recipientDiv.getStyle().clearVisibility();
            triggerDiv.getStyle().clearVisibility();
        }



    }


    //Skin mapping

    interface DefaultBinder extends UiBinder<Widget, AdviceEditImpl> {
    }


}
