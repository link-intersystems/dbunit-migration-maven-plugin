package com.link_intersystems.dbunit.maven.mojo;

import com.link_intersystems.dbunit.migration.resources.AbstractLoggingDataSetResourcesMigrationListener;
import org.apache.maven.plugin.logging.Log;
import org.dbunit.dataset.DataSetException;

import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class MavenLogMigrationListener extends AbstractLoggingDataSetResourcesMigrationListener {

    private Log log;

    public MavenLogMigrationListener(Log log) {
        this.log = requireNonNull(log);
    }

    @Override
    protected void logMigrationSuccessful(String s) {
        log.info(s);
    }

    @Override
    protected void logMigrationFailed(DataSetException e, String s) {
        if (log.isDebugEnabled()) {
            log.error(s, e);
            return;
        }

        log.error(s);
    }

    @Override
    protected void logStartMigration(String s) {
        log.info(s);
    }

    @Override
    protected void logResourcesSupplied(String s, Supplier<String> supplier) {
        log.info(s);
        if (log.isDebugEnabled()) {
            log.debug(supplier.get());
        }
    }

    @Override
    protected void logMigrationsFinished(String s, Supplier<String> supplier) {
        log.info(s);
        if (log.isDebugEnabled()) {
            log.debug(supplier.get());
        }
    }
}
