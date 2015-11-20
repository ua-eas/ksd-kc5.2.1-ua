package edu.arizona.kra.proposaldevelopment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.common.util.StringUtils;
import org.kuali.kra.infrastructure.PermissionConstants;
import org.kuali.kra.lookup.KraLookupableHelperServiceImpl;
import org.kuali.kra.service.KcPersonService;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.config.property.Config;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.coreservice.framework.CoreFrameworkServiceLocator;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.Column;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.UrlFactory;

import edu.arizona.kra.proposaldevelopment.dao.PropDevRoutingStateDao;
import edu.arizona.kra.service.CustomAuthorizationService;



@SuppressWarnings({ "serial", "unchecked" })
public class PropDevRoutingStateLookupableHelperServiceImpl extends KraLookupableHelperServiceImpl {

    private static final String SEARCH_RESULT_PROPERTY_NAME_DOCUMENT_NUMBER = "proposalDocumentNumber";
    private static final String SEARCH_RESULT_PROPERTY_NAME_ROUTE_LOG = "routeLog";
    private static final String SEARCH_RESULT_PROPERTY_NAME_SPONSOR = "sponsorName";
    private static final String SEARCH_RESULT_PROPERTY_NAME_LEAD_UNIT_NUMBER = "leadUnitNumber";
    private static final String SEARCH_RESULT_PROPERTY_NAME_LEAD_UNIT_NAME = "leadUnitName";
    
    private static final String PROPDEV_DOCUMENT = "ProposalDevelopmentDocument";
    private static final String PROPDEV_NUMBER = "proposalNumber";
    private static final String PROPDEV_ACTION = "proposalDevelopmentProposal.do";
    
    private CustomAuthorizationService authorizationService;
    private PropDevRoutingStateDao dataAccessObject;
    private KcPersonService kcPersonService;
  
    
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
        List <ProposalDevelopmentRoutingState> results = new ArrayList<ProposalDevelopmentRoutingState>();
        try {
            results = getDataAccessObject().getPropDevRoutingState(fieldValues);
        } catch (Exception e){
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
                    break;
                }
            }
        }
    }
    
    
    protected PropDevRoutingStateDao getDataAccessObject() {
        return dataAccessObject;
    }

    public void setDataAccessObject(PropDevRoutingStateDao dataAccessObject) {
        this.dataAccessObject = dataAccessObject;
    }
    
    public void setAuthorizationService(CustomAuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }
    
    protected CustomAuthorizationService getAuthorizationService() {
        return authorizationService;
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
        return super.getInquiryUrl(bo, propertyName);
    }

    
    protected HtmlData.AnchorHtmlData generateRouteLogUrl(String documentId) {
        HtmlData.AnchorHtmlData link = new HtmlData.AnchorHtmlData();
        if (isRouteLogPopup()) {
            link.setTarget("_blank");
        }
        else {
            link.setTarget("_self");
        }
        link.setDisplayText("Route Log for document " + documentId);
        String url = ConfigContext.getCurrentContextConfig().getProperty(Config.KEW_URL) + "/" +
                "RouteLog.do?documentId=" + documentId;
        link.setHref(url);
        return link;
    }
     
    
    protected HtmlData.AnchorHtmlData generateDocumentUrl(ProposalDevelopmentRoutingState propDevRoutingState) {
        String documentNumber = propDevRoutingState.getProposalDocumentNumber();
        String proposalNumber = propDevRoutingState.getProposalNumber();
        String unitNumber = propDevRoutingState.getLeadUnitNumber();
        String currentUser = GlobalVariables.getUserSession().getPrincipalId();
        
        boolean canModifyProposal = authorizationService.hasPermissionOnPropDev(currentUser, proposalNumber, unitNumber, PermissionConstants.MODIFY_PROPOSAL);
        boolean canViewProposal = authorizationService.hasPermissionOnPropDev(currentUser, proposalNumber, unitNumber, PermissionConstants.VIEW_PROPOSAL);
        
        if ( canViewProposal ){
            HtmlData.AnchorHtmlData link = new HtmlData.AnchorHtmlData();
            link.setTarget("_blank");
            link.setDisplayText(documentNumber);

            Properties parameters = new Properties();
            parameters.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, KRADConstants.DOC_HANDLER_METHOD);
            parameters.put(KRADConstants.PARAMETER_COMMAND, KewApiConstants.DOCSEARCH_COMMAND);
            parameters.put(KRADConstants.DOCUMENT_TYPE_NAME, getDocumentTypeName());
            parameters.put("viewDocument", !canModifyProposal);
            parameters.put("docId", documentNumber);
            String url  = UrlFactory.parameterizeUrl("../"+getHtmlAction(), parameters);

            link.setHref(url);
            return link;
        }
        
        return new HtmlData.AnchorHtmlData();
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
    
    /**
     * Returns true if the route log should open in a new window.
     */
    public boolean isRouteLogPopup() {
        return BooleanUtils.toBooleanDefaultIfNull(
                CoreFrameworkServiceLocator.getParameterService().getParameterValueAsBoolean(KewApiConstants.KEW_NAMESPACE,
                        KRADConstants.DetailTypes.DOCUMENT_SEARCH_DETAIL_TYPE,
                        KewApiConstants.DOCUMENT_SEARCH_ROUTE_LOG_POPUP_IND), true);
    }
    
}
