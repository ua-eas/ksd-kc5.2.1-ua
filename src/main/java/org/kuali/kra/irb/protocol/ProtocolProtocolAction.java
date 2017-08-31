/*
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.kra.irb.protocol;

import static org.kuali.kra.infrastructure.Constants.MAPPING_BASIC;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kra.bo.FundingSourceType;
import org.kuali.kra.bo.ResearchAreaBase;
import org.kuali.kra.common.notification.service.KcNotificationService;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.infrastructure.KeyConstants;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.kra.irb.Protocol;
import org.kuali.kra.irb.ProtocolAction;
import org.kuali.kra.irb.ProtocolDocument;
import org.kuali.kra.irb.ProtocolDocumentRule;
import org.kuali.kra.irb.ProtocolEventBase;
import org.kuali.kra.irb.ProtocolForm;
import org.kuali.kra.irb.ResearchArea;
import org.kuali.kra.irb.actions.ProtocolActionType;
import org.kuali.kra.irb.actions.notification.FundingSourceNotificationRenderer;
import org.kuali.kra.irb.actions.risklevel.ProtocolAddRiskLevelEvent;
import org.kuali.kra.irb.actions.risklevel.ProtocolRiskLevel;
import org.kuali.kra.irb.actions.risklevel.ProtocolRiskLevelService;
import org.kuali.kra.irb.actions.risklevel.ProtocolUpdateRiskLevelEvent;
import org.kuali.kra.irb.notification.IRBNotificationContext;
import org.kuali.kra.irb.notification.IRBProtocolNotification;
import org.kuali.kra.irb.protocol.funding.AddProtocolFundingSourceEvent;
import org.kuali.kra.irb.protocol.funding.LookupProtocolFundingSourceEvent;
import org.kuali.kra.irb.protocol.funding.ProtocolFundingSource;
import org.kuali.kra.irb.protocol.funding.ProtocolFundingSourceService;
import org.kuali.kra.irb.protocol.funding.ProtocolFundingSourceServiceImpl;
import org.kuali.kra.irb.protocol.funding.ProtocolProposalDevelopmentDocumentService;
import org.kuali.kra.irb.protocol.funding.SaveProtocolFundingSourceLinkEvent;
import org.kuali.kra.irb.protocol.location.AddProtocolLocationEvent;
import org.kuali.kra.irb.protocol.location.ProtocolLocation;
import org.kuali.kra.irb.protocol.location.ProtocolLocationService;
import org.kuali.kra.irb.protocol.participant.AddProtocolParticipantEvent;
import org.kuali.kra.irb.protocol.participant.ProtocolParticipant;
import org.kuali.kra.irb.protocol.participant.ProtocolParticipantService;
import org.kuali.kra.irb.protocol.reference.AddProtocolReferenceEvent;
import org.kuali.kra.irb.protocol.reference.ProtocolReference;
import org.kuali.kra.irb.protocol.reference.ProtocolReferenceBean;
import org.kuali.kra.irb.protocol.reference.ProtocolReferenceService;
import org.kuali.kra.irb.protocol.reference.ProtocolReferenceType;
import org.kuali.kra.irb.protocol.research.ProtocolResearchAreaService;
import org.kuali.kra.proposaldevelopment.bo.DevelopmentProposal;
import org.kuali.kra.proposaldevelopment.document.ProposalDevelopmentDocument;
import org.kuali.kra.protocol.ProtocolDocumentBase;
import org.kuali.kra.protocol.actions.submit.ProtocolSubmissionBase;
import org.kuali.kra.protocol.protocol.funding.ProtocolFundingSourceBase;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The ProtocolProtocolAction corresponds to the Protocol tab (web page). It is responsible for handling all user requests from that
 * tab (web page).
 */
public class ProtocolProtocolAction extends ProtocolAction {
    private static final Logger LOG = LoggerFactory.getLogger(ProtocolProtocolAction.class);
    
    private static final String CONFIRM_DELETE_PROTOCOL_FUNDING_SOURCE_KEY = "confirmDeleteProtocolFundingSource";

