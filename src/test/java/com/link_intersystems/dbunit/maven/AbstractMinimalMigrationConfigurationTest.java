package com.link_intersystems.dbunit.maven;

import java.nio.file.Path;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class AbstractMinimalMigrationConfigurationTest extends AbstractMojoTest {
    @Override
    protected TestMavenProject createTestMavenProject(Path basepath) {
        return new TestMavenProject(basepath, "minimal-migration-configuration.zip");
    }
}
