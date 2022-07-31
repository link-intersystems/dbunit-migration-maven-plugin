package com.link_intersystems.dbunit.maven;

import com.link_intersystems.dbunit.stream.resource.file.DataSetFileConfig;
import org.dbunit.dataset.DataSetException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
class FlywayMigrateMojoFullConfigTest extends AbstractMojoTest {

    @Override
    protected TestMavenProject createTestMavenProject(Path basepath) {
        return new TestMavenProject(basepath, "full-migration-configuration.zip");
    }

    @Test
    void execute() throws Exception {
        FlywayMigrateMojo mojo = lookupConfiguredMojo("flyway-migrate");

        mojo.execute();

        assertDataSetsMigrated();
    }

    private void assertDataSetsMigrated() throws IOException, DataSetException {
        TestMavenProject testMavenProject = getTestMavenProject();
        DataSetFileConfig dataSetFileConfig = new DataSetFileConfig();
        dataSetFileConfig.setColumnSensing(true);
        testMavenProject.setDataSetFileConfig(dataSetFileConfig);

        testMavenProject.assertDataSet("target/flat/tiny-sakila-flat.xml", MigratedDataSetAssertion::assertDataSetMigrated);
        testMavenProject.assertDataSet("target/flat/tiny-sakila-flat-column-sensing.xml", MigratedDataSetAssertion::assertDataSetMigrated);
        testMavenProject.assertDataSet("target/xml/tiny-sakila.xml", MigratedDataSetAssertion::assertDataSetMigrated);
        testMavenProject.assertDataSet("target/tiny-sakila-csv", MigratedDataSetAssertion::assertDataSetMigrated);
    }
}