package com.link_intersystems.dbunit.stream.resources.detection.file.sql;

import com.link_intersystems.dbunit.sql.consumer.DefaultTableLiteralFormatResolver;
import com.link_intersystems.dbunit.sql.consumer.SqlScriptDataSetConsumer;
import com.link_intersystems.dbunit.sql.consumer.TableLiteralFormatResolver;
import com.link_intersystems.dbunit.stream.producer.db.DatabaseDataSetProducerConfig;
import com.link_intersystems.dbunit.stream.resource.file.DataSetFile;
import com.link_intersystems.dbunit.testcontainers.consumer.DatabaseCustomizationConsumer;
import com.link_intersystems.dbunit.testcontainers.pool.JdbcContainerPool;
import com.link_intersystems.sql.io.SqlScript;
import com.link_intersystems.sql.io.URLScriptResource;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.stream.IDataSetConsumer;
import org.dbunit.dataset.stream.IDataSetProducer;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static java.util.Objects.requireNonNull;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class SqlDataSetFile implements DataSetFile {

    private File dataScript;
    private Charset charset = StandardCharsets.UTF_8;
    private DatabaseCustomizationConsumer beforeScriptCustomization;
    private DatabaseDataSetProducerConfig databaseDataSetProducerConfig = new DatabaseDataSetProducerConfig();
    private JdbcContainerPool jdbcContainerPool;
    private TableLiteralFormatResolver tableLiteralFormatResolver = new DefaultTableLiteralFormatResolver();


    public SqlDataSetFile(File sqlScript) {
        this.dataScript = requireNonNull(sqlScript);
    }

    private SqlDataSetFile(SqlDataSetFile sqlDataSetFile) {
        dataScript = sqlDataSetFile.dataScript;
        charset = sqlDataSetFile.charset;
        beforeScriptCustomization = sqlDataSetFile.beforeScriptCustomization;
        databaseDataSetProducerConfig = sqlDataSetFile.databaseDataSetProducerConfig;
        jdbcContainerPool = sqlDataSetFile.jdbcContainerPool;
    }

    public void setTableLiteralFormatResolver(TableLiteralFormatResolver tableLiteralFormatResolver) {
        this.tableLiteralFormatResolver = requireNonNull(tableLiteralFormatResolver);
    }

    public void setCharset(Charset charset) {
        this.charset = requireNonNull(charset);
    }

    public void setJdbcContainerPool(JdbcContainerPool jdbcContainerPool) {
        this.jdbcContainerPool = jdbcContainerPool;
    }

    public void setBeforeScriptCustomization(DatabaseCustomizationConsumer beforeScriptCustomization) {
        this.beforeScriptCustomization = beforeScriptCustomization;
    }

    public void setDatabaseDataSetProducerConfig(DatabaseDataSetProducerConfig databaseDataSetProducerConfig) {
        this.databaseDataSetProducerConfig = databaseDataSetProducerConfig;
    }

    @Override
    public IDataSetProducer createProducer() {
        URLScriptResource dataResource = URLScriptResource.fromFile(this.dataScript);
        dataResource.setCharset(charset);
        SqlScript dataScript = new SqlScript(dataResource);

        SqlScriptDataSetProducer scriptDataSetProducer = new SqlScriptDataSetProducer(dataScript);
        scriptDataSetProducer.setDatabaseCustomizationConsumer(beforeScriptCustomization);
        scriptDataSetProducer.setDatabaseDataSetProducerConfig(databaseDataSetProducerConfig);
        scriptDataSetProducer.setJdbcContainerPool(jdbcContainerPool);

        return scriptDataSetProducer;
    }

    @Override
    public IDataSetConsumer createConsumer() throws DataSetException {
        try {
            FileOutputStream outputStream = new FileOutputStream(dataScript);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, charset);
            Writer writer = new BufferedWriter(outputStreamWriter);
            SqlScriptDataSetConsumer sqlScriptDataSetConsumer = new SqlScriptDataSetConsumer(writer);
            String schema = databaseDataSetProducerConfig.getSchema();
            sqlScriptDataSetConsumer.setSchema(schema);
            sqlScriptDataSetConsumer.setTableLiteralFormatResolver(tableLiteralFormatResolver);
            return sqlScriptDataSetConsumer;
        } catch (FileNotFoundException e) {
            throw new DataSetException(e);
        }
    }

    @Override
    public DataSetFile withNewFile(File file) {
        File parentFile = file.getParentFile();
        parentFile.mkdirs();
        SqlDataSetFile sqlDataSetFile = new SqlDataSetFile(this);
        sqlDataSetFile.dataScript = file;
        return sqlDataSetFile;
    }

    @Override
    public File getFile() {
        return dataScript;
    }

    @Override
    public String toString() {
        return getFile().toString();
    }

}
