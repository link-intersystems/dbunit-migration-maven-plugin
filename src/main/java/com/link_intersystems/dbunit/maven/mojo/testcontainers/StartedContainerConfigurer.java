package com.link_intersystems.dbunit.maven.mojo.testcontainers;

import com.link_intersystems.dbunit.migration.testcontainers.*;
import org.codehaus.plexus.interpolation.InterpolationException;
import org.codehaus.plexus.interpolation.StringSearchInterpolator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class StartedContainerConfigurer implements ContainerConfigurer<GenericJdbcContainer> {

    private Logger logger = LoggerFactory.getLogger(StartedContainerConfigurer.class);

    private final GenericJdbcContainer genericJdbcContainer;
    private final GenericContainerConfig containerConfig;
    private final StringSearchInterpolator interpolator;

    public StartedContainerConfigurer(GenericJdbcContainer genericJdbcContainer, GenericContainerConfig containerConfig) {
        this.genericJdbcContainer = genericJdbcContainer;
        this.containerConfig = containerConfig;

        DockerContainerConfig dockerContainerConfig = containerConfig.getDockerContainerConfig();
        interpolator = new StringSearchInterpolator("{{", "}}");
        interpolator.addValueSource(new DockerContainerConfigValueSource(dockerContainerConfig));
        interpolator.addValueSource(new JdbcContainerValueSource(genericJdbcContainer));
    }

    @Override
    public void configure(GenericJdbcContainer container) {

        DataSourceConfig dataSourceConfig = containerConfig.getDataSourceConfig();

        String driverClassName = dataSourceConfig.getDriverClassName();
        genericJdbcContainer.withDriverClassName(driverClassName);

        String jdbcUrl = getInterpolated(dataSourceConfig::getJdbcUrl);
        logger.debug("JdbcUrl '{}' interpolated '{}'", dataSourceConfig.getJdbcUrl(), jdbcUrl);
        genericJdbcContainer.withJdbcUrl(jdbcUrl);

        String username = getInterpolated(dataSourceConfig::getUsername);
        logger.debug("Username '{}' interpolated '{}'", dataSourceConfig.getUsername(), username);
        genericJdbcContainer.withUsername(username);

        String password = getInterpolated(dataSourceConfig::getPassword);
        logger.debug("Password '{}' interpolated '{}'", dataSourceConfig.getPassword(), password);
        genericJdbcContainer.withPassword(password);

        String testQueryString = getInterpolated(dataSourceConfig::getTestQueryString);
        logger.debug("TestQueryString '{}' interpolated '{}'", dataSourceConfig.getTestQueryString(), testQueryString);
        genericJdbcContainer.withTestQueryString(testQueryString);
    }

    private String getInterpolated(Supplier<String> stringSupplier) {
        String s = stringSupplier.get();
        try {
            return interpolator.interpolate(s);
        } catch (InterpolationException e) {
            throw new RuntimeException(e);
        }
    }
}
