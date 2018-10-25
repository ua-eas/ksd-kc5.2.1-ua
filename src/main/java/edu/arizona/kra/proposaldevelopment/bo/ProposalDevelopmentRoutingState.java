/*
 * Copyright 2005-2015 The Kuali Foundation
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
package edu.arizona.kra.proposaldevelopment.bo;

import edu.arizona.kra.proposaldevelopment.PropDevRoutingStateConstants;
import org.apache.commons.lang.StringUtils;
import org.kuali.kra.bo.Sponsor;
import org.kuali.kra.bo.Unit;
import org.kuali.kra.common.permissions.Permissionable;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.infrastructure.RoleConstants;
import org.kuali.kra.proposaldevelopment.bo.DevelopmentProposal;
import org.kuali.kra.proposaldevelopment.document.ProposalDevelopmentDocument;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.bo.TransientBusinessObjectBase;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 
 * This class is encapsulates the data for the search for Proposal Developments by their routing state
 * @author nataliac
 */
public class ProposalDevelopmentRoutingState extends TransientBusinessObjectBase implements Permissionable{
    private static final long serialVersionUID = 1083657975061744488L;
    private DevelopmentProposal developmentProposal;
    private ProposalDevelopmentDocument proposalDocument;

    private String proposalNumber;
    private String proposalDocumentNumber;
    private String proposalTitle;
    private String sponsorName;
    private String sponsorCode;
    private Date sponsorDeadlineDate;
    private String sponsorDeadlineTime;
    private Date sponsorDeadlineDateTime;
    private String deadlineType;
    private String principalInvestigatorName;
    private String proposalPersonName;
    private String leadUnitNumber;
    private String leadUnitName;
    private String leadCollege;
    private String routeStopName = PropDevRoutingStateConstants.NODE_NAME_SPS_APPROVE;
    private String routeStopCollege;
    private Timestamp routeStopDate;
    private Boolean finalProposalReceived;
    private Timestamp finalProposalReceivedTime;
    private Boolean ordExpedited;
    private String spsReviewerName;
    private String spsPersonId;
    private SPSReviewer spsReviewer;
    private Unit leadUnit;
    private Unit nodeStopLeadUnit;
    private Unit workflowUnit;
    private Sponsor sponsor;
    private String workflowUnitNumber;
    private String initiatorPrincipalName;
    private String initiatorPrincipalId;



    public DevelopmentProposal getDevelopmentProposal() {
        return developmentProposal;
    }

    public void setDevelopmentProposal(DevelopmentProposal developmentProposal) {
        this.developmentProposal = developmentProposal;
    }

    public String getProposalNumber() {
        return proposalNumber;
    }

    public void setProposalNumber(String proposalNumber) {
        this.proposalNumber = proposalNumber;
    }

    public String getProposalDocumentNumber() {
        return proposalDocumentNumber;
    }

    public void setProposalDocumentNumber(String documentNumber) {
        this.proposalDocumentNumber = documentNumber;
    }

    public String getProposalTitle() {
        return proposalTitle;
    }

    public void setProposalTitle(String proposalTitle) {
        this.proposalTitle = proposalTitle;
    }

    public String getSponsorName() {
        return sponsorName;
    }

    public void setSponsorName(String sponsorName) {
        this.sponsorName = sponsorName;
    }

    public String getSponsorCode() {
        return sponsorCode;
    }

    public void setSponsorCode(String sponsorCode) {
        this.sponsorCode = sponsorCode;
    }

    public Date getSponsorDeadlineDate() {
        return sponsorDeadlineDate;
    }

    public void setSponsorDeadlineDate(Date sponsorDeadlineDate) {
        this.sponsorDeadlineDate = sponsorDeadlineDate;
    }

    public String getSponsorDeadlineTime() {
        return sponsorDeadlineTime;
    }

    public void setSponsorDeadlineTime(String sponsorDeadlineTime) {
        this.sponsorDeadlineTime = sponsorDeadlineTime;
    }

    public String getPrincipalInvestigatorName() {
        return principalInvestigatorName;
    }

    public void setPrincipalInvestigatorName(String principalInvestigatorName) {
        this.principalInvestigatorName = principalInvestigatorName;
    }

    public String getProposalPersonName() {
        return proposalPersonName;
    }

    public void setProposalPersonName(String proposalPersonName) {
        this.proposalPersonName = proposalPersonName;
    }

    public String getInitiatorPrincipalId() {
        return initiatorPrincipalId;
    }

    public void setInitiatorPrincipalId(String initiatorPrincipalId) {
        this.initiatorPrincipalId = initiatorPrincipalId;
    }

    public String getInitiatorPrincipalName() {
        return initiatorPrincipalName;
    }

    public void setInitiatorPrincipalName(String name) {
        this.initiatorPrincipalName = name;
    }

    public Person getInitiatorPerson() {
        if (StringUtils.isEmpty(initiatorPrincipalId)) {
            return null;
        }
        return KimApiServiceLocator.getPersonService().getPerson(initiatorPrincipalId);
    }

    public String getLeadUnitNumber() {
        return leadUnitNumber;
    }

