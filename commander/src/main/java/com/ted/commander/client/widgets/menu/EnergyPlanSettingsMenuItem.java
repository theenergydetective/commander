/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.widgets.menu;

import com.ted.commander.client.icons.MenuIconHolder;
import com.ted.commander.client.places.EnergyPlanListPlace;
import com.ted.commander.client.resources.WebStringResource;

public class EnergyPlanSettingsMenuItem extends MenuItem {

    public EnergyPlanSettingsMenuItem() {
        super(MenuIconHolder.get().energyPlanSettings(), WebStringResource.INSTANCE.energyPlans(), new EnergyPlanListPlace(""));
    }
}
