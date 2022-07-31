package com.link_intersystems.dbunit.maven;

import com.link_intersystems.dbunit.migration.datasets.DataSetsConfig;
import com.link_intersystems.dbunit.migration.resources.DataSetFileLocations;
import com.link_intersystems.dbunit.migration.resources.DefaultDataSetFileLocations;
import com.link_intersystems.io.FileScanner;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class DataSetsConfigFileLocations implements DataSetFileLocations {

    private MavenProject project;
    private DataSetsConfig dataSetsConfig;

    public DataSetsConfigFileLocations(MavenProject project, DataSetsConfig dataSetsConfig) {
        this.project = project;
        this.dataSetsConfig = dataSetsConfig;
    }

    @Override
    public List<File> getFiles() {
        Set<String> uniqueFiles = new HashSet<>();
        DefaultDataSetFileLocations fileLocations = new DefaultDataSetFileLocations();


        FileScanner fileScanner = new FileScanner();
        String[] patternResources = dataSetsConfig.getPatternResources();
        fileScanner.addIncludeFilePattern(patternResources);
        if (dataSetsConfig.isDefaultResources()) {
            fileScanner.addIncludeFilePattern(
                    "**/*.xml",
                    "*.xml",
                    "**/*.xls",
                    "*.xls",
                    "**/table-ordering.txt",
                    "table-ordering.txt"
            );
        }

        File sourceBasedir = dataSetsConfig.getSourceBasedir();
        List<File> scannedResources = fileScanner.scan(sourceBasedir);
        for (File scannedResource : scannedResources) {
            if (uniqueFiles.add(scannedResource.getAbsolutePath())) {
                fileLocations.add(scannedResource);
            }
        }

        String[] nonPatternResources = dataSetsConfig.getNonPatternResources();
        File basedir = project.getBasedir();
        for (String nonPatternResource : nonPatternResources) {
            File nonPatternFile = new File(basedir, nonPatternResource);

            if (uniqueFiles.add(nonPatternFile.getAbsolutePath())) {
                fileLocations.add(nonPatternFile);
            }
        }

        return fileLocations.getFiles();
    }
}
