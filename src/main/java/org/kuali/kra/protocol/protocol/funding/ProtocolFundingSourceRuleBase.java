/*
 * Copyright 2005-2016 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kra.protocol.protocol.funding;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kra.award.home.fundingproposal.AwardFundingProposal;
import org.kuali.kra.bo.FundingSourceType;
import org.kuali.kra.bo.Sponsor;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.infrastructure.KeyConstants;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.kra.rule.BusinessRuleInterface;
import org.kuali.kra.rules.ResearchDocumentRuleBase;
import org.kuali.kra.service.SponsorService;
import org.kuali.kra.institutionalproposal.home.InstitutionalProposal;
import org.kuali.kra.institutionalproposal.service.InstitutionalProposalService;

/**
 * 
 * This class provides business logic for adding a protocol funding source to a protocol. 
 * Also it uses newer paradigm for KC Event/Rule creation for reduced Interface implementation in the ProtocolDocumentRuleBase.
 */
public abstract class ProtocolFundingSourceRuleBase extends ResearchDocumentRuleBase implements BusinessRuleInterface<AddProtocolFundingSourceEventBase>{

    private ProtocolFundingSourceService protocolFundingSourceService;
    private InstitutionalProposalService institutionalProposalService;
    
    private final static String FUNDING_SOURCE_IP_TYPE = "5";
    private final static int IP_STATUS_FUNDED = 2;
        
    /**
     * This method will validate funding source based on type & check for duplicates.
     * @param addProtocolFundingSourceEvent
     * @return
     */
    public boolean processAddProtocolFundingSourceBusinessRules(AddProtocolFundingSourceEventBase addProtocolFundingSourceEvent) {
        boolean isValid = true;

        ProtocolFundingSourceBase fundingSrc = addProtocolFundingSourceEvent.getFundingSource();
        if (fundingSrc == null) {            
            isValid = false;
            reportError(Constants.PROTOCOL_FUNDING_SOURCE_TYPE_CODE_FIELD, KeyConstants.ERROR_PROTOCOL_FUNDING_SOURCE_TYPE_NOT_FOUND);             
        } else {
            isValid &= checkFundingSource(fundingSrc);
            isValid &= checkForDuplicates(addProtocolFundingSourceEvent);
            isValid &= validateInstitutionalProposalLinkage(fundingSrc);
        }
        
        return isValid;
    }
    
    /**
     * This is the standard methodName to process rule from the BusinessRuleInterface.
     * 
     * @see org.kuali.kra.rule.BusinessRuleInterface#processRules(org.kuali.kra.rule.event.KraDocumentEventBaseExtension)
     */
    public boolean processRules(AddProtocolFundingSourceEventBase addProtocolFundingSourceEvent) {
        return processAddProtocolFundingSourceBusinessRules(addProtocolFundingSourceEvent);
    }
    
    private boolean checkFundingSource(ProtocolFundingSourceBase fundingSource) {
        boolean isValid = true;
        FundingSourceType fundingSourceType = fundingSource.getFundingSourceType();
        String fundingSourceNumber = fundingSource.getFundingSourceNumber();
        String fundingSourceName =  fundingSource.getFundingSourceName();

        if (StringUtils.isBlank(fundingSource.getFundingSourceTypeCode())) {
            isValid = false;
            reportError(Constants.PROTOCOL_FUNDING_SOURCE_TYPE_CODE_FIELD, KeyConstants.ERROR_PROTOCOL_FUNDING_SOURCE_TYPE_NOT_FOUND); 
        }
        if (StringUtils.isBlank(fundingSourceNumber)) {
            isValid = false;
            reportError(Constants.PROTOCOL_FUNDING_SOURCE_NUMBER_FIELD, KeyConstants.ERROR_PROTOCOL_FUNDING_SOURCE_NUMBER_NOT_FOUND); 
        } else if (fundingSourceType != null && !getProtocolFundingSourceService().isValidIdForType(fundingSource)) {
            isValid = false;
            reportError(Constants.PROTOCOL_FUNDING_SOURCE_NUMBER_FIELD, KeyConstants.ERROR_PROTOCOL_FUNDING_SOURCE_NUMBER_INVALID_FOR_TYPE, 
                    fundingSourceType.getDescription(), fundingSourceNumber);      
        }         
        if (StringUtils.isBlank(fundingSourceName) && StringUtils.isNotBlank(fundingSource.getFundingSourceTypeCode())
                && getProtocolFundingSourceService().isEditable(fundingSource.getFundingSourceTypeCode())) {
            isValid = false;
            reportError(Constants.PROTOCOL_FUNDING_SOURCE_NAME_FIELD, KeyConstants.ERROR_PROTOCOL_FUNDING_SOURCE_NAME_NOT_FOUND);         
        }
        if (StringUtils.equalsIgnoreCase(fundingSource.getFundingSourceTypeCode(), FundingSourceType.SPONSOR)) {
            Map<String, Object> fieldValues = new HashMap<String, Object>();
            fieldValues.put("sponsorCode", fundingSource.getFundingSourceNumber());
            Sponsor sp = this.getBusinessObjectService().findByPrimaryKey(Sponsor.class, fieldValues);
            if (!this.getSponsorService().validateSponsor(sp)) {
                isValid = false;
                reportError(Constants.PROTOCOL_FUNDING_SOURCE_NUMBER_FIELD, KeyConstants.ERROR_INVALID_SPONSOR_CODE);
            }
        }

        return isValid;
    }

