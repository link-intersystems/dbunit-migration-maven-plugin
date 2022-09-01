package com.link_intersystems.dbunit.maven;

import com.link_intersystems.dbunit.stream.consumer.CopyDataSetConsumer;
import com.link_intersystems.dbunit.stream.resource.detection.DataSetFileDetection;
import com.link_intersystems.dbunit.stream.resource.file.DataSetFile;
import com.link_intersystems.io.FileScanner;
import com.link_intersystems.util.config.properties.ConfigProperties;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.stream.IDataSetProducer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class MavenTestProjectAssertions {

    private DataSetFileDetection dataSetFileDetection = new DataSetFileDetection();

    private File basedir;

    public MavenTestProjectAssertions(File basedir) {
        this.basedir = Objects.requireNonNull(basedir);

        FileScanner interpolationFileScanner = new FileScanner();
        interpolationFileScanner.addIncludeFilePattern("**/pom.xml", "pom.xml");
    }

    public void setConfigProperties(ConfigProperties configProperties) {
        dataSetFileDetection.setConfigProperties(configProperties);
    }

    public void assertDataSet(String dataSetPathname, DataSetAssertion dataSetAssertion) throws IOException, DataSetException {
        Path dataSetPath = basedir.toPath().resolve(dataSetPathname);

        DataSetFile dataSetFile = dataSetFileDetection.detect(dataSetPath.toFile());
        assertNotNull(dataSetFile, dataSetPathname);
        IDataSetProducer dataSetProducer = dataSetFile.createProducer();

        CopyDataSetConsumer copyDataSetConsumer = new CopyDataSetConsumer();
        dataSetProducer.setConsumer(copyDataSetConsumer);
        dataSetProducer.produce();
        IDataSet dataSet = copyDataSetConsumer.getDataSet();
        dataSetAssertion.assertDataSet(dataSet);
    }
}
