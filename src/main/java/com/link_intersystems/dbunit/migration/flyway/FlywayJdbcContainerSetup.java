package com.link_intersystems.dbunit.migration.flyway;

import com.link_intersystems.dbunit.testcontainers.JdbcContainer;
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
            public void prepareDataSource(DataSource dataSource) throws SQLException {
                super.prepareDataSource(dataSource);
                afterMigrate(dataSource);
            }
        };
        databaseMigrationSupport.prepareDataSource(jdbcContainer.getDataSource());
    }
}
