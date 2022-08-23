package com.link_intersystems.dbunit.migration.testcontainers;

import org.testcontainers.containers.ContainerLaunchException;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.wait.strategy.AbstractWaitStrategy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;

import static java.lang.System.currentTimeMillis;
import static java.util.Objects.requireNonNull;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class JdbcConnectionWaitStrategy extends AbstractWaitStrategy {

    private static class Timeout {

        private long startTimeMs;
        private Duration timeoutDuration;

        public Timeout(Duration timeoutDuration) {
            this.timeoutDuration = timeoutDuration;
            startTimeMs = currentTimeMillis();
        }

        public boolean isNotTimedOut() {
            Duration duration = Duration.ofMillis(currentTimeMillis() - startTimeMs);
            return duration.compareTo(timeoutDuration) < 0;
        }
    }

    private Duration timeout = Duration.ofSeconds(10);
    private Duration connectTryoutInterval = Duration.ofMillis(100);
    private String testQueryString;
    private JdbcDatabaseContainer<?> jdbcDatabaseContainer;

    public JdbcConnectionWaitStrategy(JdbcDatabaseContainer<?> jdbcDatabaseContainer) {
        this.jdbcDatabaseContainer = requireNonNull(jdbcDatabaseContainer);
    }

    public void setTimeout(Duration timeout) {
        this.timeout = requireNonNull(timeout);
    }

    public void setTestQueryString(String testQueryString) {
        this.testQueryString = testQueryString;
    }

    public void setConnectTryoutInterval(Duration connectTryoutInterval) {
        this.connectTryoutInterval = requireNonNull(connectTryoutInterval);
    }

    @Override
    protected void waitUntilReady() {
        String jdbcUrl = jdbcDatabaseContainer.getJdbcUrl();
        String username = jdbcDatabaseContainer.getUsername();
        String password = jdbcDatabaseContainer.getPassword();

        long sleepMs = Math.min(connectTryoutInterval.toMillis(), timeout.toMillis() - 100);

        SQLException latestException = null;

        Timeout timeout = new Timeout(this.timeout);

        while (timeout.isNotTimedOut()) {
            try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
                if (testQueryString != null) {
                    try (Statement statement = connection.createStatement()) {
                        statement.execute(testQueryString);
                    }
                }
                if (timeout.isNotTimedOut()) {
                    return;
                } else {
                    break;
                }
            } catch (SQLException e) {
                latestException = e;
                try {
                    Thread.sleep(sleepMs);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }

        String failureMessage = getFailureMessage();

        throw new ContainerLaunchException(failureMessage, latestException);
    }

    private String getFailureMessage() {
        StringBuilder sb = new StringBuilder();

        sb.append("Failed to open jdbc connection ");
        sb.append("'");
        sb.append(jdbcDatabaseContainer.getJdbcUrl());
        sb.append("'");

        if (testQueryString != null) {
            sb.append(" or execute test query ");
            sb.append("'");
            sb.append(testQueryString);
            sb.append("'");
        }

        sb.append(" within ");
        sb.append(timeout);

        return sb.toString();
    }
}
