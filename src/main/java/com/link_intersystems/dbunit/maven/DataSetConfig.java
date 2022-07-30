package com.link_intersystems.dbunit.maven;

import com.link_intersystems.dbunit.migration.resources.DataSetFileLocations;
import com.link_intersystems.dbunit.migration.resources.DefaultDataSetFileLocations;
import com.link_intersystems.io.FilePath;
import com.link_intersystems.io.FileScanner;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class DataSetConfig {

    private String[] resources;
    private boolean defaultResources;
    private File sourceBasedir;
    private String[] tableOrder;
    private File targetBasedir;
    private MavenProject project;

    public boolean isDefaultResources() {
        return defaultResources;
    }

    public void setDefaultResources(boolean defaultResources) {
        this.defaultResources = defaultResources;
    }

    public String[] getResources() {
        return resources;
    }

    public void setResources(String[] resources) {
        this.resources = resources;
    }

    public String[] getTableOrder() {
        return tableOrder;
    }

    public void setTableOrder(String[] tableOrder) {
        this.tableOrder = tableOrder;
    }

    public void setSourceBasedir(File sourceBasedir) {
        this.sourceBasedir = sourceBasedir;
    }

    public File getSourceBasedir() {
        return sourceBasedir;
    }

    public Path getBasepath() {
        return getSourceBasedir().toPath();
    }

    void setExpressionEvaluator(ExpressionEvaluator expressionEvaluator) throws MojoExecutionException {
        if (targetBasedir == null) {
            try {
                String projectBuildDirectory = (String) expressionEvaluator.evaluate("${project.build.directory}");
                targetBasedir = new File(projectBuildDirectory);
            } catch (ExpressionEvaluationException e) {
                throw new MojoExecutionException("'${project.build.directory}' can not be evaluated.", e);
            }
        }
    }

    void setProject(MavenProject project) throws MojoExecutionException {
        this.project = project;

        if (sourceBasedir == null) {
            List<Resource> testResources = project.getTestResources();
            if (testResources.isEmpty()) {
                throw new MojoExecutionException("dataSets.basedir can not be resolved, because no test resources exist.");
            }

            Resource resource = testResources.get(0);
            String directory = resource.getDirectory();
            sourceBasedir = new File(directory);
        }
    }


    public File getTargetBasedir() {
        return targetBasedir;
    }

    public void setTargetBasedir(File targetBasedir) {
        this.targetBasedir = targetBasedir;
    }

    public Path getTargetBasepath() {
        return getTargetBasedir().toPath();
    }


    public DataSetFileLocations getDataSetFileLocations() {
        Set<Path> uniquePaths = new HashSet<>();
        DefaultDataSetFileLocations fileLocations = new DefaultDataSetFileLocations();

        File sourceBasedir = getSourceBasedir();
        FileScanner fileScanner = new FileScanner(sourceBasedir);
        String[] patternResources = getPatternResources();
        fileScanner.addIncludeFilePattern(patternResources);
        if (isDefaultResources()) {
            fileScanner.addIncludeFilePattern(
                    "**/*.xml",
                    "*.xml",
                    "**/*.xls",
                    "*.xls",
                    "**/table-ordering.txt",
                    "table-ordering.txt"
            );
        }


        List<FilePath> scannedResources = fileScanner.scan();
        for (FilePath scannedResource : scannedResources) {
            if (uniquePaths.add(scannedResource.toAbsolutePath())) {
                fileLocations.add(scannedResource);
            }
        }

        String[] nonPatternResources = getNonPatternResources();
        Path basepath = project.getBasedir().toPath();
        for (String nonPatternResource : nonPatternResources) {
            FilePath filePath = new FilePath(basepath, Paths.get(nonPatternResource));

            if (uniquePaths.add(filePath.toAbsolutePath())) {
                fileLocations.add(filePath);
            }
        }

        return fileLocations;
    }

    public String[] getPatternResources() {
        return Arrays.stream(resources)
                .filter(r -> r.startsWith("glob:"))
                .toArray(String[]::new);
    }

    public String[] getNonPatternResources() {
        return Arrays.stream(resources)
                .filter(r -> !r.startsWith("glob:"))
                .toArray(String[]::new);
    }
}
