package com.link_intersystems.dbunit.maven;

import com.link_intersystems.dbunit.stream.producer.DataSetProducerSupport;

import java.io.File;
import java.io.IOException;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
interface DataSetProducerSupportMethod {

    public void setFile(DataSetProducerSupport producerSupport, File file) throws IOException;
}
