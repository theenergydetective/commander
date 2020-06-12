package com.ted.commander.common.enums;

public enum HolidayType {
    US,
    CANADA;

    @Override
    public String toString() {
        return name();
    }
}