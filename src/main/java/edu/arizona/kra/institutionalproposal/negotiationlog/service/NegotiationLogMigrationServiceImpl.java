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

package edu.arizona.kra.institutionalproposal.negotiationlog.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kra.bo.KcPerson;
import org.kuali.kra.bo.Sponsor;
import org.kuali.kra.bo.Unit;
import org.kuali.kra.institutionalproposal.home.InstitutionalProposal;
import org.kuali.kra.institutionalproposal.service.InstitutionalProposalService;
import org.kuali.kra.negotiations.bo.Negotiation;
import org.kuali.kra.negotiations.bo.NegotiationActivity;
import org.kuali.kra.negotiations.bo.NegotiationActivityType;
import org.kuali.kra.negotiations.bo.NegotiationAgreementType;
import org.kuali.kra.negotiations.bo.NegotiationAssociationType;
import org.kuali.kra.negotiations.bo.NegotiationLocation;
import org.kuali.kra.negotiations.bo.NegotiationUnassociatedDetail;
import org.kuali.kra.negotiations.bo.NegotiationsGroupingBase;
import org.kuali.kra.negotiations.document.NegotiationDocument;
import org.kuali.kra.negotiations.service.NegotiationService;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.UserSessionUtils;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.bo.AdHocRouteRecipient;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.util.GlobalVariables;

import edu.arizona.kra.institutionalproposal.negotiationlog.NegotiationLog;
import edu.arizona.kra.institutionalproposal.negotiationlog.dao.NegotiationLogDao;

/**
 * Custom UofA service with the only purpose to perform the Negotiation Log migration to the new 5.2.1 Negotiation object
 * @author nataliac
 */
public class NegotiationLogMigrationServiceImpl extends PlatformAwareDaoBaseOjb implements NegotiationLogMigrationService{
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(NegotiationLogMigrationService.class);
    
    private BusinessObjectService businessObjectService;
    private NegotiationService negotiationService;
    private InstitutionalProposalService institutionalProposalService;
    private DocumentService documentService;
    private DataDictionaryService dataDictionaryService;
    private NegotiationLogDao negotiationLogDao;
    

    private final static String NEGOTIATION_DOCUMENT_TYPE_NAME="NegotiationDocument";
    private final static String DEFAULT_NEGOTIATOR_ID="104125367669"; //krobertson
    private final static Date DEFAULT_DATE = new Date(2000,1,1);
    private final static Date DEFAULT_END_DATE = new Date(3000,1,1);
    private final static String DEFAULT_AGREEMENT_CODE = "SPS";
    private final static String DEFAULT_ACTIVITY_CODE = "OT";    
    private final static String SPS_LOCATION_CODE = "SPS";
    private final static String ORCA_LOCATION_CODE = "ORCA";
    private final static String CRS_LOCATION_CODE = "CRS";
    private final static int MAX_RESULTS = 100;
    //keeps default objs cached
    private final static Map<String, NegotiationsGroupingBase> defaultNegotiationBOs = new HashMap<String, NegotiationsGroupingBase>();
    
