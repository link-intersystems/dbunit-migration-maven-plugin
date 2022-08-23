package com.link_intersystems.dbunit.migration;

import org.apache.maven.plugin.MojoExecutionException;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class MigrationConfig {

    private int concurrency = 1;

    public int getConcurrency() {
        return concurrency;
    }

    public void validate() throws MojoExecutionException {
        if (concurrency < 1) {
            throw new MojoExecutionException("migration.concurrency must be 1 or greater, but is " + concurrency);
        }
    }
}
