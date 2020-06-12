/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.common.enums;


public enum MTUType {
    NET, LOAD, GENERATION, STAND_ALONE;


    public static MTUType findByType(boolean is5K, int type) {
        //The TYPE ID's of the two versions are slightly different. This maps them correctly.
        if (is5K) {
            switch (type) {
                case 0:
                    return LOAD;
                case 1:
                    return GENERATION;
                case 2:
                    return NET;
                default:
                    return STAND_ALONE;
            }
        } else {
            switch (type) {
                case 0:
                    return NET;
                case 1:
                    return LOAD;
                case 2:
                    return GENERATION;
                default:
                    return STAND_ALONE;
            }
        }
    }
}
