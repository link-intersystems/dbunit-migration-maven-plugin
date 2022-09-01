package com.link_intersystems.dbunit.stream.resources.detection.file.sql;

import com.link_intersystems.dbunit.stream.producer.db.DatabaseDataSetProducer;
import com.link_intersystems.dbunit.stream.producer.db.DatabaseDataSetProducerConfig;
import com.link_intersystems.dbunit.testcontainers.JdbcContainer;
import com.link_intersystems.dbunit.testcontainers.consumer.DatabaseCustomizationConsumer;
import com.link_intersystems.dbunit.testcontainers.consumer.DefaultContainerAwareDataSetConsumer;
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
    private SqlScript ddlScript;

    public SqlScriptDataSetProducer(SqlScript ddlScript, SqlScript dataScript) {
        this.ddlScript = requireNonNull(ddlScript);
        this.sqlScript = requireNonNull(dataScript);
    }

    public void setDatabaseDataSetProducerConfig(DatabaseDataSetProducerConfig config) {
        this.config = requireNonNull(config);
    }

    @Override
    public void setConsumer(IDataSetConsumer consumer) {
        this.dataSetConsumer = requireNonNull(consumer);
    }

    public void setDatabaseContainerSupport(JdbcContainerPool containerPool) {
        this.containerPool = requireNonNull(containerPool);
    }

    @Override
    public void produce() throws DataSetException {
        TestContainersLifecycleConsumer testContainersLifecycleConsumer = new TestContainersLifecycleConsumer(containerPool);
        DatabaseCustomizationConsumer databaseCustomizationConsumer = new DatabaseCustomizationConsumer() {

            @Override
            protected void beforeStartDataSet(JdbcContainer jdbcContainer) throws Exception {
                DataSource dataSource = jdbcContainer.getDataSource();
                try (Connection connection = dataSource.getConnection()) {
                    ddlScript.execute(connection);
                    sqlScript.execute(connection);
                }
                super.beforeStartDataSet(jdbcContainer);
            }
        };

        testContainersLifecycleConsumer.setSubsequentConsumer(databaseCustomizationConsumer);

        DefaultContainerAwareDataSetConsumer consumerDelegate = new DefaultContainerAwareDataSetConsumer() {

            @Override
            protected void startDataSet(JdbcContainer jdbcContainer) throws DataSetException {
                super.startDataSet(jdbcContainer);

                produce(jdbcContainer);
            }
        };

        databaseCustomizationConsumer.setSubsequentConsumer(consumerDelegate);

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
