package edu.arizona.kra.subaward.batch.dao;

import edu.arizona.kra.subaward.batch.bo.BiGlEntry;
import org.apache.ojb.broker.accesslayer.LookupException;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

/**
 * DAO for accessing GL Data View in BI for Subaward Invoice Feed
 * Created by nataliac on 8/3/18.
 */
public interface BiGLFeedDao {

    public List<BiGlEntry> importGLData(Date beginDate, java.sql.Date endDate) throws SQLException,LookupException;

}
