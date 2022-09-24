package com.link_intersystems.dbunit.maven.mojo;

import com.link_intersystems.dbunit.dataset.DataSetBuilder;
import com.link_intersystems.dbunit.maven.MavenTestProjectAssertions;
import com.link_intersystems.dbunit.maven.MigratedDataSetAssertion;
import com.link_intersystems.dbunit.meta.TableMetaDataBuilder;
import com.link_intersystems.dbunit.stream.resource.file.xml.FlatXmlDataSetFileConfig;
import com.link_intersystems.dbunit.table.Row;
import com.link_intersystems.dbunit.table.TableUtil;
import com.link_intersystems.maven.plugin.test.MavenTestProject;
import com.link_intersystems.maven.plugin.test.TestMojo;
import com.link_intersystems.maven.plugin.test.extensions.MojoTest;
import com.link_intersystems.util.config.properties.ConfigProperties;
import org.apache.maven.project.MavenProject;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DefaultTable;
import org.dbunit.dataset.ITable;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

        testMavenProject.assertDataSet("target/flat/tiny-sakila-flat.xml", ds -> {
            ITable filmComment = ds.getTable("film_comment");
            TableMetaDataBuilder tableMetaDataBuilder = new TableMetaDataBuilder(filmComment.getTableMetaData());
            tableMetaDataBuilder.setPkColumns("film_id");
            DefaultTable filmCommentWithId = new DefaultTable(tableMetaDataBuilder.build());
            filmCommentWithId.addTableRows(filmComment);

            Properties comment = getComment(filmCommentWithId, "1");

            assertEquals("localhost", comment.getProperty("hostname"));
            assertEquals("test", comment.getProperty("username"));
            assertEquals("test", comment.getProperty("username"));
            assertEquals("test", comment.getProperty("env.POSTGRES_DB"));
            assertEquals("HELLO WORLD", comment.getProperty("env.TEST_VALUE"));
        });
    }

    private Properties getComment(ITable film_comment, String id) throws DataSetException, IOException {
        TableUtil rows = new TableUtil(film_comment);
        Row row = rows.getRowById(id);
        String comment = (String) row.getValue("comment");

        Properties properties = new Properties();
        properties.load(new StringReader(comment));
        return properties;
    }
}