package com.ted.commander.common.model.history;

import com.ted.commander.common.model.CalendarKey;

public interface HistoryDemandPeak {

    double getDemandPeak();

    void setDemandPeak(double demandPeak);

    Long getDemandPeakTime();

    void setDemandPeakTime(Long demandPeakTime);

    CalendarKey getDemandPeakCalendarKey();

    void setDemandPeakCalendarKey(CalendarKey key);

    double getLoadPeak();

    void setLoadPeak(double LoadPeak);

    Long getLoadPeakTime();

    void setLoadPeakTime(Long LoadPeakTime);

    CalendarKey getLoadPeakCalendarKey();

    void setLoadPeakCalendarKey(CalendarKey key);


    double getGenerationPeak();

    void setGenerationPeak(double GenerationPeak);

    Long getGenerationPeakTime();

    void setGenerationPeakTime(Long GenerationPeakTime);

    CalendarKey getGenerationPeakCalendarKey();

    void setGenerationPeakCalendarKey(CalendarKey key);


}
