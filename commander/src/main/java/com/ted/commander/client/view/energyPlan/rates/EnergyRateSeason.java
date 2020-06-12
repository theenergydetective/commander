package com.ted.commander.client.view.energyPlan.rates;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.*;
import com.petecode.common.client.events.ItemChangedEvent;
import com.petecode.common.client.events.ItemChangedHandler;
import com.ted.commander.common.enums.PlanType;
import com.ted.commander.common.enums.TOUPeakType;
import com.ted.commander.common.model.EnergyPlan;
import com.ted.commander.common.model.EnergyPlanSeason;
import com.ted.commander.common.model.EnergyPlanTOULevel;
import com.ted.commander.common.model.EnergyRate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class EnergyRateSeason extends Composite {

    static Logger LOGGER = Logger.getLogger(EnergyRateSeason.class.toString());
    private static DefaultBinder defaultBinder = GWT.create(DefaultBinder.class);
    final HashMap<String, EnergyRate> rateMap = new HashMap<String, EnergyRate>();

    final HandlerManager handlerManager = new HandlerManager(this);

    @UiField
    CaptionPanel captionField;
    @UiField
    HorizontalPanel touHeaderPanel;
    @UiField
    VerticalPanel rowPanel;
    @UiField
    AbsolutePanel tierLabelPlaceholder;

    public EnergyRateSeason(final EnergyPlan energyPlan, final EnergyPlanSeason energyPlanSeason) {
        initWidget(defaultBinder.createAndBindUi(this));
        LOGGER.severe("ENERGY PLAN SEASON!!!!!!!!!!!!!!!!!!!  " + energyPlanSeason.getEnergyRateList().size());

        boolean hasTier = energyPlan.getPlanType().equals(PlanType.TIER) || energyPlan.getPlanType().equals(PlanType.TIERTOU);
        boolean hasTOU = energyPlan.getPlanType().equals(PlanType.TOU) || energyPlan.getPlanType().equals(PlanType.TIERTOU);
        /**
         * Create a map and drop any of the fields we are not using.
         */
        switch (energyPlan.getPlanType()) {
            case FLAT:
                for (EnergyRate energyRate : energyPlanSeason.getEnergyRateList()) {
                    if (energyRate.getTierId() == 0 && energyRate.getPeakType().equals(TOUPeakType.OFF_PEAK)) {
                        rateMap.put("0" + TOUPeakType.OFF_PEAK.name(), energyRate);
                        break;
                    }
                }
                break;
            case TIER:
                for (EnergyRate energyRate : energyPlanSeason.getEnergyRateList()) {
                    if (energyRate.getPeakType().equals(TOUPeakType.OFF_PEAK)) {
                        rateMap.put(energyRate.getTierId() + TOUPeakType.OFF_PEAK.name(), energyRate);
                    }
                }
                break;
            case TOU:
                for (EnergyRate energyRate : energyPlanSeason.getEnergyRateList()) {
                    if (energyRate.getTierId() == 0) {
                        if (!rateMap.containsKey(energyRate.getKey())) {
                            LOGGER.severe("ADDING ENERGY RATE TO CACHE!! " + energyRate);
                            rateMap.put(energyRate.getKey(), energyRate);
                        }
                    }
                }
                break;
            case TIERTOU:
                for (EnergyRate energyRate : energyPlanSeason.getEnergyRateList()) {
                    if (!rateMap.containsKey(energyRate.getKey())) {
                        rateMap.put(energyRate.getTierId() + energyRate.getPeakType().name(), energyRate);
                    }
                }
                break;
        }

        captionField.setCaptionText(energyPlanSeason.getSeasonName());

        if (hasTOU) {
            for (EnergyPlanTOULevel energyPlanTOULevel : energyPlan.getTouLevels()) {
                touHeaderPanel.add(new TOUHeaderCell(energyPlanTOULevel.getTouLevelName()));
            }
        }

        if (hasTier && hasTOU) {
            tierLabelPlaceholder.setVisible(true);
            for (int step = 0; step < energyPlan.getNumberTier(); step++) {
                List<EnergyRate> tierList = new ArrayList<EnergyRate>();
                for (int tou = 0; tou < energyPlan.getNumberTOU(); tou++) {
                    TOUPeakType peakType = TOUPeakType.values()[tou];
                    EnergyRate energyRate = rateMap.get(step + peakType.name());
                    if (energyRate == null) {
                        energyRate = new EnergyRate(energyPlanSeason.getId(), step, peakType);
                        rateMap.put(energyRate.getKey(), energyRate);
                        energyPlanSeason.getEnergyRateList().add(energyRate);
                    }
                    tierList.add(energyRate);
                }
                EnergyRateRow row = new EnergyRateRow(energyPlan, step, tierList);

                    row.addChangeHandler(new ItemChangedHandler<EnergyPlan>() {
                        @Override
                        public void onChanged(ItemChangedEvent<EnergyPlan> event) {
                            LOGGER.fine("Energy Rate Season 1: onChanged");
                            handlerManager.fireEvent(event);
                        }
                    });

                rowPanel.add(row);
            }

        } else if (hasTOU) {
            tierLabelPlaceholder.setVisible(false);
            List<EnergyRate> touList = new ArrayList<EnergyRate>();
            for (int tou = 0; tou < energyPlan.getNumberTOU(); tou++) {
                TOUPeakType peakType = TOUPeakType.values()[tou];
                EnergyRate energyRate = rateMap.get(0 + peakType.name());
                if (energyRate == null) {
                    energyRate = new EnergyRate(energyPlanSeason.getId(), 0, peakType);
                    energyRate.setEnergyPlanId(energyPlan.getId());
                    LOGGER.severe(">>>>>>>>>>>>>>>>>CREATING" + energyRate.getKey());
                    rateMap.put(energyRate.getKey(), energyRate);
                    energyPlanSeason.getEnergyRateList().add(energyRate);
                }
                touList.add(energyRate);
            }
            EnergyRateRow row =  new EnergyRateRow(energyPlan, 0, touList);
            row.addChangeHandler(new ItemChangedHandler<EnergyPlan>() {
                @Override
                public void onChanged(ItemChangedEvent<EnergyPlan> event) {
                    LOGGER.fine("Energy Rate Season 4: onChanged");
                    handlerManager.fireEvent(event);
                }
            });

            rowPanel.add(row);
        } else if (hasTier) {
            tierLabelPlaceholder.setVisible(true);
            for (int step = 0; step < energyPlan.getNumberTier(); step++) {
                List<EnergyRate> tierList = new ArrayList<EnergyRate>();
                EnergyRate energyRate = rateMap.get(step + TOUPeakType.OFF_PEAK.name());
                if (energyRate == null) {
                    energyRate = new EnergyRate(energyPlanSeason.getId(), step, TOUPeakType.OFF_PEAK);
                    rateMap.put(energyRate.getKey(), energyRate);
                    energyPlanSeason.getEnergyRateList().add(energyRate);
                }
                tierList.add(energyRate);
                EnergyRateRow row = new EnergyRateRow(energyPlan, step, tierList);
                row.addChangeHandler(new ItemChangedHandler<EnergyPlan>() {
                    @Override
                    public void onChanged(ItemChangedEvent<EnergyPlan> event) {
                        LOGGER.fine("Energy Rate Season 2: onChanged");
                        handlerManager.fireEvent(event);
                    }
                });
                rowPanel.add(row);
            }
        } else {
            //Flat
            //Just add a single row w/out a label
            tierLabelPlaceholder.setVisible(false);
            EnergyRate energyRate = rateMap.get("0" + TOUPeakType.OFF_PEAK.name());
            List<EnergyRate> tierList = new ArrayList<EnergyRate>();
            if (energyRate == null) {
                energyRate = new EnergyRate(energyPlanSeason.getId(), 0, TOUPeakType.OFF_PEAK);
                rateMap.put(energyRate.getKey(), energyRate);
                energyPlanSeason.getEnergyRateList().add(energyRate);
            }
            tierList.add(energyRate);
            EnergyRateRow row = new EnergyRateRow(energyPlan, 0, tierList);
            row.addChangeHandler(new ItemChangedHandler<EnergyPlan>() {
                @Override
                public void onChanged(ItemChangedEvent<EnergyPlan> event) {
                    LOGGER.fine("Energy Rate Season 3: onChanged");
                    handlerManager.fireEvent(event);
                }
            });
            rowPanel.add(row);
        }
    }

    public void setEnabled(boolean enabled) {


    }

    public void addChangeHandler(ItemChangedHandler<EnergyPlan> itemChangedHandler) {
        handlerManager.addHandler(ItemChangedEvent.TYPE, itemChangedHandler);
    }


    //Skin mapping
    @UiTemplate("EnergyRateSeason.ui.xml")
    interface DefaultBinder extends UiBinder<Widget, EnergyRateSeason> {
    }


}
