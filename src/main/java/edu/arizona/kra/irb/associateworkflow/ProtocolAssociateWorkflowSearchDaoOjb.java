package edu.arizona.kra.irb.associateworkflow;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kra.bo.CustomAttributeDocValue;
import org.kuali.kra.irb.actions.submit.ProtocolSubmission;
import org.kuali.kra.irb.onlinereview.ProtocolOnlineReview;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.dao.LookupDao;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.util.KRADConstants;

public class ProtocolAssociateWorkflowSearchDaoOjb extends PlatformAwareDaoBaseOjb implements ProtocolAssociateWorkflowSearchDao {


	private static final String CANCELED_ONLINE_REVIEW_STATUS_CODE = "X";
	private static final String SUBMITTED_TO_IRB = "Submitted to IRB";
	
	private LookupDao lookupDao;
	private DataDictionaryService dataDictionaryService;
	
	private static final String[] RESULT_COLUMNS = new String[] { "submissionDate", 
																  "protocolNumber", 
																  "protocol.protocolStatusCode", 
																  "protocol.protocolStatus.description", 
																  "protocol.expirationDate", 
																  "submissionTypeCode", 
																  "protocolSubmissionType.description", 
																  "submissionStatusCode", 
																  "submissionStatus.description", 
																  "committee.committeeId",
																  "committee.committeeName",
																  "protocol.protocolDocument.documentNumber",
																  "protocol.protocolPersons.personName",
																  "submissionId",
																  "protocolId" };
	
	/**
	 * Queries the database for search ProtocolAssociateWorkflowSearch search results
	 * based on the query criteria in fieldValues and the additional parameters.
	 * Generates a list of results that can be used by the lookup framework.
	 * 
	 * @return	A list of ProtocolAssociateWorkflowSearch search results.
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public List<ProtocolAssociateWorkflowSearch> getProtocolAssociateWorkflowResults(Map<String, String> fieldValues, 
			boolean filterChairReviewComplete, String associate) {
		Criteria criteria = buildSearchCriteria(fieldValues);
		if(filterChairReviewComplete) {
			addChairReviewCompleteCriteria(criteria);
		}
		if(StringUtils.isNotBlank(associate)) {
			addAssociateCriteria(criteria, associate);
		}
		
		ReportQueryByCriteria query = QueryFactory.newReportQuery(ProtocolSubmission.class, criteria);
		query.setAttributes(RESULT_COLUMNS);
		query.addOrderByAscending("submissionDate");
		query.addOrderByAscending("protocolNumber");
		// Outer join on committee so ProtocolSubmissions with null committees still return.
		query.setPathOuterJoin("committee");
		
		Iterator iter = this.getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
		List<ProtocolAssociateWorkflowSearch> searchResults = new ArrayList<ProtocolAssociateWorkflowSearch>();
		while(iter.hasNext()) {
			Object[] results = (Object[]) iter.next();
			ProtocolAssociateWorkflowSearch paws = new ProtocolAssociateWorkflowSearch();
			paws.setSubmissionDate(results[0] != null ? new Date(((Timestamp) results[0]).getTime()) : null);
			paws.setProtocolNumber((String) results[1]);
			paws.setProtocolStatusCode((String) results[2]);
			paws.setProtocolStatusDescription((String) results[3]);
			paws.setExpirationDate(results[4] != null ? new Date(((Timestamp) results[4]).getTime()) : null);
			paws.setSubmissionTypeCode((String) results[5]);
			paws.setSubmissionTypeDescription((String) results[6]);
			paws.setSubmissionStatusCode((String) results[7]);
			paws.setSubmissionStatusDescription((String) results[8]);
			paws.setCommitteeId((String) results[9]);
			paws.setCommitteeName((String) results[10]);
			paws.setDocumentNumber((String) results[11]);
			paws.setProtocolPiName((String) results[12]);
			paws.setSubmissionId(((BigDecimal) results[13]).longValue());
			paws.setProtocolId(((BigDecimal) results[14]).longValue());
			searchResults.add(paws);
		}
		
		return searchResults;
	}
	
	/**
	 * Adds a sub query to the main Criteria that limits the results to those
	 * that have an Associate custom attribute that matches the query.
	 * 
	 * @param criteria	The Criteria object to add to.
	 * @param associate	The user name of the Associate to filter on.
	 */
	private void addAssociateCriteria(Criteria criteria, String associate) {
		Criteria associateCriteria = new Criteria();

		associateCriteria.addEqualTo("value", associate);
		associateCriteria.addEqualTo("customAttribute.name", ProtocolAssociateWorkflowConstants.ASSOCIATE_CUSTOM_ATTRIBUTE_NAME);
		
		ReportQueryByCriteria subQuery = QueryFactory.newReportQuery(CustomAttributeDocValue.class, associateCriteria);
		subQuery.setAttributes(new String[]{ "documentNumber" });
		
		criteria.addIn("protocol.protocolDocument.documentNumber", subQuery);
	}
	