    /**
     * Migrates one individual negotiation log specified by the given negotiationLogId and return the resulting Negotiation object
     * @param negotiationLogId 
     * @return
     */
    @Override
    public Negotiation migrateNegotiationLog(String negotiationLogId) throws NegotiationMigrationException{
        LOG.debug("Starting negotiation log migration for "+negotiationLogId);
        NegotiationLog negotiationLog = getBusinessObjectService().findBySinglePrimaryKey(NegotiationLog.class, negotiationLogId);
        if ( negotiationLog == null ){
            LOG.error("Cannot find negotiation log:"+negotiationLogId);
            throw new NegotiationMigrationException("Cannot find negotiation log:"+negotiationLogId);
        }
        LOG.debug("Found negotiation log:"+negotiationLog.getTitle());
        Negotiation negotiation = getBusinessObjectService().findBySinglePrimaryKey(Negotiation.class, negotiationLogId);
        if ( negotiation != null ){
            LOG.error("Negotiation log:"+negotiationLogId+" has already been migrated....Skipping!");
            throw new NegotiationMigrationException("Negotiation log:"+negotiationLogId+" has already been migrated....Skipping!");
        }
        return migrateNegotiationLog(negotiationLog);       
    }
    
    
    /**
     * Migrates one individual negotiation log and return the resulting Negotiation object
     * @param negotiationLog 
     * @return
     */
    @Override
    public Negotiation migrateNegotiationLog(NegotiationLog negotiationLog) throws NegotiationMigrationException{
        LOG.debug("migrateNegotiationLog for "+negotiationLog.getNegotiationLogId());
        //create new document and workflow document
        NegotiationDocument negotiationDocument = createDocument( negotiationLog );
        Negotiation negotiation = createNegotiation(negotiationDocument);
        
        LOG.debug("Populating new negotiation from the negotiation log.");
        
        //Important Requirement: keep the same negotiation id as the Negotiation Log
        negotiation.setNegotiationId( negotiationLog.getNegotiationLogId().longValue());
                
        setNegotiatorDetails(negotiation, negotiationLog);
        
        setNegotiationDates(negotiation, negotiationLog);
        
        setNegotiationAgreement(negotiation, negotiationLog);
        
        setNegotiationAssociation(negotiation, negotiationLog);
        
        createNegotiationActivity(negotiation, negotiationLog);
        
        if ( negotiationLog.getClosed() ){
            closeNegotiation(negotiation);
        }
        
        saveNegotiation(negotiation);
        
  
        LOG.debug("Finished migration for negotiation log:"+negotiationLog.getNegotiationLogId());
        return negotiation;
    }
    
    
	/**
	 * Migrates all NegotiationLogs that have the specified status - true for opened or false - closed
	 * Returns a list with the Id of the NegotiationLogs that could not be migrated
	 */
    @Override
	public List<String> migrateNegotiationLogs(boolean completeStatus) throws NegotiationMigrationException{
        ArrayList<String> failedNegLogIds = new ArrayList<String>();
        ArrayList<String> succededNegLogIds = new ArrayList<String>();
        LOG.debug("Starting MigrateNegotiationLogs with status complete= "+completeStatus);
        try {
            
            Integer maxLogId = getNegotiationLogDao().findMaxNegotiationLogId();
            LOG.debug("Max negotiation log id= "+maxLogId);
            
            Integer currentLogId = 1;
            Integer lastMigratedId = 1;
            while ( lastMigratedId < maxLogId ){
                List <Integer> negotiationLogsToMigrate = getNegotiationLogDao().findNegotiationLogIds(lastMigratedId+1, MAX_RESULTS, completeStatus);
                if ( !negotiationLogsToMigrate.isEmpty() ){
                    Iterator negLogIdIterator = negotiationLogsToMigrate.iterator();
                    while ( negLogIdIterator.hasNext() ){
                        Integer currentNegotiationLogId = (Integer)negLogIdIterator.next();
                        LOG.debug("Migrating negotiation log id= "+currentNegotiationLogId);
                        if ( lastMigratedId < currentNegotiationLogId){
                            lastMigratedId = currentNegotiationLogId;
                        }
                        try {
                            migrateNegotiationLog(currentNegotiationLogId.toString());
                            succededNegLogIds.add(currentNegotiationLogId.toString());
                        } catch (Exception e){
                            LOG.debug("Failed migrating negotiation log id="+currentNegotiationLogId+" Exception:"+Arrays.toString(e.getStackTrace()));
                            failedNegLogIds.add( currentNegotiationLogId.toString() );
                        }
                    }
                }
                currentLogId +=MAX_RESULTS;
            }
            
        } catch ( Exception e ){
            e.printStackTrace();
            LOG.error("Exception when trying to migrate negotiation logs: "+Arrays.toString(e.getStackTrace()));
            throw new NegotiationMigrationException(e.getMessage());
        }
        LOG.debug("Finishing MigrateNegotiationLogs with status complete= "+completeStatus); 
        LOG.debug("Number of successfully migrated Negotiation Logs="+succededNegLogIds.size());
        LOG.debug("SUCCEEDED:\n"+Arrays.toString(succededNegLogIds.toArray()));
        LOG.debug("Number of FAILED migrated Negotiation Logs="+failedNegLogIds.size());
        LOG.debug("FAILED:\n"+Arrays.toString(failedNegLogIds.toArray()));
        return failedNegLogIds;
	}
	
