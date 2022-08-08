package com.link_intersystems.dbunit.maven;

import org.codehaus.plexus.interpolation.AbstractValueSource;

import java.io.IOException;
import java.util.Properties;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class MavenPropertiesValueSource extends AbstractValueSource {
    Properties properties = new Properties();

    protected MavenPropertiesValueSource() {
        super(false);

        try {
            properties.load(MavenTestProjectAssertions.class.getResourceAsStream("/maven.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object getValue(String expression) {
        return properties.getProperty(expression);
    }
}
