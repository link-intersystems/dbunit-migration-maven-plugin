package com.link_intersystems.dbunit.maven;

import com.link_intersystems.dbunit.migration.collection.AbstractLoggingDataSetCollectionMigrationListener;
import org.apache.maven.plugin.logging.Log;
import org.dbunit.dataset.DataSetException;

import java.util.function.Supplier;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
class MavenLogAdapter extends AbstractLoggingDataSetCollectionMigrationListener {

    private Log log;

    public MavenLogAdapter(Log log) {
        this.log = log;
    }

    @Override
    protected void logSucessfullyMigrated(String s) {
        log.info(s);
    }

    @Override
    protected void logFailedMigration(DataSetException e, String s) {
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
    protected void logDataSetCollectionMigrationFinished(String s, Supplier<String> supplier) {
        log.info(s);
        if (log.isDebugEnabled()) {
            log.debug(supplier.get());
        }
    }
}