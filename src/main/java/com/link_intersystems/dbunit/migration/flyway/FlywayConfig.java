package com.link_intersystems.dbunit.migration.flyway;

import com.link_intersystems.dbunit.flyway.FlywayMigrationConfig;
import org.flywaydb.core.api.configuration.FluentConfiguration;

import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.stream;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class FlywayConfig {

    private Map<String, String> placeholders = new HashMap<>();
    private String sourceVersion;
    private String targetVersion;
    private String[] locations;

    public void setPlaceholders(Map<String, String> placeholders) {
        this.placeholders = placeholders;
    }

    public Map<String, String> getPlaceholders() {
        return placeholders;
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

    public FlywayMigrationConfig getFlywayMigrationConfig() {
        FlywayMigrationConfig migrationConfig = new FlywayMigrationConfig();
        FluentConfiguration configure = org.flywaydb.core.Flyway.configure();

        String[] locations = getLocations();
        String[] effectiveLocations = stream(locations)
                .map(this::ensureFlywayFilesystemPrefix)
                .toArray(String[]::new);
        configure.locations(effectiveLocations);
        configure.placeholders(getPlaceholders());

        migrationConfig.setFlywayConfiguration(configure);
        migrationConfig.setSourceVersion("1");
        return migrationConfig;
    }

    private String ensureFlywayFilesystemPrefix(String location) {
        return "filesystem:".concat(location);
    }


}

