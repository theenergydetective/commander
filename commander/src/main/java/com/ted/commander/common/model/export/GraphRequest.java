package com.ted.commander.common.model.export;

import com.ted.commander.common.enums.DataExportFileType;
import com.ted.commander.common.enums.ExportGraphType;
import com.ted.commander.common.enums.GraphLineType;

import java.io.Serializable;

/**
 * Created by pete on 1/31/2015.
 */
public class GraphRequest extends ExportRequest implements Serializable {
    private GraphLineType graphLineType;
    private ExportGraphType exportGraphType;


    public GraphRequest() {
        setDataExportFileType(DataExportFileType.GRAPH);
    }

    public GraphLineType getGraphLineType() {
        return graphLineType;
    }

    public void setGraphLineType(GraphLineType graphLineType) {
        this.graphLineType = graphLineType;
    }

    public ExportGraphType getExportGraphType() {
        return exportGraphType;
    }

    public void setExportGraphType(ExportGraphType exportGraphType) {
        this.exportGraphType = exportGraphType;
    }


}
