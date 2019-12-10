package edu.arizona.kra.protocol.onlinereview.dao;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ojb.broker.query.*;
import org.kuali.kra.irb.actions.submit.ProtocolSubmission;
import org.kuali.kra.irb.actions.submit.ProtocolSubmissionStatus;
import org.kuali.kra.irb.onlinereview.ProtocolOnlineReview;
import org.kuali.kra.protocol.onlinereview.ProtocolOnlineReviewBase;
import org.kuali.rice.kns.lookup.LookupUtils;
import org.kuali.rice.krad.dao.impl.LookupDaoOjb;
import org.kuali.rice.krad.lookup.CollectionIncomplete;
import org.kuali.rice.krad.service.util.OjbCollectionAware;
import org.kuali.rice.krad.util.KRADConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Collection;

public class ProtocolOnlineReviewDaoOjb extends LookupDaoOjb implements OjbCollectionAware, ProtocolOnlineReviewDao {

    private static final Log LOG = LogFactory.getLog(ProtocolOnlineReviewDaoOjb.class);

    public List<ProtocolOnlineReviewBase> getProtocolOnlineReviewsByQuery(QueryByCriteria query) {
        logQuery(query);
        List<ProtocolOnlineReviewBase> retVal = (List<ProtocolOnlineReviewBase>) getPersistenceBrokerTemplate().getCollectionByQuery(query);

        return retVal;
    }


    private static void logQuery(Query q) {
        if (LOG.isDebugEnabled()) {
            LOG.debug(q.toString());
        }
    }


    // This builds the first set of criteria including ProtocolOnlineReviewStatus.FINAL_STATUS_CD
    // alternatively applied in org.kuali.kra.irb.onlinereview.ProtocolOnlineReviewLookupableHelperServiceImpl.filterResults
    // Need to apply these criteria in 2 separate OJB Queries to avoid the "java.sql.SQLSyntaxErrorException: ORA-00913: too many values"
    protected Criteria buildStatusFinalCriteria(Criteria baseCriteria) {
        Criteria statusFinalCriteria = baseCriteria.copy(true,true,true);

        /// Build statusFinalCriteria
        statusFinalCriteria.addLike("UPPER(reviewerApproved)", KRADConstants.YES_INDICATOR_VALUE);
        statusFinalCriteria.addLike("UPPER(adminAccepted)", KRADConstants.YES_INDICATOR_VALUE);
        statusFinalCriteria.addLike("UPPER(protocolOnlineReviewStatusCode)",  org.kuali.kra.irb.onlinereview.ProtocolOnlineReviewStatus.FINAL_STATUS_CD);

        List<String> protocolSubmissionStatusList = new ArrayList<>(
                Arrays.asList(ProtocolSubmissionStatus.APPROVED,
                        ProtocolSubmissionStatus.EXEMPT,
                        ProtocolSubmissionStatus.SPECIFIC_MINOR_REVISIONS_REQUIRED,
                        ProtocolSubmissionStatus.SUBSTANTIVE_REVISIONS_REQUIRED,
                        ProtocolSubmissionStatus.RETURNED_TO_PI));

        Criteria subCriteriaStatusFinalCriteria = new Criteria();
        subCriteriaStatusFinalCriteria.addIn("submissionStatusCode",protocolSubmissionStatusList);

        ReportQueryByCriteria statusFinalSubQuery = QueryFactory.newReportQuery( ProtocolSubmission.class, subCriteriaStatusFinalCriteria);

        // Just retrieve the submissionId from ProtocolSubmission (PROTOCOL_SUBMISSION .SUBMISSION_ID)
        // since this is a subquery to find PROTOCOL_ONLN_RVWS.SUBMISSION_ID_FK IN
        statusFinalSubQuery.setAttributes(new String[] { "submissionId"});

        statusFinalCriteria.addIn("submissionIdFk", statusFinalSubQuery);

        return statusFinalCriteria;
    }


    // This builds the second set of criteria including NOT(ProtocolOnlineReviewStatus.FINAL_STATUS_CD)
    // alternatively applied in org.kuali.kra.irb.onlinereview.ProtocolOnlineReviewLookupableHelperServiceImpl.filterResults
    // Need to apply these criteria in 2 separate OJB Queries to avoid the "java.sql.SQLSyntaxErrorException: ORA-00913: too many values"
    protected Criteria buildStatusNotFinalCriteria(Criteria baseCriteria) {
        Criteria statusNotFinalCriteria = baseCriteria.copy(true,true,true);

        /// Build statusNotFinalCriteria
        statusNotFinalCriteria.addNotLike("UPPER(protocolOnlineReviewStatusCode)", org.kuali.kra.irb.onlinereview.ProtocolOnlineReviewStatus.FINAL_STATUS_CD);

        Criteria statusNotFinalSubCriteria = new Criteria();

        List<String> protocolSubmissionStatusList = new ArrayList<>(
                Arrays.asList(ProtocolSubmissionStatus.IN_AGENDA,
                        ProtocolSubmissionStatus.SUBMITTED_TO_COMMITTEE));

        statusNotFinalSubCriteria.addIn("submissionStatusCode",protocolSubmissionStatusList);
        ReportQueryByCriteria statusNotFinalSubQuery = QueryFactory.newReportQuery( ProtocolSubmission.class, statusNotFinalSubCriteria);

        // Just retrieve the submissionId from ProtocolSubmission (PROTOCOL_SUBMISSION .SUBMISSION_ID)
        // since this is a subquery to find PROTOCOL_ONLN_RVWS.SUBMISSION_ID_FK IN
        statusNotFinalSubQuery.setAttributes(new String[] { "submissionId"});

        statusNotFinalCriteria.addIn("submissionIdFk", statusNotFinalSubQuery);

        return statusNotFinalCriteria;
    }


