package com.link_intersystems.dbunit.maven;

import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;

import java.io.IOException;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public interface DataSetAssertion {

    public void assertDataSet(IDataSet dataSet) throws DataSetException, IOException;
}
