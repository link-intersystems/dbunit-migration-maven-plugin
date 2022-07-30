package com.link_intersystems.dbunit.maven;

import com.link_intersystems.dbunit.stream.consumer.CopyDataSetConsumer;
import com.link_intersystems.dbunit.stream.producer.DataSetProducerSupport;
import com.link_intersystems.dbunit.stream.producer.DefaultDataSetProducerSupport;
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

    private Path basepath;
    private String resourcePath;

    public TestMavenProject(Path basepath, String resourcePath) {
        this.basepath = basepath;
        this.resourcePath = resourcePath;
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

    public void assertFlatXml(String dataSetPathname, DataSetAssertion dataSetAssertion) throws IOException, DataSetException {
        assertDataSet(dataSetPathname, DataSetProducerSupport::setFlatXmlProducer, dataSetAssertion);
    }

    public void assertXml(String dataSetPathname, DataSetAssertion dataSetAssertion) throws IOException, DataSetException {
        assertDataSet(dataSetPathname, DataSetProducerSupport::setXmlProducer, dataSetAssertion);
    }

    public void assertCsv(String dataSetPathname, DataSetAssertion dataSetAssertion) throws IOException, DataSetException {
        assertDataSet(dataSetPathname, DataSetProducerSupport::setCsvProducer, dataSetAssertion);
    }

    public void assertDataSet(String dataSetPathname, DataSetProducerSupportMethod producerMethod, DataSetAssertion dataSetAssertion) throws IOException, DataSetException {
        Path basepath = getBasepath();
        Path dataSetPath = basepath.resolve(dataSetPathname);
        DefaultDataSetProducerSupport producerSupport = new DefaultDataSetProducerSupport();
        producerMethod.setFile(producerSupport, dataSetPath.toFile());
        IDataSetProducer dataSetProducer = producerSupport.getDataSetProducer();

        CopyDataSetConsumer copyDataSetConsumer = new CopyDataSetConsumer();
        dataSetProducer.setConsumer(copyDataSetConsumer);
        dataSetProducer.produce();
        IDataSet dataSet = copyDataSetConsumer.getDataSet();
        dataSetAssertion.assertDataSet(dataSet);
    }
}
