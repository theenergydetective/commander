package com.ted.commander.client.radioManager;

import com.petecode.common.client.widget.PaperRadioElementManager;
import com.ted.commander.common.enums.DataExportFileType;
import com.vaadin.polymer.paper.element.PaperRadioGroupElement;


/**
 * Created by pete on 1/29/2015.
 */
public class DataExportFileTypeManager extends PaperRadioElementManager<DataExportFileType> {

    public DataExportFileTypeManager(PaperRadioGroupElement paperRadioGroup) {
        super(paperRadioGroup);
    }

    @Override
    public com.ted.commander.common.enums.DataExportFileType getValue() {
        return com.ted.commander.common.enums.DataExportFileType.valueOf(getRadioGroupValue());
    }

    @Override
    public void setValue(com.ted.commander.common.enums.DataExportFileType enumType, boolean b) {
        paperRadioGroup.setSelected(enumType.name());
        valueChangeManager.setValue(enumType, b);
    }
}
