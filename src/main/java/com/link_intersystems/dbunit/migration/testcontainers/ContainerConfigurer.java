package com.link_intersystems.dbunit.migration.testcontainers;

import org.testcontainers.containers.GenericContainer;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public interface ContainerConfigurer<T extends GenericContainer<T>> {

    public void configure(T container);
}
