package edu.arizona.kra.committee.dao;

import java.util.Collection;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kra.committee.bo.Committee;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;
import org.kuali.rice.krad.service.util.OjbCollectionAware;

/**
 * An extended DAO that is making findByMaxSequenceNumber(...) more efficient.
 * This implementation also gets around an OJB bug that was not setting
 * Committee.sequenceNumber correctly in some cases.
 * 
 */
public class CommitteeDaoOjb extends PlatformAwareDaoBaseOjb implements OjbCollectionAware, CommitteeDao {

	private static final String COMMITTEE_ID = "committeeId";
	private static final String SEQ_NUM_ATTR_NAME = "sequenceNumber";
	private static final String SEQ_NUM_COL_NAME = "sequence_number";
	private static final String KLEENE_STAR = "*";
	private static final String PERCENT = "%";



    /**
     * Get results that satisfy fieldValues AND also have the
     * largest value returned from max(sequence_number). This method
     * should only ever return one Committee, or an empty collection
     * if no records match.
     * 
     * @param committeeId The ID for which query should be constrained to.
     * @return The Committee records that satisfy having the passed-in ID and has the highest sequence number.
     */
    @SuppressWarnings("unchecked")
	public Collection<Committee> findByMaxSequenceNumber(String committeeId) {
    	Criteria criteria = new Criteria();
    	if(committeeId.contains(KLEENE_STAR)){
    		criteria.addLike(COMMITTEE_ID, committeeId.replace(KLEENE_STAR, PERCENT));
    	}else{
    		criteria.addEqualTo(COMMITTEE_ID, committeeId);
    	}
    	criteria.addEqualTo(SEQ_NUM_ATTR_NAME, getSubQueryMaxSequenceNumber());
    	return (Collection<Committee>)getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(Committee.class, criteria));
    }


    /*
     * Create the inner query to enforce max(sequence_number)
     */
    private ReportQueryByCriteria getSubQueryMaxSequenceNumber(){
    	Criteria subCritMaxSequenceNumber = new Criteria();
    	subCritMaxSequenceNumber.addEqualToField(COMMITTEE_ID, Criteria.PARENT_QUERY_PREFIX + COMMITTEE_ID);
    	ReportQueryByCriteria subQueryMaxSequenceNumber = QueryFactory.newReportQuery(Committee.class, subCritMaxSequenceNumber);
    	subQueryMaxSequenceNumber.setAttributes(new String[] { "max("+SEQ_NUM_COL_NAME+")" });
    	return subQueryMaxSequenceNumber;
    }


}