	/**
	 * Helper method to generate the search Criteria for the query.
	 *  
	 * @param fieldValues	The map of field names and values to filter on.
	 * @return				Returns the Criteria built from the fieldValues.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Criteria buildSearchCriteria(Map fieldValues) {
		
		//convert search values to collections for multiselects
		for (String multiselectField : ProtocolAssociateWorkflowConstants.MULTISELECT_FIELDS) {
			String stringSearhValue = (String)fieldValues.get(multiselectField);
			
			if ( !StringUtils.isBlank(stringSearhValue) ) {
				List<String> searchFieldsCollection = Arrays.asList(stringSearhValue.split(","));
				
				fieldValues.remove(multiselectField);
				fieldValues.put(multiselectField, searchFieldsCollection);
			}
		}
		
		Criteria criteria = getCollectionCriteriaFromMap(new ProtocolAssociateWorkflowSearch(), fieldValues);
		
		criteria.addEqualTo("protocol.protocolPersons.protocolPersonRoleId", "PI");
		criteria.addEqualTo("protocol.active", KRADConstants.YES_INDICATOR_VALUE);
		
		// Criteria to pick only the ProtocolSubmission with the max submissionNumber.
		Criteria subCriteria = new Criteria();
		subCriteria.setAlias("p2");
		subCriteria.addEqualToField("protocolId", Criteria.PARENT_QUERY_PREFIX + "protocolId");
		subCriteria.addGreaterThanField("submissionNumber", Criteria.PARENT_QUERY_PREFIX + "submissionNumber");
		ReportQueryByCriteria subQuery = QueryFactory.newReportQuery(ProtocolSubmission.class, subCriteria);
		
		criteria.addNotExists(subQuery);
		
		return criteria;
	}
	
	/**
	 * Adds the Criteria for the Chair Review Complete query option to the main
	 * search criteria. It builds criteria based on reviewer approved and admin
	 * accepted sub queries.
	 * 
	 * @param criteria	The Criteria object to add to.
	 */
	private void addChairReviewCompleteCriteria(Criteria criteria) {
		criteria.addEqualTo("protocol.protocolStatus.description", SUBMITTED_TO_IRB);
		addReviewerApprovedSubQuery(criteria);
		addAdminAcceptedSubQuery(criteria);
	}

	/**
	 * Adds the criteria that filters on the admin accepted sub query. 
	 * 
	 * @param criteria	The Criteria object to add to.
	 */
	private void addAdminAcceptedSubQuery(Criteria criteria) {
		ReportQueryByCriteria adminAcceptedSubQuery = getAdminAcceptedSubQuery();
		if(adminAcceptedSubQuery != null) {
			criteria.addIn("protocol.protocolId", adminAcceptedSubQuery);
		}
	}
	
	/**
	 * Generates a sub query on the ProtocolOnlineReview such that all
	 * adminAccepted values are No.
	 * 
	 * @return	A query of ProtocolOnlineReview.
	 */
	private ReportQueryByCriteria getAdminAcceptedSubQuery() {
		Criteria adminAcceptedCriteria = new Criteria();
		adminAcceptedCriteria.addEqualTo("adminAccepted", KRADConstants.NO_INDICATOR_VALUE);
		adminAcceptedCriteria.addNotEqualTo("protocolOnlineReviewStatusCode", CANCELED_ONLINE_REVIEW_STATUS_CODE);
		ReportQueryByCriteria adminAcceptedSubQuery = QueryFactory.newReportQuery(ProtocolOnlineReview.class, adminAcceptedCriteria);
		adminAcceptedSubQuery.setAttributes(new String[] { "protocolId" });
		
		return adminAcceptedSubQuery;
	}
	
	/**
	 * Adds the criteria that filters on the reviewer approved sub query. 
	 * 
	 * @param criteria	The Criteria object to add to.
	 */
	private void addReviewerApprovedSubQuery(Criteria criteria) {
		ReportQueryByCriteria reviewerApprovedSubQuery = getReviewerApprovedSubQuery();
		if(reviewerApprovedSubQuery != null) {
			criteria.addNotIn("protocol.protocolId", reviewerApprovedSubQuery);
		}
	}

