package com.link_intersystems.dbunit.migration.flyway;

import com.link_intersystems.dbunit.migration.DataSourceProperties;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
class InterpolatedPlaceholdersSourceTransformerTest {

    @Test
    void transform() {
        InterpolatedPlaceholdersSourceTransformer transformer = new InterpolatedPlaceholdersSourceTransformer();

        PlaceholdersSource placeholdersSource = PlaceholdersSource.fromMap(p -> {
            p.put("a", "{{username}}");
        });
        DataSourceProperties dataSourceProperties = mock(DataSourceProperties.class);
        when(dataSourceProperties.getUsername()).thenReturn("john_doe");

        PlaceholdersSource transformedPlaceholdersSource = transformer.transform(placeholdersSource, dataSourceProperties);

        Map<String, String> placeholders = transformedPlaceholdersSource.getPlaceholders();

        assertEquals("john_doe", placeholders.get("a"));
    }
}