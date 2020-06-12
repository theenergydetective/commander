package com.ted.commander.server.model;

import com.ted.commander.common.enums.TOUPeakType;
import com.ted.commander.common.model.history.HistoryTOU;


/**
 * Created by pete on 1/29/2016.
 */
public class TOUDifference {
    TOUPeakType touPeakType = TOUPeakType.OFF_PEAK;
    double net = 0.0;
    double load = 0.0;
    double generation = 0.0;


    public TOUPeakType getTouPeakType() {
        return touPeakType;
    }

    public void setTouPeakType(TOUPeakType touPeakType) {
        this.touPeakType = touPeakType;
    }

    public double getNet() {
        return net;
    }

    public void setNet(double net) {
        this.net = net;
    }

    public double getLoad() {
        return load;
    }

    public void setLoad(double load) {
        this.load = load;
    }

    public double getGeneration() {
        return generation;
    }

    public void setGeneration(double generation) {
        this.generation = generation;
    }


    public void addTo(HistoryTOU historyTOU) {
        switch (touPeakType) {
            case OFF_PEAK:
                historyTOU.setTouOffPeakNet(historyTOU.getTouOffPeakNet() + net);
                historyTOU.setTouOffPeakGen(historyTOU.getTouOffPeakGen() + generation);
                historyTOU.setTouOffPeakLoad(historyTOU.getTouOffPeakLoad() + load);
                break;
            case PEAK:
                historyTOU.setTouPeakNet(historyTOU.getTouPeakNet() + net);
                historyTOU.setTouPeakGen(historyTOU.getTouPeakGen() + generation);
                historyTOU.setTouPeakLoad(historyTOU.getTouPeakLoad() + load);


                break;
            case MID_PEAK:
                historyTOU.setTouMidPeakNet(historyTOU.getTouMidPeakNet() + net);
                historyTOU.setTouMidPeakGen(historyTOU.getTouMidPeakGen() + generation);
                historyTOU.setTouMidPeakLoad(historyTOU.getTouMidPeakLoad() + load);
                break;

            case SUPER_PEAK:
                historyTOU.setTouSuperPeakNet(historyTOU.getTouSuperPeakNet() + net);
                historyTOU.setTouSuperPeakGen(historyTOU.getTouSuperPeakGen() + generation);
                historyTOU.setTouSuperPeakLoad(historyTOU.getTouSuperPeakLoad() + load);
                break;

        }

    }

}
