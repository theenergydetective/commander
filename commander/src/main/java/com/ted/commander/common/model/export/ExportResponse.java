package com.ted.commander.common.model.export;

import java.io.Serializable;


public class ExportResponse implements Serializable {
    String url = "";

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
