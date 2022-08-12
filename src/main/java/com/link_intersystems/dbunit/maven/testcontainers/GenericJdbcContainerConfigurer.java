package com.link_intersystems.dbunit.maven.testcontainers;

import com.link_intersystems.dbunit.migration.testcontainers.ContainerConfigurer;
import com.link_intersystems.dbunit.migration.testcontainers.DockerContainerConfig;
import com.link_intersystems.dbunit.migration.testcontainers.GenericContainerConfig;
import com.link_intersystems.dbunit.migration.testcontainers.GenericJdbcContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class GenericJdbcContainerConfigurer implements ContainerConfigurer<GenericJdbcContainer> {

    private Logger logger = LoggerFactory.getLogger(GenericJdbcContainerConfigurer.class);

    private GenericContainerConfig containerConfig;

    public GenericJdbcContainerConfigurer(GenericContainerConfig containerConfig) {
        this.containerConfig = requireNonNull(containerConfig);
    }

    public void configure(GenericJdbcContainer genericJdbcContainer) {
        StartedContainerConfigurer startedContainerConfigurer = new StartedContainerConfigurer(genericJdbcContainer, containerConfig);
        genericJdbcContainer.withStartedContainerConfigurer(startedContainerConfigurer);

        DockerContainerConfig dockerContainerConfig = containerConfig.getDockerContainerConfig();
        Map<String, String> env = dockerContainerConfig.getEnv();
        for (Map.Entry<String, String> envEntry : env.entrySet()) {
            String key = envEntry.getKey();
            String value = envEntry.getValue();
            if (logger.isDebugEnabled()) {
                logger.debug("Adding container environment variable: {} = {}", key, value);
            }
            genericJdbcContainer.addEnv(key, value);
        }

        String[] command = dockerContainerConfig.getCommand();
        if (command != null) {
            genericJdbcContainer.setCommand(command);
        }

        genericJdbcContainer.addExposedPort(dockerContainerConfig.getExposedPort());
    }


}
