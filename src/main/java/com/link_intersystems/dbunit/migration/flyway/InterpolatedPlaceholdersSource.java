package com.link_intersystems.dbunit.migration.flyway;

import org.codehaus.plexus.interpolation.InterpolationException;
import org.codehaus.plexus.interpolation.Interpolator;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class InterpolatedPlaceholdersSource implements PlaceholdersSource {

    private final Interpolator interpolator;
    private PlaceholdersSource placeholdersSource;

    public InterpolatedPlaceholdersSource(Interpolator interpolator, PlaceholdersSource placeholdersSource) {
        this.interpolator = requireNonNull(interpolator);
        this.placeholdersSource = requireNonNull(placeholdersSource);
    }

    @Override
    public Map<String, String> getPlaceholders() {
        Map<String, String> placeholders = placeholdersSource.getPlaceholders();
        try {
            return iterpolate(interpolator, placeholders);
        } catch (InterpolationException e) {
            throw new RuntimeException("Unable to interpolate placeholders.", e);
        }
    }

    protected Map<String, String> iterpolate(Interpolator interpolator, Map<String, String> placeholders) throws InterpolationException {
        Map<String, String> interpolatedPlaceholders = new HashMap<>();

        for (Map.Entry<String, String> placeholderEntry : placeholders.entrySet()) {
            String placeholderName = placeholderEntry.getKey();
            String placeholderValue = placeholderEntry.getValue();
            String interpolatedPlaceholderValue = interpolator.interpolate(placeholderValue);
            interpolatedPlaceholders.put(placeholderName, interpolatedPlaceholderValue);
        }

        return interpolatedPlaceholders;
    }
}
