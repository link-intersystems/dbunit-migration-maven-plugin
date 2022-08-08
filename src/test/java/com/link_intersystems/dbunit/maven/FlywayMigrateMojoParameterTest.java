package com.link_intersystems.dbunit.maven;

import com.link_intersystems.maven.plugin.test.MavenTestProject;
import com.link_intersystems.maven.plugin.test.TestMojo;
import com.link_intersystems.maven.plugin.test.extensions.MojoTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
@ExtendWith(MojoTest.class)
class FlywayMigrateMojoParameterTest {

    @Test
    @MavenTestProject("/minimal-migration-configuration")
    void execute(@TestMojo(goal = "flyway-migrate") FlywayMigrateMojo mojo) {
        Map<String, String> placeholderMap = mojo.flyway.getPlaceholders();

        Map<String, String> expectedPlaceholders = new HashMap<>();
        expectedPlaceholders.put("new_first_name_column_name", "firstname");
        expectedPlaceholders.put("new_last_name_column_name", "lastname");

        assertNull(mojo.flyway.getLocations());

        assertEquals(expectedPlaceholders, placeholderMap);
    }

}