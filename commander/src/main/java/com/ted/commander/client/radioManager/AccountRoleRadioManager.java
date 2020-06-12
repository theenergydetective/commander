package com.ted.commander.client.radioManager;

import com.google.gwt.core.client.GWT;
import com.petecode.common.client.widget.PaperRadioElementManager;
import com.ted.commander.common.enums.AccountRole;
import com.vaadin.polymer.paper.element.PaperRadioGroupElement;


/**
 * Created by pete on 1/29/2015.
 */
public class AccountRoleRadioManager extends PaperRadioElementManager<AccountRole> {

    public AccountRoleRadioManager(PaperRadioGroupElement paperRadioGroup) {
        super(paperRadioGroup);
    }

    @Override
    public AccountRole getValue() {
        return AccountRole.valueOf(getRadioGroupValue());
    }

    @Override
    public void setValue(AccountRole enumType, boolean b) {
        GWT.log("SETTING ACCOUNT:" + enumType.name() + " " + b);
        paperRadioGroup.setSelected(enumType.name());
        valueChangeManager.setValue(enumType, b);
    }
}
