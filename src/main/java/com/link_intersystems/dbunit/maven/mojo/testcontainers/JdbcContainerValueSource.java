package com.link_intersystems.dbunit.maven.mojo.testcontainers;

import com.link_intersystems.dbunit.migration.testcontainers.GenericJdbcContainer;
import org.codehaus.plexus.interpolation.AbstractValueSource;

import java.util.List;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class JdbcContainerValueSource extends AbstractValueSource {

    private GenericJdbcContainer jdbcContainer;

    public JdbcContainerValueSource(GenericJdbcContainer jdbcContainer) {
        super(false);
        this.jdbcContainer = jdbcContainer;
    }

    @Override
    public Object getValue(String expression) {
        if ("host".equals(expression)) {
            return jdbcContainer.getHost();
        }

        if ("port".equals(expression)) {
            List<Integer> exposedPorts = jdbcContainer.getExposedPorts();
            if (!exposedPorts.isEmpty()) {
                return jdbcContainer.getMappedPort(exposedPorts.get(0));
            }
        }

        return null;
    }
}
