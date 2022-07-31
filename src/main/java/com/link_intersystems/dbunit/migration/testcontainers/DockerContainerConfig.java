package com.link_intersystems.dbunit.migration.testcontainers;

import org.codehaus.plexus.interpolation.AbstractValueSource;

import java.util.HashMap;
import java.util.Map;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class DockerContainerConfig extends AbstractValueSource {

    private int exposedPort;
    private Map<String, String> env = new HashMap<>();
    private String[] command;

    public DockerContainerConfig() {
        super(false);
    }


    public String[] getCommand() {
        return command;
    }

    public Map<String, String> getEnv() {
        return env;
    }

    public int getExposedPort() {
        return exposedPort;
    }

    @Override
    public Object getValue(String expression) {
        if (expression.startsWith("env.")) {
            String envName = expression.substring(0, "env.".length());
            Map<String, String> env = getEnv();
            return env.get(envName);
        }

        if (command != null && "command".equals(expression)) {
            return String.join(" ", command);
        }

        return null;
    }

}
