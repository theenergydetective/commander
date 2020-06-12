package com.ted.commander.client.view.energyPlan.tou;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.petecode.common.client.events.ItemChangedEvent;
import com.petecode.common.client.events.ItemChangedHandler;
import com.ted.commander.common.enums.TOUPeakType;
import com.ted.commander.common.model.EnergyPlan;
import com.ted.commander.common.model.EnergyPlanSeason;
import com.ted.commander.common.model.EnergyPlanTOU;

import java.util.HashMap;
import java.util.logging.Logger;

public class TOUSeason extends Composite {

    static Logger LOGGER = Logger.getLogger(TOUSeason.class.toString());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);


    final HandlerManager handlerManager = new HandlerManager(this);
    final EnergyPlan energyPlan;
    final EnergyPlanSeason energyPlanSeason;
    @UiField
    CaptionPanel captionField;
    @UiField
    VerticalPanel contentPanel;
    private boolean valid;


    public TOUSeason(final EnergyPlan energyPlan, final EnergyPlanSeason energyPlanSeason) {
        this.energyPlan = energyPlan;
        this.energyPlanSeason = energyPlanSeason;


        initWidget(defaultBinder.createAndBindUi(this));



        if (energyPlanSeason.getSeasonName() != null) captionField.setCaptionText(energyPlanSeason.getSeasonName());
        else {
            captionField.setCaptionText("Season " + (energyPlanSeason.getId() + 1));
        }

        HashMap<TOUPeakType, EnergyPlanTOU> amMap = new HashMap<TOUPeakType, EnergyPlanTOU>();
        HashMap<TOUPeakType, EnergyPlanTOU> pmMap = new HashMap<TOUPeakType, EnergyPlanTOU>();

        for (EnergyPlanTOU energyPlanTOU : energyPlanSeason.getTouList()) {
            if (energyPlanTOU.isMorningTou()) {
                amMap.put(energyPlanTOU.getPeakType(), energyPlanTOU);
            }
            else pmMap.put(energyPlanTOU.getPeakType(), energyPlanTOU);
        }

        if (energyPlan.getNumberTOU() == 1) energyPlan.setNumberTOU(2);

        for (int step = 0; step < energyPlan.getNumberTOU(); step++) {
            TOUPeakType peakType = TOUPeakType.values()[step];
            LOGGER.fine("PEAK TYPE: " + peakType);


            if (amMap.get(peakType) == null) {
                EnergyPlanTOU energyPlanTOU = new EnergyPlanTOU();
                energyPlanTOU.setSeasonId(energyPlanSeason.getId());
                energyPlanTOU.setPeakType(peakType);
                energyPlanTOU.setMorningTou(true);
                amMap.put(energyPlanTOU.getPeakType(), energyPlanTOU);
                energyPlanSeason.getTouList().add(energyPlanTOU);
                LOGGER.fine("------ CREATED AM: " + energyPlanTOU);
            }


            if (pmMap.get(peakType) == null) {
                EnergyPlanTOU energyPlanTOU = new EnergyPlanTOU();
                energyPlanTOU.setSeasonId(energyPlanSeason.getId());
                energyPlanTOU.setPeakType(peakType);
                energyPlanTOU.setMorningTou(false);
                pmMap.put(energyPlanTOU.getPeakType(), energyPlanTOU);
                energyPlanSeason.getTouList().add(energyPlanTOU);

            }




            TOURow tr = new TOURow(energyPlan, energyPlanSeason, amMap.get(peakType), pmMap.get(peakType));

            tr.addChangeHandler(new ItemChangedHandler<EnergyPlan>() {
                @Override
                public void onChanged(ItemChangedEvent<EnergyPlan> event) {
                    handlerManager.fireEvent(event);
                }
            });


            contentPanel.insert(tr, 0);
        }

    }

    public void addChangeHandler(ItemChangedHandler<EnergyPlan> handler) {
        handlerManager.addHandler(ItemChangedEvent.TYPE, handler);
    }

    public void setEnabled(boolean enabled) {

    }

    public boolean isValid() {
        boolean valid = true;
        for (int i = 0; i < contentPanel.getWidgetCount(); i++) {
            TOURow touRow = ((TOURow) contentPanel.getWidget(i));
            if (!touRow.isValid()) valid = false;
        }
        return valid;
    }


    //Skin mapping
    @UiTemplate("TOUSeason.ui.xml")
    interface DefaultBinder extends UiBinder<Widget, TOUSeason> {
    }

}
