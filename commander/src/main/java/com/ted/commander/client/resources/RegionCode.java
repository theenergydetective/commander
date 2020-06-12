/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.resources;

/**
 * Created by pete on 9/17/2014.
 */
public class RegionCode {

    private final String countryCode;
    private final String regionCode;
    private final String description;

    public RegionCode(String countryCode, String regionCode, String description) {
        this.countryCode = countryCode;
        this.regionCode = regionCode;
        this.description = description;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RegionCode that = (RegionCode) o;

        if (countryCode != null ? !countryCode.equals(that.countryCode) : that.countryCode != null) return false;
        if (regionCode != null ? !regionCode.equals(that.regionCode) : that.regionCode != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = countryCode != null ? countryCode.hashCode() : 0;
        result = 31 * result + (regionCode != null ? regionCode.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "RegionCode{" +
                "countryCode='" + countryCode + '\'' +
                ", regionCode='" + regionCode + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

}
