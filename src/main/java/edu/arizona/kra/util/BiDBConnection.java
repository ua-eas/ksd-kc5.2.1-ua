package edu.arizona.kra.util;

import org.apache.ojb.broker.accesslayer.LookupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.Closeable;
import java.sql.*;

/**
 * Class for properly obtaining a simple BI connection for reading only, no transactions involved.
 * Designed to be used with Java> 1.7 in a try-with-resources statement
 *
 *
 * @author nataliac
 */
public class BiDBConnection implements Closeable{
    private static final Logger LOG = LoggerFactory.getLogger(BiDBConnection.class);

    private Connection conn = null;
    private PreparedStatement ps = null;
    private ResultSet rs = null;
    private DataSource dataSource = null;

    public BiDBConnection(DataSource dataSource){
        this.dataSource = dataSource;
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
    public ResultSet executeQuery(String sqlQuery, Object[] queryParams) throws SQLException{
        LOG.info("executeQuery={}",sqlQuery);
        conn = dataSource.getConnection();
        ps = conn.prepareStatement(sqlQuery);
        setParams(ps, queryParams);
        rs = ps.executeQuery();
        LOG.debug("executeQuery successfully ended.");
        return rs;
    }



    protected void setParams(PreparedStatement ps, Object[] queryParams) throws SQLException{
        if (ps!=null && queryParams != null && queryParams.length > 0){
            for (int i = 0; i < queryParams.length; i++) {
                if (queryParams[i] == null) {
                    ps.setNull(i + 1, Integer.MIN_VALUE);
                } else if (queryParams[i] instanceof Timestamp){
                    ps.setTimestamp(i + 1, (Timestamp) queryParams[i]);
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
                rs = null;
            } catch (Exception ex) {
                LOG.warn("Failed to close ResultSet.", ex);
            }
        }
        if ( ps != null ){
            try {
                ps.close();
                ps = null;
            } catch (Exception ex) {
                LOG.warn("Failed to close PreparedStatement.", ex);
            }
        }
        if ( conn != null ){
            try {
                conn.close();
                conn = null;
            } catch (Exception ex) {
                LOG.warn("Failed to release Connection.", ex);
            }
        }
    }


}