	/**
	 * Create new enclosing NegotiationDocument with the associated workflow doc (initiated state)
	 * Doc description set to 'Migrated from Negotiation Log ###'
	 * TODO: Figure out which user should own the docs+negotiations? Maybe a dedicated NegotiationUser?7
	 * 
	 * @return
	 * @throws NegotiationMigrationException
	 */
	private NegotiationDocument createDocument(NegotiationLog negotiationLog) throws NegotiationMigrationException {
	    LOG.debug("Creating new NegotiationDocument");
	    Document doc;
	    try {
    	    doc = getDocumentService().getNewDocument(getDataDictionaryService().getDocumentClassByTypeName(NEGOTIATION_DOCUMENT_TYPE_NAME));
    	    UserSessionUtils.addWorkflowDocument(GlobalVariables.getUserSession(), doc.getDocumentHeader().getWorkflowDocument());
    	    doc.getDocumentHeader().setDocumentDescription("Migrated from Negotiation Log "+negotiationLog.getNegotiationLogId());
	    } catch (WorkflowException e) {
	        LOG.error("Cannot create new negotiation document/workflow!");
	        throw new NegotiationMigrationException("Cannot create new negotiation document/workflow! "+Arrays.toString(e.getStackTrace()));
	    }
	    LOG.debug("Finished Creating new NegotiationDocument:"+doc.getDocumentNumber());
	    return (NegotiationDocument) doc;
	}
	
	
	/**
	 * Create a new and empty Negotiation object with the Status=in progress with the correct links to its associated NegotiationDocument
	 * @param negotiationDocument
	 * @return
	 */
	private Negotiation createNegotiation(NegotiationDocument negotiationDocument) {
        LOG.debug("Creating new Negotiation");
        Negotiation negotiation = negotiationDocument.getNegotiation();
        negotiation.setNegotiationStatus(
                getNegotiationService().getNegotiationStatus(getNegotiationService().getInProgressStatusCodes().get(0)));
        negotiation.setNegotiationStatusId(negotiationDocument.getNegotiation().getNegotiationStatus().getId());
        negotiation.setNegotiationDocument(negotiationDocument);
        negotiation.setDocumentNumber(negotiationDocument.getDocumentNumber());
        LOG.debug("Finished Creating new Negotiation:"+negotiation.getNegotiationDocument().getDocumentNumber());
        return negotiation;
    }
	
	
	/**
	 * Sets the negotiator id, full name or default values if the negotiation log does not have a correct personId
	 * @param negotiation
	 * @param negotiationLog
	 */
	private void setNegotiatorDetails(Negotiation negotiation, NegotiationLog negotiationLog){
	    LOG.debug("setNegotiatorDetails personId="+negotiationLog.getNegotiatorPersonId());
	    KcPerson negotiator = negotiationLog.getNegotiator();
	    if ( StringUtils.isEmpty( negotiationLog.getNegotiatorPersonId() ) || negotiator == null ){
	        LOG.debug("setNegotiatorDetails personId is NULL, using the the default negotiator.");
	        negotiator = KcPerson.fromPersonId( DEFAULT_NEGOTIATOR_ID );
	    } 
	    
	    LOG.debug("Negotiator = "+negotiator.getFullName());
	    negotiation.setNegotiatorPersonId( negotiator.getPersonId() );
	    negotiation.setNegotiatorName( negotiator.getFullName() );
	    negotiation.setNegotiatorUserName( negotiator.getUserName() );
	    
	    LOG.debug("Finished setNegotiatorDetails");
	}
	
	
	private void setNegotiationDates(Negotiation negotiation, NegotiationLog negotiationLog){
	    LOG.debug("Start setNegotiationDates");
	    Date startDate = negotiationLog.getDateReceived();
	    Date endDate = negotiationLog.getDateClosed();
	    
	    if ( startDate == null ){
	        if ( negotiationLog.getStartDate() == null ){
	            if ( negotiationLog.getNegotiationStart() == null ){
	                if ( negotiationLog.getBackstop() == null ){
	                    LOG.debug(negotiationLog.getNegotiationLogId()+":Using default start date = "+DEFAULT_DATE);
	                    startDate = DEFAULT_DATE;
	                } else {
	                    startDate = negotiationLog.getBackstop();
	                }
	            } else {
	                startDate = negotiationLog.getNegotiationStart();
	            }
	        } else {
	            startDate = negotiationLog.getStartDate();
	        }
	    }
	    negotiation.setNegotiationStartDate(startDate);
	    
	    if (negotiationLog.getClosed()){
	        if ( endDate == null ){
	            if ( negotiationLog.getEndDate() == null ){
	                if ( negotiationLog.getNegotiationComplete() == null ){
	                    if ( negotiationLog.getUpdateTimestamp()==null ){
	                        LOG.debug("Using default end date.");
	                        endDate = DEFAULT_END_DATE;
	                    }
	                    else {
	                        endDate = new Date(negotiationLog.getUpdateTimestamp().getTime());
	                    }
	                }
	                else {
	                    endDate = negotiationLog.getNegotiationComplete();
	                }
	            } else {
	                endDate = negotiationLog.getEndDate();
	            }
	            
	        }
	        negotiation.setNegotiationEndDate(endDate);
	    }
	    
	    LOG.debug("Finished setNegotiationDates log="+negotiationLog.getNegotiationLogId()+" CLosed="+negotiationLog.getClosed()+" SD="+negotiation.getNegotiationStartDate()+" ED="+ negotiation.getNegotiationEndDate());
	}
	
	
	/**
	 * Sets the negotiation agreement type accordingly
	 * @param negotiation
	 * @param negotiationLog
	 */
	private void setNegotiationAgreement(Negotiation negotiation, NegotiationLog negotiationLog){
	    LOG.debug("Start setNegotiationAgreement");
	    String agreementTypeCode =  negotiationLog.getNegotiationAgreementType();
	    // TODO is there additional mapping to be done for existing types?
	    NegotiationAgreementType agreementType = findNegotiationTypeByCode(NegotiationAgreementType.class, agreementTypeCode, DEFAULT_AGREEMENT_CODE);
	    negotiation.setNegotiationAgreementType( agreementType );
	    negotiation.setNegotiationAgreementTypeId( agreementType.getId() );
	    LOG.debug("Finished setNegotiationAgreement");
    }
    