    /**
     * @see org.kuali.kra.web.struts.action.KraTransactionalDocumentActionBase#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        LOG.debug("execute() ENTER -> super.execute(...)");
        ActionForward actionForward = super.execute(mapping, form, request, response);
        LOG.debug("execute() Returned from super.execute(...) actionForward="+(actionForward==null?"NULL":actionForward.getName()));
        // Following is for protocol lookup - edit protocol
        ProtocolForm protocolForm = (ProtocolForm) form;
        String commandParam = request.getParameter(KRADConstants.PARAMETER_COMMAND);
        LOG.debug("execute() commandParam="+commandParam);
        if (StringUtils.isNotBlank(commandParam) && commandParam.equals(KewApiConstants.DOCSEARCH_COMMAND)
                && StringUtils.isNotBlank(request.getParameter("submissionId"))) {
            // protocolsubmission lookup
            for (ProtocolSubmissionBase protocolSubmission : protocolForm.getProtocolDocument().getProtocol().getProtocolSubmissions()) {
                if (StringUtils.isNotBlank(request.getParameter("submissionId"))) {
                    protocolForm.getProtocolDocument().getProtocol().setNotifyIrbSubmissionId(Long.parseLong(request.getParameter("submissionId")));
                }
                if (request.getParameter("submissionId").equals(protocolSubmission.getSubmissionId().toString())) {
                    protocolForm.getProtocolDocument().getProtocol().setProtocolSubmission(protocolSubmission);
                    break;
                }
            }
        }
        LOG.debug("execute() before  protocolForm.getProtocolHelper().prepareView()");
        protocolForm.getProtocolHelper().prepareView();

        if ( Constants.MAPPING_PROTOCOL_ONLINE_REVIEW.equals(commandParam) || (actionForward!=null && actionForward.getName().contains(Constants.MAPPING_PROTOCOL_ONLINE_REVIEW))){
            //force OnlineReview helper initialization
            LOG.debug("execute() COMMAND=" + commandParam + " BEFORE initializing online review helper...");
            ((ProtocolForm) form).getOnlineReviewsActionHelper().init(true);
            LOG.debug("execute() COMMAND=" + commandParam + " AFTER initializing online review helper...");
        }

        LOG.debug("execute() EXIT");
        return actionForward;
    }

    @Override
    public ActionForward headerTab(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        ProtocolForm protocolform = (ProtocolForm) form;

        String command = request.getParameter("command");
        String docId = request.getParameter("docId");
        LOG.debug("headerTab() ENTER command=  "+command+" docId="+docId);
        if (StringUtils.isNotEmpty(command) && command.equals("displayDocSearchView") && StringUtils.isNotEmpty(docId)) {
            // copy link from protocol lookup - Copy Action
            Document retrievedDocument = KRADServiceLocatorWeb.getDocumentService().getByDocumentHeaderId(docId);
            protocolform.setDocument(retrievedDocument);
        }
        LOG.debug("headerTab() _> calling super.headerTab(...)");
        return super.headerTab(mapping, form, request, response);
    }

    @Override
    protected <T extends BusinessObject> void processMultipleLookupResults(ProtocolDocumentBase protocolDocument,
            Class<T> lookupResultsBOClass, Collection<T> selectedBOs) {
        if (lookupResultsBOClass.isAssignableFrom(ResearchArea.class)) {
            ProtocolResearchAreaService service = KraServiceLocator.getService(ProtocolResearchAreaService.class);
            service.addProtocolResearchArea(protocolDocument.getProtocol(), (Collection<ResearchAreaBase>) selectedBOs);
            // finally do validation and error reporting for inactive research areas
            (new ProtocolDocumentRule()).processProtocolResearchAreaBusinessRules((ProtocolDocument) protocolDocument);
        }
        
        
    }
    
    /**
     * 
     * This method adds an <code>ProtocolParticipant</code> business object to the list of <code>ProtocolParticipants</code>
     * business objects It gets called upon add action on the Participant Types sub-panel of the Protocol panel
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward addProtocolParticipant(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) 
        throws Exception {
        
        ProtocolForm protocolForm = (ProtocolForm) form;
        ProtocolParticipant newProtocolParticipant = ((ProtocolHelper) protocolForm.getProtocolHelper()).getNewProtocolParticipant();
        List<ProtocolParticipant> protocolParticipants = ((Protocol) protocolForm.getProtocolDocument().getProtocol()).getProtocolParticipants();

        if (applyRules(new AddProtocolParticipantEvent((ProtocolDocument) protocolForm.getProtocolDocument(), newProtocolParticipant, protocolParticipants))) {
            getProtocolParticipantService().addProtocolParticipant((Protocol) protocolForm.getProtocolDocument().getProtocol(), newProtocolParticipant);
            ((ProtocolHelper) protocolForm.getProtocolHelper()).setNewProtocolParticipant(new ProtocolParticipant());          
        }

        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    /**
     * 
     * This method deletes an <code>ProtocolParticipant</code> business object from the list of <code>ProtocolParticipants</code>
     * business objects It gets called upon delete action on the Participant Types sub-panel of the Protocol panel
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward deleteProtocolParticipant(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) 
        throws Exception {
        
        ProtocolForm protocolForm = (ProtocolForm) form;
        ((Protocol) protocolForm.getProtocolDocument().getProtocol()).getProtocolParticipants().remove(getLineToDelete(request));

        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    /**
     * This method is hook to KNS, it adds ProtocolReference. Method is called in protocolAdditonalInformation.tag
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     
    public ActionForward addProtocolReference(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ProtocolForm protocolForm = (ProtocolForm) form;
        ProtocolReference newProtocolReference = protocolForm.getNewProtocolReference();

        if (applyRules(new AddProtocolReferenceEvent(Constants.EMPTY_STRING, protocolForm.getProtocolDocument(), newProtocolReference))) {

            ProtocolReferenceService service = KraServiceLocator.getService(ProtocolReferenceService.class);

            service.addProtocolReference(protocolForm.getProtocolDocument().getProtocol(), newProtocolReference);

            protocolForm.setNewProtocolReference(new ProtocolReference());

        }

        return mapping.findForward(Constants.MAPPING_BASIC);
    }*/
    
