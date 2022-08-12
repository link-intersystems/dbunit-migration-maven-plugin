package com.link_intersystems.dbunit.maven.testcontainers;

import com.link_intersystems.dbunit.migration.testcontainers.GenericContainerConfig;
import com.link_intersystems.dbunit.migration.testcontainers.GenericJdbcContainer;
import com.link_intersystems.dbunit.migration.testcontainers.TestcontainersConfig;
import com.link_intersystems.dbunit.testcontainers.DatabaseContainerSupport;
import com.link_intersystems.dbunit.testcontainers.DefaultDatabaseContainerSupport;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.slf4j.Logger;

import java.util.Properties;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class DatabaseContainerSupportFactory {

    public DatabaseContainerSupport getDatabaseContainerSupport(TestcontainersConfig testcontainersConfig, Logger logger) {
        GenericContainerConfig containerConfig = testcontainersConfig.getContainerConfig();
        String image = testcontainersConfig.getImage();
        if (containerConfig != null) {
            return getDatabaseContainerSupport(containerConfig, image, logger);
        }
        return DatabaseContainerSupport.getDatabaseContainerSupport(image);
    }

    protected DatabaseContainerSupport getDatabaseContainerSupport(GenericContainerConfig genericContainerConfig, String image, Logger logger) {
        DefaultDatabaseContainerSupport defaultDatabaseContainerSupport = new DefaultDatabaseContainerSupport(() -> {
            GenericJdbcContainer genericJdbcContainer = new GenericJdbcContainer(image);

            GenericJdbcContainerConfigurer mavenConfigurer = new GenericJdbcContainerConfigurer(genericContainerConfig);
            mavenConfigurer.configure(genericJdbcContainer);

            if (logger != null) {
                genericJdbcContainer.setLogger(logger);
            }
            return genericJdbcContainer;
        });

        Properties dbunitConfigProperties = genericContainerConfig.getDbunitConfigProperties();
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
