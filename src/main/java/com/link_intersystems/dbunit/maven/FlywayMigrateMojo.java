package com.link_intersystems.dbunit.maven;

import com.link_intersystems.dbunit.flyway.FlywayMigrationConfig;
import com.link_intersystems.dbunit.migration.collection.DataSetCollectionFlywayMigration;
import com.link_intersystems.dbunit.migration.resources.BasepathTargetPathSupplier;
import com.link_intersystems.dbunit.migration.resources.DataSetFileLocationsScanner;
import com.link_intersystems.dbunit.migration.resources.DefaultDataSetResourcesSupplier;
import com.link_intersystems.dbunit.stream.consumer.DataSetConsumerPipeTransformerAdapter;
import com.link_intersystems.dbunit.stream.consumer.ExternalSortTableConsumer;
import com.link_intersystems.dbunit.stream.resource.file.DataSetFileDetection;
import com.link_intersystems.dbunit.table.DefaultTableOrder;
import com.link_intersystems.dbunit.table.TableOrder;
import com.link_intersystems.dbunit.testcontainers.DatabaseContainerSupport;
import com.link_intersystems.dbunit.testcontainers.DatabaseContainerSupportFactory;
import com.link_intersystems.io.FilePath;
import com.link_intersystems.io.FileScanner;
import org.apache.maven.model.Build;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
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

    @Parameter
    private File[] flywayLocations;

    @Parameter(defaultValue = "${project.build.directory}")
    private File targetFolder;

    @Parameter
    private FlywayConfig flyway = new FlywayConfig();

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

        DataSetCollectionFlywayMigration flywayMigration = new DataSetCollectionFlywayMigration();

        DataSetFileLocationsScanner fileLocations = new DataSetFileLocationsScanner(baseDirectory.toPath());

        DefaultDataSetResourcesSupplier dataSetResourcesSupplier = new DefaultDataSetResourcesSupplier(fileLocations, new DataSetFileDetection());
        flywayMigration.setDataSetResourcesSupplier(dataSetResourcesSupplier);

        DatabaseContainerSupport containerSupport = DatabaseContainerSupportFactory.INSTANCE.createPostgres("postgres:latest");
        flywayMigration.setDatabaseContainerSupport(containerSupport);


        Path targetPath = targetFolder.toPath();

        BasepathTargetPathSupplier basepathTargetPathSupplier = new BasepathTargetPathSupplier(baseDirectory.toPath(), targetPath);
        flywayMigration.setTargetDataSetFileSupplier(basepathTargetPathSupplier);

        FlywayMigrationConfig migrationConfig = createFlywayMigrationConfig();
        flywayMigration.setMigrationConfig(migrationConfig);
        flywayMigration.setMigrationListener(new MavenLogAdapter(getLog()));

        TableOrder tableOrder = new DefaultTableOrder("language", "film", "actor", "film_actor");
        ExternalSortTableConsumer externalSortTableConsumer = new ExternalSortTableConsumer(tableOrder);
        flywayMigration.setBeforeMigration(new DataSetConsumerPipeTransformerAdapter(externalSortTableConsumer));

        flywayMigration.exec();

    }

    FlywayConfig getFlywayConfig() {
        return flyway;
    }

    private FlywayMigrationConfig createFlywayMigrationConfig() {
        FlywayConfig flywayConfig = getFlywayConfig();
        return flywayConfig.createFlywayMigrationConfig(this::guessFlywayLocations);

    }

    private String[] guessFlywayLocations() {

        List<FilePath> filePaths = new ArrayList<>();
        Build build = project.getBuild();
        List<Resource> resources = build.getResources();
        for (Resource resource : resources) {
            String directory = resource.getDirectory();
            FileScanner fileScanner = new FileScanner(new File(directory));
            fileScanner.addIncludeDirectoryPatterns("**/db/migration", "db/migration");
            filePaths.addAll(fileScanner.scan());
        }

        return filePaths.stream().map(FilePath::toAbsolutePath).map(Path::toString).map(s -> "filesystem:".concat(s)).toArray(String[]::new);
    }


}