    /**
     * Checks if the funding source is an InstitutionalProposal that is already funded by an Award and returns an error message.
     * (see UAR-1941 for details)
     * @param fundingSource
     * @return
     */
    private boolean validateInstitutionalProposalLinkage(ProtocolFundingSourceBase fundingSource) {
        FundingSourceType fundingSourceType = fundingSource.getFundingSourceType();
        if (FUNDING_SOURCE_IP_TYPE.equals(fundingSourceType.getFundingSourceTypeCode())){
        	InstitutionalProposal ip = getInstitutionalProposalSeervice().getActiveInstitutionalProposalVersion(fundingSource.getFundingSourceNumber());
        	if ( ip != null && IP_STATUS_FUNDED ==ip.getStatusCode().intValue()){
        		String awardNumber = "";
        		if ( ip.getAwardFundingProposalsExist() ){
        			awardNumber = ip.getAwardFundingProposal(0).getAward().getAwardNumber();
        		}
        		reportError(Constants.PROTOCOL_FUNDING_SOURCE_NAME_FIELD, KeyConstants.ERROR_PROTOCOL_FUNDING_SOURCE_LINKING, awardNumber);
        		return false;
        	}
        }
       return true;
    }
    
    private boolean checkForDuplicates(AddProtocolFundingSourceEventBase addProtocolFundingSourceEvent) {
        boolean isValid = true;
        
        ProtocolFundingSourceBase fundingSrc = addProtocolFundingSourceEvent.getFundingSource();
        List<ProtocolFundingSourceBase> fundingSources = addProtocolFundingSourceEvent.getProtocolFundingSources();
        for (ProtocolFundingSourceBase theFundingSource : fundingSources) {
            if (fundingSrc.equals(theFundingSource)) {
                isValid = false;
                reportError(Constants.PROTOCOL_FUNDING_SOURCE_NUMBER_FIELD, KeyConstants.ERROR_PROTOCOL_FUNDING_SOURCE_DUPLICATE); 
            }
        }
        
        return isValid;
    }

    private ProtocolFundingSourceService getProtocolFundingSourceService() {
        if (protocolFundingSourceService == null) {
            protocolFundingSourceService =  KraServiceLocator.getService(getProtocolFundingSourceServiceClassHook());
        }
        return protocolFundingSourceService;
    }
    
    private InstitutionalProposalService getInstitutionalProposalSeervice() {
        if (institutionalProposalService == null) {
        	institutionalProposalService =  KraServiceLocator.getService(InstitutionalProposalService.class);
        }
        return institutionalProposalService;
    }

    protected abstract Class<? extends ProtocolFundingSourceService> getProtocolFundingSourceServiceClassHook();

    /**
     * This method is for mocks in JUnit.
     * @param protocolFundingSourceService
     */
    public void setProtocolFundingSourceService(ProtocolFundingSourceService protocolFundingSourceService) {
        this.protocolFundingSourceService = protocolFundingSourceService;
    }
    
    private SponsorService getSponsorService() {
        return KraServiceLocator.getService(SponsorService.class);
    }
    
}
