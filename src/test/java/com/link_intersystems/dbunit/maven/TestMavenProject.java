package com.link_intersystems.dbunit.maven;

import com.link_intersystems.dbunit.stream.consumer.CopyDataSetConsumer;
import com.link_intersystems.dbunit.stream.resource.file.DataSetFile;
import com.link_intersystems.dbunit.stream.resource.file.DataSetFileConfig;
import com.link_intersystems.dbunit.stream.resource.file.DataSetFileDetection;
import com.link_intersystems.io.FileScanner;
import com.link_intersystems.io.Unzip;
import com.link_intersystems.maven.StreamingInterpolator;
import org.codehaus.plexus.interpolation.InterpolationException;
import org.codehaus.plexus.interpolation.StringSearchInterpolator;
import org.codehaus.plexus.util.IOUtil;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.stream.IDataSetProducer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class TestMavenProject {

    private FileScanner interpolationFileScanner = new FileScanner();
    private DataSetFileDetection dataSetFileDetection = new DataSetFileDetection();
    private MavenPropertiesValueSource mavenPropertiesValueSource = new MavenPropertiesValueSource();

    private Path basepath;
    private String resourcePath;

    public TestMavenProject(Path basepath, String resourcePath) {
        this.basepath = basepath;
        this.resourcePath = resourcePath;

        interpolationFileScanner.addIncludeFilePattern("**/pom.xml", "pom.xml");
    }

    public void setDataSetFileConfig(DataSetFileConfig dataSetFileConfig) {
        dataSetFileDetection.setDataSetFileConfig(dataSetFileConfig);
    }

    public void create() {
        ClassLoader classLoader = getClassLoader();
        try (InputStream in = classLoader.getResourceAsStream(resourcePath)) {
            Unzip.unzip(in, basepath);
            interpolate(basepath.toFile());
        } catch (IOException | InterpolationException e) {
            throw new RuntimeException(e);
        }
    }

    private void interpolate(File basedir) throws IOException, InterpolationException {
        List<File> files = interpolationFileScanner.scan(basedir);
        for (File file : files) {
            interpolateSingleFile(file);
        }
    }

    private void interpolateSingleFile(File file) throws IOException, InterpolationException {
        File targetFile = new File(file.getParent(), file.getName() + ".tmp");
        try (StreamingInterpolator reader = new StreamingInterpolator(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8), "#{", "}")) {
            reader.addValueSource(mavenPropertiesValueSource);
            try (Writer writer = new OutputStreamWriter(new FileOutputStream(targetFile), StandardCharsets.UTF_8)) {
                IOUtil.copy(reader, writer);
            }
        }

        file.delete();
        targetFile.renameTo(file);
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
