package com.link_intersystems.dbunit.migration.datasets;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class DataSetsConfig {

    private String[] resources = new String[0];
    private boolean defaultResources;
    private File sourceBasedir;
    private String[] tableOrder;
    private File targetBasedir;

    private String charset = "UTF-8";

    private FlatXmlConfig flatXml = new FlatXmlConfig();
    private SqlConfig sql = new SqlConfig();



    public boolean isDefaultResources() {
        return defaultResources || resources.length == 0;
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

    public File getSourceBasedir() {
        return sourceBasedir;
    }

    public void setSourceBasedir(File sourceBasedir) {
        this.sourceBasedir = sourceBasedir;
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

    public Charset getCharset() {
        return Charset.forName(charset);
    }

    public void setCharset(Charset charset) {
        this.charset = charset.name();
    }

    public FlatXmlConfig getFlatXml() {
        return flatXml;
    }

    public SqlConfig getSql() {
        return sql;
    }
}
