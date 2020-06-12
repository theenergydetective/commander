/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.style;


import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ThemedContainer extends Composite implements HasWidgets, AcceptsOneWidget {

    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);
    @UiField
    HTMLPanel mainPanel;

    List<Widget> widgetList = new ArrayList<Widget>();

    public ThemedContainer() {
        initWidget(defaultBinder.createAndBindUi(this));
    }


    @Override
    public void add(Widget widget) {
        clear();
        mainPanel.add(widget);
        widgetList.add(widget);
    }

    @Override
    public void clear() {
        widgetList.clear();
        mainPanel.clear();
    }

    @Override
    public Iterator<Widget> iterator() {
        return widgetList.iterator();
    }

    @Override
    public boolean remove(Widget widget) {
        if (widgetList.remove(widget)){
            mainPanel.clear();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void setWidget(IsWidget isWidget) {
        add(isWidget.asWidget());
    }

    //Skin mapping

    interface DefaultBinder extends UiBinder<Widget, ThemedContainer> {
    }


}
