package edu.arizona.kra.protocol.onlinereview.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ojb.broker.query.*;
import org.kuali.kra.irb.actions.submit.ProtocolSubmission;
import org.kuali.kra.irb.actions.submit.ProtocolSubmissionStatus;
import org.kuali.kra.irb.onlinereview.ProtocolOnlineReview;
import org.kuali.kra.protocol.onlinereview.ProtocolOnlineReviewBase;
import org.kuali.rice.kns.lookup.LookupUtils;
import org.kuali.rice.krad.dao.impl.LookupDaoOjb;
import org.kuali.rice.krad.service.util.OjbCollectionAware;
import org.kuali.rice.krad.util.KRADConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ProtocolOnlineReviewDaoOjb extends LookupDaoOjb implements OjbCollectionAware, ProtocolOnlineReviewDao {

    private static final Log LOG = LogFactory.getLog(ProtocolOnlineReviewDaoOjb.class);

    @SuppressWarnings("unchecked")
    @Override
    public List<ProtocolOnlineReviewBase> getCollectionByQuery(ReportQueryByCriteria query){
        List<ProtocolOnlineReviewBase> searchResults = new ArrayList();
        searchResults = (List<ProtocolOnlineReviewBase>) getPersistenceBrokerTemplate().getCollectionByQuery(query);
        return searchResults;
    }


    public List<ProtocolOnlineReviewBase> getProtocolOnlineReviewsByQuery(QueryByCriteria query) {
        List<ProtocolOnlineReviewBase> retVal = null;
        logQuery(query);
        retVal = (List<ProtocolOnlineReviewBase>) getPersistenceBrokerTemplate().getCollectionByQuery(query);

        return retVal;
    }


    public List<ProtocolOnlineReviewBase> getProtocolOnlineReviewsByReportQuery(ReportQueryByCriteria query) {
        List<ProtocolOnlineReviewBase> retVal = null;

        logQuery(query);
        retVal = (List<ProtocolOnlineReviewBase>) getPersistenceBrokerTemplate().getCollectionByQuery(query);

        return retVal;
    }


    private static void logQuery(Query q) {
        if (LOG.isDebugEnabled()) {
            LOG.debug(q.toString());
        }
    }


    public List<ProtocolOnlineReviewBase> getCustomSearchResults() {
        return getCustomSearchResults();
    }


    @Override
    public List<ProtocolOnlineReviewBase> getCustomSearchResults(Map<String, String> fieldValues) {

        List<ProtocolOnlineReviewBase> searchResults = new ArrayList();

        Long matchingResultsCount = null;

        ProtocolOnlineReview protocolOnlineReview = new ProtocolOnlineReview();

        Criteria criteria1 = this.getCollectionCriteriaFromMap( protocolOnlineReview, fieldValues);

        if (!((String)fieldValues.get("dateDue")).isEmpty() &&
                ((String)fieldValues.get("dateDue")).startsWith("<=")) {
            String dateDueString = (fieldValues.get("dateDue")).substring(2);
            criteria1.addOrderByDescending("dateDue");
        }

        if (!((String)fieldValues.get("dateRequested")).isEmpty() &&
                ((String)fieldValues.get("dateRequested")).startsWith("<=")) {
            String dateRequestedString = (fieldValues.get("dateRequested")).substring(2);
            criteria1.addOrderByDescending("dateRequested");
        }

        Integer searchResultsLimit = LookupUtils.getSearchResultsLimit(ProtocolOnlineReview.class);

        if (searchResultsLimit != null && searchResultsLimit >= 0) {
            matchingResultsCount = new Long((long)this.getPersistenceBrokerTemplate().getCount(QueryFactory.newReportQuery(ProtocolOnlineReview.class, criteria1)));
            org.kuali.rice.krad.lookup.LookupUtils.applySearchResultsLimit(ProtocolOnlineReview.class, criteria1, this.getDbPlatform(), searchResultsLimit);
        }

        if (matchingResultsCount == null || matchingResultsCount.intValue() <= searchResultsLimit) {
            matchingResultsCount = new Long(0L);
        }

        // Below Code Adds Logic performed by filterresults()
        Criteria criteria2 = criteria1.copy(true,true,true); /// Make a copy of the base criteria for reuse

        /// Process Query1
        criteria1.addLike("UPPER(reviewerApproved)", KRADConstants.YES_INDICATOR_VALUE);
        criteria1.addLike("UPPER(adminAccepted)", KRADConstants.YES_INDICATOR_VALUE);
        criteria1.addLike("UPPER(protocolOnlineReviewStatusCode)",  org.kuali.kra.irb.onlinereview.ProtocolOnlineReviewStatus.FINAL_STATUS_CD);

        List protocolSubmissionStatusList1 = new ArrayList<String>(
                Arrays.asList(ProtocolSubmissionStatus.APPROVED,
                        ProtocolSubmissionStatus.EXEMPT,
                        ProtocolSubmissionStatus.SPECIFIC_MINOR_REVISIONS_REQUIRED,
                        ProtocolSubmissionStatus.SUBSTANTIVE_REVISIONS_REQUIRED,
                        ProtocolSubmissionStatus.RETURNED_TO_PI));

        Criteria subCriteria1 = new Criteria();
        subCriteria1.addIn("submissionStatusCode",protocolSubmissionStatusList1);

        ReportQueryByCriteria subQuery1 = QueryFactory.newReportQuery( ProtocolSubmission.class, subCriteria1);

        // Just retrieve the submissionId from ProtocolSubmission (PROTOCOL_SUBMISSION .SUBMISSION_ID)
        // since this is a subquery to find PROTOCOL_ONLN_RVWS.SUBMISSION_ID_FK IN
        subQuery1.setAttributes(new String[] { "submissionId"});

        criteria1.addIn("submissionIdFk", subQuery1);

        QueryByCriteria porQuery1 = QueryFactory.newReportQuery( ProtocolOnlineReview.class, criteria1);

        searchResults.addAll( getProtocolOnlineReviewsByQuery(porQuery1));

        /// Process Query2
        criteria2.addNotLike("UPPER(protocolOnlineReviewStatusCode)", org.kuali.kra.irb.onlinereview.ProtocolOnlineReviewStatus.FINAL_STATUS_CD);

        Criteria subCriteria2 = new Criteria();

        List protocolSubmissionStatusList2 = new ArrayList<String>(
        Arrays.asList(ProtocolSubmissionStatus.IN_AGENDA,
                ProtocolSubmissionStatus.SUBMITTED_TO_COMMITTEE));

        subCriteria2.addIn("submissionStatusCode",protocolSubmissionStatusList2);
        ReportQueryByCriteria subQuery2 = QueryFactory.newReportQuery( ProtocolSubmission.class, subCriteria2);

        // Just retrieve the submissionId from ProtocolSubmission (PROTOCOL_SUBMISSION .SUBMISSION_ID)
        // since this is a subquery to find PROTOCOL_ONLN_RVWS.SUBMISSION_ID_FK IN
        subQuery2.setAttributes(new String[] { "submissionId"});

        criteria2.addIn("submissionIdFk", subQuery2);

        QueryByCriteria porQuery2 = QueryFactory.newReportQuery( ProtocolOnlineReview.class, criteria2);

        searchResults.addAll( getProtocolOnlineReviewsByQuery(porQuery2));

        return searchResults;
    }
}