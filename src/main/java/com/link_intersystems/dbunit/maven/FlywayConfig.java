package com.link_intersystems.dbunit.maven;

import com.link_intersystems.dbunit.flyway.FlywayMigrationConfig;
import org.flywaydb.core.api.configuration.FluentConfiguration;

import java.util.Collections;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class FlywayConfig {

    private Map<String, String> placeholders;
    private String sourceVersion;
    private String targetVersion;
    private String[] locations;

    public void setPlaceholders(Map<String, String> placeholders) {
        this.placeholders = placeholders;
    }

    public Map<String, String> getPlaceholders() {
        return placeholders;
    }

    public Map<String, String> getPlaceholderMap() {
        return placeholders == null ? Collections.emptyMap() : placeholders;
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

    public FlywayMigrationConfig createFlywayMigrationConfig(Supplier<String[]> defaultLocationsSupplier) {
        FlywayMigrationConfig migrationConfig = new FlywayMigrationConfig();
        FluentConfiguration configure = org.flywaydb.core.Flyway.configure();

        String[] locations = getLocations();
        if (locations == null) {
            locations = defaultLocationsSupplier.get();
        }
        configure.locations(locations);
        Map<String, String> placeholderMap = getPlaceholderMap();
        configure.placeholders(placeholderMap);

        migrationConfig.setFlywayConfiguration(configure);
        migrationConfig.setSourceVersion("1");
        return migrationConfig;
    }
}

