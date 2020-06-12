package com.ted.commander.common.enums;

/**
 * Created by pete on 1/31/2015.
 */
public enum DataExportFileType {
    CSV(".csv"),
    XML(".xml"),
    JSON(".json"),
    XLS(".xls"),
    GRAPH(".csv");

    final String extension;

    DataExportFileType(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }
}
