package com.ted.commander.client.places;

import java.util.HashMap;

/**
 * Created by pete on 8/6/2015.
 */
public class PlaceUtil {
    public static HashMap<String, String> parseParameters(String parameters){
        HashMap<String, String> parameterMap = new HashMap<String, String>();
        String[] parameterPairs = parameters.split("&");
        for (int i = 0; i < parameterPairs.length; i++) {
            String[] nameAndValue = parameterPairs[i].split("=");
            parameterMap.put(nameAndValue[0].toLowerCase(), nameAndValue[1]);
        }
        return parameterMap;
    }
}
