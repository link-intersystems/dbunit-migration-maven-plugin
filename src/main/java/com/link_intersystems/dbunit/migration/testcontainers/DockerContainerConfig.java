package com.link_intersystems.dbunit.migration.testcontainers;

import java.util.HashMap;
import java.util.Map;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class DockerContainerConfig {

    private int exposedPort;
    private Map<String, String> env = new HashMap<>();
    private String[] command;

    public String[] getCommand() {
        return command;
    }

    public Map<String, String> getEnv() {
        return env;
    }

    public int getExposedPort() {
        return exposedPort;
    }

}
