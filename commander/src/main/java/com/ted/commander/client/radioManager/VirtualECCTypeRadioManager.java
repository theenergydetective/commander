package com.ted.commander.client.radioManager;

import com.petecode.common.client.widget.PaperRadioElementManager;
import com.ted.commander.common.enums.VirtualECCType;
import com.vaadin.polymer.paper.element.PaperRadioGroupElement;


/**
 * Created by pete on 1/29/2015.
 */
public class VirtualECCTypeRadioManager extends PaperRadioElementManager<VirtualECCType> {

    public VirtualECCTypeRadioManager(PaperRadioGroupElement paperRadioGroup) {
        super(paperRadioGroup);
    }

    @Override
    public VirtualECCType getValue() {
        return VirtualECCType.valueOf(getRadioGroupValue());
    }

    @Override
    public void setValue(VirtualECCType virtualECCType, boolean b) {
        paperRadioGroup.setSelected(virtualECCType.name());
        valueChangeManager.setValue(virtualECCType, b);
    }
}
