package edu.arizona.kra.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.Closeable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.accesslayer.LookupException;
import org.springmodules.orm.ojb.OjbFactoryUtils;


public class DBConnection implements Closeable{
    private static final Log LOG = LogFactory.getLog(DBConnection.class);

    private Connection conn = null;
    private PreparedStatement ps = null;
    private ResultSet rs = null;
    private PersistenceBroker broker = null;

    public DBConnection(PersistenceBroker pb){
        broker = pb;
    }


    public ResultSet executeQuery(String sqlQuery, Object[] queryParams) throws SQLException, LookupException{ 
        LOG.debug("executeQuery="+sqlQuery);
        conn = broker.serviceConnectionManager().getConnection();
        ps = conn.prepareStatement(sqlQuery);
        if (queryParams != null && queryParams.length > 0){
            for (int i = 0; i < queryParams.length; i++) {
                if (queryParams[i] == null) {
                    ps.setNull(i + 1, Integer.MIN_VALUE);
                } else {
                    ps.setObject(i + 1, queryParams[i]);
                }
            }
        }
        rs = ps.executeQuery();
        LOG.debug("executeQuery successfully ended.");
        return rs;
    }


    @Override
    public void close() {
        LOG.debug("close()");
        if (rs != null) {
            try {
                rs.close();
            } catch (Exception ex) {
                LOG.warn("Failed to close ResultSet.", ex);
            }
        }
        if ( ps != null ){
            try {
                ps.close();
            } catch (Exception ex) {
                LOG.warn("Failed to close PreparedStatement.", ex);
            }
        }
        if ( conn != null ){
            try {
                broker.serviceConnectionManager().releaseConnection();
            } catch (Exception ex) {
                LOG.warn("Failed to release Connection.", ex);
            }
        }
        if (broker != null) {
            try {
                OjbFactoryUtils.releasePersistenceBroker(broker, broker.getPBKey());
            } catch (Exception e) {
                LOG.error("Failed releasing PersistenceBroker: " + e.getMessage(), e);
            }
        }
    }


}
