package edu.arizona.kra.irb.pdf.impl;

import edu.arizona.kra.irb.pdf.ProtocolNumberDao;
import edu.arizona.kra.util.DBConnection;
import org.apache.log4j.Logger;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


public class ProtocolNumberDaoOjb extends PlatformAwareDaoBaseOjb implements ProtocolNumberDao {
    private static final Logger LOG = Logger.getLogger(ProtocolNumberDaoOjb.class);
    private static final String PROTOCOL_NUMBER_QUERY = "select PROTOCOL_NUMBER from PROTOCOL where active = 'Y'";

    @Override
    public List<String> getActiveProtocolNumbers() {
        List<String> protocolNumbers = new ArrayList<>();
        DBConnection dbc = new DBConnection(this.getPersistenceBroker(true));

        try {
            LOG.info("Starting protocol numbers query...");
            ResultSet rs = dbc.executeQuery(PROTOCOL_NUMBER_QUERY, new Object[]{});
            while (rs.next()) {
                protocolNumbers.add(rs.getString(1));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        LOG.info(String.format("Found %d protocol numbers.", protocolNumbers.size()));
        return protocolNumbers;
    }
}
