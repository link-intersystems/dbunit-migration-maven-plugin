package com.link_intersystems.dbunit.maven;

import com.link_intersystems.dbunit.testcontainers.DatabaseContainerSupport;
import org.apache.maven.plugin.logging.Log;

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


    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public DatabaseContainerSupport getDatabaseContainerSupport(Log log) {
        if (containerConfig != null) {
            return containerConfig.getDatabaseContainerSupport(image, log);
        }
        return DatabaseContainerSupport.getDatabaseContainerSupport(getImage());
    }
}
