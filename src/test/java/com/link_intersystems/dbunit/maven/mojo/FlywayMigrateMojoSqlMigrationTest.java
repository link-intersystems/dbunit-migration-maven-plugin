package com.link_intersystems.dbunit.maven.mojo;

import com.link_intersystems.dbunit.migration.flyway.FlywayMigration;
import com.link_intersystems.dbunit.testcontainers.DBunitJdbcContainer;
import com.link_intersystems.dbunit.testcontainers.DatabaseContainerSupport;
import com.link_intersystems.maven.plugin.test.MavenTestProject;
import com.link_intersystems.maven.plugin.test.TestMojo;
import com.link_intersystems.maven.plugin.test.extensions.MojoTest;
import com.link_intersystems.sql.io.SqlScript;
import com.link_intersystems.sql.io.StatementReader;
import com.link_intersystems.sql.io.StringScriptResource;
import org.apache.commons.io.IOUtils;
import org.apache.maven.project.MavenProject;
import org.dbunit.dataset.DataSetException;
import org.flywaydb.core.api.MigrationVersion;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.containers.JdbcDatabaseContainer;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
@ExtendWith(MojoTest.class)
@MavenTestProject("/sql-migration")
class FlywayMigrateMojoSqlMigrationTest {

    @Test
    void execute(@TestMojo(goal = "flyway-migrate", debugEnabled = true) FlywayMigrateMojo mojo, MavenProject mavenProject) throws Exception {
        mojo.execute();

        String outputDirectory = mavenProject.getBuild().getDirectory();

        String migratedScript = IOUtils.toString(new FileInputStream(new File(outputDirectory, "sql/sakila.sql")));

        assertScriptExecutable(migratedScript);
        assertMigratedScript(migratedScript);
    }

    private static void assertScriptExecutable(String migratedScript) throws DataSetException, SQLException {
        DatabaseContainerSupport postgresSupport = DatabaseContainerSupport.getDatabaseContainerSupport("postgres");
        JdbcDatabaseContainer<?> jdbcDatabaseContainer = postgresSupport.create();
        DBunitJdbcContainer dBunitJdbcContainer = new DBunitJdbcContainer(jdbcDatabaseContainer);
        dBunitJdbcContainer.start();
        DataSource dataSource = dBunitJdbcContainer.getDataSource();

        SqlScript sqlScript = new SqlScript(new StringScriptResource(migratedScript));
        FluentConfiguration configuration = new FluentConfiguration().locations("sql-migration/src/main/resources/db/migration");
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("new_first_name_column_name", "firstname");
        placeholders.put("new_last_name_column_name", "lastname");
        configuration.placeholders(placeholders);
        FlywayMigration flywayMigration = new FlywayMigration(configuration);
        flywayMigration.execute(dataSource, MigrationVersion.LATEST);
        try (Connection connection = dataSource.getConnection()) {
            sqlScript.execute(connection);
        }
    }

    private static void assertMigratedScript(String migratedScript) throws IOException {
        StatementReader statementReader = new StatementReader(new StringReader(migratedScript));

        assertTrue(statementReader.hasNext());
        assertEquals("insert into language (language_id, name, last_update) values ('11111111-1111-1111-1111-111111111111', 'English', '2006-02-15 05:02:19')", statementReader.next());

        assertTrue(statementReader.hasNext());
        assertEquals("insert into film (film_id, title, release_year, language_id, original_language_id, rental_duration, rental_rate, length, replacement_cost, rating, special_features, last_update) values (1, 'ACADEMY DINOSAUR', '2006-01-01', '11111111-1111-1111-1111-111111111111', null, 6, 0.99, 86, 20.99, 'PG', 'Deleted Scenes,Behind the Scenes', '2006-02-15 05:03:42')", statementReader.next());

        assertTrue(statementReader.hasNext());
        assertEquals("insert into film (film_id, title, release_year, language_id, original_language_id, rental_duration, rental_rate, length, replacement_cost, rating, special_features, last_update) values (23, 'ANACONDA CONFESSIONS', '2006-01-01', '11111111-1111-1111-1111-111111111111', null, 3, 0.99, 92, 9.99, 'R', 'Trailers,Deleted Scenes', '2006-02-15 05:03:42')", statementReader.next());

        assertTrue(statementReader.hasNext());
        assertEquals("insert into film_description (film_id, description) values (1, 'A Epic Drama of a Feminist And a Mad Scientist who must Battle a Teacher in The Canadian Rockies')", statementReader.next());

        assertTrue(statementReader.hasNext());
        assertEquals("insert into film_description (film_id, description) values (23, 'A Lacklusture Display of a Dentist And a Dentist who must Fight a Girl in Australia')", statementReader.next());

        assertTrue(statementReader.hasNext());
        assertEquals("insert into actor (actor_id, firstname, lastname, last_update) values (1, 'PENELOPE', 'GUINESS', '2006-02-15 04:34:33')", statementReader.next());

        assertTrue(statementReader.hasNext());
        assertEquals("insert into actor (actor_id, firstname, lastname, last_update) values (2, 'NICK', 'WAHLBERG', '2006-02-15 04:34:33')", statementReader.next());

        assertTrue(statementReader.hasNext());
        assertEquals("insert into film_actor (actor_id, film_id, last_update) values (1, 1, '2006-02-15 05:05:03')", statementReader.next());

        assertTrue(statementReader.hasNext());
        assertEquals("insert into film_actor (actor_id, film_id, last_update) values (1, 23, '2006-02-15 05:05:03')", statementReader.next());

        assertFalse(statementReader.hasNext());
    }


}