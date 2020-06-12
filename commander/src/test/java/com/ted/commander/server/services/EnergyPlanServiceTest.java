package com.ted.commander.server.services;

import com.ted.commander.common.model.*;
import com.ted.commander.server.dao.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Date;
import java.util.TimeZone;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

/**
 * This is a unit test to test the new energy post service (DATABASE 2.0). This uses Account 52 as the 'playback'
 */
@RunWith(MockitoJUnitRunner.class)
public class EnergyPlanServiceTest {

    @Mock
    EnergyPlanDAO energyPlanDAO;

    @Mock
    EnergyPlanSeasonDAO energyPlanSeasonDAO;

    @Mock
    EnergyPlanTOULevelDAO energyPlanTOULevelDAO;

    @Mock
    EnergyPlanTierDAO energyPlanTierDAO;

    @Mock
    AdditionalChargeDAO additionalChargeDAO;

    @Mock
    DemandChargeTierDAO demandChargeTierDAO;

    @Mock
    DemandChargeTOUDAO demandChargeTOUDAO;

    @Mock
    VirtualECCDAO virtualECCDAO;


    @Mock
    EnergyPlanTOUDAO energyPlanTOUDAO;

    @Mock
    EnergyRateDAO energyRateDAO;


    @InjectMocks
    EnergyPlanService energyPlanService;
    


    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        reset(energyPlanDAO);
        reset(energyPlanSeasonDAO);
        reset(energyPlanTOULevelDAO);
        reset(energyPlanTierDAO);
        reset(additionalChargeDAO);
        reset(demandChargeTierDAO);
        reset(demandChargeTOUDAO);
        reset(virtualECCDAO);
        reset(energyPlanTOUDAO);
        reset(energyRateDAO);
    }




}
