/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.view.locationEdit.mtuList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.petecode.common.client.events.ItemSelectedEvent;
import com.petecode.common.client.events.ItemSelectedHandler;
import com.ted.commander.client.view.locationEdit.LocationEditView;
import com.ted.commander.common.model.AccountMembers;
import com.ted.commander.common.model.VirtualECCMTU;

import java.util.List;
import java.util.logging.Logger;


public class LocationMTUListView extends Composite {

    static final Logger LOGGER = Logger.getLogger(AccountMembers.class.getName());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);

    boolean editable;
    LocationEditView.Presenter presenter;


    ItemSelectedHandler<VirtualECCMTU> itemSelectedHandler = new ItemSelectedHandler<VirtualECCMTU>() {
        @Override
        public void onSelected(ItemSelectedEvent<VirtualECCMTU> event) {
            LOGGER.fine("MTU SELECTED FOR EDITING: " + event.getItem());
            presenter.editVirtualMTU(event.getItem());
        }
    };

    @UiField
    VerticalPanel mtuListPanel;

    @UiConstructor
    public LocationMTUListView(final boolean isEditable) {
        this.editable = isEditable;
        initWidget(defaultBinder.createAndBindUi(this));


    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public void setMTUList(List<VirtualECCMTU> mtuList) {
        mtuListPanel.clear();
        for (VirtualECCMTU mtu : mtuList) {
            LocationMTUTile mtuTile = new LocationMTUTile(mtu, editable);
            if (editable) mtuTile.addItemSelectedHandler(itemSelectedHandler);
            mtuListPanel.add(mtuTile);
        }
    }

    public void setPresenter(LocationEditView.Presenter presenter) {
        this.presenter = presenter;
    }


    interface DefaultBinder extends UiBinder<Widget, LocationMTUListView> {
    }


}
