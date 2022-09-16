package com.link_intersystems.dbunit.migration.datasets;


import com.link_intersystems.dbunit.stream.consumer.sql.DefaultTableLiteralFormatResolver;
import com.link_intersystems.dbunit.stream.consumer.sql.TableLiteralFormatResolver;

import java.text.MessageFormat;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class SqlConfig {

    private String tableLiteralFormatResolverClassName;

    public void setTableLiteralFormatResolverClassName(String tableLiteralFormatResolverClassName) {
        this.tableLiteralFormatResolverClassName = tableLiteralFormatResolverClassName;
    }

    public String getTableLiteralFormatResolverClassName() {
        return tableLiteralFormatResolverClassName;
    }

    public TableLiteralFormatResolver getTableLiteralFormatResolver() {
        if (tableLiteralFormatResolverClassName != null) {
            try {
                Class<?> tableLiteralFormatResolverClass = Class.forName(getTableLiteralFormatResolverClassName());
                return (TableLiteralFormatResolver) tableLiteralFormatResolverClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                String msg = MessageFormat.format("Unable to create an instance of {0}", tableLiteralFormatResolverClassName);
                throw new RuntimeException(msg, e);
            }
        }
        return new DefaultTableLiteralFormatResolver();
    }
}
