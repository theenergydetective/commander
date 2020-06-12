package com.ted.commander.server.model;

import java.math.BigDecimal;
import java.util.Objects;

public class WeatherKeyCoord {
    final BigDecimal lat;
    final BigDecimal lng;

    public WeatherKeyCoord(BigDecimal lat, BigDecimal lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public BigDecimal getLat() {
        return lat;
    }

    public BigDecimal getLng() {
        return lng;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WeatherKeyCoord that = (WeatherKeyCoord) o;
        return Objects.equals(lat, that.lat) &&
                Objects.equals(lng, that.lng);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lat, lng);
    }
}