    public ActionForward addProtocolReferenceBean(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ProtocolForm protocolForm = (ProtocolForm) form;
        ProtocolReferenceBean bean = (ProtocolReferenceBean) protocolForm.getNewProtocolReferenceBean();
        
        if (applyRules(new AddProtocolReferenceEvent(Constants.EMPTY_STRING, protocolForm.getProtocolDocument(), bean))) {
            ProtocolReferenceType type = this.getBusinessObjectService().findBySinglePrimaryKey(ProtocolReferenceType.class, bean.getProtocolReferenceTypeCode());
            
            ProtocolReference ref = new ProtocolReference(bean, (Protocol) protocolForm.getProtocolDocument().getProtocol(), type);
            
            ProtocolReferenceService service = KraServiceLocator.getService(ProtocolReferenceService.class);

            service.addProtocolReference(protocolForm.getProtocolDocument().getProtocol(), ref);
            
            protocolForm.setNewProtocolReferenceBean(new ProtocolReferenceBean());
        }
        
        return mapping.findForward(Constants.MAPPING_BASIC);
    }


    /**
     * This method is hook to KNS, it deletes selected ProtocolReference from the UI list. Method is called in
     * protocolAdditonalInformation.tag
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward deleteProtocolReference(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ProtocolForm protocolForm = (ProtocolForm) form;

        protocolForm.getProtocolDocument().getProtocol().getProtocolReferences().remove(getLineToDelete(request));

        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    /**
     * This method is hook to KNS, it deletes selected ProtocolResearchArea from the UI list. Method is called in
     * protocolAdditonalInformation.tag
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward deleteProtocolResearchArea(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ProtocolForm protocolForm = (ProtocolForm) form;
        protocolForm.getProtocolDocument().getProtocol().getProtocolResearchAreas().remove(getLineToDelete(request));
        // finally do validation and error reporting for inactive research areas
        (new ProtocolDocumentRule()).processProtocolResearchAreaBusinessRules((ProtocolDocument) protocolForm.getProtocolDocument());
        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    /**
     * This method is linked to ProtocolLocationService to perform the action - Add Protocol Location. Method is called in
     * protocolLocations.tag
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward addProtocolLocation(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ProtocolForm protocolForm = (ProtocolForm) form;
        ProtocolLocation newProtocolLocation = (ProtocolLocation) protocolForm.getProtocolHelper().getNewProtocolLocation();

        if (applyRules(new AddProtocolLocationEvent(Constants.EMPTY_STRING, protocolForm.getProtocolDocument(), newProtocolLocation))) {
            getProtocolLocationService().addProtocolLocation(protocolForm.getProtocolDocument().getProtocol(), newProtocolLocation);
            protocolForm.getProtocolHelper().setNewProtocolLocation(new ProtocolLocation());
        }

        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    /**
     * This method is linked to ProtocolLocationService to perform the action - Delete Protocol Location. Method is called in
     * protocolLocations.tag
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward deleteProtocolLocation(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ProtocolForm protocolForm = (ProtocolForm) form;
        protocolForm.getProtocolDocument().getProtocol().getProtocolLocations().remove(getLineToDelete(request));
        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    /**
     * This method is linked to ProtocolLocationService to perform the action - Clear Protocol Location address. Method is called in
     * protocolLocations.tag
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward clearProtocolLocationAddress(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ProtocolForm protocolForm = (ProtocolForm) form;
        getProtocolLocationService().clearProtocolLocationAddress(protocolForm.getProtocolDocument().getProtocol(),
                getSelectedLine(request));
        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    /**
     * This method is to get protocol participant service
     * 
     * @return ProtocolPersonnelService
     */
    private ProtocolParticipantService getProtocolParticipantService() {
        return (ProtocolParticipantService) KraServiceLocator.getService("protocolParticipantTypeService");
    }

