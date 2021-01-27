package edu.arizona.kra.irb.pdf.impl;

import edu.arizona.kra.irb.pdf.ProtocolNumberDao;
import edu.arizona.kra.util.DBConnection;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


public class ProtocolNumberDaoOjb extends PlatformAwareDaoBaseOjb implements ProtocolNumberDao {
    private static final Logger LOG = Logger.getLogger(ProtocolNumberDaoOjb.class);
    private String activeProtocolNumberQuery;
    private String startFromDate;
    private String endToDate;
    private boolean isInitialized;
    private ConfigurationService kualiConfigurationService;


    public ProtocolNumberDaoOjb() {
        this.isInitialized = false;
    }


    /*
     * Can't do this in the constructor since the kualiConfigurationService
     * isn't available yet when this class is instantiated by spring
     */
    private void init() {
        String activeSqlFilePath
                = getKualiConfigurationService().getPropertyValueAsString("protocol.pdf.active.query.file");
        this.activeProtocolNumberQuery = getSqlQueryStringFromResource(activeSqlFilePath);
        this.startFromDate = getKualiConfigurationService().getPropertyValueAsString("protocol.pdf.start.from.date");
        this.endToDate = getKualiConfigurationService().getPropertyValueAsString("protocol.pdf.end.to.date");
        isInitialized = true;
    }


    @Override
    public List<String> getActiveProtocolNumbers() {
        if (!isInitialized) {
            init();
        }

        LOG.info("Starting protocol number query...");
        Object[] dateCriteria = new Object[]{startFromDate, endToDate};

        List<String> protocolNumbers = new ArrayList<>();
        try {
            DBConnection dbc = new DBConnection(this.getPersistenceBroker(true));
            ResultSet rs = dbc.executeQuery(activeProtocolNumberQuery, dateCriteria);
            while (rs.next()) {
                protocolNumbers.add(rs.getString(1));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        LOG.info(String.format("Found %d protocol numbers.", protocolNumbers.size()));
        return protocolNumbers;
    }


    private String getSqlQueryStringFromResource(String sqlFilePath) {
        String sqlString;
        try {
            // Read all lines from file, collapse into one line
            String fileContents = IOUtils.toString(
                    getClass().getResourceAsStream(sqlFilePath), StandardCharsets.UTF_8.name());
            sqlString = fileContents.replaceAll("\\s+", " ");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return sqlString;
    }


    public ConfigurationService getKualiConfigurationService() {
        return kualiConfigurationService;
    }

    public void setKualiConfigurationService(ConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }
}
