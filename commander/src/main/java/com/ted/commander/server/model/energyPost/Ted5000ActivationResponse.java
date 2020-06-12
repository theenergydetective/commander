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
public class Ted5000ActivationResponse {
    @XmlElement
    public String PostServer;

    @XmlElement
    public String UseSSL = "F";

    @XmlElement
    public int PostPort = 80;

    @XmlElement
    public String PostURL = "/api/postData";

    @XmlElement
    public String AuthToken;

    @XmlElement
    public int PostRate = 1;

    @XmlElement
    public String HighPrec = "T";

    @XmlElement
    public String Spyder = "T";

}