    /**
     * Sets the negotiation association type accordingly
     * TODO how to delimit the difference between associated InstProposal and Proposal Log?
     * @param negotiation
     * @param negotiationLog
     */
    private void setNegotiationAssociation(Negotiation negotiation, NegotiationLog negotiationLog){
        LOG.debug("Start setNegotiationAssociation");
        String proposalNumber = negotiationLog.getProposalNumber();
        InstitutionalProposal proposal = null;
        if ( !StringUtils.isEmpty( proposalNumber) ){
            proposal = findInstitutionalProposal(proposalNumber);
            if ( proposal!=null ){
                LOG.debug("Found the IP "+proposalNumber+" associated with this log. Setting association type to IP");
                NegotiationAssociationType ipAssociation = findNegotiationTypeByCode(NegotiationAssociationType.class, NegotiationAssociationType.INSTITUATIONAL_PROPOSAL_ASSOCIATION, NegotiationAssociationType.INSTITUATIONAL_PROPOSAL_ASSOCIATION);
                negotiation.setNegotiationAssociationType( ipAssociation );
                negotiation.setNegotiationAssociationTypeId( ipAssociation.getId() );
                //TODO: is this proposal number or proposalid???
                negotiation.setAssociatedDocument( proposal );
                negotiation.setAssociatedDocumentId( proposalNumber );
                LOG.debug("Finished setNegotiationAssociation to IP "+proposalNumber);
                return;
            }
        }
        //proposal number not existant or wrong, copy all data into the unaassoc obj
        LOG.debug("No associated IP "+proposalNumber+" was found. Creating an unassociated obj congtaining all data this log. Setting association type to NONE");
        buildNegotiationAssociatedDetail(negotiationLog, negotiation);
        LOG.debug("Finished setNegotiationAssociation");
    }
    
