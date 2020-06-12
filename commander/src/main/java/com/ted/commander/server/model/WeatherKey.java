package com.ted.commander.server.model;

import java.math.BigDecimal;

/**
 * Created by pete on 4/7/2015.
 */
public class WeatherKey {

    long id = 0;
    long cityId = 0;
    BigDecimal lat;
    BigDecimal lon;
    boolean metric = false; //US Default

    public long getCityId() {
        return cityId;
    }

    public void setCityId(long cityId) {
        this.cityId = cityId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public BigDecimal getLat() {
        return lat;
    }

    public void setLat(BigDecimal lat) {
        this.lat = lat;
    }

    public BigDecimal getLon() {
        return lon;
    }

    public void setLon(BigDecimal lon) {
        this.lon = lon;
    }

    public boolean isMetric() {
        return metric;
    }

    public void setMetric(boolean metric) {
        this.metric = metric;
    }

    @Override
    public String toString() {
        return "WeatherKey{" +
                "id=" + id +
                ", cityId=" + cityId +
                ", lat=" + lat +
                ", lon=" + lon +
                '}';
    }
}
