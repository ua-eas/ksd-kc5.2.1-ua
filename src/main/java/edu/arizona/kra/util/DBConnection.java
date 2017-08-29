package edu.arizona.kra.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.Closeable;


import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.accesslayer.LookupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springmodules.orm.ojb.OjbFactoryUtils;

/**
 * Class for properly obtaining/releasing connections from the PersistenceBroker
 * Designed to be used with Java> 1.7 in a try-with-resources statement
 *
 * (see PropDevRoutingStateDaoOjb for usage examples)
 *
 * @author nataliac
 */
public class DBConnection implements Closeable{
    private static final Logger LOG = LoggerFactory.getLogger(DBConnection.class);

    private Connection conn = null;
    private PreparedStatement ps = null;
    private ResultSet rs = null;
    private PersistenceBroker broker = null;

    public DBConnection(PersistenceBroker pb){
        broker = pb;
    }

    /**
     * Executes a query an returns the resulting result set
     *
     * @param sqlQuery
     * @param queryParams
     * @return
     * @throws SQLException
     * @throws LookupException
     */
    public ResultSet executeQuery(String sqlQuery, Object[] queryParams) throws SQLException, LookupException{ 
        LOG.debug("executeQuery={}",sqlQuery);
        conn = broker.serviceConnectionManager().getConnection();
        ps = conn.prepareStatement(sqlQuery);
        setParams(ps, queryParams);
        rs = ps.executeQuery();
        LOG.debug("executeQuery successfully ended.");
        return rs;
    }

    /**
     * Executes an update query returning the result code
     * @param sqlQuery
     * @param queryParams
     * @return
     * @throws SQLException
     * @throws LookupException
     */
    public int executeUpdate(String sqlQuery, Object[] queryParams) throws SQLException, LookupException{
        LOG.debug("executeUpdate={}",sqlQuery);
        conn = broker.serviceConnectionManager().getConnection();
        ps = conn.prepareStatement(sqlQuery);
        setParams(ps, queryParams);
        int resultCode = ps.executeUpdate();
        LOG.debug("executeUpdate ended with result={}",resultCode);
        return resultCode;
    }


    protected void setParams(PreparedStatement ps, Object[] queryParams) throws SQLException{
        if (ps!=null && queryParams != null && queryParams.length > 0){
            for (int i = 0; i < queryParams.length; i++) {
                if (queryParams[i] == null) {
                    ps.setNull(i + 1, Integer.MIN_VALUE);
                } else {
                    ps.setObject(i + 1, queryParams[i]);
                }
            }
        }
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
