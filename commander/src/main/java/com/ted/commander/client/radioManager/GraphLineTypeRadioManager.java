package com.ted.commander.client.radioManager;

import com.petecode.common.client.widget.PaperRadioElementManager;
import com.ted.commander.common.enums.GraphLineType;
import com.vaadin.polymer.paper.element.PaperRadioGroupElement;


/**
 * Created by pete on 1/29/2015.
 */
public class GraphLineTypeRadioManager extends PaperRadioElementManager<GraphLineType> {

    public GraphLineTypeRadioManager(PaperRadioGroupElement paperRadioGroup) {
        super(paperRadioGroup);
    }

    @Override
    public GraphLineType getValue() {
        return GraphLineType.valueOf(getRadioGroupValue());
    }

    @Override
    public void setValue(GraphLineType enumType, boolean b) {
        paperRadioGroup.setSelected(enumType.name());
        valueChangeManager.setValue(enumType, b);
    }
}
