/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.client.model;



import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OAuthResponse {

    static final OAuthResponseCodec codec = GWT.create(OAuthResponseCodec.class);

    private String access_token = null;
    private String refresh_token = null;
    private String token_type = null;
    private Long expires_in = 0l;
    private Long expiresEpoch = 0l;
    private String scope = null;
    private String error = null;
    private String error_description = null;

    public OAuthResponse() {
    }

    public OAuthResponse(String access_token) {
        this.access_token = access_token;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public Long getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(Long expires_in) {
        this.expires_in = expires_in;

    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public String getError() {
        return error;
    }

    public String getError_description() {
        return error_description;
    }

    public void setError_description(String error_description) {
        this.error_description = error_description;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public Long getExpiresEpoch() {
        return expiresEpoch;
    }

    public void setExpiresEpoch(Long expiresEpoch) {
        this.expiresEpoch = expiresEpoch;
    }


    public static OAuthResponse fromJsonString(String jsonString) {
        JSONValue jsonValue = JSONParser.parseLenient(jsonString);
        return codec.decode(jsonValue);
    }

    @Override
    public String toString() {
        return "OAuthResponse{" +
                "access_token='" + access_token + '\'' +
                ", refresh_token='" + refresh_token + '\'' +
                ", token_type='" + token_type + '\'' +
                ", expires_in=" + expires_in +
                ", expiresEpoch=" + expiresEpoch +
                ", scope='" + scope + '\'' +
                ", error='" + error + '\'' +
                ", error_description='" + error_description + '\'' +
                '}';
    }

    public boolean isExpired() {
        if (expiresEpoch == 0) return true;
        Date now = new Date();
        long nowEpoch = now.getTime();
        nowEpoch += 60000;
        //Add a minute in case there is some time wobble.
        return (nowEpoch > expiresEpoch);
    }

    public boolean isError() {
        if (error == null) return false;
        if (error.isEmpty()) return false;
        //These two test cases has to do w/ the GWT json parser actually setting a string value to 'null' if its undefined.
        if (error.toLowerCase().equals("null")) return false;
        if (error.toLowerCase().equals("undefined")) return false;
        return true;
    }

    public void setError(String error) {
        this.error = error;
    }
}
