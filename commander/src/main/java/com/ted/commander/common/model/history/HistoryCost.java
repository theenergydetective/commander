package com.ted.commander.common.model.history;


public interface HistoryCost {

    double getRateInEffect();

    void setRateInEffect(double rateInEffect);

    double getNetCost();

    void setNetCost(double netCost);

    double getLoadCost();

    void setLoadCost(double loadCost);

    double getGenCost();

    void setGenCost(double genCost);

}
