package com.link_intersystems.dbunit.migration.flyway;

import com.link_intersystems.dbunit.migration.DataSourceProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
class DataSourcePropertiesValueSourceTest {

    private DataSourceProperties dataSourceProperties;
    private DataSourcePropertiesValueSource valueSource;

    @BeforeEach
    void setUp() {
        dataSourceProperties = mock(DataSourceProperties.class);
        valueSource = new DataSourcePropertiesValueSource(dataSourceProperties);
    }

    @Test
    void unknownExpression() {
        assertNull(valueSource.getValue("test"));
    }

    private void testUsername(String expression) {
        when(dataSourceProperties.getUsername()).thenReturn("someValue");

        assertEquals("someValue", valueSource.getValue(expression));
    }

    @Test
    void getUsername() {
        valueSource.setExpressionMapping("user", DataSourcePropertiesValueSource.Property.USERNAME);

        testUsername("user");
    }

    @Test
    void getDefaultPassword() {
        String expression = "password";
        testPassword(expression);
    }

    private void testPassword(String expression) {
        String exceptedValue = "someValue";
        when(dataSourceProperties.getPassword()).thenReturn(exceptedValue);

        assertEquals(exceptedValue, valueSource.getValue(expression));
    }


    @Test
    void getPassword() {
        valueSource.setExpressionMapping("pass", DataSourcePropertiesValueSource.Property.PASSWORD);
        testPassword("pass");
    }


    @Test
    void getDefaultDatabaseName() {
        testDatabaseName("databaseName");
    }

    @Test
    void getDatabaseName() {
        valueSource.setExpressionMapping("db", DataSourcePropertiesValueSource.Property.DATABASE_NAME);
        testDatabaseName("db");
    }

    private void testDatabaseName(String databaseName) {
        String exceptedValue = "someValue";
        when(dataSourceProperties.getDatabaseName()).thenReturn(exceptedValue);

        assertEquals(exceptedValue, valueSource.getValue(databaseName));
    }

    @Test
    void getDefaultHost() {
        testHost("host");
    }

    @Test
    void getHost() {
        valueSource.setExpressionMapping("h", DataSourcePropertiesValueSource.Property.HOST);
        testHost("h");
    }

    private void testHost(String host) {
        String exceptedValue = "someValue";
        when(dataSourceProperties.getHost()).thenReturn(exceptedValue);

        assertEquals(exceptedValue, valueSource.getValue(host));
    }

    @Test
    void getDefaultPort() {
        testPort("port");
    }

    @Test
    void getPort() {
        valueSource.setExpressionMapping("p", DataSourcePropertiesValueSource.Property.PORT);
        testPort("p");
    }

    private void testPort(String port) {
        String exceptedValue = "someValue";
        when(dataSourceProperties.getPort()).thenReturn(exceptedValue);

        assertEquals(exceptedValue, valueSource.getValue(port));
    }

    @Test
    void getDefaultJdbcUrl() {
        testJdbcUrl("jdbcUrl");
    }

    @Test
    void getJdbcUrl() {
        valueSource.setExpressionMapping("jdbc", DataSourcePropertiesValueSource.Property.JDBC_URL);
        testJdbcUrl("jdbc");
    }

    private void testJdbcUrl(String jdbcUrl) {
        String exceptedValue = "someValue";
        when(dataSourceProperties.getJdbcUrl()).thenReturn(exceptedValue);

        assertEquals(exceptedValue, valueSource.getValue(jdbcUrl));
    }

    @Test
    void getDefaultEnvironmentValue() {
        HashMap<String, String> env = new HashMap<>();
        env.put("a", "someValue");
        when(dataSourceProperties.getEnvironment()).thenReturn(env);

        assertEquals("someValue", valueSource.getValue("env.a"));
    }

    @Test
    void getEnvironmentValue() {
        HashMap<String, String> env = new HashMap<>();
        env.put("a", "someValue");
        when(dataSourceProperties.getEnvironment()).thenReturn(env);
        valueSource.setEnvironmentExpressionPrefix("ENV_");

        assertEquals("someValue", valueSource.getValue("ENV_a"));
    }
}