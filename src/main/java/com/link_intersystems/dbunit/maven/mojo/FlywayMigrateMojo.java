package com.link_intersystems.dbunit.maven.mojo;

import com.link_intersystems.dbunit.maven.autoconfig.DataSetAutoConfig;
import com.link_intersystems.dbunit.maven.autoconfig.DataSetsConfigFileLocations;
import com.link_intersystems.dbunit.maven.autoconfig.FlywayAutoConfig;
import com.link_intersystems.dbunit.maven.testcontainers.FlywayTestcontainersMigrationDataSetPipeFactory;
import com.link_intersystems.dbunit.migration.MigrationConfig;
import com.link_intersystems.dbunit.migration.datasets.DataSetsConfig;
import com.link_intersystems.dbunit.migration.datasets.DataSetsMigrationParticipant;
import com.link_intersystems.dbunit.migration.flyway.FlywayConfig;
import com.link_intersystems.dbunit.migration.flyway.FlywayMigrationParticipant;
import com.link_intersystems.dbunit.migration.resources.DataSetResourcesMigration;
import com.link_intersystems.dbunit.migration.testcontainers.TestcontainersConfig;
import com.link_intersystems.dbunit.stream.resource.DataSetResource;
import com.link_intersystems.dbunit.stream.resource.detection.DataSetFileDetection;
import com.link_intersystems.dbunit.stream.resource.detection.DetectingDataSetFileResourcesSupplier;
import com.link_intersystems.dbunit.stream.resource.file.DataSetFileLocations;
import com.link_intersystems.maven.logging.ConcurrentLog;
import com.link_intersystems.maven.logging.ThreadAwareLog;
import com.link_intersystems.maven.logging.slf4j.Slf4JMavenLogAdapter;
import com.link_intersystems.util.config.properties.ConfigProperties;
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
import org.slf4j.Logger;

import java.util.List;
import java.util.concurrent.Executors;

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

    @Parameter
    protected MigrationConfig migration = new MigrationConfig();

    @Override
    public void execute() throws MojoExecutionException {
        autoConfigure();
        validateConfigurations();

        executeMigration();
    }

    protected void autoConfigure() throws MojoExecutionException {
        FlywayAutoConfig flywayAutoConfig = new FlywayAutoConfig(project);
        flywayAutoConfig.configure(flyway);

        ExpressionEvaluator expressionEvaluator = new PluginParameterExpressionEvaluator(mavenSession, mojoExecution);
        DataSetAutoConfig dataSetAutoConfig = new DataSetAutoConfig(project, expressionEvaluator);
        dataSetAutoConfig.configure(dataSets);
    }

    protected void validateConfigurations() throws MojoExecutionException {
        migration.validate();
    }

    protected void executeMigration() {
        DataSetResourcesMigration dataSetResourcesMigration = new DataSetResourcesMigration();

        Logger logger = getSlf4JLogger();
        dataSetResourcesMigration.setLogger(logger);
        dataSetResourcesMigration.setExecutorService(Executors.newFixedThreadPool(migration.getConcurrency()));

        FlywayMigrationParticipant flywayMigrationParticipant = new FlywayMigrationParticipant(flyway, getFlywayTransformerFactory());
        flywayMigrationParticipant.configure(dataSetResourcesMigration);

        DataSetsMigrationParticipant dataSetsMigrationParticipant = new DataSetsMigrationParticipant(dataSets);
        dataSetsMigrationParticipant.configure(dataSetResourcesMigration);
        dataSetsMigrationParticipant.setLogger(logger);

        ConfigProperties config = new ConfigProperties();
        flywayMigrationParticipant.applyConfigProperties(config);
        dataSetsMigrationParticipant.applyConfigProperties(config);

        List<DataSetResource> dataSetResources = getDataSetResources(config);

        dataSetResourcesMigration.exec(dataSetResources);
    }


    /*
     * TODO Would be better to use the Slf4J mechanism (LoggerFactoryBinder) to adapt the maven log.
     */
    protected Logger getSlf4JLogger() {
        return new Slf4JMavenLogAdapter(new ThreadAwareLog(new ConcurrentLog(getLog())));
    }

    public FlywayTestcontainersMigrationDataSetPipeFactory getFlywayTransformerFactory() {
        Log mavenLog = getLog();
        return new FlywayTestcontainersMigrationDataSetPipeFactory(testcontainers, migration, mavenLog);
    }

    protected List<DataSetResource> getDataSetResources(ConfigProperties config) {
        DataSetFileDetection fileDetection = new DataSetFileDetection();
        fileDetection.setConfigProperties(config);

        DataSetFileLocations dataSetFileLocations = new DataSetsConfigFileLocations(project, dataSets);
        DetectingDataSetFileResourcesSupplier dataSetResourcesSupplier = new DetectingDataSetFileResourcesSupplier(dataSetFileLocations, fileDetection);

        return dataSetResourcesSupplier.getDataSetResources();
    }
}