    /**
     * This method is to get protocol location service
     * 
     * @return ProtocolLocationService
     */
    private ProtocolLocationService getProtocolLocationService() {
        return (ProtocolLocationService) KraServiceLocator.getService("protocolLocationService");
    }

    /**
     * This method is linked to ProtocolFundingService to perform the action - Add Protocol Funding Source. Method is called in
     * protocolFundingSources.tag
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward addProtocolFundingSource(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ProtocolForm protocolForm = (ProtocolForm) form;
        ProtocolDocument protocolDocument = (ProtocolDocument) protocolForm.getProtocolDocument();
        ProtocolFundingSource fundingSource = (ProtocolFundingSource) protocolForm.getProtocolHelper().getNewFundingSource();
        List<ProtocolFundingSourceBase> protocolFundingSources = protocolDocument.getProtocol().getProtocolFundingSources();
        AddProtocolFundingSourceEvent event = new AddProtocolFundingSourceEvent(Constants.EMPTY_STRING, protocolDocument,
            fundingSource, (List)protocolFundingSources);

        protocolForm.getProtocolHelper().syncFundingSources(protocolDocument.getProtocol());

        if (applyRules(event)) {
            protocolDocument.getProtocol().getProtocolFundingSources().add(protocolForm.getProtocolHelper().getNewFundingSource());
            protocolForm.getProtocolHelper().setNewFundingSource(new ProtocolFundingSource());
        }

        return mapping.findForward(Constants.MAPPING_BASIC);
    }
    
    /**
     * This method is linked to ProtocolFundingSourceService to Delete a ProtocolFundingSource. Method is called in
     * protocolFundingSources.tag
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward deleteProtocolFundingSource(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) 
        throws Exception {
        
        return confirm(buildParameterizedConfirmationQuestion(mapping, form, request, response, CONFIRM_DELETE_PROTOCOL_FUNDING_SOURCE_KEY,
                KeyConstants.QUESTION_PROTOCOL_FUNDING_SOURCE_DELETE_CONFIRMATION), CONFIRM_DELETE_PROTOCOL_FUNDING_SOURCE_KEY, "");
    }

    /**
     * This method is linked to ProtocolFundingSourceService to Delete a ProtocolFundingSource. Method is called in
     * protocolFundingSources.tag
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward confirmDeleteProtocolFundingSource(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) 
        throws Exception {
        
        Object question = request.getParameter(KRADConstants.QUESTION_INST_ATTRIBUTE_NAME);
        if (CONFIRM_DELETE_PROTOCOL_FUNDING_SOURCE_KEY.equals(question)) {
            ProtocolForm protocolForm = (ProtocolForm) form;
            ProtocolDocument protocolDocument = (ProtocolDocument) protocolForm.getProtocolDocument();
            
            ProtocolFundingSource protocolFundingSource = (ProtocolFundingSource) protocolDocument.getProtocol().getProtocolFundingSources().remove(getLineToDelete(request));
            protocolForm.getProtocolHelper().getDeletedProtocolFundingSources().add(protocolFundingSource);
        }
        
        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    /**
     * This method is linked to ProtocolFundingSourceService to View a ProtocolFundingSource. Method is called in
     * protocolFundingSources.tag
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward viewProtocolFundingSource(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ProtocolForm protocolForm = (ProtocolForm) form;

        // Note that if the getSelectedLine doesn't find the line number in the new window's request attributes,
        // so we'll get it from the parameter list instead
        String line = request.getParameter("line");
        int lineNumber = Integer.parseInt(line);

        ProtocolFundingSource protocolFundingSource = (ProtocolFundingSource) protocolForm.getProtocolDocument().getProtocol().getProtocolFundingSources().get(
                lineNumber);

        String viewFundingSourceUrl = getProtocolFundingSourceService()
                .getViewProtocolFundingSourceUrl(protocolFundingSource, this);

        if (StringUtils.isNotEmpty(viewFundingSourceUrl)) {
            return new ActionForward(viewFundingSourceUrl, true);
        }
        else {
            return mapping.findForward(Constants.MAPPING_BASIC);
        }
    }

    /**
     * Exposing this to be used in ProtocolFundingSource Service so we can avoid stacking funding source conditional logic in the
     * action
     *
     */
    @Override
    public String buildForwardUrl(String routeHeaderId) {
        return super.buildForwardUrl(routeHeaderId);
    }


