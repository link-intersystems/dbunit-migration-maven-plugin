package com.link_intersystems.dbunit.maven;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
class FlywayMigrateMojoParameterTest extends AbstractMinimalMigrationConfigurationTest {

    @Test
    void execute() throws Exception {
        FlywayMigrateMojo mojo = lookupConfiguredMojo("flyway-migrate");

        Map<String, String> placeholderMap = mojo.flyway.getPlaceholderMap();

        Map<String, String> expectedPlaceholders = new HashMap<>();
        expectedPlaceholders.put("new_first_name_column_name", "firstname");
        expectedPlaceholders.put("new_last_name_column_name", "lastname");

        assertNull(mojo.flyway.getLocations());

        assertEquals(expectedPlaceholders, placeholderMap);
    }
}