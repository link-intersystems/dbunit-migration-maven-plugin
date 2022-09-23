package com.link_intersystems.dbunit;

import com.link_intersystems.dbunit.stream.consumer.sql.DefaultTableLiteralFormatResolver;
import com.link_intersystems.sql.format.DateLiteralFormat;
import com.link_intersystems.sql.format.DefaultTableLiteralFormat;
import com.link_intersystems.sql.statement.TableLiteralFormat;
import org.apache.commons.codec.Decoder;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.ITableMetaData;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class CustomTableLiteralFormatResolver extends DefaultTableLiteralFormatResolver {

    private final DefaultTableLiteralFormat tableLiteralFormat;

    public CustomTableLiteralFormatResolver() {
        // just to ensure that the compile classpath is resolved too.
        Class<Decoder> decoderClass = Decoder.class;
        System.out.println(decoderClass);
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        DateLiteralFormat dateLiteralFormat = new DateLiteralFormat(formatter, ZoneId.systemDefault());
        tableLiteralFormat = new DefaultTableLiteralFormat();
        tableLiteralFormat.addLiteralFormat("timeColumn", dateLiteralFormat);
    }

    @Override
    public TableLiteralFormat getTableLiteralFormat(ITableMetaData tableMetaData) throws DataSetException {

        return tableLiteralFormat;
    }
}
