package com.link_intersystems.dbunit.migration.datasets;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class DataSetsConfig {

    private String[] resources;
    private boolean defaultResources;
    private File sourceBasedir;
    private String[] tableOrder;
    private File targetBasedir;

    public boolean isDefaultResources() {
        return defaultResources;
    }

    public void setDefaultResources(boolean defaultResources) {
        this.defaultResources = defaultResources;
    }

    public String[] getResources() {
        return resources;
    }

    public void setResources(String[] resources) {
        this.resources = resources;
    }

    public String[] getTableOrder() {
        return tableOrder;
    }

    public void setTableOrder(String[] tableOrder) {
        this.tableOrder = tableOrder;
    }

    public void setSourceBasedir(File sourceBasedir) {
        this.sourceBasedir = sourceBasedir;
    }

    public File getSourceBasedir() {
        return sourceBasedir;
    }

    public Path getBasepath() {
        return getSourceBasedir().toPath();
    }


    public File getTargetBasedir() {
        return targetBasedir;
    }

    public void setTargetBasedir(File targetBasedir) {
        this.targetBasedir = targetBasedir;
    }

    public Path getTargetBasepath() {
        return getTargetBasedir().toPath();
    }

    public String[] getPatternResources() {
        return Arrays.stream(resources)
                .filter(r -> r.startsWith("glob:"))
                .toArray(String[]::new);
    }

    public String[] getNonPatternResources() {
        return Arrays.stream(resources)
                .filter(r -> !r.startsWith("glob:"))
                .toArray(String[]::new);
    }
}