    public void setLeadUnitNumber(String leadUnitNumber) {
        this.leadUnitNumber = leadUnitNumber;
    }

    public String getLeadUnitName() {
        return leadUnitName;
    }

    public void setLeadUnitName(String leadUnitName) {
        this.leadUnitName = leadUnitName;
    }

    public String getRouteStopName() {
        return routeStopName;
    }

    public void setRouteStopName(String routeStopName) {
        this.routeStopName = routeStopName;
    }

    public String getRouteStopCollege() {
        return routeStopCollege;
    }

    public void setRouteStopCollege(String routeStopCollege) {
        this.routeStopCollege = routeStopCollege;
    }

    public Timestamp getRouteStopDate() {
        return routeStopDate;
    }

    public void setRouteStopDate(Timestamp routeStopDate) {
        this.routeStopDate = routeStopDate;
    }

    public Boolean isFinalProposalReceived() {
        return finalProposalReceived;
    }

    public void setFinalProposalReceived(Boolean finalProposalReceived) {
        this.finalProposalReceived = finalProposalReceived;
    }

    public Timestamp getFinalProposalReceivedTime() {
        return finalProposalReceivedTime;
    }

    public void setFinalProposalReceivedTime(Timestamp finalProposalReceivedTime) {
        this.finalProposalReceivedTime = finalProposalReceivedTime;
    }

    public SPSReviewer getSPSReviewer() {
        return spsReviewer;
    }

    public void setSPSReviewer(SPSReviewer reviewer) {
        spsReviewer = reviewer;
        if (reviewer != null){
            spsReviewerName = reviewer.getFullName();
            spsPersonId = reviewer.getPrincipalId();
        }
    }

    public String getSPSReviewerName(){
        return spsReviewerName;
    }

    public Boolean isORDExpedited() {
        return ordExpedited;
    }

    public void setORDExpedited(Boolean ordExpedited) {
        this.ordExpedited = ordExpedited;
    }

    public String getSPSPersonId() {
        return spsPersonId;
    }

    public void setSPSPersonId(String spsPersonId) {
        this.spsPersonId = spsPersonId;
    }

    public void setSPSReviewerName(String spsReviewerName) {
        this.spsReviewerName = spsReviewerName;
    }

    /**
     * Returns the route image which can be used to construct the route log link in custom lookup helper code.
     */
    public String getRouteLog() {
        return "<img alt=\"Route Log for Document\" src=\"images/my_route_log.gif\"/>";
    }

    public String getLeadCollege() {
        return leadCollege;
    }

    public void setLeadCollege(String leadCollege) {
        this.leadCollege = leadCollege;
    }

    public Unit getNodeStopLeadUnit(){
        return this.nodeStopLeadUnit;
    }

    public void setNodeStopLeadUnit(Unit unit){
        this.nodeStopLeadUnit = unit;
    }

    public Unit getLeadUnit(){
        return this.leadUnit;
    }

    public void setLeadUnit(Unit unit){
        this.leadUnit = unit;
    }

    public Unit getWorkflowUnit() {
        return workflowUnit;
    }

    public void setWorkflowUnit(Unit workflowUnit) {
        this.workflowUnit = workflowUnit;
    }

    public Sponsor getSponsor() {
        return sponsor;
    }

    public void setSponsor(Sponsor sponsor) {
        this.sponsor = sponsor;
    }

    public ProposalDevelopmentDocument getProposalDocument() {
        return proposalDocument;
    }

    public void setProposalDocument(ProposalDevelopmentDocument proposalDocument) {
        this.proposalDocument = proposalDocument;
    }

    public String getWorkflowUnitNumber(){
        return workflowUnitNumber;
    }

    public void setWorkflowUnitNumber(String workflowUnitNumber){
        this.workflowUnitNumber = workflowUnitNumber;
    }

    public Date getSponsorDeadlineDateTime() {
        return sponsorDeadlineDateTime;
    }

    public void setSponsorDeadlineDateTime(Date sponsorDeadlineDateTime) {
        this.sponsorDeadlineDateTime = sponsorDeadlineDateTime;
    }

    public String getDeadlineType() {
        return deadlineType;
    }

    public void setDeadlineType(String deadlineType) {
        this.deadlineType = deadlineType;
    }

    @Override
    public String getDocumentKey() {
        return Permissionable.PROPOSAL_KEY;
    }

    @Override
    public String getDocumentNumberForPermission() {
        return getProposalNumber();
    }

    @Override
    public String getDocumentRoleTypeCode() {
        return RoleConstants.PROPOSAL_ROLE_TYPE;
    }

    @Override
    public String getNamespace() {
        return Constants.MODULE_NAMESPACE_PROPOSAL_DEVELOPMENT;
    }

    @Override
    public List<String> getRoleNames() {
        List<String> roleNames = new ArrayList<String>();

        roleNames.add(RoleConstants.VIEWER);
        //roleNames.add(RoleConstants.SPS_REVIEWER);

        return roleNames;
    }

    @Override
    public void populateAdditionalQualifiedRoleAttributes(Map<String, String> arg0) {
    }



}