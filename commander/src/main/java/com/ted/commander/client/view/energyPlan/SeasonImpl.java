
package com.ted.commander.client.view.energyPlan;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;
import com.petecode.common.client.logging.ConsoleLoggerFactory;
import com.petecode.common.client.widget.paper.HasValidation;
import com.petecode.common.client.widget.paper.PaperDatePicker;
import com.petecode.common.client.widget.paper.PaperInputDecorator;

import java.util.Date;
import java.util.logging.Logger;


public class SeasonImpl extends Composite implements SeasonView {

    static final Logger LOGGER = ConsoleLoggerFactory.getLogger(SeasonImpl.class);

    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_MEDIUM);

    @UiField
    PaperInputDecorator nameField;
    @UiField
    PaperDatePicker dateField;


    Presenter presenter;

    public SeasonImpl() {
        initWidget(uiBinder.createAndBindUi(this));


    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public HasValidation<String> seasonName() {
        return nameField;
    }

    @Override
    public HasValue<Date> seasonDate() {
        return dateField;
    }


    interface MyUiBinder extends UiBinder<Widget, SeasonImpl> {
    }
}
