/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.model.energyPost;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by pete on 9/12/2014.
 */
@XmlRootElement
public class Ted5000Activation {

    @XmlElement(name = "Gateway")
    public String gateway;

    @XmlElement(name = "Unique")
    public String unique;

    @Override
    public String toString() {
        return "Ted5000Activation{" +
                "gateway='" + gateway + '\'' +
                ", unique='" + unique + '\'' +
                '}';
    }

    public Long getECCId() {
        return Long.parseLong(gateway, 16);
    }
}
