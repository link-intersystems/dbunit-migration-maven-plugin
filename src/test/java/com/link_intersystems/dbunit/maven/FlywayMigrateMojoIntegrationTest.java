package com.link_intersystems.dbunit.maven;

import com.link_intersystems.dbunit.stream.consumer.CopyDataSetConsumer;
import com.link_intersystems.dbunit.stream.producer.DataSetProducerSupport;
import com.link_intersystems.dbunit.stream.producer.DefaultDataSetProducerSupport;
import com.link_intersystems.dbunit.table.Row;
import com.link_intersystems.dbunit.table.TableUtil;
import com.link_intersystems.io.Unzip;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.plugin.testing.stubs.MavenProjectStub;
import org.apache.maven.project.DefaultProjectBuildingRequest;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingResult;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.stream.IDataSetProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
class FlywayMigrateMojoIntegrationTest extends AbstractMojoTestCase {

    private Path tmpDir;
    private Path pomXmlPath;

    @BeforeEach
    protected void setUp(@TempDir Path tmpDir) throws Exception {
        this.tmpDir = tmpDir;
        super.setUp();

        try (InputStream in = getClass().getResourceAsStream("/migrate-project.zip")) {
            Unzip.unzip(in, tmpDir);
        }

        pomXmlPath = tmpDir.resolve("pom.xml");
    }

    @Test
    void execute() throws Exception {

        ProjectBuilder projectBuilder = lookup(ProjectBuilder.class);
        File testPom = pomXmlPath.toFile();
        DefaultProjectBuildingRequest request = new DefaultProjectBuildingRequest();
        MavenSession mavenSession = newMavenSession(new MavenProjectStub());
        request.setRepositorySession(mavenSession.getRepositorySession());
        ProjectBuildingResult projectBuildingResult = projectBuilder.build(testPom, request);
        MavenProject mavenProject = projectBuildingResult.getProject();
        mavenSession.setCurrentProject(mavenProject);

        FlywayMigrateMojo mojo = (FlywayMigrateMojo) lookupConfiguredMojo(mavenProject, "flyway-migrate");
        mojo.setLog(new SystemStreamLog() {
            @Override
            public boolean isDebugEnabled() {
                return true;
            }
        });

        mojo.execute();

        assertDataSetsMigrated();
    }

    private void assertDataSetsMigrated() throws IOException, DataSetException {
        assertDataSetMigrated("target/flat/tiny-sakila-flat.xml", (ps, file) -> ps.setFlatXmlProducer(file));
        assertDataSetMigrated("target/xml/tiny-sakila.xml", (ps, file) -> ps.setXmlProducer(file));
        assertDataSetMigrated("target/tiny-sakila-csv", (ps, file) -> ps.setCsvProducer(file));
    }

    private void assertDataSetMigrated(String dataSetPathname, DataSetProducerSupportMethod producerMethod) throws DataSetException, IOException {
        Path dataSetPath = tmpDir.resolve(dataSetPathname);
        DefaultDataSetProducerSupport producerSupport = new DefaultDataSetProducerSupport();
        producerMethod.setFile(producerSupport, dataSetPath.toFile());
        IDataSetProducer dataSetProducer = producerSupport.getDataSetProducer();

        CopyDataSetConsumer copyDataSetConsumer = new CopyDataSetConsumer();
        dataSetProducer.setConsumer(copyDataSetConsumer);
        dataSetProducer.produce();
        IDataSet dataSet = copyDataSetConsumer.getDataSet();
        assertDataSetMigrated(dataSet);
    }

    private void assertDataSetMigrated(IDataSet dataSet) throws DataSetException {
        ITable actorTable = dataSet.getTable("actor");
        assertNotNull(actorTable);
        assertEquals(2, actorTable.getRowCount());
        TableUtil actorUtil = new TableUtil(actorTable);
        Row firstRow = actorUtil.getRow(0);
        assertEquals("PENELOPE", firstRow.getValueByColumnName("firstname"));

        ITable languageTable = dataSet.getTable("language");
        assertNotNull(languageTable);
        assertEquals(1, languageTable.getRowCount());

        ITable filmTable = dataSet.getTable("film");
        assertNotNull(filmTable);
        assertEquals(44, filmTable.getRowCount());

        ITable filmDescription = dataSet.getTable("film_description");
        assertNotNull(filmDescription);
        assertEquals(44, filmDescription.getRowCount());

        ITable filmActorTable = dataSet.getTable("film_actor");
        assertNotNull(filmActorTable);
        assertEquals(44, filmActorTable.getRowCount());
    }

    private static interface DataSetProducerSupportMethod {

        public void setFile(DataSetProducerSupport producerSupport, File file) throws IOException;
    }
}