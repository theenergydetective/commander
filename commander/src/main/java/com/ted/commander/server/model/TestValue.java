package com.ted.commander.server.model;

public class TestValue{
    long id;
    int value;

    public TestValue(long id, int value) {
        this.id = id;
        this.value = value;
    }

    public long getId() {
        return id;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "TestValue{" +
                "id=" + id +
                ", value=" + value +
                '}';
    }
}