	/**
	 * Generates a sub query on the ProtocolOnlineReview such that all
	 * reviewerApproved values are No.
	 * 
	 * @return	A query of ProtocolOnlineReview.
	 */
	private ReportQueryByCriteria getReviewerApprovedSubQuery() {
		Criteria reviewerApprovedCriteria = new Criteria();
		reviewerApprovedCriteria.addEqualTo("reviewerApproved", KRADConstants.NO_INDICATOR_VALUE);
		reviewerApprovedCriteria.addNotEqualTo("protocolOnlineReviewStatusCode", CANCELED_ONLINE_REVIEW_STATUS_CODE);
		ReportQueryByCriteria reviewerApprovedSubQuery = QueryFactory.newReportQuery(ProtocolOnlineReview.class, reviewerApprovedCriteria);
		reviewerApprovedSubQuery.setAttributes(new String[] { "protocolId" });
		
		return reviewerApprovedSubQuery;
	}

	/**
	 * Queries from the datababase the Custom Attribute values for of a custom attribute name,
	 * given list of document numbers.
	 * 
	 * @param documentNumbers			The list of document numbers for which to get the custom attributes.
	 * @param customAttributeFieldName	The name of the custom attribute for which to get the value.
	 * @return							A collection of custom attributes for the given document numbers.
	 */
	@SuppressWarnings("unchecked")
	public Collection<CustomAttributeDocValue> getCustomAttributeValuesForDocs(Collection<String> documentNumbers, String customAttributeFieldName) {
		Criteria associateCriteria = new Criteria();
		associateCriteria.addEqualTo("customAttribute.name", customAttributeFieldName);
		associateCriteria.addIn("documentNumber", documentNumbers);
		
		QueryByCriteria query = QueryFactory.newQuery(CustomAttributeDocValue.class, associateCriteria);
		query.addOrderByAscending("documentNumber");
		Collection<CustomAttributeDocValue> results = this.getPersistenceBrokerTemplate().getCollectionByQuery(query);
		
		return results;
	}
	
	/**
     * Builds up criteria object based on the object and map.
     * @param businessObject
     * @param formProps
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	private Criteria getCollectionCriteriaFromMap(BusinessObject businessObject, Map formProps) {
        Criteria criteria = new Criteria();
        Iterator propsIter = formProps.keySet().iterator();
        while (propsIter.hasNext()) {
            String propertyName = (String) propsIter.next();
            if (formProps.get(propertyName) instanceof List) {
            	List<String> searchValuesList = (List<String>)formProps.get(propertyName);
            	criteria.addAndCriteria(buildOrCriteriaFromMultiValueList(searchValuesList, propertyName));
            } else {
                if (!lookupDao.createCriteria(businessObject, (String) formProps.get(propertyName), propertyName, isCaseSensitive(businessObject,  propertyName), false, criteria)) {
                    continue;
                }
            }
        }
        return criteria;
    }
    
    /**
     * Takes a list of values and builds OR Criteria for each one on the given field name.
     * 
     * @param valuesList	List of values for which to create each OR Criteria.
     * @param propertyName	The field name that the criteria should be built for.
     * @return				A Criteria for each value in the multivalue valuesList.
     */
	private Criteria buildOrCriteriaFromMultiValueList(List<String> valuesList, String propertyName) {
		Criteria orCriteria = new Criteria();
		for (String value : valuesList) {
			Criteria equalCriteria = new Criteria();
			equalCriteria.addEqualTo(propertyName, value);
			orCriteria.addOrCriteria(equalCriteria);
		}
		
		return orCriteria;
	}
    
    
    /*
     * extract method for casesensitive in method getCollectionCriteriaFromMap
     */
    protected boolean isCaseSensitive(BusinessObject persistBo, String  propertyName) {
        
        boolean caseInsensitive = false;
        if (dataDictionaryService.isAttributeDefined(persistBo.getClass(), propertyName)) {
            caseInsensitive = !dataDictionaryService.getAttributeForceUppercase(persistBo.getClass(), propertyName);
        }
        return caseInsensitive;
    }
	
    
    /**
     * @param lookupDao
     */
    public void setLookupDao(LookupDao lookupDao) {
        this.lookupDao = lookupDao;
    }
    
	/**
	 * @return the dataDictionaryService
	 */
	protected DataDictionaryService getDataDictionaryService() {
		return dataDictionaryService;
	}

	/**
	 * @param dataDictionaryService the dataDictionaryService to set
	 */
	public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
		this.dataDictionaryService = dataDictionaryService;
	}

    
}
