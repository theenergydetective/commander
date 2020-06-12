/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.resources;

/**
 * Created by pete on 9/17/2014.
 */
public class CountryCode {

    private final String code;
    private final String country;

    public CountryCode(String code, String country) {
        this.code = code;
        this.country = country;
    }

    public String getCode() {
        return code;
    }

    public String getCountry() {
        return country;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CountryCode that = (CountryCode) o;

        if (code != null ? !code.equals(that.code) : that.code != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return code != null ? code.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "CountryCode{" +
                "code='" + code + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}
