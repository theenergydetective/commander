package com.ted.commander.client.radioManager;

import com.petecode.common.client.widget.PaperRadioElementManager;
import com.ted.commander.common.enums.DemandPlanType;
import com.vaadin.polymer.paper.element.PaperRadioGroupElement;


public class DemandPlanTypeRadioManager extends PaperRadioElementManager<DemandPlanType> {

    public DemandPlanTypeRadioManager(PaperRadioGroupElement paperRadioGroup) {
        super(paperRadioGroup);
    }

    @Override
    public DemandPlanType getValue() {
        return DemandPlanType.valueOf(getRadioGroupValue());
    }

    @Override
    public void setValue(DemandPlanType enumType, boolean b) {
        paperRadioGroup.setSelected(enumType.name());
        valueChangeManager.setValue(enumType, b);
    }
}