    /**
     * 
     * Takes care of forwarding to the lookup action.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward performFundingSourceLookup(ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        
        ActionForward returnAction = null;

        ProtocolForm protocolForm = (ProtocolForm) form;

        String fundingSourceTypeCode = protocolForm.getProtocolHelper().getNewFundingSource().getFundingSourceTypeCode();

        LookupProtocolFundingSourceEvent event = new LookupProtocolFundingSourceEvent(
            Constants.EMPTY_STRING, ((ProtocolForm) form).getDocument(), fundingSourceTypeCode, ProtocolEventBase.ErrorType.HARDERROR);

        if (applyRules(event)) {
            Entry<String, String> entry = getProtocolFundingSourceService().getLookupParameters(fundingSourceTypeCode);

            String boClassName = entry.getKey();
            String fieldConversions = entry.getValue();
            String fullParameter = (String) request.getAttribute(KRADConstants.METHOD_TO_CALL_ATTRIBUTE);
            String updatedParameter = getProtocolFundingSourceService().updateLookupParameter(fullParameter, boClassName, fieldConversions);

            request.setAttribute(KRADConstants.METHOD_TO_CALL_ATTRIBUTE, updatedParameter);
            returnAction = super.performLookup(mapping, form, request, response);

            protocolForm.getProtocolHelper().setEditProtocolFundingSourceName(false);
        } else {
            returnAction = mapping.findForward(MAPPING_BASIC);
        }

        return returnAction;
    }


    /**
     * This method is to get protocol location service
     * 
     * @return ProtocolFundingSourceService
     */
    private ProtocolFundingSourceService getProtocolFundingSourceService() {
        return (ProtocolFundingSourceService) KraServiceLocator.getService(ProtocolFundingSourceService.class);
    }


    /**
     * This method is to return Proposal Development Document service
     * 
     * @return ProtocolProposalDevelopmentDocumentService
     */
    private ProtocolProposalDevelopmentDocumentService getProtocolProposalDevelopmentDocumentService() {
        return (ProtocolProposalDevelopmentDocumentService) KraServiceLocator.getService(ProtocolProposalDevelopmentDocumentService.class);
    }

