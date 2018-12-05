package edu.arizona.kra.subaward.batch.dao;

import java.util.List;

/**
 * DAO for accessing GL Data Imported from BI for Subaward Invoice Feed
 * Created by nataliac on 8/3/18.
 */
public interface SubawardGlFeedDao {

    int emptyGLTempTable();

    List<Long> findActiveSubawardIdforPO(String purchaseOrderNumber);
}
