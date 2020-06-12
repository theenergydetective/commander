package com.ted.commander.client.radioManager;

import com.petecode.common.client.widget.PaperRadioElementManager;
import com.ted.commander.common.enums.MeterReadCycle;
import com.vaadin.polymer.paper.element.PaperRadioGroupElement;


/**
 * Created by pete on 1/29/2015.
 */
public class MeterReadCycleRadioManager extends PaperRadioElementManager<MeterReadCycle> {

    public MeterReadCycleRadioManager(PaperRadioGroupElement paperRadioGroup) {
        super(paperRadioGroup);
    }

    @Override
    public MeterReadCycle getValue() {
        return MeterReadCycle.valueOf(getRadioGroupValue());
    }

    @Override
    public void setValue(MeterReadCycle enumType, boolean b) {
        paperRadioGroup.setSelected(enumType.name());
        valueChangeManager.setValue(enumType, b);
    }
}
