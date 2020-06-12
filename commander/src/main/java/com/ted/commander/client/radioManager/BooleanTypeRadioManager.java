package com.ted.commander.client.radioManager;

import com.petecode.common.client.widget.PaperRadioElementManager;
import com.vaadin.polymer.paper.element.PaperRadioGroupElement;


/**
 * Created by pete on 1/29/2015.
 */
public class BooleanTypeRadioManager extends PaperRadioElementManager<Boolean> {

    public BooleanTypeRadioManager(PaperRadioGroupElement paperRadioGroupElement) {
        super(paperRadioGroupElement);
    }

    @Override
    public Boolean getValue() {
        return "TRUE".equals(getRadioGroupValue());
    }

    @Override
    public void setValue(Boolean bType, boolean b) {
        paperRadioGroup.setSelected(bType ? "TRUE" : "FALSE");
        valueChangeManager.setValue(bType, b);
    }
}
