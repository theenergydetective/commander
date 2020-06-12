package com.ted.commander.client.util;

/**
 * Created by pete on 5/11/2016.
 */
public class MathUtil {

    public static boolean doubleEquals(double a, double b, double epsilon){
        return a == b ? true : Math.abs(a - b) < epsilon;
    }
}
