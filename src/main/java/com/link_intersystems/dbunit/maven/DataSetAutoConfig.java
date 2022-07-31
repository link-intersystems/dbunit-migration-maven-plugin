package com.link_intersystems.dbunit.maven;

import com.link_intersystems.dbunit.migration.datasets.DataSetsConfig;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;

import java.io.File;
import java.util.List;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class DataSetAutoConfig {

    private final MavenProject project;
    private final ExpressionEvaluator expressionEvaluator;

    public DataSetAutoConfig(MavenProject project, ExpressionEvaluator expressionEvaluator) {
        this.project = project;
        this.expressionEvaluator = expressionEvaluator;
    }

    public void configure(DataSetsConfig dataSets) throws MojoExecutionException {
        File targetBasedir = dataSets.getTargetBasedir();
        if (targetBasedir == null) {
            try {
                String projectBuildDirectory = (String) expressionEvaluator.evaluate("${project.build.directory}");
                targetBasedir = new File(projectBuildDirectory);
            } catch (ExpressionEvaluationException e) {
                throw new MojoExecutionException("'${project.build.directory}' can not be evaluated.", e);
            }
        }
        dataSets.setTargetBasedir(targetBasedir);


        File sourceBasedir = dataSets.getSourceBasedir();
        if (sourceBasedir == null) {
            List<Resource> testResources = project.getTestResources();
            if (testResources.isEmpty()) {
                throw new MojoExecutionException("dataSets.basedir can not be resolved, because no test resources exist.");
            }

            Resource resource = testResources.get(0);
            String directory = resource.getDirectory();
            sourceBasedir = new File(directory);
        }
        dataSets.setSourceBasedir(sourceBasedir);
    }

}
