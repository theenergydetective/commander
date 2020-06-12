package com.ted.commander.client.view.energyPlan.tier;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.petecode.common.client.events.ItemChangedEvent;
import com.petecode.common.client.events.ItemChangedHandler;
import com.ted.commander.client.model.Instance;
import com.ted.commander.common.model.EnergyPlan;
import com.ted.commander.common.model.EnergyPlanSeason;
import com.ted.commander.common.model.EnergyPlanTier;

import java.util.logging.Logger;

public class TierSeason extends Composite {

    static Logger LOGGER = Logger.getLogger(TierSeason.class.toString());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);


    final HandlerManager handlerManager = new HandlerManager(this);
    final EnergyPlan energyPlan;
    final EnergyPlanSeason energyPlanSeason;
    @UiField
    CaptionPanel captionField;
    @UiField
    VerticalPanel contentPanel;
    ItemChangedHandler<EnergyPlanTier> tierValueChangeHandler = new ItemChangedHandler<EnergyPlanTier>() {
        @Override
        public void onChanged(ItemChangedEvent<EnergyPlanTier> event) {
            doMinCheck();
            handlerManager.fireEvent(new ItemChangedEvent<EnergyPlan>(energyPlan));
        }
    };


    public TierSeason(final Instance instance, final EnergyPlan energyPlan, final EnergyPlanSeason energyPlanSeason) {
        this.energyPlan = energyPlan;
        this.energyPlanSeason = energyPlanSeason;
        switch (instance.getSkin()) {
            default:
                initWidget(defaultBinder.createAndBindUi(this));
        }




        if (energyPlanSeason.getSeasonName() != null) captionField.setCaptionText(energyPlanSeason.getSeasonName());
        else {
            captionField.setCaptionText("Season " + (energyPlanSeason.getId() + 1));
        }

        if (energyPlan.getNumberTier() == 1) energyPlan.setNumberTier(2);


        for (int step = 0; step < energyPlan.getNumberTier(); step++) {
            if (energyPlanSeason.getTierList().size() <= step) {
                LOGGER.fine("----Creating a new Tier" + energyPlan.getNumberTier());
                EnergyPlanTier energyPlanTier = new EnergyPlanTier();
                energyPlanTier.setId(step);
                energyPlanTier.setKwh(0l);
                energyPlanSeason.getTierList().add(energyPlanTier);
            }
            TierRow tr = new TierRow(instance, energyPlan, energyPlanSeason.getTierList().get(step));
            tr.addChangeHandler(tierValueChangeHandler);
            contentPanel.add(tr);
        }
        doMinCheck();

        getElement().getStyle().setProperty("float", "left");
        getElement().getStyle().setMargin(8, Style.Unit.PX);
    }

    private void doMinCheck() {
        for (int i = 1; i < contentPanel.getWidgetCount(); i++) {
            TierRow tr = (TierRow) contentPanel.getWidget(i);
            TierRow prevTR = (TierRow) contentPanel.getWidget(i - 1);
            tr.updateMinValue(prevTR.getValue() + 1);
        }
    }

    public void setEnabled(boolean enabled) {

    }

    public void addChangeHandler(ItemChangedHandler<EnergyPlan> handler){
        handlerManager.addHandler(ItemChangedEvent.TYPE, handler);
    }

    //Skin mapping
    interface DefaultBinder extends UiBinder<Widget, TierSeason> {
    }

}
