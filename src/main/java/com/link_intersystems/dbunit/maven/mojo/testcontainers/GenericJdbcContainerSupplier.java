package com.link_intersystems.dbunit.maven.mojo.testcontainers;

import com.link_intersystems.dbunit.migration.testcontainers.GenericContainerConfig;
import com.link_intersystems.dbunit.migration.testcontainers.GenericJdbcContainer;
import com.link_intersystems.dbunit.migration.testcontainers.TestcontainersConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.JdbcDatabaseContainer;

import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

class GenericJdbcContainerSupplier implements Supplier<JdbcDatabaseContainer<?>> {

    private Logger logger = LoggerFactory.getLogger(GenericJdbcContainerSupplier.class);
    private TestcontainersConfig testcontainersConfig;

    public GenericJdbcContainerSupplier(TestcontainersConfig testcontainersConfig) {
        this.testcontainersConfig = testcontainersConfig;
    }

    public void setLogger(Logger logger) {
        this.logger = requireNonNull(logger);
    }

    @Override
    public JdbcDatabaseContainer<?> get() {
        String image = testcontainersConfig.getImage();
        GenericJdbcContainer genericJdbcContainer = new GenericJdbcContainer(image);

        GenericContainerConfig containerConfig = testcontainersConfig.getContainerConfig();
        GenericJdbcContainerConfigurer mavenConfigurer = new GenericJdbcContainerConfigurer(containerConfig);
        mavenConfigurer.configure(genericJdbcContainer);

        if (logger != null) {
            genericJdbcContainer.setLogger(logger);
        }
        return genericJdbcContainer;
    }
}