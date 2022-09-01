package com.link_intersystems.dbunit.stream.resources.detection.file.sql;

import com.link_intersystems.dbunit.stream.resource.detection.DataSetFileDetector;
import com.link_intersystems.dbunit.stream.resource.file.DataSetFile;

import java.io.File;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class SqlDataSetFileDetector implements DataSetFileDetector {
    @Override
    public DataSetFile detect(File filePath) {
        if (filePath.getName().endsWith(".sql")) {

        }
        return null;
    }
}
