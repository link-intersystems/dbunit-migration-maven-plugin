package com.link_intersystems.dbunit.maven;

import org.dbunit.dataset.DataSetException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
class FlywayMigrateMojoIntegrationTest extends AbstractMinimalMigrationConfigurationTest {

    @Test
    void execute() throws Exception {
        FlywayMigrateMojo mojo = lookupConfiguredMojo("flyway-migrate");

        mojo.execute();

        assertDataSetsMigrated();
    }

    private void assertDataSetsMigrated() throws IOException, DataSetException {
        TestMavenProject testMavenProject = getTestMavenProject();

        testMavenProject.assertFlatXml("target/flat/tiny-sakila-flat.xml", MigratedDataSetAssertion::assertDataSetMigrated);
        testMavenProject.assertXml("target/xml/tiny-sakila.xml", MigratedDataSetAssertion::assertDataSetMigrated);
        testMavenProject.assertCsv("target/tiny-sakila-csv", MigratedDataSetAssertion::assertDataSetMigrated);
    }
}