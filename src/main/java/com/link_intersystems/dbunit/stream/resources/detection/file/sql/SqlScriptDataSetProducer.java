package com.link_intersystems.dbunit.stream.resources.detection.file.sql;

import com.link_intersystems.dbunit.stream.consumer.DataSetConsumerPipe;
import com.link_intersystems.dbunit.stream.producer.db.DatabaseDataSetProducer;
import com.link_intersystems.dbunit.stream.producer.db.DatabaseDataSetProducerConfig;
import com.link_intersystems.dbunit.testcontainers.JdbcContainer;
import com.link_intersystems.dbunit.testcontainers.consumer.DatabaseCustomizationConsumer;
import com.link_intersystems.dbunit.testcontainers.consumer.JdbcContainerAwareDataSetConsumer;
import com.link_intersystems.dbunit.testcontainers.consumer.TestContainersLifecycleConsumer;
import com.link_intersystems.dbunit.testcontainers.pool.JdbcContainerPool;
import com.link_intersystems.sql.io.SqlScript;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.stream.DefaultConsumer;
import org.dbunit.dataset.stream.IDataSetConsumer;
import org.dbunit.dataset.stream.IDataSetProducer;

import javax.sql.DataSource;
import java.sql.Connection;

import static java.util.Objects.requireNonNull;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class SqlScriptDataSetProducer implements IDataSetProducer {

    private IDataSetConsumer dataSetConsumer = new DefaultConsumer();
    private JdbcContainerPool containerPool = null;

    private SqlScript sqlScript;
    private DatabaseDataSetProducerConfig config = new DatabaseDataSetProducerConfig();
    private DatabaseCustomizationConsumer databaseCustomizationConsumer;

    public SqlScriptDataSetProducer(SqlScript sqlScript) {
        this.sqlScript = requireNonNull(sqlScript);
    }

    public void setDatabaseCustomizationConsumer(DatabaseCustomizationConsumer databaseCustomizationConsumer) {
        this.databaseCustomizationConsumer = databaseCustomizationConsumer;
    }

    public void setDatabaseDataSetProducerConfig(DatabaseDataSetProducerConfig config) {
        this.config = requireNonNull(config);
    }

    @Override
    public void setConsumer(IDataSetConsumer consumer) {
        this.dataSetConsumer = requireNonNull(consumer);
    }

    public void setJdbcContainerPool(JdbcContainerPool containerPool) {
        this.containerPool = requireNonNull(containerPool);
    }

    @Override
    public void produce() throws DataSetException {
        TestContainersLifecycleConsumer testContainersLifecycleConsumer = new TestContainersLifecycleConsumer(containerPool);

        DataSetConsumerPipe dataSetConsumerPipe = new DataSetConsumerPipe();

        if (databaseCustomizationConsumer != null) {
            dataSetConsumerPipe.add(databaseCustomizationConsumer);
        }

        dataSetConsumerPipe.add(new DatabaseCustomizationConsumer() {

            @Override
            protected void beforeStartDataSet(JdbcContainer jdbcContainer) throws Exception {
                DataSource dataSource = jdbcContainer.getDataSource();
                try (Connection connection = dataSource.getConnection()) {
                    sqlScript.execute(connection);
                }
                super.beforeStartDataSet(jdbcContainer);
            }
        });

        testContainersLifecycleConsumer.setSubsequentConsumer(dataSetConsumerPipe);

        dataSetConsumerPipe.add(new JdbcContainerAwareDataSetConsumer() {

            @Override
            protected void startDataSet(JdbcContainer jdbcContainer) throws DataSetException {
                super.startDataSet(jdbcContainer);

                produce(jdbcContainer);
            }
        });

        try {
            testContainersLifecycleConsumer.startDataSet();
        } finally {
            testContainersLifecycleConsumer.endDataSet();
        }
    }

    protected void produce(JdbcContainer jdbcContainer) throws DataSetException {
        IDatabaseConnection databaseConnection = jdbcContainer.getDatabaseConnection();

        DatabaseDataSetProducer dataSetProducer = new DatabaseDataSetProducer(databaseConnection, config);

        dataSetProducer.setConsumer(dataSetConsumer);

        dataSetProducer.produce();
    }
}
