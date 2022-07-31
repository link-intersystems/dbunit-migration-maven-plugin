package com.link_intersystems.dbunit.maven;

import com.link_intersystems.dbunit.table.Row;
import com.link_intersystems.dbunit.table.TableUtil;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class MigratedDataSetAssertion {

    public static void assertDataSetMigrated(IDataSet dataSet) throws DataSetException {
        ITable actorTable = dataSet.getTable("actor");
        assertNotNull(actorTable);
        assertEquals(2, actorTable.getRowCount());
        TableUtil actorUtil = new TableUtil(actorTable);
        Row firstRow = actorUtil.getRow(0);
        assertEquals("PENELOPE", firstRow.getValueByColumnName("firstname"));
        Row secondRow = actorUtil.getRow(1);
        assertEquals("WAHLBERG", secondRow.getValueByColumnName("lastname"));

        ITable languageTable = dataSet.getTable("language");
        assertNotNull(languageTable);
        assertEquals(1, languageTable.getRowCount());

        ITable filmTable = dataSet.getTable("film");
        assertNotNull(filmTable);
        assertEquals(44, filmTable.getRowCount());

        ITable filmDescription = dataSet.getTable("film_description");
        assertNotNull(filmDescription);
        assertEquals(44, filmDescription.getRowCount());

        ITable filmActorTable = dataSet.getTable("film_actor");
        assertNotNull(filmActorTable);
        assertEquals(44, filmActorTable.getRowCount());
    }
}
