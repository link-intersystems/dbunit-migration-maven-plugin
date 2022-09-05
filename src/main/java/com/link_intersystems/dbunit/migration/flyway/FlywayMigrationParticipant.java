package com.link_intersystems.dbunit.migration.flyway;

import com.link_intersystems.dbunit.maven.testcontainers.FlywayTestcontainersMigrationDataSetPipeFactory;
import com.link_intersystems.dbunit.migration.resources.DataSetResourcesMigration;
import com.link_intersystems.dbunit.stream.producer.db.DatabaseDataSetProducerConfig;
import com.link_intersystems.dbunit.stream.resources.detection.file.sql.SqlDataSetFileConfig;
import com.link_intersystems.dbunit.testcontainers.JdbcContainer;
import com.link_intersystems.dbunit.testcontainers.consumer.DatabaseCustomizationConsumer;
import com.link_intersystems.util.config.properties.ConfigProperties;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class FlywayMigrationParticipant {


    private FlywayConfig flywayConfig;
    private FlywayTestcontainersMigrationDataSetPipeFactory flywayTransformerFactory;

    public FlywayMigrationParticipant(FlywayConfig flywayConfig, FlywayTestcontainersMigrationDataSetPipeFactory flywayTransformerFactory) {
        this.flywayConfig = flywayConfig;
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
        DatabaseCustomizationConsumer databaseCustomizationConsumer = new DatabaseCustomizationConsumer() {
            @Override
            protected void beforeStartDataSet(JdbcContainer jdbcContainer) throws SQLException {
                FlywayMigrationConfig flywayMigrationConfig = getFlywayMigrationConfig();
                flywayMigrationConfig.setRemoveFlywayTables(true);
                FlywayDatabaseMigrationSupport databaseMigrationSupport = new FlywayDatabaseMigrationSupport(flywayMigrationConfig) {
                    @Override
                    public void prepareDataSource(DataSource dataSource) throws SQLException {
                        super.prepareDataSource(dataSource);
                        afterMigrate(dataSource);
                    }
                };
                databaseMigrationSupport.prepareDataSource(jdbcContainer.getDataSource());
            }
        };
        config.setProperty(SqlDataSetFileConfig.DATABASE_CUSTOMIZATION_CONSUMER, databaseCustomizationConsumer);
        config.setProperty(SqlDataSetFileConfig.JDBC_CONTAINER_POOL, flywayTransformerFactory.createContainerPool());

        DatabaseDataSetProducerConfig databaseDataSetProducerConfig = new DatabaseDataSetProducerConfig();
        config.setProperty(SqlDataSetFileConfig.DATABASE_DATA_SET_PRODUCER_CONFIG, databaseDataSetProducerConfig);
    }
}
