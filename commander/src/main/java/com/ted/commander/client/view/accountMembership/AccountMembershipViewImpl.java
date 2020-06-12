/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.accountMembership;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.ui.client.widget.dialog.ConfirmDialog;
import com.petecode.common.client.events.CloseEvent;
import com.petecode.common.client.events.CloseHandler;
import com.petecode.common.client.widget.paper.PaperLabel;
import com.ted.commander.client.radioManager.AccountRoleRadioManager;
import com.ted.commander.client.resources.WebStringResource;
import com.ted.commander.client.widgets.toolbar.TitleBar;
import com.ted.commander.common.enums.AccountRole;
import com.vaadin.polymer.paper.element.PaperRadioGroupElement;
import com.vaadin.polymer.paper.widget.PaperButton;

import java.util.logging.Logger;


public class AccountMembershipViewImpl extends Composite implements AccountMembershipView {

    static final Logger LOGGER = Logger.getLogger(AccountMembershipViewImpl.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);
    Presenter presenter;
    @UiField
    TitleBar titleBar;
    @UiField
    ScrollPanel frameScrollPanel;

    @UiField
    PaperButton deleteButton;
    @UiField
    PaperLabel accountNameField;
    @UiField
    PaperLabel accountMemberNameField;
    @UiField
    PaperLabel accountMemberEmailField;
    @UiField
    PaperRadioGroupElement accountRoleGroup;

    final AccountRoleRadioManager accountRoleRadioManager;

    public AccountMembershipViewImpl() {
        initWidget(defaultBinder.createAndBindUi(this));
        frameScrollPanel.setWidth(Window.getClientWidth() + "px");
        frameScrollPanel.setHeight(Window.getClientHeight() + "px");
        frameScrollPanel.setAlwaysShowScrollBars(false);

        accountRoleRadioManager = new AccountRoleRadioManager(accountRoleGroup);

        titleBar.addCloseHandler(new CloseHandler() {
            @Override
            public void onClose(CloseEvent event) {
                presenter.goToAccountPlace();
            }
        });

        deleteButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                ConfirmDialog.ConfirmCallback callback = new ConfirmDialog.ConfirmCallback() {
                    @Override
                    public void onOk() {
                        presenter.deleteAccount();
                    }

                    @Override
                    public void onCancel() {
                        LOGGER.fine("delete cancelled");
                    }
                };

                WebStringResource stringRes = WebStringResource.INSTANCE;
                ConfirmDialog confirmDialog = new ConfirmDialog(stringRes.confirmDelete(), stringRes.confirmDeleteMember(), callback, stringRes.yes(), stringRes.no());
                confirmDialog.show();
            }
        });


    }

    @Override
    public void setLogo(ImageResource imageResource) {
        titleBar.setLogo(imageResource);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public HasValue<String> getAccountNameField() {
        return accountNameField;
    }

    @Override
    public HasValue<String> getNameField() {
        return accountMemberNameField;
    }

    @Override
    public HasValue<String> getEmailField() {
        return accountMemberEmailField;
    }

    @Override
    public HasValue<AccountRole> getAccountRoleValue() {
        return accountRoleRadioManager;
    }

    interface DefaultBinder extends UiBinder<Widget, AccountMembershipViewImpl> {
    }


}
