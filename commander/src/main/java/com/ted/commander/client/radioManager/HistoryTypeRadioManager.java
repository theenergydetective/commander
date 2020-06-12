package com.ted.commander.client.radioManager;

import com.petecode.common.client.widget.PaperRadioElementManager;
import com.ted.commander.common.enums.HistoryType;
import com.vaadin.polymer.paper.element.PaperRadioGroupElement;


/**
 * Created by pete on 1/29/2015.
 */
public class HistoryTypeRadioManager extends PaperRadioElementManager<HistoryType> {

    public HistoryTypeRadioManager(PaperRadioGroupElement paperRadioGroup) {
        super(paperRadioGroup);
    }

    @Override
    public HistoryType getValue() {
        return HistoryType.valueOf(getRadioGroupValue());
    }

    @Override
    public void setValue(HistoryType historyType, boolean b) {
        paperRadioGroup.setSelected(historyType.name());
        valueChangeManager.setValue(historyType, b);
    }
}
