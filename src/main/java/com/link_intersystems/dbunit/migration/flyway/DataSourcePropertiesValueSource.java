package com.link_intersystems.dbunit.migration.flyway;

import com.link_intersystems.dbunit.migration.DataSourceProperties;
import org.codehaus.plexus.interpolation.AbstractValueSource;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class DataSourcePropertiesValueSource extends AbstractValueSource {

    public static enum Property {
        USERNAME("username", DataSourceProperties::getUsername),
        PASSWORD("password", DataSourceProperties::getPassword),
        DATABASE_NAME("databaseName", DataSourceProperties::getDatabaseName),
        HOST("host", DataSourceProperties::getHost),
        PORT("port", DataSourceProperties::getPort),
        JDBC_URL("jdbcUrl", DataSourceProperties::getJdbcUrl);

        private String defaultExpression;
        private Function<DataSourceProperties, Object> getter;

        Property(String defaultExpression, Function<DataSourceProperties, Object> getter) {
            this.defaultExpression = defaultExpression;
            this.getter = getter;
        }

        public String getDefaultExpression() {
            return defaultExpression;
        }

        Object getValue(DataSourceProperties dataSourceProperties) {
            return getter.apply(dataSourceProperties);
        }
    }

    private Map<String, Property> propertiesByExpression = new HashMap<>();
    private String environmentExpressionPrefix = "env.";

    private DataSourceProperties dataSourceProperties;

    public DataSourcePropertiesValueSource(DataSourceProperties dataSourceProperties) {
        super(false);
        this.dataSourceProperties = requireNonNull(dataSourceProperties);

        for (Property property : Property.values()) {
            propertiesByExpression.put(property.getDefaultExpression(), property);
        }
    }

    public void setEnvironmentExpressionPrefix(String environmentExpressionPrefix) {
        this.environmentExpressionPrefix = environmentExpressionPrefix;
    }

    public void setExpressionMapping(String expression, Property property) {
        propertiesByExpression.put(requireNonNull(expression), requireNonNull(property));
    }

    @Override
    public Object getValue(String expression) {
        if (expression.startsWith(environmentExpressionPrefix)) {
            String envKey = expression.substring(environmentExpressionPrefix.length());
            Map<String, String> environment = dataSourceProperties.getEnvironment();
            return environment.get(envKey);
        }

        Property property = propertiesByExpression.get(expression);
        if (property != null) {
            return property.getValue(dataSourceProperties);
        }
        return null;
    }
}
