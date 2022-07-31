package com.link_intersystems.dbunit.maven;

import com.link_intersystems.dbunit.flyway.FlywayMigrationConfig;
import com.link_intersystems.io.FileScanner;
import org.apache.maven.model.Build;
import org.apache.maven.model.Resource;
import org.apache.maven.project.MavenProject;
import org.flywaydb.core.api.configuration.FluentConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.stream;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class FlywayConfig {

    public static final String[] DEFAULT_FLYWAY_LOCATIONS = {"**/db/migration", "db/migration"};

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

    public FlywayMigrationConfig getFlywayMigrationConfig() {
        FlywayMigrationConfig migrationConfig = new FlywayMigrationConfig();
        FluentConfiguration configure = org.flywaydb.core.Flyway.configure();

        String[] locations = getLocations();
        String[] effectiveLocations = stream(locations)
                .map(this::ensureFlywayFilesystemPrefix)
                .toArray(String[]::new);
        configure.locations(effectiveLocations);
        Map<String, String> placeholderMap = getPlaceholderMap();
        configure.placeholders(placeholderMap);

        migrationConfig.setFlywayConfiguration(configure);
        migrationConfig.setSourceVersion("1");
        return migrationConfig;
    }

    private String ensureFlywayFilesystemPrefix(String location) {
        return "filesystem:".concat(location);
    }

    public void setProject(MavenProject project) {
        if (locations == null) {
            locations = guessFlywayLocations(project);
        }
    }

    protected String[] guessFlywayLocations(MavenProject project) {
        List<File> files = new ArrayList<>();
        Build build = project.getBuild();
        List<Resource> resources = build.getResources();

        FileScanner fileScanner = new FileScanner();
        fileScanner.addIncludeDirectoryPatterns(DEFAULT_FLYWAY_LOCATIONS);

        for (Resource resource : resources) {
            String directory = resource.getDirectory();
            files.addAll(fileScanner.scan(new File(directory)));
        }

        return files.stream()
                .map(File::getAbsolutePath)
                .toArray(String[]::new);
    }
}

