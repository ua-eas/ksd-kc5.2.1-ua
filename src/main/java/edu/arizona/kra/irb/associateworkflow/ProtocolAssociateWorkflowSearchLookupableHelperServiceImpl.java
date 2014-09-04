package edu.arizona.kra.irb.associateworkflow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kra.bo.CustomAttributeDocValue;
import org.kuali.kra.bo.Rolodex;
import org.kuali.kra.infrastructure.PermissionConstants;
import org.kuali.kra.irb.Protocol;
import org.kuali.kra.irb.ProtocolLookupConstants;
import org.kuali.kra.lookup.KraLookupableHelperServiceImpl;
import org.kuali.kra.protocol.personnel.ProtocolPersonBase;
import org.kuali.kra.service.KcPersonService;
import org.kuali.kra.service.KraAuthorizationService;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.kns.document.authorization.BusinessObjectRestrictions;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * 
 * This class is to create action links and inquiry url for Protocol Associate Workflow Search lookup.
 */
@SuppressWarnings({ "serial", "deprecation" })
public class ProtocolAssociateWorkflowSearchLookupableHelperServiceImpl extends KraLookupableHelperServiceImpl {

	private static final String PROTOCOL_DOCUMENT = "ProtocolDocument";
	private static final String PROTOCOL_NUMBER = "protocolNumber";
	private static final String PROTOCOL_ACTION = "protocolProtocol.do";
	private static final String CHAIR_REVIEW_COMPLETE_FIELD_NAME = "chairReviewComplete";
	private static final String ASSOCIATE_ID_FIELD_NAME = "associateUser.principalName";
	private static final String PROTOCOL_PI_NAME_FIELD_NAME = "protocolPiName";
	private static final String PROTOCOL_PERSON_PI_NAME_FIELD_NAME = "protocol.protocolPersons.personName";
	private static final String DOC_TYPE_NAME_PARAM = "&docTypeName=";
	
	private static final String[] MULTISELECT_FIELDS = new String[] { 
		"protocolStatusCode",
		"submissionTypeCode",
		"submissionStatusCode",
		"committeeId"
	};
	
	private ProtocolAssociateWorkflowSearchDao dataAccessObject;
	private KraAuthorizationService kraAuthorizationService;
	private KcPersonService kcPersonService;
	
	/**
	 * Method that generates the search results for the lookup framework.
	 * 
	 * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResults(java.util.Map)
	 */
	@Override
	public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
		// These fields are handled specially and aren't queried as is in the OJB criteria
		// so we remove them from the fieldValues map and handle them differently.
		String associateName = fieldValues.remove(ASSOCIATE_ID_FIELD_NAME);
		String chairReviewComplete = fieldValues.remove(CHAIR_REVIEW_COMPLETE_FIELD_NAME);

		// Map the protocol's person name to the piName field for the criteria to be built correctly.
		fieldValues.put(PROTOCOL_PERSON_PI_NAME_FIELD_NAME, fieldValues.remove(PROTOCOL_PI_NAME_FIELD_NAME));
		
		//Map the correct multiselect values to the fieldValues. This is not done correctly in the LookupForm in Rice
		for (String multiselectFieldName : MULTISELECT_FIELDS) {
			fieldValues.remove(multiselectFieldName);
			fieldValues.put(multiselectFieldName, getMultiselectFieldValue(multiselectFieldName));
		}
		
		List<ProtocolAssociateWorkflowSearch> searchResults = dataAccessObject.getProtocolAssociateWorkflowResults(
				fieldValues, KRADConstants.YES_INDICATOR_VALUE.equals(chairReviewComplete), associateName);
		
		populateCustomAttributeFields(searchResults);
		
