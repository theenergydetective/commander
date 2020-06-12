/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.model.energyPost;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;

/**
 * Created by pete on 9/12/2014.
 */
@XmlRootElement(name = "MTU")
public class EnergyMTUPost implements Serializable {

    private String mtuSerial;
    private int mtuTypeOrdinal;
    private List<EnergyCumulativePost> cumulativePostList;
//    private boolean a5k;

    public EnergyMTUPost() {
    }

//    public EnergyMTUPost(String mtuSerial, int mtuTypeOrdinal, boolean a5k) {
//        this.mtuSerial = mtuSerial;
//        this.mtuTypeOrdinal = mtuTypeOrdinal;
//        this.a5k = a5k;
//        cumulativePostList = new ArrayList<EnergyCumulativePost>();
//    }

    @XmlAttribute(name = "ID")
    public String getMtuSerial() {
        return mtuSerial;
    }

    public void setMtuSerial(String mtuSerial) {
        this.mtuSerial = mtuSerial;
    }


    @XmlAttribute(name = "type")
    public int getMtuTypeOrdinal() {
        return mtuTypeOrdinal;
    }

    public void setMtuTypeOrdinal(int mtuTypeOrdinal) {
        this.mtuTypeOrdinal = mtuTypeOrdinal;
    }

    @XmlElement(name = "cumulative")
    public List<EnergyCumulativePost> getCumulativePostList() {
        return cumulativePostList;
    }

    public void setCumulativePostList(List<EnergyCumulativePost> cumulativePostList) {
        this.cumulativePostList = cumulativePostList;
    }

    @Override
    public String toString() {
        return "EnergyMTUPost{" +
                "mtuSerial='" + mtuSerial + '\'' +
                ", mtuTypeOrdinal=" + mtuTypeOrdinal +
                ", cumulativePostList=" + cumulativePostList +
                '}';
    }

    @JsonIgnore
    public Long getMTUId() {
        return Long.parseLong(mtuSerial, 16);
    }

    @JsonIgnore
    public boolean is5k() {
        return mtuSerial.startsWith("10");
    }
}