    /**
     * This method is linked to ProtocolFundingService to perform the action - Create Proposal Development. Method is called in
     * protocolFundingSources.tag
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward createProposalDevelopment(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ProtocolForm protocolForm = (ProtocolForm) form;
        ProtocolDocument protocolDocument = (ProtocolDocument) protocolForm.getProtocolDocument();
        
        if ( protocolForm.getProtocolHelper().isProtocolProposalDevelopmentLinkingEnabled())
        {
            ProtocolProposalDevelopmentDocumentService service = getProtocolProposalDevelopmentDocumentService(); 
            ProposalDevelopmentDocument proposalDevelopmentDocument = service.createProposalDevelopmentDocument(protocolForm);
            if (proposalDevelopmentDocument != null )
            {
                DevelopmentProposal developmentProposal = proposalDevelopmentDocument.getDevelopmentProposal();
        
                ProtocolFundingSourceServiceImpl protocolFundingSourceServiceImpl = (ProtocolFundingSourceServiceImpl) getProtocolFundingSourceService(); 
                ProtocolFundingSource proposalProtocolFundingSource = (ProtocolFundingSource) protocolFundingSourceServiceImpl.updateProtocolFundingSource(FundingSourceType.PROPOSAL_DEVELOPMENT, developmentProposal.getProposalNumber(), developmentProposal.getSponsorName());
                proposalProtocolFundingSource.setProtocol(protocolDocument.getProtocol());
               
                List<ProtocolFundingSourceBase> protocolFundingSources = protocolDocument.getProtocol().getProtocolFundingSources();
                AddProtocolFundingSourceEvent event = new AddProtocolFundingSourceEvent(Constants.EMPTY_STRING, protocolDocument,
                        proposalProtocolFundingSource, (List)protocolFundingSources);
                
                if (applyRules(event)) {
                    protocolDocument.getProtocol().getProtocolFundingSources().add(proposalProtocolFundingSource);
                }
            }
        }
        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    @Override
    public void preSave(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        super.preSave(mapping, form, request, response);
        
        preSaveProtocol(form);        
    }

    private void preSaveProtocol(ActionForm form)  throws Exception {
        ProtocolForm protocolForm = (ProtocolForm) form;
        ProtocolDocument protocolDocument = (ProtocolDocument) protocolForm.getProtocolDocument();
        List<ProtocolFundingSourceBase> protocolFundingSources = protocolDocument.getProtocol().getProtocolFundingSources();
        List<ProtocolFundingSourceBase> deletedProtocolFundingSources = protocolForm.getProtocolHelper().getDeletedProtocolFundingSources();
        protocolForm.getProtocolHelper().setNewProtocolFundingSources(protocolForm.getProtocolHelper().findNewFundingSources());
        setDeletedFundingSource(form);
        protocolForm.getProtocolHelper().prepareRequiredFieldsForSave();
        protocolForm.getProtocolHelper().createInitialProtocolAction();
        
        if (protocolDocument.getProtocol().isNew()) {
            if (applyRules(new SaveProtocolFundingSourceLinkEvent(protocolDocument, (List)protocolFundingSources, (List)deletedProtocolFundingSources))) {
                protocolForm.getProtocolHelper().syncSpecialReviewsWithFundingSources();
            }
        }
        
    }
    
    private void setDeletedFundingSource(ActionForm form) {
        
        ProtocolForm protocolForm = (ProtocolForm) form;
       protocolForm.setDeletedProtocolFundingSources(new ArrayList<ProtocolFundingSourceBase> ());
        for (ProtocolFundingSourceBase fundingSource : protocolForm.getProtocolHelper().getDeletedProtocolFundingSources()) {
            if (fundingSource.getProtocolFundingSourceId() != null) {
                protocolForm.getDeletedProtocolFundingSources().add(fundingSource);
            }
        }
    }
    
    @Override
    protected ActionForward saveOnClose(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        preSaveProtocol(form);        
        ActionForward forward = super.saveOnClose(mapping, form, request, response);
        if (GlobalVariables.getMessageMap().hasNoErrors()) {
            fundingSourceNotification(form);
        }
        return forward;
    }
    
    public ActionForward performProtocolAction(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        super.docHandler(mapping, form, request, response);
        
        return super.protocolActions(mapping, form, request, response);
    }

    @Override
    public void postSave(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        super.postSave(mapping, form, request, response);
        fundingSourceNotification(form);

    }   

    private void fundingSourceNotification(ActionForm form) {
        ProtocolForm protocolForm = (ProtocolForm) form;
        boolean fundingSourceGenerated = false;
        Protocol protocol = (Protocol) protocolForm.getProtocolDocument().getProtocol();
        for (ProtocolFundingSourceBase fundingSource : protocolForm.getProtocolHelper().getNewProtocolFundingSources()) {
            String fundingType = "'" + fundingSource.getFundingSourceType().getDescription() + "': " + fundingSource.getFundingSourceNumber();
            FundingSourceNotificationRenderer renderer = new FundingSourceNotificationRenderer(protocol, fundingType, "linked to");
            IRBNotificationContext context = new IRBNotificationContext(protocol, ProtocolActionType.FUNDING_SOURCE, "Funding Source", renderer);
            getKcNotificationService().sendNotificationAndPersist(context, new IRBProtocolNotification(), protocol);
            fundingSourceGenerated = true;
        }
        for (ProtocolFundingSourceBase fundingSource : protocolForm.getDeletedProtocolFundingSources()) {
            if (fundingSource.getProtocolFundingSourceId() != null) {
                String fundingType = "'" + fundingSource.getFundingSourceType().getDescription() + "': " + fundingSource.getFundingSourceNumber();
                FundingSourceNotificationRenderer renderer = new FundingSourceNotificationRenderer(protocol, fundingType, "removed from");
                IRBNotificationContext context = new IRBNotificationContext(protocol, ProtocolActionType.FUNDING_SOURCE, "Funding Source", renderer);
                getKcNotificationService().sendNotificationAndPersist(context, new IRBProtocolNotification(), protocol);
                fundingSourceGenerated = true;
            }
        }
        if(fundingSourceGenerated) {
            try {
                getProtocolActionRequestService().generateFundingSource(protocolForm);
            }catch(Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    
    private KcNotificationService getKcNotificationService() {
        return KraServiceLocator.getService(KcNotificationService.class);
    }
    
    /**
     * This method is linked to ProtocolRiskLevelService to perform the action - Add Risk Level. Method is called in
     * protocolRiskLevel.tag
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward addRiskLevel(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ProtocolForm protocolForm = (ProtocolForm) form;
        ProtocolDocument document = protocolForm.getProtocolDocument();
        ProtocolRiskLevel newProtocolRiskLevel = protocolForm.getProtocolHelper().getNewProtocolRiskLevel();

        if (applyRules(new ProtocolAddRiskLevelEvent(document, "protocolHelper", newProtocolRiskLevel))) {
        	getProtocolRiskLevelService().addRiskLevel(newProtocolRiskLevel, document.getProtocol());
            protocolForm.getProtocolHelper().setNewProtocolRiskLevel(new ProtocolRiskLevel());
        }

        return mapping.findForward(Constants.MAPPING_BASIC);
    }
    
    /** Deletes a risk level from the protocol indicated by the task name in the request.
    * 
    * @param mapping The mapping associated with this action.
    * @param form The Protocol form.
    * @param request The HTTP request
    * @param response The HTTP response
    * @return the forward to the current page
    */
    public ActionForward deleteRiskLevel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
       ProtocolForm protocolForm = (ProtocolForm) form;
       ProtocolDocument document = protocolForm.getProtocolDocument();
       int lineNumber = getSelectedLine(request);
       Protocol protocol = document.getProtocol();
       getProtocolRiskLevelService().deleteRiskLevel(lineNumber, protocol);
       return mapping.findForward(Constants.MAPPING_BASIC);
    }


    /**
     * Updates a persisted risk level , moving the persisted risk level to Inactive status and adding a 
     * new Active status risk level.
     * 
     * @param mapping The mapping associated with this action.
     * @param form The Protocol form.
     * @param request The HTTP request
     * @param response The HTTP response
     * @return the forward to the current page
     */
    public ActionForward updateRiskLevel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        ProtocolForm protocolForm = (ProtocolForm) form;
        ProtocolDocument document = protocolForm.getProtocolDocument();
       
        int lineNumber = getSelectedLine(request);
        ProtocolRiskLevel currentProtocolRiskLevel = document.getProtocol().getProtocolRiskLevels().get(lineNumber);
        ProtocolRiskLevel newProtocolRiskLevel = protocolForm.getProtocolHelper().getNewProtocolRiskLevel();
        
        if (applyRules(new ProtocolUpdateRiskLevelEvent(document, lineNumber))) {
            getProtocolRiskLevelService().updateRiskLevel(currentProtocolRiskLevel, newProtocolRiskLevel);
        }
        
        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    private ProtocolRiskLevelService getProtocolRiskLevelService() {
        return KraServiceLocator.getService(ProtocolRiskLevelService.class);
    }
}