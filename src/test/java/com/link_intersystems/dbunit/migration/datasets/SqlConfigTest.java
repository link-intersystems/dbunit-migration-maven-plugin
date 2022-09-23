package com.link_intersystems.dbunit.migration.datasets;

import com.link_intersystems.dbunit.stream.consumer.sql.DefaultTableLiteralFormatResolver;
import com.link_intersystems.dbunit.stream.consumer.sql.TableLiteralFormatResolver;
import com.link_intersystems.sql.statement.TableLiteralFormat;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.ITableMetaData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
class SqlConfigTest {

    static class SomeTableLiteralFormatResolver implements TableLiteralFormatResolver {

        @Override
        public TableLiteralFormat getTableLiteralFormat(ITableMetaData iTableMetaData) throws DataSetException {
            return null;
        }
    }

    @Test
    void getTableLiteralFormatResolver() {
        SqlConfig sqlConfig = new SqlConfig();
        sqlConfig.setTableLiteralFormatResolverClassName("com.link_intersystems.dbunit.migration.datasets.SqlConfigTest$SomeTableLiteralFormatResolver");

        TableLiteralFormatResolver tableLiteralFormatResolver = sqlConfig.getTableLiteralFormatResolver();
        assertTrue(SomeTableLiteralFormatResolver.class.isInstance(tableLiteralFormatResolver));
    }

    @Test
    void getDefaultTableLiteralFormatResolver() {
        SqlConfig sqlConfig = new SqlConfig();
        TableLiteralFormatResolver tableLiteralFormatResolver = sqlConfig.getTableLiteralFormatResolver();
        assertTrue(DefaultTableLiteralFormatResolver.class.isInstance(tableLiteralFormatResolver));
    }

    @Test
    void getUnknownTableLiteralFormatResolver() {
        SqlConfig sqlConfig = new SqlConfig();
        sqlConfig.setTableLiteralFormatResolverClassName("unknownClassName");

        assertThrows(RuntimeException.class, () -> sqlConfig.getTableLiteralFormatResolver());
    }
}