    /**
     * Creates the first and only activity that will encompass all that happened in the negotiation log
     * @param negotiation
     * @param negotiationLog
     * @return
     */
    private NegotiationActivity createNegotiationActivity(Negotiation negotiation, NegotiationLog negotiationLog){
        LOG.debug("Start createNegotiationActivity");
        NegotiationActivity activity = new NegotiationActivity();
        
        
        NegotiationActivityType activityType = findNegotiationTypeByCode( NegotiationActivityType.class, DEFAULT_ACTIVITY_CODE, DEFAULT_ACTIVITY_CODE);
        activity.setActivityType(activityType);
        activity.setActivityTypeId( activity.getActivityType().getId() );
        activity.setLocation( findActivityLocation(negotiationLog) );
        activity.setLocationId( activity.getLocation().getId() );
        activity.setDescription( buildActivityDescription(negotiationLog));
        
        //activity cannot start before the negotiation started, make sure they are in sync.
        activity.setStartDate( negotiation.getNegotiationStartDate() );
        activity.setCreateDate(activity.getStartDate());
        
        
        if ( negotiationLog.getClosed() ){
           //activity cannot end after the negotiation ended, make sure they are in sync.
           activity.setEndDate( negotiation.getNegotiationEndDate());
        } 
        
        activity.setLastModifiedUsername(GlobalVariables.getUserSession().getPrincipalName());
        activity.setLastModifiedDate(activity.getEndDate()!=null?activity.getEndDate():new Date(System.currentTimeMillis()));
        
        activity.setRestricted(false);
        
        negotiation.getActivities().add(activity);
        
        LOG.debug("Finished createNegotiationActivity log="+negotiationLog.getNegotiationLogId()+" SD="+activity.getStartDate()+" ED="+activity.getEndDate()+" User="+activity.getLastModifiedUsername());
        return null;
    }
    
    /**
     * Closes a negotiation and takes the necessary steps through the workflow.
     * @param negotiation
     */
    private void closeNegotiation(Negotiation negotiation)throws NegotiationMigrationException{
        LOG.debug("Start closeNegotiation");
        try {
            negotiation.setNegotiationStatus(  getNegotiationService().getNegotiationStatus(getNegotiationService().getCompleteStatusCode()) );
            negotiation.setNegotiationStatusId( negotiation.getNegotiationStatus().getId());
            if ( negotiation.getNegotiationEndDate() == null ){
                negotiation.setNegotiationEndDate(DEFAULT_END_DATE);
            }
            getDocumentService().completeDocument(negotiation.getNegotiationDocument(), "", new ArrayList<AdHocRouteRecipient>());
        } catch (Exception e){
            LOG.error("Error when closing Negotiationnegotiation id "+ negotiation.getNegotiationId()+" \n"+Arrays.toString(e.getStackTrace()));
            throw new NegotiationMigrationException( e.getMessage() );
        }
        LOG.debug("Finished closeNegotiation");
    }
    
    
    /**
     * Saves a negotiation and all the related objects associated with it
     * TODO: Figure out if we need to save separately the associated detail(if any) and the negotiation document + workflow
     * @param negotiation
     */
    private void saveNegotiation(Negotiation negotiation) throws NegotiationMigrationException{
        LOG.debug("Start saveNegotiation");
        try {
            negotiation.getNegotiationDocument().prepareForSave();
            getDocumentService().saveDocument(negotiation.getNegotiationDocument());
            getBusinessObjectService().save(negotiation);
            if ( negotiation.getUnAssociatedDetail() != null ){
                getBusinessObjectService().save( negotiation.getUnAssociatedDetail() );
                //now that we have an ID after saving the detail obj, make sure we preserve it in the assocDocId
                negotiation.setAssociatedDocumentId( negotiation.getUnAssociatedDetail().getNegotiationUnassociatedDetailId().toString());
                getBusinessObjectService().save(negotiation);
            }
        } catch (Exception e){
            LOG.error("Error when saving migrated negotiation id "+ negotiation.getNegotiationId()+" \n"+Arrays.toString(e.getStackTrace()));
            throw new NegotiationMigrationException( e.getMessage() );
        }
        LOG.debug("Finished saveNegotiation");
    }
	
    
    /**
     * Finds the NegotiationsGroupingBase type of object depending on the provided code, or returns the default one, if none found.
     * Once it finds a default object for a given code, it will store it in the "cache" map for default objs=defaultNegotiationBOs
     * @param clazz
     * @param code
     * @param defaultCode
     * @return
     */
    private <T extends NegotiationsGroupingBase>T findNegotiationTypeByCode(Class<T>clazz, String code, String defaultCode){
        Map<String,String> criteria = new HashMap();
        Collection<T> negotiationTypeBOs = null;
        //if no code provided, use the default one
        if ( !StringUtils.isEmpty(code) ){
            criteria.put("code", code);
            negotiationTypeBOs = getBusinessObjectService().findMatching(clazz, criteria);
        }
        if ( StringUtils.isEmpty(code) || negotiationTypeBOs == null ||  negotiationTypeBOs.isEmpty() ){
                //Code in didn't match any of the new ones.... setting it to the default one
                LOG.debug(clazz.toString() +" for Code "+ code + " not found. Using default:"+defaultCode);
                String key = defaultCode+":"+clazz.getName();
                if ( !defaultNegotiationBOs.containsKey(key) ){
                    criteria.put("code", defaultCode);
                    negotiationTypeBOs = getBusinessObjectService().findMatching(clazz, criteria);
                    if ( negotiationTypeBOs == null ||  negotiationTypeBOs.isEmpty() ){
                        LOG.error("Cannot find "+clazz.toString()+" default with code:"+defaultCode);
                        throw new NegotiationMigrationException("Cannot find "+clazz.toString()+" default with code:"+defaultCode);
                    }
                    defaultNegotiationBOs.put(key, (NegotiationsGroupingBase) negotiationTypeBOs.iterator().next());
                }
                return (T) defaultNegotiationBOs.get(key);
        }     
        return (T) negotiationTypeBOs.iterator().next();
    }
    
