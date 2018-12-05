package edu.arizona.kra.subaward.batch.dao.ojb;

import edu.arizona.kra.subaward.batch.InvoiceFeedConstants;
import edu.arizona.kra.subaward.batch.dao.SubawardGlFeedDao;
import edu.arizona.kra.util.DBConnection;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nataliac on 8/3/18.
 */
public class SubawardGlFeedDaoOjb extends PlatformAwareDaoBaseOjb implements SubawardGlFeedDao {
    private static final Logger LOG = LoggerFactory.getLogger(SubawardGlFeedDaoOjb.class);


    public int emptyGLTempTable() {
        LOG.debug("SubawardGlFeedDaoOjb.emptyGLTempTable ENTER");
        int deletedRows = 0;
        String sqlQuery = InvoiceFeedConstants.CLEAR_GL_IMPORT_TABLE_QUERY;

        try (DBConnection dbc = new DBConnection(this.getPersistenceBroker(true)) ) {

            deletedRows = dbc.executeUpdate(sqlQuery, null);

        } catch (Exception e){
            LOG.error("Could not clean up the temporary GL Import Table Subaward. ABORTING...", e);
            throw new RuntimeException("Could not clean up the temporary GL Import Table Subaward. ABORTING...",e);
        }

        LOG.info("SubawardGlFeedDaoOjb.emptyGLTempTable FINISH");
        return deletedRows;
    }


    public List<Long> findActiveSubawardIdforPO(String purchaseOrderNumber){
        LOG.debug("SubawardGlFeedDaoOjb.findActiveSubawardIdforPO ENTER");
        String sqlQuery = InvoiceFeedConstants.FIND_ACTIVE_SUBAWARD_ID_FOR_PO_QUERY;
        Object[] params = new Object[] { purchaseOrderNumber };
        List<Long> result = new ArrayList<Long>(1);

        try (DBConnection dbc = new DBConnection(this.getPersistenceBroker(true)) ) {

            ResultSet rs = dbc.executeQuery(sqlQuery, params);

            while (rs.next()) {
                result.add ( rs.getLong(1) );
            }
        } catch (Exception e){
            LOG.error("Exception while trying to find a matching ACTIVE Subaward for PO Number="+purchaseOrderNumber, e);
            throw new RuntimeException("Exception while trying to find a matching ACTIVE Subaward for PO Number="+purchaseOrderNumber, e);
        }


        LOG.debug("SubawardGlFeedDaoOjb.findActiveSubawardIdforPO="+purchaseOrderNumber+"FINISHED result size="+result.size());
        return result;

    }

}
