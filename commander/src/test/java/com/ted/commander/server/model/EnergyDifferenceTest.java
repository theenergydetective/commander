package com.ted.commander.server.model;

import com.ted.commander.common.model.history.EnergyDifference;
import com.ted.commander.common.model.history.HistoryBillingCycle;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * This is a unit test to test the new energy post service (DATABASE 2.0). This uses Account 52 as the 'playback'
 */
@RunWith(MockitoJUnitRunner.class)
public class EnergyDifferenceTest {

    @Test
    public void testAddTo(){

        EnergyDifference energyDifference = new EnergyDifference();
        energyDifference.setNet(1.0);
        energyDifference.setGeneration(-1.1234567);
        energyDifference.setLoad(-1.1234567);

        long iterations = 1000000;

        HistoryBillingCycle historyBillingCycle = new HistoryBillingCycle();

        for (long i=0; i < iterations; i++){
            energyDifference.addTo(historyBillingCycle);
        }

        System.err.println();
        System.err.println(historyBillingCycle);


 }


}
