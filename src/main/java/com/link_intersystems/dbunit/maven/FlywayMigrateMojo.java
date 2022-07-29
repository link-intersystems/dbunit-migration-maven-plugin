package com.link_intersystems.dbunit.maven;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.List;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
@Mojo(name = "flyway-migrate")
public class FlywayMigrateMojo extends AbstractMojo {

    @Parameter(property = "project", readonly = true, required = true)
    private MavenProject project;

    /**
     * Defaults to the first test resources' directory.
     */
    @Parameter
    private File dataSetBaseDirectory;

    public File getBaseDirectory() {
        if (dataSetBaseDirectory == null) {
            List<Resource> testResources = project.getTestResources();
            if (testResources.isEmpty()) {
                throw new RuntimeException("dataSetBaseDirectory can not be resolved, because no test resources exist.");
            }

            Resource resource = testResources.get(0);
            String directory = resource.getDirectory();
            return new File(directory);
        }

        return dataSetBaseDirectory;
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        File baseDirectory = getBaseDirectory();
    }
}
