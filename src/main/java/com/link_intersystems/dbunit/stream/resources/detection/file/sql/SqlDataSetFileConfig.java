package com.link_intersystems.dbunit.stream.resources.detection.file.sql;

import com.link_intersystems.dbunit.sql.consumer.DefaultTableLiteralFormatResolver;
import com.link_intersystems.dbunit.sql.consumer.TableLiteralFormatResolver;
import com.link_intersystems.dbunit.stream.producer.db.DatabaseDataSetProducerConfig;
import com.link_intersystems.dbunit.testcontainers.consumer.DatabaseCustomizationConsumer;
import com.link_intersystems.dbunit.testcontainers.pool.JdbcContainerPool;
import com.link_intersystems.util.config.properties.ConfigProperty;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public interface SqlDataSetFileConfig {

    public static final ConfigProperty<DatabaseDataSetProducerConfig> DATABASE_DATA_SET_PRODUCER_CONFIG = ConfigProperty.named("databaseDataSetProducerConfig").typed(DatabaseDataSetProducerConfig.class);
    public static final ConfigProperty<DatabaseCustomizationConsumer> DATABASE_CUSTOMIZATION_CONSUMER = ConfigProperty.named("databaseCustomizationConsumer").typed(DatabaseCustomizationConsumer.class);
    public static final ConfigProperty<JdbcContainerPool> JDBC_CONTAINER_POOL = ConfigProperty.named("jdbcContainerPool").typed(JdbcContainerPool.class);
    public static final ConfigProperty<TableLiteralFormatResolver> TABLE_LITERAL_FORMAT_RESOLVER = ConfigProperty.named("tableLiteralFormatResolver").typed(TableLiteralFormatResolver.class).withDefaultValue(new DefaultTableLiteralFormatResolver());

    public DatabaseDataSetProducerConfig getDatabaseDataSetProducerConfig();


    public DatabaseCustomizationConsumer getDatabaseCustomizationConsumer();

    public JdbcContainerPool getJdbcContainerPool();

    public TableLiteralFormatResolver getTableLiteralFormatResolver();

}
