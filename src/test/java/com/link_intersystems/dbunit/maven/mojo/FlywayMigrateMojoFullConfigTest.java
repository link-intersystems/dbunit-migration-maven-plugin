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

import java.io.File;
import java.io.IOException;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
@MojoTest
@MavenTestProject("/full-migration-configuration")
class FlywayMigrateMojoFullConfigTest {

    @Test
    void execute(@TestMojo(goal = "flyway-migrate") FlywayMigrateMojo mojo, MavenProject mavenProject) throws Exception {
        mojo.execute();

        assertDataSetsMigrated(mavenProject.getBasedir());
    }

    private void assertDataSetsMigrated(File basedir) throws IOException, DataSetException {
        MavenTestProjectAssertions testMavenProject = new MavenTestProjectAssertions(basedir);
        ConfigProperties configProperties = new ConfigProperties();
        configProperties.setProperty(FlatXmlDataSetFileConfig.COLUMN_SENSING, true);
        testMavenProject.setConfigProperties(configProperties);

        testMavenProject.assertDataSet("target/flat/tiny-sakila-flat.xml", MigratedDataSetAssertion::assertDataSetMigrated);
        testMavenProject.assertDataSet("target/flat/tiny-sakila-flat-column-sensing.xml", MigratedDataSetAssertion::assertDataSetMigrated);
        testMavenProject.assertDataSet("target/xml/tiny-sakila.xml", MigratedDataSetAssertion::assertDataSetMigrated);
        testMavenProject.assertDataSet("target/tiny-sakila-csv", MigratedDataSetAssertion::assertDataSetMigrated);
    }
}