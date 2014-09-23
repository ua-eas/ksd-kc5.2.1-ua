package edu.arizona.kra.committee.service.impl;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.kuali.kra.committee.bo.Committee;

import edu.arizona.kra.committee.dao.CommitteeDao;

/**
 * A custom class to leverage a new DAO that is more selective
 * by SQL query. The original DAO returned a set of results,
 * and then filtered in-memory.
 * 
 * Additionally, OJB has a bug that sets an incorrect sequence
 * number on the last Committee of the returned collection in
 * special cases (the conditions of which weren't ever identified).
 * 
 */
public class CustomCommitteeServiceImpl extends org.kuali.kra.committee.service.impl.CommitteeServiceImpl{
	
	private CommitteeDao committeeDao;
	

	/**
	 * This method should return one Committee that contains the supplied committeeId
	 * AND that has the highest sequence number of the records that also share
	 * committeeId.
	 * 
	 * @param committeeId The ID that will constrain what records will be considered, and
	 *                    which one will be returned, e.g. "Full Committee".
	 * @return The Committee record that satisfies having the passed-in ID and has the
	 *         highest sequence number.
	 */
	@Override
	public Committee getCommitteeById(String committeeId) {
        Committee committee = null;
        if (!StringUtils.isBlank(committeeId)) {
            Collection<Committee> committees = getCommitteeDao().findByMaxSequenceNumber(committeeId);
            if (committees.size() > 1) {
                throw new RuntimeException("getCommitteeById() returned more than one result!");
            }else if(committees.size() == 1){
            	committee = committees.iterator().next();
            }
        }
        return committee;
	}


	protected CommitteeDao getCommitteeDao() {
		return committeeDao;
	}


	public void setCommitteeDao(CommitteeDao committeeDao) {
		this.committeeDao = committeeDao;
	}
	
	
}
