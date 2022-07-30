package com.link_intersystems.dbunit.maven;

import java.util.Arrays;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class Flyway {

    private Placeholder[] placeholders = new Placeholder[0];

    public void setPlaceholders(Placeholder[] placeholders) {
        this.placeholders = placeholders;
    }

    public Placeholder[] getPlaceholders() {
        return placeholders;
    }

    public Map<String, String> getPlaceholderMap() {
        return Arrays.stream(placeholders)
                .collect(toMap(
                                Placeholder::getName,
                                Placeholder::getReplacement
                        )
                );
    }
}
