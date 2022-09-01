package com.link_intersystems.dbunit.stream.resources.detection.file.sql;

import com.link_intersystems.dbunit.sql.consumer.SqlScriptWriter;
import com.link_intersystems.dbunit.stream.resource.DataSetResource;
import com.link_intersystems.sql.dialect.DefaultSqlDialect;
import com.link_intersystems.sql.dialect.SqlDialect;
import com.link_intersystems.sql.io.SqlScript;
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
public class SqlDataSetResource implements DataSetResource {

    private final File ddlScript;
    private final File dataScript;
    private Charset charset = StandardCharsets.UTF_8;

    public SqlDataSetResource(File ddlScript, File dataScript) {
        this.ddlScript = requireNonNull(ddlScript);
        this.dataScript = requireNonNull(dataScript);
    }

    @Override
    public IDataSetProducer createProducer() {
        URLScriptResource ddlResource = URLScriptResource.fromFile(this.ddlScript);
        ddlResource.setCharset(charset);
        SqlScript ddlScript = new SqlScript(ddlResource);

        URLScriptResource dataResource = URLScriptResource.fromFile(this.dataScript);
        dataResource.setCharset(charset);
        SqlScript dataScript = new SqlScript(dataResource);

        SqlScriptDataSetProducer scriptDataSetProducer = new SqlScriptDataSetProducer(ddlScript, dataScript);
        return scriptDataSetProducer;
    }

    @Override
    public IDataSetConsumer createConsumer() throws DataSetException {
        SqlDialect dialect = new DefaultSqlDialect();

        try {
            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataScript),
                    charset));
            return new SqlScriptWriter(dialect, writer);
        } catch (FileNotFoundException e) {
            throw new DataSetException(e);
        }
    }
}
