package com.link_intersystems.dbunit.maven;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class DataSourceConfig {
    private String driverClassName;
    private String jdbcUrl;
    private String username;
    private String password;
    private String testQueryString;

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTestQueryString() {
        return testQueryString;
    }

    public void setTestQueryString(String testQueryString) {
        this.testQueryString = testQueryString;
    }
}
