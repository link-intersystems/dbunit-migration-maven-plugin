package com.link_intersystems.dbunit.stream.resources.detection.file.sql;

import com.link_intersystems.dbunit.stream.resource.detection.DataSetFileDetector;
import com.link_intersystems.dbunit.stream.resource.detection.DataSetFileDetectorProvider;
import com.link_intersystems.util.config.properties.ConfigProperties;
import com.link_intersystems.util.config.properties.ConfigPropertiesProxy;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class SqlDataSetFileDetectionProvider implements DataSetFileDetectorProvider {
    @Override
    public DataSetFileDetector getDataSetFileDetector(ConfigProperties configProperties) {
        SqlDataSetFileConfig sqlDataSetFileConfig = ConfigPropertiesProxy.create(configProperties, SqlDataSetFileConfig.class);
        return new SqlDataSetFileDetector(sqlDataSetFileConfig);
    }
}
