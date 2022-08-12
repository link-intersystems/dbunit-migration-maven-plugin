package com.link_intersystems.dbunit.migration.testcontainers;

import com.github.dockerjava.api.command.InspectContainerResponse;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class GenericJdbcContainer extends JdbcDatabaseContainer<GenericJdbcContainer> {


    private Logger logger = LoggerFactory.getLogger(GenericJdbcContainer.class);

    private String jdbcUrl;
    private String username;
    private String password;
    private String testQueryString;
    private String driverClassName;
    private ContainerConfigurer<GenericJdbcContainer> startedContainerConfigurer;

    public GenericJdbcContainer(String dockerImageName) {
        super(DockerImageName.parse(dockerImageName));
    }

    public GenericJdbcContainer withStartedContainerConfigurer(ContainerConfigurer<GenericJdbcContainer> startedContainerConfigurer) {
        this.startedContainerConfigurer = startedContainerConfigurer;
        return this;
    }

    @Override
    protected Logger logger() {
        if (logger.isDebugEnabled()) {
            return logger;
        }
        return super.logger();
    }

    public void setLogger(Logger logger) {
        this.logger = requireNonNull(logger);
    }

    public GenericJdbcContainer withDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
        return this;
    }

    @Override
    public String getDriverClassName() {
        return driverClassName;
    }

    public GenericJdbcContainer withJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
        return this;
    }

    @Override
    public String getJdbcUrl() {
        return jdbcUrl;
    }

    @Override
    public GenericJdbcContainer withUsername(String username) {
        this.username = username;
        return this;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public GenericJdbcContainer withPassword(String password) {
        this.password = password;
        return this;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public GenericJdbcContainer withTestQueryString(String testQueryString) {
        this.testQueryString = testQueryString;
        return this;
    }

    @Override
    protected String getTestQueryString() {
        return testQueryString;
    }

    @NotNull
    @Override
    protected Set<Integer> getLivenessCheckPorts() {
        List<Integer> exposedPorts = getExposedPorts();

        return exposedPorts.stream().map(this::getMappedPort).collect(Collectors.toSet());
    }

    @Override
    protected void waitUntilContainerStarted() {
        getWaitStrategy().waitUntilReady(this);
    }

    @Override
    protected void containerIsStarted(InspectContainerResponse containerInfo) {
        super.containerIsStarted(containerInfo);

        if (startedContainerConfigurer != null) {
            startedContainerConfigurer.configure(this);
        }
    }
}
