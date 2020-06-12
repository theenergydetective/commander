package com.ted.commander.common.model.history;


public interface HistoryMTU extends HistoryDemandPeak {


    double getEnergy();

    void setEnergy(double energy);

    double getDemandPeak();

    void setDemandPeak(double demandPeak);

    double getTouOffPeak();

    void setTouOffPeak(double touOffPeak);

    double getTouPeak();

    void setTouPeak(double touPeak);

    double getTouMidPeak();

    void setTouMidPeak(double touMidPeak);

    double getTouSuperPeak();

    void setTouSuperPeak(double touSuperPeak);

}
