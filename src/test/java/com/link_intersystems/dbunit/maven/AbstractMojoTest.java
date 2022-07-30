package com.link_intersystems.dbunit.maven;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.plugin.testing.stubs.MavenProjectStub;
import org.apache.maven.project.DefaultProjectBuildingRequest;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingResult;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public abstract class AbstractMojoTest {

    private static class MojoTestCaseAdapter extends AbstractMojoTestCase {
        @Override
        public void setUp() throws Exception {
            super.setUp();
        }

        public <T> T doLookup(Class<T> role) throws ComponentLookupException {
            return lookup(role);
        }

        @Override
        public MavenSession newMavenSession(MavenProject project) {
            return super.newMavenSession(project);
        }

        @Override
        public Mojo lookupConfiguredMojo(MavenProject project, String goal) throws Exception {
            return super.lookupConfiguredMojo(project, goal);
        }
    }

    private MojoTestCaseAdapter testCaseAdapter = new MojoTestCaseAdapter();

    private TestMavenProject testMavenProject;
    private MavenSession mavenSession;
    private MavenProject mavenProject;

    @BeforeEach
    protected void setUp(@TempDir Path tmpDir) throws Exception {
        testCaseAdapter.setUp();

        testMavenProject = createTestMavenProject(tmpDir);
        testMavenProject.create();

        Path pomPath = testMavenProject.getPomPath();
        File testPom = pomPath.toFile();

        ProjectBuilder projectBuilder = testCaseAdapter.doLookup(ProjectBuilder.class);
        DefaultProjectBuildingRequest request = new DefaultProjectBuildingRequest();
        mavenSession = testCaseAdapter.newMavenSession(new MavenProjectStub());
        request.setRepositorySession(mavenSession.getRepositorySession());
        ProjectBuildingResult projectBuildingResult = projectBuilder.build(testPom, request);
        mavenProject = projectBuildingResult.getProject();
        mavenSession.setCurrentProject(mavenProject);
    }

    public MavenProject getMavenProject() {
        return mavenProject;
    }

    public MavenSession getMavenSession() {
        return mavenSession;
    }

    public TestMavenProject getTestMavenProject() {
        return testMavenProject;
    }

    protected abstract TestMavenProject createTestMavenProject(Path basepath);

    @SuppressWarnings("unchecked")
    protected <T extends Mojo> T lookupConfiguredMojo(String goal) throws Exception {
        MavenProject mavenProject = getMavenProject();
        Mojo mojo = testCaseAdapter.lookupConfiguredMojo(mavenProject, goal);
        mojo.setLog(new SystemStreamLog() {
            @Override
            public boolean isDebugEnabled() {
                return true;
            }
        });
        return (T) mojo;
    }
}
