package com.link_intersystems.dbunit.maven;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
class FlywayMigrateMojoParameterTest extends AbstractMojoTest {

    @Override
    protected TestMavenProject createTestMavenProject(Path basepath) {
        return new TestMavenProject(basepath, "minimal-migration-configuration.zip");
    }

    @Test
    void execute() throws Exception {
        FlywayMigrateMojo mojo = lookupConfiguredMojo("flyway-migrate");

        Map<String, String> placeholderMap = mojo.flyway.getPlaceholders();

        Map<String, String> expectedPlaceholders = new HashMap<>();
        expectedPlaceholders.put("new_first_name_column_name", "firstname");
        expectedPlaceholders.put("new_last_name_column_name", "lastname");

        assertNull(mojo.flyway.getLocations());

        assertEquals(expectedPlaceholders, placeholderMap);
    }

}