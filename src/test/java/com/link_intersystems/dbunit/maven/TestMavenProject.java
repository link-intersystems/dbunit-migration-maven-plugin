package com.link_intersystems.dbunit.maven;

import com.link_intersystems.dbunit.stream.consumer.CopyDataSetConsumer;
import com.link_intersystems.dbunit.stream.resource.file.DataSetFile;
import com.link_intersystems.dbunit.stream.resource.file.DataSetFileConfig;
import com.link_intersystems.dbunit.stream.resource.file.DataSetFileDetection;
import com.link_intersystems.io.Unzip;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.stream.IDataSetProducer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class TestMavenProject {

    DataSetFileDetection dataSetFileDetection = new DataSetFileDetection();

    private Path basepath;
    private String resourcePath;

    public TestMavenProject(Path basepath, String resourcePath) {
        this.basepath = basepath;
        this.resourcePath = resourcePath;
    }

    public void setDataSetFileConfig(DataSetFileConfig dataSetFileConfig) {
        dataSetFileDetection.setDataSetFileConfig(dataSetFileConfig);
    }

    public void create() {
        ClassLoader classLoader = getClassLoader();
        try (InputStream in = classLoader.getResourceAsStream(resourcePath)) {
            Unzip.unzip(in, basepath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ClassLoader getClassLoader() {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        if (contextClassLoader != null) {
            return contextClassLoader;
        }
        return getClass().getClassLoader();
    }

    public Path getPomPath() {
        return basepath.resolve("pom.xml");
    }

    public Path getBasepath() {
        return basepath;
    }

    public void assertDataSet(String dataSetPathname, DataSetAssertion dataSetAssertion) throws IOException, DataSetException {
        Path basepath = getBasepath();
        Path dataSetPath = basepath.resolve(dataSetPathname);

        DataSetFile dataSetFile = dataSetFileDetection.detect(dataSetPath.toFile());
        IDataSetProducer dataSetProducer = dataSetFile.createProducer();

        CopyDataSetConsumer copyDataSetConsumer = new CopyDataSetConsumer();
        dataSetProducer.setConsumer(copyDataSetConsumer);
        dataSetProducer.produce();
        IDataSet dataSet = copyDataSetConsumer.getDataSet();
        dataSetAssertion.assertDataSet(dataSet);
    }
}
