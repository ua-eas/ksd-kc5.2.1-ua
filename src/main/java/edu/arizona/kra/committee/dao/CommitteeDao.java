package edu.arizona.kra.committee.dao;

import java.util.Collection;

import org.kuali.kra.committee.bo.Committee;

/**
 * An interface for classes that implement new logic that
 * is not already present BuusinessObjectService in order to
 * perform more complex queries against the Committee table.
 * 
 */
public interface CommitteeDao {
	
	/**
	 * A method to acquire a list of Committee objects by committeeId. 
	 * 
	 * @param committeeId The ID for which to constarin a query by.
	 * @return A list of Committee objects that share committeeId
	 */
	public Collection<Committee> findByMaxSequenceNumber(String committeeId);
}