		return searchResults;
	}
	
	protected String getMultiselectFieldValue( String fieldName ) {
		Map<String, String[]> requestParameters = getParameters();
		String[] stringArrayValue = requestParameters.get(fieldName);
		return StringUtils.join(stringArrayValue, ',');
	}
	
	/**
	 * Populate the Associate and Tracking Comments fields of each ProtocolAssociateWorkflowSearch
	 * from the Custom Attribute table.  Depends on the DAO to get a collection of custom attribute
	 * search results.
	 * 
	 * @param associateWorkflowResults	The list of results to apply the custom attributes to. 
	 */
	private void populateCustomAttributeFields(List<ProtocolAssociateWorkflowSearch> associateWorkflowResults) {
		// If the result set is empty, there are no documents for which to get the custom attribute fields.
		if(associateWorkflowResults.size() == 0) {
			return;
		}
		
		Collection<String> docNums = buildDocumentNumbersArray(associateWorkflowResults);
		
		Collection<CustomAttributeDocValue> associates = dataAccessObject.getCustomAttributeValuesForDocs(docNums, ProtocolAssociateWorkflowSearch.ASSOCIATE_CUSTOM_ATTRIBUTE_NAME);
		Collection<CustomAttributeDocValue> trackingComments = dataAccessObject.getCustomAttributeValuesForDocs(docNums, ProtocolAssociateWorkflowSearch.TRACKING_COMMENT_CUSTOM_ATTRIBUTE_NAME);
		
		Map<String, String> associatesMap = mapifyCustomAttributeValues(associates);
		Map<String, String> trackingCommMap = mapifyCustomAttributeValues(trackingComments);
		
		for (ProtocolAssociateWorkflowSearch paws : associateWorkflowResults) {
			paws.setAssociateUserId(associatesMap.get(paws.getDocumentNumber()));
			paws.setTrackingComments(trackingCommMap.get(paws.getDocumentNumber()));
		}
		
	}
	
	/**
	 * Builds a list of Protocol document numbers.
	 * 
	 * @param searchResults	The list of search results to get the document numbers from.
	 * @return				A collection of document numbers.
	 */
	private Collection<String> buildDocumentNumbersArray(List<ProtocolAssociateWorkflowSearch> searchResults) {
		Collection<String> docNums = new ArrayList<String>();
		for (ProtocolAssociateWorkflowSearch protocolAssociateWorkflowSearch : searchResults) {
			docNums.add(protocolAssociateWorkflowSearch.getDocumentNumber());
		}
		
		return docNums;
	}

	/**
	 * Takes a collection of custom attributes and maps the protocol document numbers
	 * to their values.
	 * 
	 * @param custAttrs	The collection of custom attributes to get the document numbers 
	 * 					and values from.
	 * @return			A map of document numbers to custom attribute values.
	 */
	private Map<String, String> mapifyCustomAttributeValues(Collection<CustomAttributeDocValue> custAttrs) {
		HashMap<String, String> map = new HashMap<String, String>();
		for (CustomAttributeDocValue custAttr : custAttrs) {
			map.put(custAttr.getDocumentNumber(), custAttr.getValue());
		}
		return map;
	}
	
    /**
     * This method is to add 'edit' and 'view' link for ProtocolAssociateWorkflowSearch lookup result.
     * 
     * @see org.kuali.kra.lookup.KraLookupableHelperServiceImpl#getCustomActionUrls(org.kuali.rice.kns.bo.BusinessObject,
     *      java.util.List)
     */
    @SuppressWarnings("rawtypes")
	@Override
    public List<HtmlData> getCustomActionUrls(BusinessObject businessObject, List pkNames) {
    	ProtocolAssociateWorkflowSearch paws = ((ProtocolAssociateWorkflowSearch) businessObject);
    	Protocol protocol = getProtocol(paws.getProtocolId());
    	paws.setProtocol(protocol);
    	
        List<HtmlData> htmlDataList = new ArrayList<HtmlData>();
        if (kraAuthorizationService.hasPermission(getUserIdentifier(), protocol, PermissionConstants.MODIFY_PROTOCOL)) {
            AnchorHtmlData editHtmlData = getViewLink(paws);
            String href = editHtmlData.getHref();
            href = href.replace("viewDocument=true", "viewDocument=false");
            editHtmlData.setHref(href);
            editHtmlData.setDisplayText("edit");
            htmlDataList.add(editHtmlData);
        }
        if (kraAuthorizationService.hasPermission(getUserIdentifier(), protocol, PermissionConstants.VIEW_PROTOCOL)) {
        	AnchorHtmlData editHtmlData = getViewLink(paws);
        	editHtmlData.setTitle("View Protocol " + paws.getProtocolNumber() );
            htmlDataList.add(editHtmlData);
        }
        return htmlDataList;
    }
    
	/** 
	 * Method that builds the action url title in the search results table
	 * 
	 * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getActionUrlTitleText(org.kuali.rice.krad.bo.BusinessObject, java.lang.String, java.util.List, org.kuali.rice.kns.document.authorization.BusinessObjectRestrictions)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	protected String getActionUrlTitleText(BusinessObject businessObject, String displayText, List pkNames, BusinessObjectRestrictions businessObjectRestrictions) {
		ProtocolAssociateWorkflowSearch paws = ((ProtocolAssociateWorkflowSearch) businessObject);
		String titleText = displayText + " Protocol " + paws.getProtocolNumber();
		return titleText;
	}
    
    /**
     * Gets the Protocol BO that matches the primary key value.
     * 
     * @param protocolId	The primary key of the Protocol to fetch.
     * @return				The Protocol object that matches the primary key.
     */
    private Protocol getProtocol(Long protocolId) {
    	Map<String, Long> pks = new HashMap<String, Long>();
    	pks.put("protocolId", protocolId);
    	Protocol protocol = (Protocol) businessObjectService.findByPrimaryKey(Protocol.class, pks);
    	return protocol;
	}

	/*
     * Create the view link url for ProtocolAssociateWorkflowSearch.
     * Based on code for ProtocolSubmission. 
     */
    protected AnchorHtmlData getViewLink(ProtocolAssociateWorkflowSearch businessObject) {
        AnchorHtmlData viewHtmlData = super.getViewLink(businessObject.getProtocol().getProtocolDocument());
        String submissionIdParam = "&submissionId=" + businessObject.getSubmissionId();
        String href = viewHtmlData.getHref();
        href = href.replace(DOC_TYPE_NAME_PARAM, submissionIdParam + DOC_TYPE_NAME_PARAM);
        viewHtmlData.setHref(href);
        return viewHtmlData;
    }

	@Override
    protected String getHtmlAction() {
        return PROTOCOL_ACTION;
    }

	@Override
	protected String getDocumentTypeName() {
		return PROTOCOL_DOCUMENT;
	}

    @Override
    protected String getKeyFieldName() {
        return PROTOCOL_NUMBER;
    }
    
    protected String getUserIdentifier() {
        return GlobalVariables.getUserSession().getPrincipalId();
    }

	public ProtocolAssociateWorkflowSearchDao getDataAccessObject() {
		return dataAccessObject;
	}

	public void setDataAccessObject(ProtocolAssociateWorkflowSearchDao dataAccessObject) {
		this.dataAccessObject = dataAccessObject;
	}
	
    public void setKraAuthorizationService(KraAuthorizationService kraAuthorizationService) {
        this.kraAuthorizationService = kraAuthorizationService;
    }
    
    public void setKcPersonService(KcPersonService kcPersonService) {
    	this.kcPersonService = kcPersonService;
    }
    
    /**
     * Generates the inquiry URL's for the various related business objects.
     * Code is modeled after that in the ProtocolSubmissionLookupableHelperService.
     * 
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getInquiryUrl(org.kuali.rice.kns.bo.BusinessObject, java.lang.String)
     */
    @Override
    public HtmlData getInquiryUrl(BusinessObject bo, String propertyName) {
    	Protocol protocol = ((ProtocolAssociateWorkflowSearch) bo).getProtocol();
    	
        BusinessObject inqBo = bo;
        String inqPropertyName = propertyName;
        HtmlData inqUrl = new AnchorHtmlData();
        if (PROTOCOL_NUMBER.equals(propertyName)) {
            inqBo = protocol;
        } else if ("piName".equals(propertyName)) {
            ProtocolPersonBase principalInvestigator = protocol.getPrincipalInvestigator();
            if (principalInvestigator != null) {
                if (StringUtils.isNotBlank(principalInvestigator.getPersonId())) {
                    inqBo = this.kcPersonService.getKcPersonByPersonId(principalInvestigator.getPersonId());
                    inqPropertyName = ProtocolLookupConstants.Property.PERSON_ID;
                }
                else {
                    if (principalInvestigator.getRolodexId() != null) {
                        inqBo = new Rolodex();
                        ((Rolodex) inqBo).setRolodexId(principalInvestigator.getRolodexId());
                        inqPropertyName = ProtocolLookupConstants.Property.ROLODEX_ID;
                    }
                }
            }
        }
        if (inqBo != null) {
            // withdraw committeeidfk = null will cause inqbo=null
            inqUrl = super.getInquiryUrl(inqBo, inqPropertyName);
        }
        return inqUrl;
    }
}
