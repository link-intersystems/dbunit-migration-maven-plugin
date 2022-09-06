package com.link_intersystems.dbunit.migration.datasets;

import com.link_intersystems.dbunit.sql.consumer.DefaultTableLiteralFormatResolver;
import com.link_intersystems.dbunit.sql.consumer.TableLiteralFormatResolver;

import java.lang.reflect.InvocationTargetException;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class SqlConfig {

    private String tableLiteralFormatResolverClassName;

    public TableLiteralFormatResolver getTableLiteralFormatResolver() {
        if(tableLiteralFormatResolverClassName != null){
            try {
                Class<?> tableLiteralFormatResolverClass = Class.forName(this.tableLiteralFormatResolverClassName);
                return (TableLiteralFormatResolver) tableLiteralFormatResolverClass.getDeclaredConstructor().newInstance();
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return new DefaultTableLiteralFormatResolver();
    }
}
