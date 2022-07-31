package com.link_intersystems.dbunit.migration.testcontainers;

import com.link_intersystems.dbunit.testcontainers.DatabaseContainerSupport;
import org.slf4j.Logger;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class TestcontainersConfig {

    private String image;

    private GenericContainerConfig containerConfig;


    public GenericContainerConfig getContainerConfig() {
        return containerConfig;
    }

    public void setContainerConfig(GenericContainerConfig containerConfig) {
        this.containerConfig = containerConfig;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public DatabaseContainerSupport getDatabaseContainerSupport(Logger logger) {
        if (containerConfig != null) {
            return containerConfig.getDatabaseContainerSupport(image, logger);
        }
        return DatabaseContainerSupport.getDatabaseContainerSupport(getImage());
    }
}
