
package com.ted.commander.client.view.energyPlanList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Widget;
import com.petecode.common.client.logging.ConsoleLoggerFactory;

import java.util.logging.Logger;


public class EnergyPlanRowImpl extends Composite implements EnergyPlanRowView {

    static final Logger LOGGER = ConsoleLoggerFactory.getLogger(EnergyPlanRowImpl.class);

    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_MEDIUM);
    @UiField
    FocusPanel mainPanel;
    @UiField
    DivElement titleField;
    @UiField
    DivElement descriptionField;




    EnergyPlanRowView.Presenter presenter;

    public EnergyPlanRowImpl() {
        initWidget(uiBinder.createAndBindUi(this));

        mainPanel.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                presenter.select();
            }
        });
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }


    @Override
    public void setName(String name) {
        titleField.setInnerText(name);
    }

    @Override
    public void setDescription(String description) {
        descriptionField.setInnerText(description);
    }


    interface MyUiBinder extends UiBinder<Widget, EnergyPlanRowImpl> {
    }
}
