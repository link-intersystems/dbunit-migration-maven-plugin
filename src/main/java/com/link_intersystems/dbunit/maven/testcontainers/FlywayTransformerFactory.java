package com.link_intersystems.dbunit.maven.testcontainers;

import com.link_intersystems.dbunit.migration.DatabaseMigrationSupport;
import com.link_intersystems.dbunit.migration.MigrationConfig;
import com.link_intersystems.dbunit.migration.MigrationDataSetTransformerFactory;
import com.link_intersystems.dbunit.migration.testcontainers.GenericContainerConfig;
import com.link_intersystems.dbunit.migration.testcontainers.TestcontainersConfig;
import com.link_intersystems.dbunit.migration.testcontainers.TestcontainersMigrationDataSetTransformerFactory;
import com.link_intersystems.dbunit.stream.consumer.DataSetTransormer;
import com.link_intersystems.dbunit.testcontainers.DBunitJdbcContainer;
import com.link_intersystems.dbunit.testcontainers.DatabaseContainerSupport;
import com.link_intersystems.dbunit.testcontainers.DefaultDatabaseContainerSupport;
import com.link_intersystems.dbunit.testcontainers.commons.CommonsRunningContainerPool;
import com.link_intersystems.dbunit.testcontainers.pool.RunningContainerPool;
import com.link_intersystems.maven.logging.ConcurrentLog;
import com.link_intersystems.maven.logging.ThreadAwareLog;
import com.link_intersystems.maven.logging.slf4j.Slf4JMavenLogAdapter;
import org.apache.maven.plugin.logging.Log;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.slf4j.Logger;

import java.util.Properties;

import static java.util.Objects.requireNonNull;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class FlywayTransformerFactory implements MigrationDataSetTransformerFactory {

    private TestcontainersConfig testcontainersConfig;
    private MigrationConfig migration;
    private Log log;

    public FlywayTransformerFactory(TestcontainersConfig testcontainersConfig, MigrationConfig migration, Log log) {
        this.testcontainersConfig = requireNonNull(testcontainersConfig);
        this.migration = migration;
        this.log = requireNonNull(log);
    }

    @Override
    public DataSetTransormer createTransformer(DatabaseMigrationSupport databaseMigrationSupport) {
        Slf4JMavenLogAdapter mavenLogAdapter = new Slf4JMavenLogAdapter(new ThreadAwareLog(new ConcurrentLog(log)));
        DatabaseContainerSupport containerSupport = getDatabaseContainerSupport(testcontainersConfig, mavenLogAdapter);
        RunningContainerPool containerPool = CommonsRunningContainerPool.createPool(() -> new DBunitJdbcContainer(containerSupport), migration.getConcurrency());
        TestcontainersMigrationDataSetTransformerFactory migrationDataSetTransformerFactory = new TestcontainersMigrationDataSetTransformerFactory(containerPool);

        return migrationDataSetTransformerFactory.createTransformer(databaseMigrationSupport);
    }

    protected DatabaseContainerSupport getDatabaseContainerSupport(TestcontainersConfig testcontainersConfig, Logger logger) {
        GenericContainerConfig containerConfig = testcontainersConfig.getContainerConfig();

        if (containerConfig != null) {
            return getMojoConfiguredDatabaseContainerSupport(testcontainersConfig, logger);
        }

        String image = testcontainersConfig.getImage();
        return DatabaseContainerSupport.getDatabaseContainerSupport(image);
    }

    protected DatabaseContainerSupport getMojoConfiguredDatabaseContainerSupport(TestcontainersConfig testcontainersConfig, Logger logger) {
        GenericJdbcContainerSupplier genericJdbcContainerSupplier = new GenericJdbcContainerSupplier(testcontainersConfig);
        genericJdbcContainerSupplier.setLogger(logger);

        DefaultDatabaseContainerSupport defaultDatabaseContainerSupport = new DefaultDatabaseContainerSupport(genericJdbcContainerSupplier);

        GenericContainerConfig containerConfig = testcontainersConfig.getContainerConfig();
        Properties dbunitConfigProperties = containerConfig.getDbunitConfigProperties();
        if (!dbunitConfigProperties.isEmpty()) {
            DatabaseConfig databaseConfig = defaultDatabaseContainerSupport.getDatabaseConfig();
            applyConfiguration(databaseConfig, dbunitConfigProperties);
        }

        return defaultDatabaseContainerSupport;
    }

    private void applyConfiguration(DatabaseConfig databaseConfig, Properties dbunitConfigProperties) {
        try {
            databaseConfig.setPropertiesByString(dbunitConfigProperties);
        } catch (DatabaseUnitException e) {
            throw new RuntimeException("Can not apply dbunit configuration.", e);
        }
    }
}