    // General query was returning more search results from the DB than the default resultSetLimit and relying on method
    // org.kuali.kra.irb.onlinereview.ProtocolOnlineReviewLookupableHelperServiceImpl.filterResults to filter the extras
    // out on the webserver however the resultSetLimit was discarding the set that included the desired results
    // to resolve this applied the filterResults() criteria at the database for cases where there were no from dates
    // so that only desired results are returned from the DB and not excluded by the default resultSetLimit limitation
    @Override
    public Collection<ProtocolOnlineReviewBase> getCustomSearchResults(Map<String, String> fieldValues) {

        Collection<ProtocolOnlineReviewBase> searchResults = new ArrayList<>();

        Long matchingResultsCount = null;

        ProtocolOnlineReview protocolOnlineReview = new ProtocolOnlineReview();

        // Applying the filter criteria previously applied in org.kuali.kra.irb.onlinereview.ProtocolOnlineReviewLookupableHelperServiceImpl.filterResults
        // Need to apply these criteria in 2 separate OJB Queries to avoid the "java.sql.SQLSyntaxErrorException: ORA-00913: too many values"
        // Create baseCriteria that will be incorporated in each of the filterQueries 'statusFinalCriteria' and 'statusNotFinalCriteria'
        Criteria baseCriteria = this.getCollectionCriteriaFromMap( protocolOnlineReview, fieldValues);

        if (StringUtils.isNotBlank(fieldValues.get("dateDue")) &&
                (fieldValues.get("dateDue")).startsWith("<=")) {
            baseCriteria.addOrderByDescending("dateDue");
        }

        if (StringUtils.isNotBlank(fieldValues.get("dateRequested")) &&
                (fieldValues.get("dateRequested")).startsWith("<=")) {
            baseCriteria.addOrderByDescending("dateRequested");
        }

        Integer searchResultsLimit = LookupUtils.getSearchResultsLimit(ProtocolOnlineReview.class);

        // Below Code Adds Logic performed by first set of criteria applied in filterResults() as 'statusFinalCriteria'
        Criteria statusFinalCriteria = buildStatusFinalCriteria(baseCriteria);

        if (searchResultsLimit != null && searchResultsLimit >= 0) {
            matchingResultsCount = (long)this.getPersistenceBrokerTemplate().getCount(QueryFactory.newReportQuery(ProtocolOnlineReview.class, statusFinalCriteria));
            // Below applySearchResultsLimit() applies SQL to WHERE clause to limit the number of search results returned by the database
            org.kuali.rice.krad.lookup.LookupUtils.applySearchResultsLimit(ProtocolOnlineReview.class, statusFinalCriteria, this.getDbPlatform(), searchResultsLimit);
        }

        if (matchingResultsCount == null || matchingResultsCount.intValue() <= searchResultsLimit) {
            matchingResultsCount = 0L;
        }

        QueryByCriteria statusFinalQuery = QueryFactory.newReportQuery( ProtocolOnlineReview.class, statusFinalCriteria);

        searchResults.addAll( getProtocolOnlineReviewsByQuery(statusFinalQuery));

        // Below Code Adds Logic performed by second set of criteria applied in filterResults() as 'statusNotFinalCriteria'
        Criteria statusNotFinalCriteria = buildStatusNotFinalCriteria(baseCriteria);

        if (searchResultsLimit != null && searchResultsLimit >= 0) {
            matchingResultsCount +=(long)this.getPersistenceBrokerTemplate().getCount(QueryFactory.newReportQuery(ProtocolOnlineReview.class, statusNotFinalCriteria));
            // Below applySearchResultsLimit() applies SQL to WHERE clause to limit the number of search results returned by the database
            org.kuali.rice.krad.lookup.LookupUtils.applySearchResultsLimit(ProtocolOnlineReview.class, statusNotFinalCriteria, this.getDbPlatform(), searchResultsLimit);
        }

        if ( matchingResultsCount.intValue() <= searchResultsLimit) {
            matchingResultsCount = 0L;
        }

        QueryByCriteria statusNotFinalQuery = QueryFactory.newReportQuery( ProtocolOnlineReview.class, statusNotFinalCriteria);

        searchResults.addAll( getProtocolOnlineReviewsByQuery(statusNotFinalQuery));

        return new CollectionIncomplete(searchResults, matchingResultsCount);
    }
}