package com.link_intersystems.dbunit.maven.testcontainers;

import com.link_intersystems.dbunit.migration.testcontainers.DockerContainerConfig;
import org.codehaus.plexus.interpolation.AbstractValueSource;

import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class DockerContainerConfigValueSource extends AbstractValueSource {
    private DockerContainerConfig dockerContainerConfig;

    public DockerContainerConfigValueSource(DockerContainerConfig dockerContainerConfig) {
        super(false);
        this.dockerContainerConfig = requireNonNull(dockerContainerConfig);
    }

    @Override
    public Object getValue(String expression) {
        if (expression.startsWith("env.")) {
            String envName = expression.substring("env.".length());
            Map<String, String> env = dockerContainerConfig.getEnv();
            return env.get(envName);
        }

        String[] command = dockerContainerConfig.getCommand();
        if (command != null && "command".equals(expression)) {
            return String.join(" ", command);
        }

        return null;
    }
}
