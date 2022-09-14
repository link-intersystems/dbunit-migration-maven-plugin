package com.link_intersystems.dbunit.migration.flyway;

import com.link_intersystems.dbunit.maven.testcontainers.FlywayTestcontainersMigrationDataSetPipeFactory;
import com.link_intersystems.dbunit.migration.datasets.DataSetsConfig;
import com.link_intersystems.dbunit.migration.resources.DataSetResourcesMigration;
import com.link_intersystems.dbunit.stream.consumer.sql.TableLiteralFormatResolver;
import com.link_intersystems.dbunit.stream.producer.db.DatabaseDataSetProducerConfig;
import com.link_intersystems.dbunit.stream.resource.sql.SqlDataSetFileConfig;
import com.link_intersystems.dbunit.testcontainers.JdbcContainerSetup;
import com.link_intersystems.dbunit.testcontainers.TestcontainersDatabaseConnectionBorrower;
import com.link_intersystems.dbunit.testcontainers.pool.JdbcContainerPool;
import com.link_intersystems.util.config.properties.ConfigProperties;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class FlywayMigrationParticipant {


    private FlywayConfig flywayConfig;
    private DataSetsConfig dataSetsConfig;
    private FlywayTestcontainersMigrationDataSetPipeFactory flywayTransformerFactory;

    public FlywayMigrationParticipant(FlywayConfig flywayConfig, DataSetsConfig dataSetsConfig, FlywayTestcontainersMigrationDataSetPipeFactory flywayTransformerFactory) {
        this.flywayConfig = flywayConfig;
        this.dataSetsConfig = dataSetsConfig;
        this.flywayTransformerFactory = flywayTransformerFactory;
    }

    FlywayMigrationConfig getFlywayMigrationConfig() {
        FlywayMigrationConfigFactory flywayMigrationConfigFactory = new FlywayMigrationConfigFactory();
        return flywayMigrationConfigFactory.create(flywayConfig);
    }

    FlywayDatabaseMigrationSupport getFlywayDatabaseMigrationSupport() {
        FlywayMigrationConfig flywayMigrationConfig = getFlywayMigrationConfig();
        return new FlywayDatabaseMigrationSupport(flywayMigrationConfig);
    }

    public void configure(DataSetResourcesMigration dataSetResourcesMigration) {
        configureMigrationTransformer(dataSetResourcesMigration);
        configureMigrationSupport(dataSetResourcesMigration);
    }

    protected void configureMigrationTransformer(DataSetResourcesMigration dataSetsMigrations) {
        dataSetsMigrations.setMigrationDataSetTransformerFactory(flywayTransformerFactory);
    }

    protected void configureMigrationSupport(DataSetResourcesMigration dataSetsMigrations) {
        FlywayDatabaseMigrationSupport databaseMigrationSupport = getFlywayDatabaseMigrationSupport();
        dataSetsMigrations.setDatabaseMigrationSupport(databaseMigrationSupport);
    }

    public void applyConfigProperties(ConfigProperties config) {
        JdbcContainerPool containerPool = flywayTransformerFactory.createContainerPool();
        JdbcContainerSetup jdbcContainerSetup = new FlywayJdbcContainerSetup(getFlywayMigrationConfig());
        config.setProperty(SqlDataSetFileConfig.DATABASE_CONNECTION_BORROWER, new TestcontainersDatabaseConnectionBorrower(containerPool, jdbcContainerSetup));

        DatabaseDataSetProducerConfig databaseDataSetProducerConfig = new DatabaseDataSetProducerConfig();
        config.setProperty(SqlDataSetFileConfig.DATABASE_DATA_SET_PRODUCER_CONFIG, databaseDataSetProducerConfig);

        TableLiteralFormatResolver tableLiteralFormatResolver = dataSetsConfig.getSql().getTableLiteralFormatResolver();
        config.setProperty(SqlDataSetFileConfig.TABLE_LITERAL_FORMAT_RESOLVER, tableLiteralFormatResolver);
    }
}
