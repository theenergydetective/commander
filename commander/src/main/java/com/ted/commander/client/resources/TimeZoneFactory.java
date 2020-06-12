/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.resources;


import com.ted.commander.common.enums.LanguageCode;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class TimeZoneFactory {
    static final Logger LOGGER = Logger.getLogger(TimeZoneFactory.class.getName());
    protected static TimeZoneFactory timeZoneFactory;

    List<TimeZoneCode> timeZoneList = new ArrayList<TimeZoneCode>();

    public static TimeZoneFactory getInstance(LanguageCode languageCode) {
        if (timeZoneFactory == null) {
            timeZoneFactory = new TimeZoneFactory();

            //Load the appropriate language file
            String timeZoneFile = null;
            switch (languageCode) {
                case ES:
                    timeZoneFile = TextFileResource.INSTANCE.ES_TIMEZONE().getText();
                    break;
                default: {
                    timeZoneFile = TextFileResource.INSTANCE.EN_TIMEZONE().getText();
                }
            }

            //Parse the country codes
            String[] csvRow = timeZoneFile.split("\n");
            for (String s : csvRow) {
                String[] values = s.split(",");
                if (values.length == 2) {

                    TimeZoneCode timeZoneCode = new TimeZoneCode(values[0], values[1]);
                    timeZoneFactory.timeZoneList.add(timeZoneCode);
                }

            }
        }
        return timeZoneFactory;
    }

    public List<TimeZoneCode> getTimeZoneList() {
        return timeZoneList;
    }
}
