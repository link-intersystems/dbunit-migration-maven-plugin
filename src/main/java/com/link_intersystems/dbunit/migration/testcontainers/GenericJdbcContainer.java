package com.link_intersystems.dbunit.migration.testcontainers;

import org.codehaus.plexus.interpolation.AbstractValueSource;
import org.codehaus.plexus.interpolation.InterpolationException;
import org.codehaus.plexus.interpolation.StringSearchInterpolator;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class GenericJdbcContainer extends JdbcDatabaseContainer<GenericJdbcContainer> {

    private final DataSourceConfig dataSourceConfig;
    private final DockerContainerConfig dockerContainerConfig;
    private final StringSearchInterpolator interpolator;
    private Logger logger = LoggerFactory.getLogger(GenericJdbcContainer.class);
    public GenericJdbcContainer(String dockerImageName, GenericContainerConfig genericContainerConfig) {
        super(DockerImageName.parse(dockerImageName));
        dataSourceConfig = genericContainerConfig.getDataSourceConfig();
        dockerContainerConfig = genericContainerConfig.getDockerContainerConfig();
        interpolator = new StringSearchInterpolator("{{", "}}");
        interpolator.addValueSource(dockerContainerConfig);
        interpolator.addValueSource(new ValueSourceAdapter());

        String[] command = dockerContainerConfig.getCommand();
        if (command != null) {
            this.setCommand(command);
        }

        addExposedPort(dockerContainerConfig.getExposedPort());
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public String getDriverClassName() {
        return dataSourceConfig.getDriverClassName();
    }

    @Override
    public String getJdbcUrl() {
        String interpolated = getInterpolated(dataSourceConfig::getJdbcUrl);
        logger.debug("JdbcUrl '{}' interpolated '{}'", dataSourceConfig.getJdbcUrl(), interpolated);
        return interpolated;
    }

    private String getInterpolated(Supplier<String> stringSupplier) {
        String s = stringSupplier.get();
        try {
            return interpolator.interpolate(s);
        } catch (InterpolationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getUsername() {
        String interpolated = getInterpolated(dataSourceConfig::getUsername);
        logger.debug("Username '{}' interpolated '{}'", dataSourceConfig.getUsername(), interpolated);
        return interpolated;
    }

    @Override
    public String getPassword() {
        String interpolated = getInterpolated(dataSourceConfig::getPassword);
        logger.debug("Password '{}' interpolated '{}'", dataSourceConfig.getPassword(), interpolated);
        return interpolated;
    }

    @Override
    protected String getTestQueryString() {
        String interpolated = getInterpolated(dataSourceConfig::getTestQueryString);
        logger.debug("TestQueryString '{}' interpolated '{}'", dataSourceConfig.getTestQueryString(), interpolated);
        return interpolated;
    }

    @NotNull
    @Override
    protected Set<Integer> getLivenessCheckPorts() {
        int exposedPort = dockerContainerConfig.getExposedPort();
        return Collections.singleton(getMappedPort(exposedPort));
    }

    @Override
    protected void configure() {
        Map<String, String> env = dockerContainerConfig.getEnv();
        for (Map.Entry<String, String> envEntry : env.entrySet()) {
            String key = envEntry.getKey();
            String value = envEntry.getValue();
            if (logger.isDebugEnabled()) {
                logger.debug("Adding container environment variable: {} = {}", key, value);
            }
            addEnv(key, value);
        }

    }

    @Override
    protected void waitUntilContainerStarted() {
        getWaitStrategy().waitUntilReady(this);
    }

    @Override
    protected Logger logger() {
        if (logger.isDebugEnabled()) {
            return logger;
        }
        return super.logger();
    }

    private class ValueSourceAdapter extends AbstractValueSource {

        ValueSourceAdapter() {
            super(false);
        }

        @Override
        public Object getValue(String expression) {
            if ("host".equals(expression)) {
                return getHost();
            }

            if ("port".equals(expression)) {
                return getMappedPort(dockerContainerConfig.getExposedPort());
            }

            return null;
        }
    }
}