    private NegotiationLocation findActivityLocation(NegotiationLog negotiationLog){
        String locationCode = negotiationLog.getLocation();
        if ( !StringUtils.isEmpty(locationCode) && ORCA_LOCATION_CODE.equalsIgnoreCase( locationCode )  ){
           locationCode = CRS_LOCATION_CODE;
        } else {
            LOG.debug("Using default location:"+SPS_LOCATION_CODE);
            locationCode = SPS_LOCATION_CODE;
        }
        return findNegotiationTypeByCode( NegotiationLocation.class, locationCode, SPS_LOCATION_CODE);
    }
    
    /**
     * Find an Institutional proposal by its number, first search active ones, otherwise search all inactive.
     * @param proposalNumber
     * @return
     */
    private InstitutionalProposal findInstitutionalProposal(String proposalNumber) {
        InstitutionalProposal ip = getInstitutionalProposalService().getActiveInstitutionalProposalVersion(proposalNumber);
        if (ip == null) {
            //the proposal_number doesn't have an active one associated with it. so grab an inactive one, this will happen when a
            //a proposal log has been promoted to an institutional proposal but not completed yet.
            Map<String,String> params = new HashMap<String, String>();
            params.put("PROPOSAL_NUMBER", proposalNumber);
            Collection<InstitutionalProposal> proposals = getBusinessObjectService().findMatching(InstitutionalProposal.class, params);
            if (proposals != null && proposals.size() > 0) {
                ip = proposals.iterator().next();
            }
        }
        return ip;
    }
    
