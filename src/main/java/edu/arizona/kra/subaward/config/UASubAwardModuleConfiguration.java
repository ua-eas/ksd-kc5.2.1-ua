package edu.arizona.kra.subaward.config;

import org.kuali.rice.krad.bo.ModuleConfiguration;

import javax.sql.DataSource;

/**
 * Created by nataliac on 8/14/18.
 */
public class UASubAwardModuleConfiguration extends ModuleConfiguration {
    private DataSource biDataSource;


    public DataSource getBiDataSource() {
        return biDataSource;
    }

    public void setBiDataSource(DataSource biDataSource) {
        this.biDataSource = biDataSource;
    }

}
