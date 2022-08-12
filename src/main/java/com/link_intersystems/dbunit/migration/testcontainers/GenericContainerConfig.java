package com.link_intersystems.dbunit.migration.testcontainers;

import java.util.Properties;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class GenericContainerConfig {

    private DataSourceConfig dataSource;
    private DockerContainerConfig dockerContainer;
    private Properties dbunitConfigProperties = new Properties();


    public DataSourceConfig getDataSourceConfig() {
        return dataSource;
    }


    public DockerContainerConfig getDockerContainerConfig() {
        return dockerContainer;
    }

    public void setDockerContainer(DockerContainerConfig dockerContainer) {
        this.dockerContainer = dockerContainer;
    }

    public Properties getDbunitConfigProperties() {
        return dbunitConfigProperties;
    }
}