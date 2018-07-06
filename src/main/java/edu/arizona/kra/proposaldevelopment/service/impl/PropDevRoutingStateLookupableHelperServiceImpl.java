/*
 * Copyright 2005-2015 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.osedu.org/licenses/ECL-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.arizona.kra.proposaldevelopment.service.impl;

import edu.arizona.kra.proposaldevelopment.PropDevRoutingStateConstants;
import edu.arizona.kra.proposaldevelopment.bo.ProposalDevelopmentRoutingState;
import edu.arizona.kra.proposaldevelopment.service.CustomAuthorizationService;
import edu.arizona.kra.proposaldevelopment.service.PropDevRoutingStateService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.common.util.StringUtils;
import org.kuali.kra.lookup.KraLookupableHelperServiceImpl;
import org.kuali.kra.service.KcPersonService;
import org.kuali.rice.core.api.config.property.Config;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.Column;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.BeanPropertyComparator;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.UrlFactory;

import java.util.*;


/**
 * Lookup Helper for the Proposal Development Routing State Dasbhoard.
 * @author nataliac
 */
@SuppressWarnings({ "unchecked" })
public class PropDevRoutingStateLookupableHelperServiceImpl extends KraLookupableHelperServiceImpl {
    private static final long serialVersionUID = -5481786990345273205L;
    private static final String SEARCH_RESULT_PROPERTY_NAME_DOCUMENT_NUMBER = "proposalDocumentNumber";
    private static final String SEARCH_RESULT_PROPERTY_NAME_ROUTE_LOG = "routeLog";
    private static final String SEARCH_RESULT_PROPERTY_NAME_SPONSOR = "sponsorName";
    private static final String SEARCH_RESULT_PROPERTY_NAME_LEAD_UNIT_NUMBER = "leadUnitNumber";
    private static final String SEARCH_RESULT_PROPERTY_NAME_LEAD_UNIT_NAME = "leadUnitName";
    private static final String SEARCH_RESULT_PROPERTY_NAME_ORD_EXPEDITED = "ORDExpedited";
    private static final String SEARCH_RESULT_PROPERTY_NAME_SPS_REVIEWER = "SPSReviewerName";
    private static final String SEARCH_RESULT_PROPERTY_NAME_INITIATOR = "initiatorPrincipalName";
    
    private static final String PROPDEV_DOCUMENT = "ProposalDevelopmentDocument";
    private static final String PROPDEV_NUMBER = "proposalNumber";
    private static final String PROPDEV_ACTION = "proposalDevelopmentProposal.do";
    
    private PropDevRoutingStateService propDevRoutingStateService;
    private CustomAuthorizationService authorizationService;
    private KcPersonService kcPersonService;
  
    private boolean canAssignORDExpedited = false;
    private boolean canAssignSPSReviewer = false;
    
    private static final Log LOG = LogFactory.getLog(PropDevRoutingStateLookupableHelperServiceImpl.class);
    
    /**
     * Method that generates the search results for the lookup framework.
     * Called by performLookup()
     * 
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        LOG.debug("getSearchResults():"+fieldValues.toString());
        checkUserPermissions();
        
        List <ProposalDevelopmentRoutingState> results = new ArrayList<ProposalDevelopmentRoutingState>();
        try {
            results = getPropDevRoutingStateService().findPropDevRoutingState(fieldValues);
            // sort list by expedited, then SPS Approve route stop then Final Proposal Received Date
            if (results.size() > 1) {
                // sort list DESC by ORDExpedited so 'Yes' rows will be shown first
                List defaultSortColumns = getDefaultSortColumns();
                if (defaultSortColumns.size() > 0) {
                    Collections.sort(results, Collections.reverseOrder(new BeanPropertyComparator(defaultSortColumns, true)));
                }
            }
        } catch (Exception e){
            e.printStackTrace();
            LOG.error(e);
        }
        return results;
    }
    
    @Override
    public Collection<? extends BusinessObject> performLookup(LookupForm lookupForm, Collection resultTable, boolean bounded) {
        Collection<? extends BusinessObject> lookupResult = super.performLookup(lookupForm, resultTable, bounded);
        postProcessResults(resultTable);
        return lookupResult;
    }
    
    /**
     * Takes a collection of result rows and changes the routeLog column to be rendered as html
     */
    protected void postProcessResults(Collection<ResultRow> resultRows) {
        for (ResultRow resultRow : resultRows) {
            for (Column column : resultRow.getColumns()) {
                // modify the route log column so that xml values are not escaped (allows for the route log <img ...> to be rendered properly)
                if ( SEARCH_RESULT_PROPERTY_NAME_ROUTE_LOG.equalsIgnoreCase(column.getPropertyName())) {
                    column.setEscapeXMLValue(false);
                } else if ( SEARCH_RESULT_PROPERTY_NAME_SPS_REVIEWER.equalsIgnoreCase(column.getPropertyName()) && canAssignSPSReviewer()){
                    setSPSReviewerAnchor(column, (ProposalDevelopmentRoutingState)resultRow.getBusinessObject());  
                }
            }
        }
    }
    
