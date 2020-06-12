/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.common.model;


import com.ted.commander.common.enums.VirtualECCType;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Represents a virtual ECC in the system.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class VirtualECC implements Serializable {

    Long id;
    Long accountId;
    String name;
    String street1;
    String street2;
    String city;
    String state;
    String postal;
    String country = "US";
    String timezone = "US/Eastern";


    long weatherId = 0;
    VirtualECCType systemType = VirtualECCType.NET_ONLY;
    Long energyPlanId = 0l;

    public VirtualECC() {
    }

    public VirtualECC(Long accountId, String name, VirtualECCType systemType, String timezone) {
        this.accountId = accountId;
        this.name = name;
        this.systemType = systemType;
        this.timezone = timezone;
        this.street1 = "";
        this.street2 = "";
        this.city = "";
        this.country = "US";
        this.postal = "";
        this.weatherId = 0;

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }


    public String getStreet1() {
        return street1;
    }

    public void setStreet1(String street1) {
        this.street1 = street1;
    }

    public String getStreet2() {
        return street2;
    }

    public void setStreet2(String street2) {
        this.street2 = street2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostal() {
        return postal;
    }

    public void setPostal(String postal) {
        this.postal = postal;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public VirtualECCType getSystemType() {
        return systemType;
    }

    public void setSystemType(VirtualECCType systemType) {
        this.systemType = systemType;
    }

    public long getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(long weatherId) {
        this.weatherId = weatherId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VirtualECC that = (VirtualECC) o;

        if (weatherId != that.weatherId) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (accountId != null ? !accountId.equals(that.accountId) : that.accountId != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (street1 != null ? !street1.equals(that.street1) : that.street1 != null) return false;
        if (street2 != null ? !street2.equals(that.street2) : that.street2 != null) return false;
        if (city != null ? !city.equals(that.city) : that.city != null) return false;
        if (state != null ? !state.equals(that.state) : that.state != null) return false;
        if (postal != null ? !postal.equals(that.postal) : that.postal != null) return false;
        if (country != null ? !country.equals(that.country) : that.country != null) return false;
        if (timezone != null ? !timezone.equals(that.timezone) : that.timezone != null) return false;
        if (systemType != that.systemType) return false;
        return energyPlanId != null ? energyPlanId.equals(that.energyPlanId) : that.energyPlanId == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (accountId != null ? accountId.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (street1 != null ? street1.hashCode() : 0);
        result = 31 * result + (street2 != null ? street2.hashCode() : 0);
        result = 31 * result + (city != null ? city.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);
        result = 31 * result + (postal != null ? postal.hashCode() : 0);
        result = 31 * result + (country != null ? country.hashCode() : 0);
        result = 31 * result + (timezone != null ? timezone.hashCode() : 0);
        result = 31 * result + (int) (weatherId ^ (weatherId >>> 32));
        result = 31 * result + (systemType != null ? systemType.hashCode() : 0);
        result = 31 * result + (energyPlanId != null ? energyPlanId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "VirtualECC{" +
                "id=" + id +
                ", accountId=" + accountId +
                ", name='" + name + '\'' +
                ", street1='" + street1 + '\'' +
                ", street2='" + street2 + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", postal='" + postal + '\'' +
                ", country='" + country + '\'' +
                ", timezone='" + timezone + '\'' +
                ", weatherId=" + weatherId +
                ", systemType=" + systemType +
                '}';
    }

    @JsonIgnore
    public String getFormattedAddress() {

        StringBuilder stringBuilder = new StringBuilder();
        if (street1 != null && !street1.isEmpty()) stringBuilder.append(street1);

//        if (street2 != null && !street2.isEmpty()) {
//            if (stringBuilder.length() != 0) stringBuilder.append(", ");
//            stringBuilder.append(street2);
//        }

        if (city != null && !city.isEmpty()) {
            if (stringBuilder.length() != 0) stringBuilder.append(" ");
            stringBuilder.append(city);
        }

        if (state != null && !state.isEmpty()) {
            if (stringBuilder.length() != 0) stringBuilder.append(", ");
            stringBuilder.append(state);
        }

        return stringBuilder.toString();
    }

    public Long getEnergyPlanId() {
        if (energyPlanId == null) return 0l;
        return energyPlanId;
    }

    public void setEnergyPlanId(Long energyPlanId) {
        this.energyPlanId = energyPlanId;
    }
}
