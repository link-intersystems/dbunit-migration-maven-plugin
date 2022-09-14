package com.link_intersystems.dbunit.stream.resources.detection.file.sql;


import com.link_intersystems.dbunit.stream.consumer.sql.SqlScriptDataSetConsumer;
import com.link_intersystems.dbunit.stream.producer.sql.SqlScriptDataSetProducer;
import com.link_intersystems.dbunit.testcontainers.DBunitJdbcContainer;
import com.link_intersystems.dbunit.testcontainers.DatabaseContainerSupport;
import com.link_intersystems.dbunit.testcontainers.TestcontainersDatabaseConnectionBorrower;
import com.link_intersystems.dbunit.testcontainers.pool.SingleJdbcContainerPool;
import com.link_intersystems.sql.io.SqlScript;
import com.link_intersystems.sql.io.StatementReader;
import com.link_intersystems.sql.io.URLScriptResource;
import org.dbunit.dataset.DataSetException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
class SqlScriptDataSetProducerTest {

    @Test
    void produce() throws DataSetException, IOException {
        URLScriptResource urlScriptResource = new URLScriptResource(SqlScriptDataSetProducerTest.class.getResource("sakila.sql"));
        SqlScript sqlScript = new SqlScript(urlScriptResource);
        SqlScript ddlScript = new SqlScript(new URLScriptResource(SqlScriptDataSetProducerTest.class.getResource("sakila-ddl.sql")));
        SingleJdbcContainerPool singleJdbcContainerPool = new SingleJdbcContainerPool(new DBunitJdbcContainer(DatabaseContainerSupport.getDatabaseContainerSupport("postgres:latest")));
        TestcontainersDatabaseConnectionBorrower connectionBorrower = new TestcontainersDatabaseConnectionBorrower(singleJdbcContainerPool, jdbcContainer -> {
            try (Connection connection = jdbcContainer.getDatabaseConnection().getConnection()) {
                ddlScript.execute(connection);
            }
        });

        SqlScriptDataSetProducer producer = new SqlScriptDataSetProducer(connectionBorrower, sqlScript);

        StringWriter sw = new StringWriter();
        producer.setConsumer(new SqlScriptDataSetConsumer(sw));
        producer.produce();


        StatementReader statementReader = new StatementReader(new StringReader(sw.toString()));

        assertTrue(statementReader.hasNext());
        assertEquals("insert into actor (actor_id, first_name, last_name, last_update) values (1, 'PENELOPE', 'GUINESS', '2006-02-15 04:34:33')", statementReader.next());

        assertTrue(statementReader.hasNext());
        assertEquals("insert into actor (actor_id, first_name, last_name, last_update) values (2, 'NICK', 'WAHLBERG', '2006-02-15 04:34:33')", statementReader.next());

        assertTrue(statementReader.hasNext());

        assertEquals("insert into film (film_id, title, description, release_year, language_id, original_language_id, rental_duration, rental_rate, length, replacement_cost, rating, special_features, last_update) values (1, 'ACADEMY DINOSAUR', 'A Epic Drama of a Feminist And a Mad Scientist who must Battle a Teacher in The Canadian Rockies', '2006-01-01', 1, null, 6, 0.99, 86, 20.99, 'PG', 'Deleted Scenes,Behind the Scenes', '2006-02-15 05:03:42')", statementReader.next());

        assertTrue(statementReader.hasNext());
        assertEquals("insert into film (film_id, title, description, release_year, language_id, original_language_id, rental_duration, rental_rate, length, replacement_cost, rating, special_features, last_update) values (23, 'ANACONDA CONFESSIONS', 'A Lacklusture Display of a Dentist And a Dentist who must Fight a Girl in Australia', '2006-01-01', 1, null, 3, 0.99, 92, 9.99, 'R', 'Trailers,Deleted Scenes', '2006-02-15 05:03:42')", statementReader.next());

        assertTrue(statementReader.hasNext());
        assertEquals("insert into film_actor (actor_id, film_id, last_update) values (1, 1, '2006-02-15 05:05:03')", statementReader.next());

        assertTrue(statementReader.hasNext());
        assertEquals("insert into film_actor (actor_id, film_id, last_update) values (1, 23, '2006-02-15 05:05:03')", statementReader.next());

        assertTrue(statementReader.hasNext());
        assertEquals("insert into language (language_id, name, last_update) values (1, 'English', '2006-02-15 05:02:19')", statementReader.next());

        assertFalse(statementReader.hasNext());
    }
}