    private void checkUserPermissions(){
        LOG.debug("checkUserPermissions():Start");
        String currentUser = GlobalVariables.getUserSession().getPrincipalId();
        
        this.canAssignORDExpedited = getAuthorizationService().hasSPSPermission(currentUser, PropDevRoutingStateConstants.EDIT_ORD_EXPEDITED_PERMISSION);
        this.canAssignSPSReviewer = getAuthorizationService().hasSPSPermission(currentUser, PropDevRoutingStateConstants.EDIT_SPS_REVIEWER_PERMISSION);
        LOG.debug("checkUserPermissions():Finished: canAssignORDExpedited="+canAssignORDExpedited+" canAssignSPSReviewer="+canAssignSPSReviewer);       
    }
    
    public boolean canAssignORDExpedited() {
        return canAssignORDExpedited;
    }

    public boolean canAssignSPSReviewer() {
        return canAssignSPSReviewer;
    }

    public void setAuthorizationService(CustomAuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }
    
    protected CustomAuthorizationService getAuthorizationService() {
        return authorizationService;
    }
    
    public PropDevRoutingStateService getPropDevRoutingStateService() {
        return propDevRoutingStateService;
    }

    public void setPropDevRoutingStateService(PropDevRoutingStateService propDevRoutingStateService) {
        this.propDevRoutingStateService = propDevRoutingStateService;
    }

    protected KcPersonService getKcPersonService(){
        return kcPersonService;
    }
    
    public void setKcPersonService(KcPersonService kcPersonService) {
        this.kcPersonService = kcPersonService;
    }
    
    @Override
    protected String getDocumentTypeName() {
        return PROPDEV_DOCUMENT;
    }

    @Override
    protected String getHtmlAction() {
        return PROPDEV_ACTION;
    }

