package com.link_intersystems.dbunit.migration.flyway;

import java.util.HashMap;
import java.util.Map;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class FlywayConfig {

    private Map<String, String> placeholders = new HashMap<>();
    private String sourceVersion;
    private String targetVersion;
    private String[] locations;

    public Map<String, String> getPlaceholders() {
        return placeholders;
    }

    public void setPlaceholders(Map<String, String> placeholders) {
        this.placeholders = placeholders;
    }

    public String getSourceVersion() {
        return sourceVersion;
    }

    public void setSourceVersion(String sourceVersion) {
        this.sourceVersion = sourceVersion;
    }

    public String getTargetVersion() {
        return targetVersion;
    }

    public void setTargetVersion(String targetVersion) {
        this.targetVersion = targetVersion;
    }

    public String[] getLocations() {
        return locations;
    }

    public void setLocations(String[] locations) {
        this.locations = locations;
    }


}

