package com.link_intersystems.dbunit.maven.mojo;

import com.link_intersystems.dbunit.maven.autoconfig.DataSetAutoConfig;
import com.link_intersystems.dbunit.maven.autoconfig.DataSetsConfigFileLocations;
import com.link_intersystems.dbunit.maven.autoconfig.FlywayAutoConfig;
import com.link_intersystems.dbunit.migration.datasets.DataSetsConfig;
import com.link_intersystems.dbunit.migration.flyway.FlywayConfig;
import com.link_intersystems.dbunit.migration.flyway.FlywayDatabaseMigrationSupport;
import com.link_intersystems.dbunit.migration.flyway.FlywayMigrationConfig;
import com.link_intersystems.dbunit.migration.flyway.FlywayMigrationConfigFactory;
import com.link_intersystems.dbunit.migration.resources.DataSetResourcesMigration;
import com.link_intersystems.dbunit.migration.resources.RebaseTargetpathDataSetResourceSupplier;
import com.link_intersystems.dbunit.migration.resources.TargetDataSetResourceSupplier;
import com.link_intersystems.dbunit.maven.testcontainers.FlywayTransformerFactory;
import com.link_intersystems.dbunit.migration.testcontainers.TestcontainersConfig;
import com.link_intersystems.dbunit.stream.consumer.DataSetConsumerPipeTransformerAdapter;
import com.link_intersystems.dbunit.stream.consumer.DataSetTransormer;
import com.link_intersystems.dbunit.stream.consumer.ExternalSortTableConsumer;
import com.link_intersystems.dbunit.stream.resource.DataSetResource;
import com.link_intersystems.dbunit.stream.resource.detection.DataSetFileDetection;
import com.link_intersystems.dbunit.stream.resource.detection.DetectingDataSetFileResourcesSupplier;
import com.link_intersystems.dbunit.stream.resource.file.DataSetFileConfig;
import com.link_intersystems.dbunit.stream.resource.file.DataSetFileLocations;
import com.link_intersystems.dbunit.table.DefaultTableOrder;
import com.link_intersystems.dbunit.table.TableOrder;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.PluginParameterExpressionEvaluator;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;

import java.nio.file.Path;
import java.util.List;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
@Mojo(name = "flyway-migrate")
public class FlywayMigrateMojo extends AbstractMojo {

    @Parameter(property = "project", readonly = true, required = true)
    protected MavenProject project;

    @Parameter(property = "mojoExecution", readonly = true, required = true)
    protected MojoExecution mojoExecution;

    @Parameter(defaultValue = "${session}")
    protected MavenSession mavenSession;

    @Parameter
    protected FlywayConfig flyway = new FlywayConfig();

    @Parameter
    protected DataSetsConfig dataSets = new DataSetsConfig();

    @Parameter
    protected TestcontainersConfig testcontainers = new TestcontainersConfig();

    @Override
    public void execute() throws MojoExecutionException {
        autoConfigure();

        executeMigration();
    }

    protected void autoConfigure() throws MojoExecutionException {
        FlywayAutoConfig flywayAutoConfig = new FlywayAutoConfig(project);
        flywayAutoConfig.configure(flyway);

        ExpressionEvaluator expressionEvaluator = new PluginParameterExpressionEvaluator(mavenSession, mojoExecution);
        DataSetAutoConfig dataSetAutoConfig = new DataSetAutoConfig(project, expressionEvaluator);
        dataSetAutoConfig.configure(dataSets);
    }

    protected void executeMigration() {
        DataSetResourcesMigration dataSetsMigrations = new DataSetResourcesMigration();

        configureMigrationTransformer(dataSetsMigrations);

        configureMigrationSupport(dataSetsMigrations);

        TargetDataSetResourceSupplier targetDataSetResourceSupplier = getTargetDataSetResourceSupplier(dataSets);
        dataSetsMigrations.setTargetDataSetResourceSupplier(targetDataSetResourceSupplier);
        dataSetsMigrations.setBeforeMigration(getBeforeMigrationTransformer());
        dataSetsMigrations.setMigrationListener(getMigrationListener());

        List<DataSetResource> dataSetResources = getDataSetResources();
        dataSetsMigrations.exec(dataSetResources);
    }


    protected void configureMigrationTransformer(DataSetResourcesMigration dataSetsMigrations) {
        Log mavenLog = getLog();
        FlywayTransformerFactory migrationDataSetTransformerFactory = new FlywayTransformerFactory(testcontainers, mavenLog);
        dataSetsMigrations.setMigrationDataSetTransformerFactory(migrationDataSetTransformerFactory);
    }

    protected void configureMigrationSupport(DataSetResourcesMigration dataSetsMigrations) {
        FlywayMigrationConfig flywayMigrationConfig = getFlywayMigrationConfig();
        FlywayDatabaseMigrationSupport databaseMigrationSupport = new FlywayDatabaseMigrationSupport(flywayMigrationConfig);
        dataSetsMigrations.setDatabaseMigrationSupport(databaseMigrationSupport);
    }

    protected FlywayMigrationConfig getFlywayMigrationConfig() {
        FlywayMigrationConfigFactory flywayMigrationConfigFactory = new FlywayMigrationConfigFactory();
        return flywayMigrationConfigFactory.create(flyway);

    }

    protected TargetDataSetResourceSupplier getTargetDataSetResourceSupplier(DataSetsConfig dataSets) {
        Path basepath = dataSets.getBasepath();
        Path targetPath = dataSets.getTargetBasepath();
        return new RebaseTargetpathDataSetResourceSupplier(basepath, targetPath);
    }

    protected DataSetTransormer getBeforeMigrationTransformer() {
        String[] tableOrderConfig = dataSets.getTableOrder();
        if (tableOrderConfig != null) {
            TableOrder tableOrder = new DefaultTableOrder(tableOrderConfig);
            ExternalSortTableConsumer externalSortTableConsumer = new ExternalSortTableConsumer(tableOrder);
            return new DataSetConsumerPipeTransformerAdapter(externalSortTableConsumer);
        }
        return null;
    }

    protected MavenLogMigrationListener getMigrationListener() {
        return new MavenLogMigrationListener(getLog());
    }

    protected List<DataSetResource> getDataSetResources() {
        DataSetFileConfig config = new DataSetFileConfig();
        config.setCharset(dataSets.getCharset());
        config.setColumnSensing(dataSets.isColumnSensing());

        DataSetFileDetection fileDetection = new DataSetFileDetection();
        fileDetection.setDataSetFileConfig(config);

        DataSetFileLocations dataSetFileLocations = new DataSetsConfigFileLocations(project, dataSets);
        DetectingDataSetFileResourcesSupplier dataSetResourcesSupplier = new DetectingDataSetFileResourcesSupplier(dataSetFileLocations, fileDetection);

        return dataSetResourcesSupplier.getDataSetResources();
    }

}
