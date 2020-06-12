package com.ted.commander.client.radioManager;

import com.petecode.common.client.widget.PaperRadioElementManager;
import com.ted.commander.common.enums.MTUType;
import com.vaadin.polymer.paper.element.PaperRadioGroupElement;


/**
 * Created by pete on 1/29/2015.
 */
public class MTUTypeRadioManager extends PaperRadioElementManager<MTUType> {

    public MTUTypeRadioManager(PaperRadioGroupElement paperRadioGroup) {
        super(paperRadioGroup);
    }

    @Override
    public MTUType getValue() {
        return MTUType.valueOf(getRadioGroupValue());
    }

    @Override
    public void setValue(MTUType enumType, boolean b) {
        paperRadioGroup.setSelected(enumType.name());
        valueChangeManager.setValue(enumType, b);
    }
}
