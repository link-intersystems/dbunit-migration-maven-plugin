package com.link_intersystems.dbunit.migration.testcontainers;

import com.link_intersystems.maven.logging.Level;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class TestcontainersConfig {

    private String image;

    private GenericContainerConfig containerConfig;

    private String logLevel = "off";


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

    public Level getLogLevel() {
        if (logLevel == null) {
            return Level.off;
        }
        return Level.valueOf(logLevel.toLowerCase());
    }
}
