package com.link_intersystems.dbunit.maven;

import com.link_intersystems.dbunit.flyway.FlywayMigrationConfig;
import com.link_intersystems.dbunit.maven.slf4j.Slf4JMavenLogAdapter;
import com.link_intersystems.dbunit.migration.collection.DataSetCollectionFlywayMigration;
import com.link_intersystems.dbunit.migration.datasets.DataSetsConfig;
import com.link_intersystems.dbunit.migration.flyway.FlywayConfig;
import com.link_intersystems.dbunit.migration.resources.BasepathTargetPathSupplier;
import com.link_intersystems.dbunit.migration.resources.DataSetFileLocations;
import com.link_intersystems.dbunit.migration.resources.DefaultDataSetResourcesSupplier;
import com.link_intersystems.dbunit.migration.resources.TargetDataSetResourceSupplier;
import com.link_intersystems.dbunit.migration.testcontainers.TestcontainersConfig;
import com.link_intersystems.dbunit.stream.consumer.DataSetConsumerPipeTransformerAdapter;
import com.link_intersystems.dbunit.stream.consumer.DataSetTransormer;
import com.link_intersystems.dbunit.stream.consumer.ExternalSortTableConsumer;
import com.link_intersystems.dbunit.stream.resource.file.DataSetFileConfig;
import com.link_intersystems.dbunit.stream.resource.file.DataSetFileDetection;
import com.link_intersystems.dbunit.table.DefaultTableOrder;
import com.link_intersystems.dbunit.table.TableOrder;
import com.link_intersystems.dbunit.testcontainers.DatabaseContainerSupport;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.PluginParameterExpressionEvaluator;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;

import java.nio.file.Path;

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
        FlywayAutoConfig flywayAutoConfig = new FlywayAutoConfig(project);
        flywayAutoConfig.configure(flyway);

        ExpressionEvaluator expressionEvaluator = new PluginParameterExpressionEvaluator(mavenSession, mojoExecution);
        DataSetAutoConfig dataSetAutoConfig = new DataSetAutoConfig(project, expressionEvaluator);
        dataSetAutoConfig.configure(dataSets);


        executeMigration();
    }

    protected void executeMigration() {
        DataSetCollectionFlywayMigration flywayMigration = new DataSetCollectionFlywayMigration();

        DataSetFileLocations dataSetFileLocations = new DataSetsConfigFileLocations(project, dataSets);

        DataSetFileDetection fileDetection = new DataSetFileDetection();
        DataSetFileConfig config = new DataSetFileConfig();
        fileDetection.setDataSetFileConfig(config);
        DefaultDataSetResourcesSupplier dataSetResourcesSupplier = new DefaultDataSetResourcesSupplier(dataSetFileLocations, fileDetection);
        flywayMigration.setDataSetResourcesSupplier(dataSetResourcesSupplier);

        DatabaseContainerSupport containerSupport = testcontainers.getDatabaseContainerSupport(new Slf4JMavenLogAdapter(getLog()));
        flywayMigration.setDatabaseContainerSupport(containerSupport);


        flywayMigration.setTargetDataSetResourceSupplier(getTargetDataSetResourceSupplier(dataSets));
        flywayMigration.setMigrationConfig(getFlywayMigrationConfig());
        flywayMigration.setMigrationListener(getMigrationListener());
        flywayMigration.setBeforeMigration(getBeforeMigrationTransformer());


        flywayMigration.exec();
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

    protected MavenLogAdapter getMigrationListener() {
        return new MavenLogAdapter(getLog());
    }

    protected TargetDataSetResourceSupplier getTargetDataSetResourceSupplier(DataSetsConfig dataSets) {
        Path basepath = dataSets.getBasepath();
        Path targetPath = dataSets.getTargetBasepath();
        return new BasepathTargetPathSupplier(basepath, targetPath);
    }

    protected FlywayMigrationConfig getFlywayMigrationConfig() {
        return flyway.getFlywayMigrationConfig();

    }


}
