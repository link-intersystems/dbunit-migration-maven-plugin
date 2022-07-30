package com.link_intersystems.dbunit.maven;

import com.link_intersystems.io.Unzip;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.plugin.testing.stubs.MavenProjectStub;
import org.apache.maven.project.DefaultProjectBuildingRequest;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
class FlywayMigrateMojoParameterTest extends AbstractMojoTestCase {

    private Path pomXmlPath;

    @BeforeEach
    protected void setUp(@TempDir Path tmpDir) throws Exception {
        super.setUp();

        try (InputStream in = getClass().getResourceAsStream("/migrate-project.zip")) {
            Unzip.unzip(in, tmpDir);
        }

        pomXmlPath = tmpDir.resolve("pom.xml");
    }

    @Test
    void execute() throws Exception {

        ProjectBuilder projectBuilder = lookup(ProjectBuilder.class);
        File testPom = pomXmlPath.toFile();
        DefaultProjectBuildingRequest request = new DefaultProjectBuildingRequest();
        MavenSession mavenSession = newMavenSession(new MavenProjectStub());
        request.setRepositorySession(mavenSession.getRepositorySession());
        ProjectBuildingResult projectBuildingResult = projectBuilder.build(testPom, request);
        MavenProject mavenProject = projectBuildingResult.getProject();
        mavenSession.setCurrentProject(mavenProject);

        FlywayMigrateMojo mojo = (FlywayMigrateMojo) lookupConfiguredMojo(mavenProject, "flyway-migrate");

        Flyway flywayConfig = mojo.getFlywayConfig();
        Map<String, String> placeholderMap = flywayConfig.getPlaceholderMap();


        Map<String, String> expectedPlaceholders = new HashMap<>();
        expectedPlaceholders.put("new_first_name_column_name", "firstname");
        expectedPlaceholders.put("new_last_name_column_name", "lastname");

        Assertions.assertEquals(expectedPlaceholders, placeholderMap);

    }
}