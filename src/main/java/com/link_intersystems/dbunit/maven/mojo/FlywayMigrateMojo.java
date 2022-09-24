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

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Migrates a collection of DBUnit data set files from one database schema version to another with the use of Flyway and testcontainers.
 *
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
@Mojo(name = "flyway-migrate")
public class FlywayMigrateMojo extends AbstractMojo {

    @Parameter(property = "project", readonly = true, required = true)
    protected MavenProject project;

    @Parameter(property = "mojoExecution", readonly = true, required = true)
    protected MojoExecution mojoExecution;

    @Parameter(defaultValue = "${session}", required = true, readonly = true)
    protected MavenSession mavenSession;

    /**
     * Flyway configuration.
     * <p>
     * see <a href="https://link-intersystems.github.io/dbunit-migration-maven-plugin/config-options.html#flyway-options">Flyway Options</a>
     * </p>
     */
    @Parameter
    protected FlywayConfig flyway = new FlywayConfig();

    /**
     * DataSet selection configuration.
     * <p>
     * see <a href="https://link-intersystems.github.io/dbunit-migration-maven-plugin/config-options.html#dataset-options">Dataset Options</a>
     * </p>
     */
    @Parameter
    protected DataSetsConfig dataSets = new DataSetsConfig();

    /**
     * Testcontainers database configuration.
     *
     * <p>
     * see <a href="https://link-intersystems.github.io/dbunit-migration-maven-plugin/config-options.html#testcontainers-options">Testcontainers Container Options</a>
     * </p>
     */

    @Parameter
    protected TestcontainersConfig testcontainers = new TestcontainersConfig();

    /**
     * Migration process related configuration.
     *
     * <p>
     * see <a href="https://link-intersystems.github.io/dbunit-migration-maven-plugin/config-options.html#flyway-options">Flyway Option</a>
     * </p>
     */
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

        FlywayMigrationParticipant flywayMigrationParticipant = new FlywayMigrationParticipant(flyway, dataSets, getFlywayTransformerFactory());
        flywayMigrationParticipant.configure(dataSetResourcesMigration);

        DataSetsMigrationParticipant dataSetsMigrationParticipant = new DataSetsMigrationParticipant(migration, dataSets);
        dataSetsMigrationParticipant.configure(dataSetResourcesMigration);
        dataSetsMigrationParticipant.setLogger(logger);

        ConfigProperties config = new ConfigProperties();
        flywayMigrationParticipant.applyConfigProperties(project, config);
        dataSetsMigrationParticipant.applyConfigProperties(config);

        List<DataSetResource> dataSetResources = getDataSetResources(config);

        logDataSetResources(logger, dataSetResources);

        dataSetResourcesMigration.exec(dataSetResources);
    }

    private void logDataSetResources(Logger logger, List<DataSetResource> dataSetResources) {
        StringBuilder sb = new StringBuilder();
        sb.append("Found ");
        sb.append(dataSetResources.size());
        sb.append(" data set resources to migrate:\n");
        Iterator<DataSetResource> iterator = dataSetResources.iterator();
        while (iterator.hasNext()) {
            DataSetResource dataSetResource = iterator.next();
            sb.append("\t\u2022 ");
            sb.append(dataSetResource);
            if (iterator.hasNext()) {
                sb.append("\n");
            }
        }

        logger.info(sb.toString());
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
