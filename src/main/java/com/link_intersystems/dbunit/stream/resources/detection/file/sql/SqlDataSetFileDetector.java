package com.link_intersystems.dbunit.stream.resources.detection.file.sql;

import com.link_intersystems.dbunit.stream.resource.detection.DataSetFileDetector;
import com.link_intersystems.dbunit.stream.resource.file.DataSetFile;

import java.io.File;

import static java.util.Objects.requireNonNull;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class SqlDataSetFileDetector implements DataSetFileDetector {
    private SqlDataSetFileConfig sqlDataSetFileConfig;

    public SqlDataSetFileDetector(SqlDataSetFileConfig sqlDataSetFileConfig) {
        this.sqlDataSetFileConfig = requireNonNull(sqlDataSetFileConfig);
    }

    @Override
    public DataSetFile detect(File filePath) {
        if (filePath.getName().endsWith(".sql")) {
            SqlDataSetFile sqlDataSetFile = new SqlDataSetFile(filePath);
            sqlDataSetFile.setDatabaseDataSetProducerConfig(sqlDataSetFileConfig.getDatabaseDataSetProducerConfig());
            sqlDataSetFile.setBeforeScriptCustomization(sqlDataSetFileConfig.getDatabaseCustomizationConsumer());
            sqlDataSetFile.setJdbcContainerPool(sqlDataSetFileConfig.getJdbcContainerPool());
            sqlDataSetFile.setTableLiteralFormatResolver(sqlDataSetFileConfig.getTableLiteralFormatResolver());
            return sqlDataSetFile;
        }
        return null;
    }
}
