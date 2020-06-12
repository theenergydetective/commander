/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.resources;

import com.ted.commander.common.enums.LanguageCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class CountryCodeFactory {
    static final Logger LOGGER = Logger.getLogger(CountryCodeFactory.class.getName());
    protected static CountryCodeFactory countryCodeFactory;

    List<CountryCode> countryCodeList = new ArrayList<CountryCode>();
    HashMap<String, List<RegionCode>> regionCodeMap = new HashMap<String, List<RegionCode>>();

    public static CountryCodeFactory getInstance(LanguageCode languageCode) {
        if (countryCodeFactory == null) {

            countryCodeFactory = new CountryCodeFactory();

            String countryCSV = null;
            String regionCSV = null;

            switch (languageCode) {
                case ES:
                    countryCSV = TextFileResource.INSTANCE.ES_COUNTRYCODE().getText();
                    regionCSV = TextFileResource.INSTANCE.ES_REGION().getText();
                    break;
                default: {
                    countryCSV = TextFileResource.INSTANCE.EN_COUNTRYCODE().getText();
                    regionCSV = TextFileResource.INSTANCE.EN_REGION().getText();
                }
            }

            //Parse the country codes
            String[] csvRow = countryCSV.split("\n");
            for (String s : csvRow) {
                String[] csvData = s.trim().split(",");
                if (csvData.length == 2) {
                    CountryCode countryCode = new CountryCode(csvData[0], csvData[1].replace("\"", ""));
                    countryCodeFactory.countryCodeList.add(countryCode);
                }
            }

            //Parse the region codes
            csvRow = regionCSV.split("\n");

            for (String s : csvRow) {
                String[] csvData = s.trim().split(",");
                if (csvData.length != 3) continue;
                ;
                List<RegionCode> regionCodeList = countryCodeFactory.regionCodeMap.get(csvData[0]);
                if (regionCodeList == null) {
                    regionCodeList = new ArrayList<RegionCode>();
                    countryCodeFactory.regionCodeMap.put(csvData[0], regionCodeList);
                }
                RegionCode regionCode = new RegionCode(csvData[0], csvData[1], csvData[2]);
                regionCodeList.add(regionCode);
            }


        }

        return countryCodeFactory;
    }

    public List<CountryCode> getCountryCodeList() {
        return countryCodeList;
    }

    public List<RegionCode> getRegionCodeList(String countryCode) {
        LOGGER.fine("Looking up region code for " + countryCode);
        return regionCodeMap.get(countryCode);
    }
}
