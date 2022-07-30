package com.link_intersystems.dbunit.maven;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
class FlywayMigrateMojoParameterTest extends AbstractMinimalMigrationConfigurationTest {

    @Test
    void execute() throws Exception {
        FlywayMigrateMojo mojo = lookupConfiguredMojo("flyway-migrate");

        FlywayConfig flywayConfig = mojo.getFlywayConfig();
        Map<String, String> placeholderMap = flywayConfig.getPlaceholderMap();


        Map<String, String> expectedPlaceholders = new HashMap<>();
        expectedPlaceholders.put("new_first_name_column_name", "firstname");
        expectedPlaceholders.put("new_last_name_column_name", "lastname");

        Assertions.assertNull(flywayConfig.getLocations());

        Assertions.assertEquals(expectedPlaceholders, placeholderMap);

    }
}