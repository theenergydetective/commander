package com.ted.commander.client.radioManager;


import com.petecode.common.client.widget.PaperRadioElementManager;
import com.ted.commander.client.enums.ComparisonGraphType;
import com.vaadin.polymer.paper.element.PaperRadioGroupElement;


/**
 * Created by pete on 1/29/2015.
 */
public class ComparisonGraphTypeRadioManager extends PaperRadioElementManager<ComparisonGraphType> {

    public ComparisonGraphTypeRadioManager(PaperRadioGroupElement paperRadioGroup) {
        super(paperRadioGroup);
    }

    @Override
    public ComparisonGraphType getValue() {
        return ComparisonGraphType.valueOf(getRadioGroupValue());
    }

    @Override
    public void setValue(ComparisonGraphType enumType, boolean b) {
        paperRadioGroup.setSelected(enumType.name());
        valueChangeManager.setValue(enumType, b);
    }
}
