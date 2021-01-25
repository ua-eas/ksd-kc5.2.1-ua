package edu.arizona.kra.protocol.pdf;

import edu.arizona.kra.protocol.pdf.ProtocolNumberDao;
import edu.arizona.kra.util.DBConnection;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;


public class ProtocolNumberDaoOjb extends PlatformAwareDaoBaseOjb implements ProtocolNumberDao {
    private static final String PROTOCOL_NUMBER_QUERY = "select PROTOCOL_NUMBER from PROTOCOL where active = 'Y'";

    @Override
    public Set<String> getActiveProtocolNumbers() {
        Set<String> protocolNumbers = new HashSet<>();
        DBConnection dbc = new DBConnection(this.getPersistenceBroker(true));

        try {
            ResultSet rs = dbc.executeQuery(PROTOCOL_NUMBER_QUERY, new Object[]{});
            while (rs.next()) {
                protocolNumbers.add(rs.getString(1));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return protocolNumbers;
    }
}
