/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.util;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

public class TestUtil {

    static Long lastKey = null;

    /**
     * Returns a unique key for testing (used w/ email address, usernames, etc).
     *
     * @return
     */
    public static String getUniqueKey() {
        Long l = new Date().getTime();
        while (l.equals(lastKey)) {
            l = new Date().getTime();
        }
        lastKey = l;
        return l.toString();
    }

    public static String getFileAsString(String fileName) throws Exception{
        InputStream in =TestUtil.class.getClassLoader().getResourceAsStream(fileName);
        BufferedReader br= new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line).append("\n");
        }
        br.close();
        in.close();
        return sb.toString();
    };

}
