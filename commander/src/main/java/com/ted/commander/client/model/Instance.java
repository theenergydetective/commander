/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.model;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.storage.client.Storage;
import com.ted.commander.client.enums.BrowserStorageField;
import com.ted.commander.client.enums.LogoType;
import com.ted.commander.client.enums.SkinType;
import com.ted.commander.common.model.*;

import java.util.logging.Logger;

/**
 * This keeps the runtime properties of the instance
 */
public class Instance {


    static final InstanceCodec instanceCodec = GWT.create(InstanceCodec.class);

    static final Logger LOGGER = Logger.getLogger(Instance.class.toString());
    protected SkinType skin = SkinType.DEFAULT;
    protected LogoType logo = LogoType.DEFAULT;
    VirtualECC dashboardLocation = null;
    VirtualECC lastEditLocation = null;
    DashboardOptions dashboardOptions = new DashboardOptions();

    ComparisonQueryRequest comparisonQueryRequest = new ComparisonQueryRequest();

    AccountMembership lastEditedAccountMembership;
    AccountMember lastEditedAccountMember;
    MTU lastEditedMTU;

    private VirtualECCMTU lastEditedVirtualMTU;
    private String refreshToken;
    private boolean showMaintenanceDialog = true;

    public Instance() {
        String resourceURL = GWT.getModuleBaseURL();
        if (resourceURL.contains("white")) {
            LOGGER.fine("USING NBPOWER!");
            logo = LogoType.NBPOWER;
            skin = SkinType.NBPOWER;
        }
    }

    public static void clear(Instance instance) {
        LOGGER.severe("Deleting the current instance");
        Storage browserStorage = Storage.getLocalStorageIfSupported();
        if (browserStorage != null)
            browserStorage.removeItem(BrowserStorageField.COMMANDER_INSTANCE_TOKEN_20150106.name());
        if (instance != null) {
            instance.refreshToken = "";
        }
    }

    public static Instance load() {

        Storage browserStorage = Storage.getLocalStorageIfSupported();
        if (browserStorage != null) {
            String json = browserStorage.getItem(BrowserStorageField.COMMANDER_INSTANCE_TOKEN_20150106.name());
            if (json != null) {
                LOGGER.fine("LOADING INSTANCE: " + json.toString());
                JSONValue jsonValue = JSONParser.parseStrict(json);
                return instanceCodec.decode(jsonValue);
            }
        }

        LOGGER.info("No instance found. returning new instance");
        return new Instance();
    }

    public static void store(Instance instance) {

        Storage browserStorage = Storage.getLocalStorageIfSupported();
        if (browserStorage != null) {
            if (instance == null)
                browserStorage.removeItem(BrowserStorageField.COMMANDER_INSTANCE_TOKEN_20150106.name());
            else {
                JSONValue json = instanceCodec.encode(instance);
                LOGGER.fine("STORING INSTANCE: " + json.toString());
                browserStorage.setItem(BrowserStorageField.COMMANDER_INSTANCE_TOKEN_20150106.name(), json.toString());
            }
        }

    }

    public SkinType getSkin() {
        return skin;
    }

    public void setSkin(SkinType skin) {
        this.skin = skin;
    }

    public LogoType getLogo() {
        return logo;
    }

    public void setLogo(LogoType logo) {
        this.logo = logo;
    }

    public DashboardOptions getDashboardOptions() {
        if (dashboardOptions == null) return new DashboardOptions();
        return dashboardOptions;
    }

    public void setDashboardOptions(DashboardOptions dashboardOptions) {
        this.dashboardOptions = dashboardOptions;
        store(this);
    }

    public VirtualECC getDashboardLocation() {
        return dashboardLocation;
    }

    public void setDashboardLocation(VirtualECC dashboardLocation) {
        this.dashboardLocation = dashboardLocation;
        store(this);
    }

    public AccountMembership getLastEditedAccountMembership() {
        return lastEditedAccountMembership;
    }

    public void setLastEditedAccountMembership(AccountMembership lastEditedAccountMembership) {
        this.lastEditedAccountMembership = lastEditedAccountMembership;
    }

    public AccountMember getLastEditedAccountMember() {
        return lastEditedAccountMember;
    }

    public void setLastEditedAccountMember(AccountMember lastEditedAccountMember) {
        this.lastEditedAccountMember = lastEditedAccountMember;
    }

    public MTU getLastEditedMTU() {
        return lastEditedMTU;
    }

    public void setLastEditedMTU(MTU lastEditedMTU) {
        this.lastEditedMTU = lastEditedMTU;
    }

    public VirtualECC getLastEditLocation() {
        return lastEditLocation;
    }

    public void setLastEditLocation(VirtualECC lastEditLocation) {
        this.lastEditLocation = lastEditLocation;
    }

    public VirtualECCMTU getLastEditedVirtualMTU() {
        return lastEditedVirtualMTU;
    }

    public void setLastEditedVirtualMTU(VirtualECCMTU lastEditedVirtualMTU) {
        this.lastEditedVirtualMTU = lastEditedVirtualMTU;
    }

    public ComparisonQueryRequest getComparisonQueryRequest() {
        return comparisonQueryRequest;
    }

    public void setComparisonQueryRequest(ComparisonQueryRequest comparisonQueryRequest) {
        this.comparisonQueryRequest = comparisonQueryRequest;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        store(this);
    }
}
