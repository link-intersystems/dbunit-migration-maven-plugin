package com.link_intersystems.dbunit.migration.testcontainers;

import com.link_intersystems.dbunit.maven.slf4j.Slf4JMavenLogAdapter;
import com.link_intersystems.dbunit.testcontainers.DatabaseContainerSupport;
import com.link_intersystems.dbunit.testcontainers.DefaultDatabaseContainerSupport;
import org.apache.maven.plugin.logging.Log;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;

import java.util.Map;
import java.util.Properties;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class GenericContainerConfig {

    private DataSourceConfig dataSource;
    private DockerContainerConfig dockerContainer;
    private Map<String, String> dbunitConfigProperties;


    public DataSourceConfig getDataSourceConfig() {
        return dataSource;
    }


    public DockerContainerConfig getDockerContainerConfig() {
        return dockerContainer;
    }

    public void setDockerContainer(DockerContainerConfig dockerContainer) {
        this.dockerContainer = dockerContainer;
    }

    public DatabaseContainerSupport getDatabaseContainerSupport(String image, Log log) {
        DefaultDatabaseContainerSupport defaultDatabaseContainerSupport = new DefaultDatabaseContainerSupport(() -> {
            GenericJdbcContainer genericJdbcContainer = new GenericJdbcContainer(image, GenericContainerConfig.this);
            if (log != null) {
                genericJdbcContainer.setLogger(new Slf4JMavenLogAdapter(log));
            }
            return genericJdbcContainer;
        });

        if (dbunitConfigProperties != null && !dbunitConfigProperties.isEmpty()) {
            DatabaseConfig databaseConfig = defaultDatabaseContainerSupport.getDatabaseConfig();
            applyConfiguration(databaseConfig, dbunitConfigProperties);
        }

        return defaultDatabaseContainerSupport;
    }

    private void applyConfiguration(DatabaseConfig databaseConfig, Map<String, String> dbunitConfigProperties) {
        Properties properties = new Properties();
        properties.putAll(dbunitConfigProperties);
        try {
            databaseConfig.setPropertiesByString(properties);
        } catch (DatabaseUnitException e) {
            throw new RuntimeException("Can not apply dbunit configuration.", e);
        }
    }
}