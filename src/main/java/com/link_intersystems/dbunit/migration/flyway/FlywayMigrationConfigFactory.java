package com.link_intersystems.dbunit.migration.flyway;

import org.flywaydb.core.api.configuration.FluentConfiguration;

import static java.util.Arrays.stream;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class FlywayMigrationConfigFactory {

    public FlywayMigrationConfig create(FlywayConfig flywayConfig) {
        FlywayMigrationConfig migrationConfig = new FlywayMigrationConfig();
        FluentConfiguration configure = org.flywaydb.core.Flyway.configure();

        String[] locations = flywayConfig.getLocations();
        String[] effectiveLocations = stream(locations)
                .map(this::ensureFlywayFilesystemPrefix)
                .toArray(String[]::new);
        configure.locations(effectiveLocations);

        configure.placeholders(flywayConfig.getPlaceholders());

        migrationConfig.setFlywayConfiguration(configure);

        String sourceVersion = flywayConfig.getSourceVersion();
        if (sourceVersion != null) {
            migrationConfig.setSourceVersion(sourceVersion);
        }

        String targetVersion = flywayConfig.getTargetVersion();
        if (targetVersion != null) {
            migrationConfig.setTargetVersion(targetVersion);
        }

        return migrationConfig;
    }

    private String ensureFlywayFilesystemPrefix(String location) {
        return "filesystem:".concat(location);
    }
}
