package com.link_intersystems.dbunit.migration.flyway;

import com.link_intersystems.dbunit.migration.DataSourceProperties;
import com.link_intersystems.dbunit.migration.testcontainers.DataSourcePropertiesAdapter;
import com.link_intersystems.dbunit.testcontainers.JdbcContainer;
import com.link_intersystems.dbunit.testcontainers.JdbcContainerProperties;
import com.link_intersystems.dbunit.testcontainers.JdbcContainerSetup;

import javax.sql.DataSource;
import java.sql.SQLException;

import static java.util.Objects.requireNonNull;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class FlywayJdbcContainerSetup implements JdbcContainerSetup {

    private FlywayMigrationConfig flywayMigrationConfig;

    public FlywayJdbcContainerSetup(FlywayMigrationConfig flywayMigrationConfig) {
        this.flywayMigrationConfig = requireNonNull(flywayMigrationConfig);
    }

    @Override
    public void setup(JdbcContainer jdbcContainer) throws SQLException {
        flywayMigrationConfig.setRemoveFlywayTables(true);
        FlywayDatabaseMigrationSupport databaseMigrationSupport = new FlywayDatabaseMigrationSupport(flywayMigrationConfig) {
            @Override
            public void prepareDataSource(DataSource dataSource, DataSourceProperties dataSourceProperties) throws SQLException {
                super.prepareDataSource(dataSource, dataSourceProperties);
                afterMigrate(dataSource);
            }

        };
        JdbcContainerProperties properties = jdbcContainer.getProperties();
        DataSourceProperties dataSourceProperties = new DataSourcePropertiesAdapter(properties);
        databaseMigrationSupport.prepareDataSource(jdbcContainer.getDataSource(), dataSourceProperties);
    }
}
