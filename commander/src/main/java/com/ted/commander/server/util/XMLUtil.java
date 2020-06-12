/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.util;


import org.w3c.dom.Element;

/**
 * Created by IntelliJ IDEA.
 * User: newki
 * Date: Feb 26, 2009
 * Time: 7:34:55 AM
 * To change this template use File | Settings | File Templates.
 */
public class XMLUtil {

    public static String getString(Element element, String elementName, int index) {
        try {
            String str = element.getElementsByTagName(elementName).item(index).getFirstChild().getNodeValue();
            if (str == null) return "";
            return str;
        } catch (Exception e) {
            return "";
        }
    }

    public static int getInteger(Element element, String elementName, int index) {
        try {
            return Integer.parseInt(getString(element, elementName, index));
        } catch (Exception e) {
            return 0;
        }
    }

    public static int getInteger(Element element, String elementName) {
        return getInteger(element, elementName, 0);
    }


    public static long getLong(Element element, String elementName, int index) {
        try {
            return Long.parseLong(getString(element, elementName, index));
        } catch (Exception e) {
            return 0;
        }
    }

    public static long getLong(Element element, String elementName) {
        return getLong(element, elementName, 0);
    }


    public static boolean getBoolean(Element element, String elementName, int index) {
        String str = getString(element, elementName, index);
        return str.toUpperCase().equals("TRUE");
    }


    public static String getString(Element element, String elementName) {
        return getString(element, elementName, 0);
    }


    public static boolean getBoolean(Element element, String elementName) {
        return getBoolean(element, elementName, 0);
    }

    public static String getSubstring(String str, int startIndex, int maxLength) {
        int strLength = str.length();
        if (startIndex + maxLength >= strLength) return str.substring(startIndex);
        return str.substring(startIndex, startIndex + maxLength);
    }


}
