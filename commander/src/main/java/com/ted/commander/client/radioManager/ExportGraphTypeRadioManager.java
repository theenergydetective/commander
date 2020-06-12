package com.ted.commander.client.radioManager;

import com.petecode.common.client.widget.PaperRadioElementManager;
import com.ted.commander.common.enums.ExportGraphType;
import com.vaadin.polymer.paper.element.PaperRadioGroupElement;


/**
 * Created by pete on 1/29/2015.
 */
public class ExportGraphTypeRadioManager extends PaperRadioElementManager<ExportGraphType> {

    public ExportGraphTypeRadioManager(PaperRadioGroupElement paperRadioGroup) {
        super(paperRadioGroup);
    }

    @Override
    public ExportGraphType getValue() {
        return ExportGraphType.valueOf(getRadioGroupValue());
    }

    @Override
    public void setValue(ExportGraphType enumType, boolean b) {
        paperRadioGroup.setSelected(enumType.name());
        valueChangeManager.setValue(enumType, b);
    }
}
