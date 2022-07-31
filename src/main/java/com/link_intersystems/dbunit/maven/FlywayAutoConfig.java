package com.link_intersystems.dbunit.maven;

import com.link_intersystems.dbunit.migration.flyway.FlywayConfig;
import com.link_intersystems.io.FileScanner;
import org.apache.maven.model.Build;
import org.apache.maven.model.Resource;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class FlywayAutoConfig {

    public static final String[] DEFAULT_FLYWAY_LOCATIONS = {"**/db/migration", "db/migration"};

    private MavenProject project;

    public FlywayAutoConfig(MavenProject project) {
        this.project = project;
    }

    public void configure(FlywayConfig flyway) {
        String[] locations = flyway.getLocations();
        if (locations == null) {
            locations = guessFlywayLocations(project);
        }
        flyway.setLocations(locations);
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
