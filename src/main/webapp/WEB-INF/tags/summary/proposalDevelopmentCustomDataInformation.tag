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

<%@ include file="/WEB-INF/jsp/kraTldHeader.jsp"%>
<%@ attribute name="transparentBackground" required="false" %>
<%@ attribute name="excludeInactive" required="false" %>
<%@ attribute name="headerAndFooter" required="false" %>

<c:set var="proposalDevelopmentAttributes"
	value="${DataDictionary.DevelopmentProposal.attributes}" />
<c:set var="scienceKeywordAttributes"
	value="${DataDictionary.ScienceKeyword.attributes}" />
<c:set var="textAreaFieldName"
	value="document.developmentProposalList[0].mailDescription" />
<c:set var="action" value="proposalDevelopmentApproverView" />
<c:set var="className"
	value="org.kuali.kra.proposaldevelopment.document.ProposalDevelopmentDocument" />
<c:set var="className"
	value="org.kuali.kra.proposaldevelopment.document.ProposalDevelopmentDocument" />
	
<c:set var="customAttributeGroups"
	value="${KualiForm.customDataHelper.customAttributeGroups}" />
<c:set var="customDataList"
	value="${KualiForm.customDataHelper.customDataList}" />
<c:set var="customDataListPrefix"
	value="customDataHelper.customDataList" />

<c:if test="${empty excludeInactive}" >
	<c:set var="excludeInactive" value="false" />
</c:if>
<c:if test="${empty headerAndFooter}" >
	<c:set var="headerAndFooter" value="true" />
</c:if>	
	
<kul:tab tabTitle="Custom Data Information" defaultOpen="false" transparentBackground="${transparentBackground }"
	tabErrorKey="">
	<div class="tab-container" align="center">
		<h3>
			<span class="subhead-left">Custom Data Information</span>
		</h3>
		<c:set var="fieldCount" value="0" />
			<c:forEach items="${customAttributeGroups}" var="customAttributeGroup" varStatus="groupStatus">
				<c:set var="fullName" value="${customAttributeGroup.key}" />
				<c:set var="tabErrorKey" value=""/>
				<c:choose>
					<c:when test="${fn:length(fullName) > 90}">
		 				<c:set var="tabTitleName" value="${fn:substring(fullName, 0, 90)}..."/>
					</c:when>
					<c:otherwise>
		 				<c:set var="tabTitleName" value="${fullName}"/>
					</c:otherwise> 
				</c:choose>
  
			<c:forEach items="${customAttributeGroup.value}" var="customAttributeDocument" varStatus="status">
				<c:forEach var="customAttribute" items="${customDataList}" varStatus="status"> 
						<c:if test="${customAttribute.customAttributeId == customAttributeDocument.customAttributeId}">
							<c:set var="customAttributeId" value="${customDataListPrefix}[${status.index}].value" />
						</c:if>
				</c:forEach>
			</c:forEach>
			<kul:tab tabTitle="${tabTitleName}" spanForLongTabTitle="true" defaultOpen="true" transparentBackground="${groupStatus.first && headerAndFooter}">
				<table cellpadding=0 cellspacing="0" class="result-table">
					<c:forEach items="${customAttributeGroups[fullName]}" var="customAttributeDocument" varStatus="status">
						<c:if test="${(excludeInactive eq false) or (excludeInactive eq true && customAttributeDocument.active eq true)}">
							<tr class="datatable">
								<th><div align="center">
									<c:out value="${customAttributeDocument.customAttribute.label}" />
								</div>
								</th>
								<td width="50%">
									<c:forEach var="customAttribute" items="${customDataList}" varStatus="status"> 
										<c:if test="${customAttribute.customAttributeId == customAttributeDocument.customAttributeId}">
											<c:set var="customAttributeIndex" value="${status.index}" />
											<c:set var="customAttributeValue" value="${customAttribute.value}" />
											<c:set var="customAttributeId" value="${customDataListPrefix}[${customAttributeIndex}].value" />
										</c:if>
									</c:forEach>
									<div align="center">
			           					<c:out value="${customAttributeValue}" />
			           				</div>
								</td>
							</tr>
						</c:if>
					</c:forEach>
				</table>
	    	</kul:tab>
	    		<c:set var="fieldCount" value="${fieldCount + fn:length(customAttributeGroup.value)}" />
			</c:forEach>
		<c:if test="${fn:length(customAttributeGroups) > 0 && headerAndFooter}">
	   		<kul:panelFooter />
		</c:if>
	</div>
</kul:tab>


