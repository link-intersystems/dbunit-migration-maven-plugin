package com.link_intersystems.dbunit.maven.mojo;

import com.link_intersystems.dbunit.maven.MavenTestProjectAssertions;
import com.link_intersystems.dbunit.maven.MigratedDataSetAssertion;
import com.link_intersystems.dbunit.stream.resource.file.xml.FlatXmlDataSetFileConfig;
import com.link_intersystems.maven.plugin.test.MavenTestProject;
import com.link_intersystems.maven.plugin.test.TestMojo;
import com.link_intersystems.maven.plugin.test.extensions.MojoTest;
import com.link_intersystems.util.config.properties.ConfigProperties;
import org.apache.maven.project.MavenProject;
import org.dbunit.dataset.DataSetException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.File;
import java.io.IOException;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
@ExtendWith(MojoTest.class)
@MavenTestProject("/migration-performance")
class FlywayMigrateMojoPerformanceTest {

    @Timeout(value = 30, unit = SECONDS)
    @Test
    void execute(@TestMojo(goal = "flyway-migrate", debugEnabled = true) FlywayMigrateMojo mojo, MavenProject mavenProject) throws Exception {
        mojo.execute();

        assertDataSetsMigrated(mavenProject.getBasedir());
    }

    private void assertDataSetsMigrated(File basedir) throws IOException, DataSetException {
        MavenTestProjectAssertions testMavenProject = new MavenTestProjectAssertions(basedir);
        ConfigProperties configProperties = new ConfigProperties();
        configProperties.setProperty(FlatXmlDataSetFileConfig.COLUMN_SENSING, true);
        testMavenProject.setConfigProperties(configProperties);

        testMavenProject.assertDataSet("target/flat/tiny-sakila-flat.xml", MigratedDataSetAssertion::assertDataSetMigrated);
        testMavenProject.assertDataSet("target/flat2/tiny-sakila-flat.xml", MigratedDataSetAssertion::assertDataSetMigrated);
        testMavenProject.assertDataSet("target/flat3/tiny-sakila-flat.xml", MigratedDataSetAssertion::assertDataSetMigrated);
        testMavenProject.assertDataSet("target/flat/tiny-sakila-flat-column-sensing.xml", MigratedDataSetAssertion::assertDataSetMigrated);
        testMavenProject.assertDataSet("target/flat2/tiny-sakila-flat-column-sensing.xml", MigratedDataSetAssertion::assertDataSetMigrated);
        testMavenProject.assertDataSet("target/flat3/tiny-sakila-flat-column-sensing.xml", MigratedDataSetAssertion::assertDataSetMigrated);
        testMavenProject.assertDataSet("target/xml/tiny-sakila.xml", MigratedDataSetAssertion::assertDataSetMigrated);
        testMavenProject.assertDataSet("target/xml2/tiny-sakila.xml", MigratedDataSetAssertion::assertDataSetMigrated);
        testMavenProject.assertDataSet("target/xml3/tiny-sakila.xml", MigratedDataSetAssertion::assertDataSetMigrated);
        testMavenProject.assertDataSet("target/tiny-sakila-csv", MigratedDataSetAssertion::assertDataSetMigrated);
        testMavenProject.assertDataSet("target/tiny-sakila-csv2", MigratedDataSetAssertion::assertDataSetMigrated);
        testMavenProject.assertDataSet("target/tiny-sakila-csv3", MigratedDataSetAssertion::assertDataSetMigrated);
    }
}