    /**
     * When there is no IP associated, the details from the negotiation log are copied by this method into a
     * NegotiationUnassociatedDetail that gets associated with the Negotiation with association type "NO"
     * TODO: Do we want to check that the PI and SponsorAwardNumber actually exists and it's correct?
     * @param negotiationLog
     * @param negotiation
     * @return
     */
    private NegotiationUnassociatedDetail buildNegotiationAssociatedDetail(NegotiationLog negotiationLog, Negotiation negotiation) {
        LOG.debug("Start building NegotiationUnassociatedDetail. Setting association type to NONE");
        Map<String, Object> searchFields = new HashMap<String, Object>();
        NegotiationAssociationType noAssociation = findNegotiationTypeByCode(NegotiationAssociationType.class, NegotiationAssociationType.NONE_ASSOCIATION, NegotiationAssociationType.NONE_ASSOCIATION);
        negotiation.setNegotiationAssociationType( noAssociation );
        negotiation.setNegotiationAssociationTypeId( noAssociation.getId() );
        
        NegotiationUnassociatedDetail detail = new NegotiationUnassociatedDetail();
        detail.setNegotiation(negotiation);
        detail.setNegotiationId(negotiation.getNegotiationId());
        
        String sponsorCode = negotiationLog.getSponsorCode();
        LOG.debug("Finding sponsor from negotiation log sponsor code="+sponsorCode);
        if ( !StringUtils.isEmpty(sponsorCode) ){
            searchFields.put("sponsorCode", sponsorCode);       
            List<Sponsor> sponsors = (List<Sponsor>)getBusinessObjectService().findMatching(Sponsor.class, searchFields);
            if(sponsors.size() > 0) {
                detail.setSponsor(sponsors.get(0));
                detail.setSponsorCode(sponsorCode);
            }
            else 
                LOG.debug("Could not find sponsor associated with Invalid sponsor code="+sponsorCode);
        }
           
        String unitNumber = negotiationLog.getUnitNumber();
        LOG.debug("Finding unit from negotiation log unit number="+unitNumber);
        if ( !StringUtils.isEmpty(unitNumber) ){
            searchFields.clear();
            searchFields.put("unitNumber", unitNumber);       
            Unit unit = getBusinessObjectService().findByPrimaryKey(Unit.class, searchFields);
            if( unit !=  null) {
                detail.setLeadUnit(unit);
                detail.setLeadUnitNumber(unit.getUnitNumber());
            }
            else 
                LOG.debug("Could not find lead unit associated with Invalid unit number="+unitNumber);
        }
        
        
       //TODO Figure out if we need defaults for any of the below commented out fields 
      //TODO  detail.setPrimeSponsor(sponsor);
      //TODO  detail.setPrimeSponsorCode(sponsor.getSponsorCode());
      //TODO  detail.setSubAwardOrganization(org);
      //TODO  detail.setSubAwardOrganizationId(org.getOrganizationId());
      //TODO rolodexId???detail.setPiRolodexId("306");
      //TODO detail.setContactAdminPersonId("10000000001");
        
        detail.setTitle(negotiationLog.getTitle());
        detail.setPiPersonId(negotiationLog.getPiPersonId());
        detail.setPiName(negotiationLog.getPiName());
        
        detail.setSponsorAwardNumber(negotiationLog.getSponsorAwardNumber());
        negotiation.setUnAssociatedDetail(detail);
        
        LOG.debug("Finished building NegotiationUnassociatedDetail.");
        return detail;
    }
    
    /**
     * Creates the activity description by a concatenating existing SPS and ORCA comments in the negotiation log
     * @param negotiationLog
     * @return
     */
    private String buildActivityDescription(NegotiationLog negotiationLog){
        StringBuffer activityDescription = new StringBuffer();
        if ( !StringUtils.isEmpty( negotiationLog.getSpsPreawardComments()) ){
            activityDescription.append("SPS Comments: \n");
            activityDescription.append(negotiationLog.getSpsPreawardComments());
            activityDescription.append("\n");
        }
        if ( !StringUtils.isEmpty( negotiationLog.getOrcaComments()) ){
            activityDescription.append("CRS Comments: \n");
            activityDescription.append(negotiationLog.getOrcaComments());
            activityDescription.append("\n");
        }
        //Activity description cannot be empty. Adding default text
        if ( StringUtils.isEmpty( activityDescription.toString() )){
            activityDescription.append("Migrated from NegotiationLog#");
            activityDescription.append(negotiationLog.getNegotiationLogId());
        }
        return activityDescription.toString();
    }
    
    
	protected BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
    
    
    protected NegotiationService getNegotiationService() {
        return negotiationService;
    }

    public void setNegotiationService(NegotiationService negotiationService) {
        this.negotiationService = negotiationService;
    }
    
    protected InstitutionalProposalService getInstitutionalProposalService() {
        return institutionalProposalService;
    }

    public void setInstitutionalProposalService(InstitutionalProposalService institutionalProposalService) {
        this.institutionalProposalService = institutionalProposalService;
    }
    
    
    protected DocumentService getDocumentService() {
        if (documentService == null) {
            documentService = KRADServiceLocatorWeb.getDocumentService();
        }
        return this.documentService;
    }
    
    DataDictionaryService getDataDictionaryService() {
        if ( dataDictionaryService == null) {
            dataDictionaryService = (DataDictionaryService) KRADServiceLocatorWeb.getDataDictionaryService();
        }
        return this.dataDictionaryService;
    }
    
    public NegotiationLogDao getNegotiationLogDao() {
        return negotiationLogDao;
    }


    public void setNegotiationLogDao(NegotiationLogDao negotiationLogDao) {
        this.negotiationLogDao = negotiationLogDao;
    }
    
	
}
