package com.ted.commander.client.radioManager;

import com.petecode.common.client.widget.PaperRadioElementManager;
import com.ted.commander.common.enums.PlanType;
import com.vaadin.polymer.paper.element.PaperRadioGroupElement;


/**
 * Created by pete on 1/29/2015.
 */
public class PlanTypeRadioManager extends PaperRadioElementManager<PlanType> {

    public PlanTypeRadioManager(PaperRadioGroupElement paperRadioGroup) {
        super(paperRadioGroup);
    }

    @Override
    public PlanType getValue() {
        return PlanType.valueOf(getRadioGroupValue());
    }

    @Override
    public void setValue(PlanType enumType, boolean b) {
        paperRadioGroup.setSelected(enumType.name());
        valueChangeManager.setValue(enumType, b);
    }
}
