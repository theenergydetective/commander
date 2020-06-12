package com.ted.commander.client.radioManager;

import com.petecode.common.client.widget.PaperRadioElementManager;
import com.vaadin.polymer.paper.element.PaperRadioGroupElement;


/**
 * Created by pete on 1/29/2015.
 */
public class IntegerTypeRadioManager extends PaperRadioElementManager<Integer> {

    public IntegerTypeRadioManager(PaperRadioGroupElement paperRadioGroup) {
        super(paperRadioGroup);
    }

    @Override
    public Integer getValue() {
        return Integer.parseInt(getRadioGroupValue().toString());
    }

    @Override
    public void setValue(Integer i, boolean b) {
        paperRadioGroup.setSelected(i.toString());
        valueChangeManager.setValue(i, b);
    }
}
