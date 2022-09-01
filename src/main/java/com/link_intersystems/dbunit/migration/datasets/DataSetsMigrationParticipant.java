package com.link_intersystems.dbunit.migration.datasets;

import com.link_intersystems.dbunit.migration.resources.*;
import com.link_intersystems.dbunit.stream.consumer.ChainableDataSetConsumer;
import com.link_intersystems.dbunit.stream.consumer.ExternalSortTableConsumer;
import com.link_intersystems.dbunit.stream.resource.file.DataSetFileConfig;
import com.link_intersystems.dbunit.stream.resource.file.xml.FlatXmlDataSetFileConfig;
import com.link_intersystems.dbunit.table.DefaultTableOrder;
import com.link_intersystems.dbunit.table.TableOrder;
import com.link_intersystems.util.config.properties.ConfigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class DataSetsMigrationParticipant {

    private Logger logger = LoggerFactory.getLogger(DataSetsMigrationParticipant.class);

    private DataSetsConfig dataSetsConfig;

    public DataSetsMigrationParticipant(DataSetsConfig dataSetsConfig) {
        this.dataSetsConfig = dataSetsConfig;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public void applyConfigProperties(ConfigProperties config) {
        config.setProperty(DataSetFileConfig.CHARSET, dataSetsConfig.getCharset());
        config.setProperty(FlatXmlDataSetFileConfig.COLUMN_SENSING, dataSetsConfig.isColumnSensing());
    }

    public void configure(DataSetResourcesMigration dataSetResourcesMigration) {
        TargetDataSetResourceSupplier targetDataSetResourceSupplier = getTargetDataSetResourceSupplier(dataSetsConfig);
        dataSetResourcesMigration.setTargetDataSetResourceSupplier(targetDataSetResourceSupplier);
        dataSetResourcesMigration.setBeforeMigrationSupplier(this::getBeforeMigrationTransformer);
        dataSetResourcesMigration.setMigrationListener(getMigrationListener());
        dataSetResourcesMigration.setAfterMigrationSupplier(this::getAfterMigrationTransformer);
    }

    protected TargetDataSetResourceSupplier getTargetDataSetResourceSupplier(DataSetsConfig dataSets) {
        Path basepath = dataSets.getBasepath();
        Path targetPath = dataSets.getTargetBasepath();
        return new RebaseTargetpathDataSetResourceSupplier(basepath, targetPath);
    }

    protected ChainableDataSetConsumer getBeforeMigrationTransformer() {
        String[] tableOrderConfig = dataSetsConfig.getTableOrder();
        if (tableOrderConfig != null) {
            TableOrder tableOrder = new DefaultTableOrder(tableOrderConfig);
            ExternalSortTableConsumer sortConsumer = new ExternalSortTableConsumer(tableOrder);
            return sortConsumer;
        }
        return null;
    }

    protected ChainableDataSetConsumer getAfterMigrationTransformer() {
        String[] tableOrderConfig = dataSetsConfig.getTableOrder();
        if (tableOrderConfig != null) {
            TableOrder tableOrder = new DefaultTableOrder(tableOrderConfig);
            ExternalSortTableConsumer sortConsumer = new ExternalSortTableConsumer(tableOrder);
            return sortConsumer;
        }
        return null;
    }

    protected DataSetResourcesMigrationListener getMigrationListener() {
        return new LoggingDataSetResourcesMigrationListener(logger);
    }

}