    @Override
    protected String getKeyFieldName() {
        return PROPDEV_NUMBER;
    }
    
    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject businessObject, List pkNames) {
        return new ArrayList<HtmlData>();
    }

    /**
     * Custom implementation of getInquiryUrl that sets up links on the result rows
     * TODO Instead of doing this, re-implement entire doLookup from AbstractLookupableHelperServiceImpl
     */
    @Override
    public HtmlData getInquiryUrl(BusinessObject bo, String propertyName) {
        ProposalDevelopmentRoutingState propDevRteState = (ProposalDevelopmentRoutingState) bo;

        if (SEARCH_RESULT_PROPERTY_NAME_DOCUMENT_NUMBER.equals(propertyName)) {
            return generateDocumentUrl(propDevRteState);
        } 
        else if (SEARCH_RESULT_PROPERTY_NAME_ROUTE_LOG.equals(propertyName)) {
            return generateRouteLogUrl(propDevRteState.getProposalDocumentNumber());
        } 
        else if (SEARCH_RESULT_PROPERTY_NAME_SPONSOR.equals(propertyName)){
            return generateSponsorUrl(propDevRteState.getSponsorCode(), propDevRteState.getSponsorName());
        }
        else if (SEARCH_RESULT_PROPERTY_NAME_LEAD_UNIT_NAME.equals(propertyName)){
            return generateLeadUnitUrl(propDevRteState.getLeadUnitNumber(), propDevRteState.getLeadUnitName());
        }
        else if (SEARCH_RESULT_PROPERTY_NAME_LEAD_UNIT_NUMBER.equals(propertyName)){
            return generateLeadUnitUrl(propDevRteState.getLeadUnitNumber(), null);
        }
        else if (SEARCH_RESULT_PROPERTY_NAME_ORD_EXPEDITED.equals(propertyName) && canAssignORDExpedited()){
            return generateORDExpeditedUrl(propDevRteState);
        }
        else if (SEARCH_RESULT_PROPERTY_NAME_INITIATOR.equals(propertyName) ){
            return generateInitiatorUrl(propDevRteState.getInitiatorPrincipalName());
        }
        return super.getInquiryUrl(bo, propertyName);
    }

    
    protected HtmlData.AnchorHtmlData generateRouteLogUrl(String documentId) {
        HtmlData.AnchorHtmlData link = new HtmlData.AnchorHtmlData();
        //open RouteLog in a new tab
        link.setTarget("_blank");
        link.setDisplayText("Route Log for document " + documentId);
        String url = ConfigContext.getCurrentContextConfig().getProperty(Config.KEW_URL) + "/" +
                "RouteLog.do?documentId=" + documentId;
        link.setHref(url);
        return link;
    }
     
    
    protected HtmlData.AnchorHtmlData generateDocumentUrl(ProposalDevelopmentRoutingState propDevRoutingState) {
        String documentNumber = propDevRoutingState.getProposalDocumentNumber();  

        HtmlData.AnchorHtmlData link = new HtmlData.AnchorHtmlData();
        link.setTarget("_blank");
        link.setDisplayText(documentNumber);

        Properties parameters = new Properties();
        parameters.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, KRADConstants.DOC_HANDLER_METHOD);
        parameters.put(KRADConstants.PARAMETER_COMMAND, KewApiConstants.DOCSEARCH_COMMAND);
        parameters.put(KRADConstants.DOCUMENT_TYPE_NAME, getDocumentTypeName());
        parameters.put("viewDocument", true);
        parameters.put("docId", documentNumber);
        String url  = UrlFactory.parameterizeUrl("../"+getHtmlAction(), parameters);

        link.setHref(url);
        return link;
    }
    
    
    protected HtmlData.AnchorHtmlData generateSponsorUrl(String sponsorCode, String sponsorName) {
        HtmlData.AnchorHtmlData link = new HtmlData.AnchorHtmlData();
        link.setTarget("_blank");
        link.setDisplayText(sponsorName);
        link.setTitle("Sponsor Sponsor Code="+sponsorCode);
        
        String url = ConfigContext.getCurrentContextConfig().getProperty(Config.KR_URL) +
                "/inquiry.do?businessObjectClassName=org.kuali.kra.bo.Sponsor&sponsorCode="+sponsorCode+"&methodToCall=start";
        link.setHref(url);
        return link;
    }
    
    protected HtmlData.AnchorHtmlData generateLeadUnitUrl(String leadUnitNumber, String leadUnitName) {
        HtmlData.AnchorHtmlData link = new HtmlData.AnchorHtmlData();
        link.setTarget("_blank");
        if (StringUtils.isEmpty(leadUnitName))
            link.setDisplayText(leadUnitNumber);
        else 
            link.setDisplayText(leadUnitName);
        link.setTitle("Unit Unit Number="+leadUnitNumber);
        
        String url = ConfigContext.getCurrentContextConfig().getProperty(Config.KR_URL) +
                "/inquiry.do?businessObjectClassName=org.kuali.kra.bo.Unit&unitNumber="+leadUnitNumber+"&methodToCall=start";
        link.setHref(url);
        return link;
    }

    protected HtmlData.AnchorHtmlData generateInitiatorUrl(String netID) {
        HtmlData.AnchorHtmlData link = new HtmlData.AnchorHtmlData();

        if(org.apache.commons.lang.StringUtils.isNotBlank(netID)) {
            link.setTarget("_blank");
            link.setDisplayText(netID);
            link.setTitle("Proposal Initiator NetID=" + netID);

            String url = ConfigContext.getCurrentContextConfig().getProperty(Config.KIM_URL) + "/identityManagementPersonInquiry.do?principalName=" + netID;
            link.setHref(url);
        }
        return link;
    }
    
    protected HtmlData.AnchorHtmlData generateORDExpeditedUrl(ProposalDevelopmentRoutingState propDevRoutingState) {
        String id = "ordexp_"+propDevRoutingState.getProposalNumber();
        HtmlData.AnchorHtmlData link = new HtmlData.AnchorHtmlData();
        link.setTarget("_self");
        String displayText = propDevRoutingState.isORDExpedited()?"Yes":"No";
        link.setDisplayText(displayText);
        link.setTitle(id);     
        link.setHref(displayText);
        return link;
    }
    
    protected void setSPSReviewerAnchor(Column column, ProposalDevelopmentRoutingState propDevRoutingState){
        HtmlData.AnchorHtmlData anchor = generateSPSReviewerUrl(propDevRoutingState);
        column.setColumnAnchor(anchor);
        column.setPropertyURL(anchor.getHref());
        column.setPropertyValue(anchor.getDisplayText());
    }

    
    protected HtmlData.AnchorHtmlData generateSPSReviewerUrl(ProposalDevelopmentRoutingState propDevRoutingState) {
        String id = "spsrew_"+propDevRoutingState.getProposalNumber();
        HtmlData.AnchorHtmlData link = new HtmlData.AnchorHtmlData();
        link.setTarget("_self");
        String displayText = (StringUtils.isEmpty(propDevRoutingState.getSPSReviewerName()))?"Not Assigned":propDevRoutingState.getSPSReviewerName();
        link.setDisplayText(displayText);
        link.setTitle(id);     
        link.setHref(propDevRoutingState.getSPSPersonId()==null?"-":propDevRoutingState.getSPSPersonId());
        return link;
    }
    
}
