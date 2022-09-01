package com.link_intersystems.dbunit.maven.mojo.testcontainers;

import com.link_intersystems.dbunit.migration.DatabaseMigrationSupport;
import com.link_intersystems.dbunit.migration.MigrationConfig;
import com.link_intersystems.dbunit.migration.MigrationDataSetPipeFactory;
import com.link_intersystems.dbunit.migration.testcontainers.GenericContainerConfig;
import com.link_intersystems.dbunit.migration.testcontainers.TestcontainersConfig;
import com.link_intersystems.dbunit.migration.testcontainers.TestcontainersMigrationDataSetPipeFactory;
import com.link_intersystems.dbunit.stream.consumer.DataSetConsumerPipe;
import com.link_intersystems.dbunit.testcontainers.DBunitJdbcContainer;
import com.link_intersystems.dbunit.testcontainers.DatabaseContainerSupport;
import com.link_intersystems.dbunit.testcontainers.DefaultDatabaseContainerSupport;
import com.link_intersystems.dbunit.testcontainers.commons.CommonsJdbcContainerPool;
import com.link_intersystems.dbunit.testcontainers.pool.JdbcContainerPool;
import com.link_intersystems.maven.logging.ConcurrentLog;
import com.link_intersystems.maven.logging.FilterLog;
import com.link_intersystems.maven.logging.Level;
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
public class FlywayTransformerFactory implements MigrationDataSetPipeFactory {

    private TestcontainersConfig testcontainersConfig;
    private MigrationConfig migration;
    private Log log;

    public FlywayTransformerFactory(TestcontainersConfig testcontainersConfig, MigrationConfig migration, Log log) {
        this.testcontainersConfig = requireNonNull(testcontainersConfig);
        this.migration = migration;
        this.log = requireNonNull(log);
    }

    @Override
    public DataSetConsumerPipe createMigrationPipe(DatabaseMigrationSupport databaseMigrationSupport) {
        JdbcContainerPool containerPool = createContainerPool();
        TestcontainersMigrationDataSetPipeFactory pipeFactory = new TestcontainersMigrationDataSetPipeFactory(containerPool);

        return pipeFactory.createMigrationPipe(databaseMigrationSupport);
    }

    public JdbcContainerPool createContainerPool() {
        FilterLog filteredLog = new FilterLog(new ThreadAwareLog(new ConcurrentLog(log)));

        Level logLevel = testcontainersConfig.getLogLevel();
        filteredLog.setLevel(logLevel);

        DatabaseContainerSupport containerSupport = getDatabaseContainerSupport(testcontainersConfig, new Slf4JMavenLogAdapter(filteredLog));
        CommonsJdbcContainerPool containerPool = CommonsJdbcContainerPool.createPool(() -> new DBunitJdbcContainer(containerSupport), migration.getConcurrency());
        return containerPool;
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
