package com.link_intersystems.dbunit.maven;

import com.link_intersystems.io.Unzip;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.plugin.testing.stubs.MavenProjectStub;
import org.apache.maven.project.DefaultProjectBuildingRequest;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingResult;
import org.codehaus.plexus.util.IOUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Path;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
class FlywayMigrateMojoTest extends AbstractMojoTestCase {

    private Path tmpDir;
    private Path pomXmlPath;

    @BeforeEach
    protected void setUp(@TempDir Path tmpDir) throws Exception {
        this.tmpDir = tmpDir;
        super.setUp();

        try(InputStream in = getClass().getResourceAsStream("/migrate-project.zip")){
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

        mojo.execute();
    }
}