package com.link_intersystems.dbunit.migration.datasets;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class FlatXmlConfig {
    private boolean columnSensing;

    public boolean isColumnSensing() {
        return columnSensing;
    }

    public void setColumnSensing(boolean columnSensing) {
        this.columnSensing = columnSensing;
    }
}
