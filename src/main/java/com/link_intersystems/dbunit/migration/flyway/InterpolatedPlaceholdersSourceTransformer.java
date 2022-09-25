package com.link_intersystems.dbunit.migration.flyway;

import com.link_intersystems.dbunit.migration.DataSourceProperties;
import org.codehaus.plexus.interpolation.StringSearchInterpolator;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class InterpolatedPlaceholdersSourceTransformer implements PlaceholdersSourceTransformer {
    @Override
    public PlaceholdersSource transform(PlaceholdersSource placeholdersSource, DataSourceProperties dataSourceProperties) {
        StringSearchInterpolator stringSearchInterpolator = new StringSearchInterpolator("{{", "}}");
        stringSearchInterpolator.addValueSource(new DataSourcePropertiesValueSource(dataSourceProperties));
        return new InterpolatedPlaceholdersSource(stringSearchInterpolator, placeholdersSource);
    }
}
