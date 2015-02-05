<%--
 Copyright 2005-2014 The Kuali Foundation

 Licensed under the Educational Community License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.osedu.org/licenses/ECL-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ page import="org.kuali.kra.infrastructure.Constants"%>
<%@ include file="/WEB-INF/jsp/kraTldHeader.jsp"%>

<link rel="stylesheet" href="css/jquery/new_kuali.css" type="text/css" />

<c:set var="displayKeywordPanel" value="<%=session.getAttribute(Constants.KEYWORD_PANEL_DISPLAY)%>" />
<c:set var="proposalDevelopmentAttributes" value="${DataDictionary.DevelopmentProposal.attributes}" />

<c:set var="readOnly" value="${not KualiForm.editingMode['modifyProposal']}" scope="request" /> 

<kul:documentPage
	showDocumentInfo="true"
	htmlFormAction="proposalDevelopmentProposal"
	documentTypeName="ProposalDevelopmentDocument"
	renderMultipart="false"
	showTabButtons="true"
	auditCount="0"
  	headerDispatch="${KualiForm.headerDispatch}"
  	headerTabActive="proposal">
  	
<div align="right"><kul:help documentTypeName="ProposalDevelopmentDocument" pageName="Proposal" /></div>
<c:choose><c:when test="${!KualiForm.hidePropDevDocDescriptionPanel}">
	<kul:documentOverview editingMode="${KualiForm.editingMode}" />
</c:when><c:otherwise>
    <div id="workarea">
    <c:set var="requiredTransparent" value="true"/>
</c:otherwise></c:choose>
<kra-pd:proposalDevelopmentRequiredFields transparent="${requiredTransparent}"/>
<kra-pd:proposalDevelopmentSponsorProgramInformation />
<kra-pd:proposalDevelopmentOrganizationAndLocation />
<c:if test="${KualiForm.proposalDevelopmentParameters['deliveryInfoDisplayIndicator'].value == 'Y'}">
   <kra-pd:proposalDevelopmentDeliveryInfo />
</c:if>

<c:if test="${displayKeywordPanel}">
<kra-pd:proposalDevelopmentKeywords />
</c:if>

<kul:panelFooter />
<kul:documentControls transactionalDocument="true" suppressRoutingControls="true" suppressCancelButton="true" />

<SCRIPT type="text/javascript">
var kualiForm = document.forms['KualiForm'];
var kualiElements = kualiForm.elements;
var $j = jQuery.noConflict();
</SCRIPT>
<script language="javascript" src="scripts/kuali_application.js"></script>
<script language="javascript" src="dwr/interface/SponsorService.js"></script>

</kul:documentPage>
