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

<%@ attribute name="htmlFormAction" required="false" %>
<%@ attribute name="renderMultipart" required="false" %>

<%! static private final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(org.kuali.kra.proposaldevelopment.bo.Narrative.class); %>
<% long startTime = 0; %>
<% long endTime = 0; %>

    
           <% startTime = System.currentTimeMillis(); %>
<c:set var="readOnly" value="${not KualiForm.editingMode['addNarratives']}" scope="request" />

<c:set var="proposalDevelopmentAttributes" value="${DataDictionary.DevelopmentProposal.attributes}" />
<c:set var="narrativeAttributes" value="${DataDictionary.Narrative.attributes}" />
<c:set var="narrativeAttachmentAttributes" value="${DataDictionary.NarrativeAttachment.attributes}" />
<script>
	var PropDevNarrative = { };
	PropDevNarrative.narrativeTypes ={
			<c:forEach items="${KualiForm.allNarrativeTypes}" var="narrative">
				'${narrative.narrativeTypeCode}' : {'allowMultiple' : '${narrative.allowMultiple}'},
			</c:forEach>
			'nil' : 'nil'
	};
	
	PropDevNarrative.defaultRequiredProperties = {
			'narrativeTypeCode' : '<c:out value="${narrativeAttributes.narrativeTypeCode.label}"/>',
			'narrativeFile' : '<c:out value="${narrativeAttachmentAttributes.fileName.label}"/>',
			'moduleStatusCode' : '<c:out value="${narrativeAttributes.moduleStatusCode.label}"/>'
	};
	PropDevNarrative.buildListOfRequiredPropertiesForNarrative = function(prefix) {
		var properties = { };
		for (var h in this.defaultRequiredProperties) {
			properties[prefix + '.' + h] = this.defaultRequiredProperties[h];
		}
		var narrativeType = jQuery(jq_escape(prefix + '.narrativeTypeCode')).val();
		if (this.narrativeTypes[narrativeType]['allowMultiple'] == 'Y') {
			properties[prefix + '.moduleTitle'] = '<c:out value="${narrativeAttributes.moduleTitle.label}" />';
		}
		return properties;
	}
</script>



<%-- We used to calculate the # of proposal attachements by fn:length(KualiForm.document.developmentProposalList[0].narratives), but this counts all of them including the 
internal attachements.  We are just going to loop through the narratives and see which ones are going to be rendered on this panel and count them up. --%>
<c:set scope = "page" var = "proposalAttachementCount" value = "0"/>
<c:set scope = "page" var="canUpdateAllStatuses" value="true"/>

<c:forEach var="narrative" items="${KualiForm.document.developmentProposalList[0].narratives}" varStatus="status">
        	<c:if test="${narrative.narrativeType.narrativeTypeGroup eq KualiForm.proposalDevelopmentParameters['proposalNarrativeTypeGroup'].value}">
        		<c:set scope = "page" var = "proposalAttachementCount" value = "${proposalAttachementCount + 1}"/>
  			</c:if>
			<c:set var="replaceKey" value="proposalAttachment.${narrative.moduleNumber}.replace" />
			<c:set var="replaceAttachment" value="${KualiForm.editingMode[replaceKey]}" />
  			<c:set var="canUpdateAllStatuses" value="${canUpdateAllStatuses and replaceAttachment}"/>
</c:forEach>


<c:set var="action" value="proposalDevelopmentAbstractsAttachments" />

<kul:tabTop tabTitle="Proposal Attachments (${proposalAttachementCount})" defaultOpen="false" tabErrorKey="newNarrative*,document.developmentProposalList[0].narrative*">
	<div class="tab-container" align="center">
	    <kra:section permission="addNarratives">
	    	<h3>
	    		<span class="subhead-left">Add Proposal Attachments</span>
	    		<span class="subhead-right"><kul:help businessObjectClassName="org.kuali.kra.proposaldevelopment.bo.Narrative" altText="help"/></span>
	        </h3>
        </kra:section>
		<c:if test="${canUpdateAllStatuses}">
			<table cellpadding=0 cellspacing=0 summary="">
				<tbody>
		            <c:if test="${fn:length(KualiForm.document.developmentProposalList[0].narratives) > 0}">
						<tr>
							<td>
								<div align="left">
									<b>Mark all attachments</b>:
									<html:select property="customDataHelper.narrativeStatusChange"
										style="font-family: Verdana, Arial, Helvetica, sans-serif; font-size:11;">
										<html:optionsCollection
											property="customDataHelper.narrativeStatuses" value="key"
											label="value"
											style="font-family: Verdana, Arial, Helvetica, sans-serif; font-size:11;" />
									</html:select>
									<html:image
										property="methodToCall.markAllNarrativeStatuses.anchor${tabKey}"
										src="${ConfigProperties.kra.externalizable.images.url}tinybutton-update.gif"
										title="update" alt="update" styleClass="tinybutton" />
								</div>
							</td>
						</tr>
					</c:if>
				</tbody>
			</table>
	    </c:if>
        <table cellpadding=0 cellspacing=0 summary="">
            <kra:section permission="addNarratives">
            	<tbody class="addline">
					<tr>
	         		<th><div align="right"><kul:htmlAttributeLabel attributeEntry="${narrativeAttributes.narrativeTypeCode}"/></div></th>
	                <td align="left" valign="middle">
	                	<kul:htmlControlAttribute property="newNarrative.narrativeTypeCode" attributeEntry="${narrativeAttributes.narrativeTypeCode}" />
					</td>
	          		<th><div align="right"><kul:htmlAttributeLabel attributeEntry="${narrativeAttachmentAttributes.fileName}"/></div></th>
	                <td align="left" valign="middle">
	                	<html:file property="newNarrative.narrativeFile" styleId="newNarrative.narrativeFile"/>
					</td>
	          	</tr>

	          	<tr>
	          		<th><div align="right"><kul:htmlAttributeLabel attributeEntry="${narrativeAttributes.moduleStatusCode}"/></div></th>
	                <td align="left" valign="middle">
	                    <kul:htmlControlAttribute property="newNarrative.moduleStatusCode" attributeEntry="${narrativeAttributes.moduleStatusCode}" />
					</td>
	          		<th><div align="right"><kul:htmlAttributeLabel attributeEntry="${narrativeAttributes.contactName}" /></div></th>
	                <td align="left" valign="middle">
	                	<kul:htmlControlAttribute property="newNarrative.contactName" attributeEntry="${narrativeAttributes.contactName}" />
					</td>
	          	</tr>
	          	<tr>
	          		<th><div align="right"><kul:htmlAttributeLabel attributeEntry="${narrativeAttributes.updateUser}" /></div></th>
	                <td align="left" valign="middle">&nbsp;</td>
	          		<th><div align="right"><kul:htmlAttributeLabel attributeEntry="${narrativeAttributes.emailAddress}" /></div></th>
	                <td align="left" valign="middle">
	                	<kul:htmlControlAttribute property="newNarrative.emailAddress" attributeEntry="${narrativeAttributes.emailAddress}" />
					</td>
	          	</tr>
	          	<tr>
	          		<th><div align="right"><kul:htmlAttributeLabel attributeEntry="${narrativeAttributes.updateTimestamp}" /></div></th>
	                <td align="left" valign="middle">&nbsp;</td>
	          		<th><div align="right"><kul:htmlAttributeLabel attributeEntry="${narrativeAttributes.phoneNumber}" /></div></th>
	                <td align="left" valign="middle">
	                	<kul:htmlControlAttribute property="newNarrative.phoneNumber" attributeEntry="${narrativeAttributes.phoneNumber}" />
					</td>
	          	</tr>
	          	<tr>
	          		<th><div align="right"><kul:htmlAttributeLabel attributeEntry="${narrativeAttributes.comments}" /></div></th>
	                <td align="left" valign="middle">
	                	<c:set var="textAreaFieldName" value="newNarrative.comments" />
	                	<kul:htmlControlAttribute property="newNarrative.comments" attributeEntry="${narrativeAttributes.comments}" />
					</td>
	          		<th><div align="right"><kul:htmlAttributeLabel attributeEntry="${narrativeAttributes.moduleTitle}" /></div></th>
	                <td align="left" valign="middle">
	                	<c:set var="textAreaFieldName" value="newNarrative.moduleTitle" />
	                	<kul:htmlControlAttribute property="newNarrative.moduleTitle" attributeEntry="${narrativeAttributes.moduleTitle}" />
					</td>
	          	</tr>
	          	<tr>
					<td colspan=4>
						<div align="center">
							<html:image property="methodToCall.addProposalAttachment"
							src='${ConfigProperties.kra.externalizable.images.url}tinybutton-add1.gif' 
							onclick="return requirePropertiesOnAdd(PropDevNarrative.buildListOfRequiredPropertiesForNarrative('newNarrative'));" styleClass="tinybutton addButton"/>
						</div>
	                </td>
	            </tr>
	            </tbody>
            </kra:section>

 
            <c:if test="${fn:length(KualiForm.document.developmentProposalList[0].narratives) > 0}">
            <tr>
            	<td colspan="4">
            	<div  align="left">
            	
        	<c:forEach var="narrative" items="${KualiForm.document.developmentProposalList[0].narratives}" varStatus="status">
        	<c:if test="${narrative.narrativeType.narrativeTypeGroup eq KualiForm.proposalDevelopmentParameters['proposalNarrativeTypeGroup'].value}">
			<c:set var="narrType" value="${narrative.narrativeType.description}"/>
			<c:set var="narrStatus" value="${narrative.narrativeStatus.description}"/>
			<c:set var="downloadKey" value="proposalAttachment.${narrative.moduleNumber}.download" />
			<c:set var="downloadAttachment" value="${KualiForm.editingMode[downloadKey]}" />
			<c:set var="replaceKey" value="proposalAttachment.${narrative.moduleNumber}.replace" />
			<c:set var="replaceAttachment" value="${KualiForm.editingMode[replaceKey]}" />
			<c:set var="deleteKey" value="proposalAttachment.${narrative.moduleNumber}.delete" />
            <c:set var="deleteAttachment" value="${KualiForm.editingMode[deleteKey]}" />
            <c:set var="modifyStatusKey" value="proposalAttachment.${narrative.moduleNumber}.modifyStatus" />
            <c:set var="modifyStatus" value="${KualiForm.editingMode[modifyStatusKey]}"/>
			<kul:innerTab parentTab="Proposal Attachments" defaultOpen="false" tabDescription="${narrType} - ${narrStatus}" tabTitle="${status.index+1}. ${narrType} - ${narrStatus}" auditCluster="proposalAttachmentsAuditWarnings" tabAuditKey="document.developmentProposalList[0].narrative[${status.index}]*">
				<div class="innerTab-container" align="left">
					<table class=tab cellpadding=0 cellspacing=0 summary="">
			          	<tr>
			          		<th><div align="right"><kul:htmlAttributeLabel attributeEntry="${narrativeAttributes.narrativeTypeCode}" /></div></th>
			                <td align="left" valign="middle">
			                	<kul:htmlControlAttribute property="document.developmentProposalList[0].narrative[${status.index}].narrativeType.description" attributeEntry="${narrativeAttributes['narrativeType.description']}" readOnly="true" />
							</td>
			          		<th><div align="right"><kul:htmlAttributeLabel attributeEntry="${narrativeAttachmentAttributes.fileName}"/></div></th>
			                <td align="left" valign="middle">
				                <div id="replaceDiv${status.index}" style="display:block;">
				                    <kra:fileicon attachment="${narrative}"/>
					                <kul:htmlControlAttribute property="document.developmentProposalList[0].narrative[${status.index}].fileName" 
					                	 readOnly="true" attributeEntry="${narrativeAttributes.fileName}" />
				                </div>
				                <div id="fileDiv${status.index}" valign="middle" style="display:none;">
				                	<html:file property="document.developmentProposalList[0].narrative[${status.index}].narrativeFile"/>
								</div>
							</td>
			          	</tr>
			          	<tr>
			          		<th><div align="right"><kul:htmlAttributeLabel attributeEntry="${narrativeAttributes.moduleStatusCode}"/></div></th>
			                <td align="left" valign="middle">
			                	<div id="updateStatusDiv${status.index}" style="display:block;">
					                <kul:htmlControlAttribute property="document.developmentProposalList[0].narrative[${status.index}].moduleStatusCode" 
					                	  readOnly="true" attributeEntry="${narrativeAttributes.moduleStatusCode}" />
				                </div>
				                <div id="attachmentStatusDiv${status.index}" valign="middle" style="display:none;">
									<html:select property="document.developmentProposalList[0].narrative[${status.index}].moduleStatusCode"
										style="font-family: Verdana, Arial, Helvetica, sans-serif; font-size:11;">
										<html:optionsCollection
											property="customDataHelper.narrativeStatuses" value="key"
											label="value"
											style="font-family: Verdana, Arial, Helvetica, sans-serif; font-size:11;" />
									</html:select>
								</div>
							</td>
			          		<th><div align="right"><kul:htmlAttributeLabel attributeEntry="${narrativeAttributes.contactName}" /></div></th>
			                <td align="left" valign="middle">
			                	<kul:htmlControlAttribute property="document.developmentProposalList[0].narrative[${status.index}].contactName" attributeEntry="${narrativeAttributes.contactName}" />
							</td>
			          	</tr>
			          	<tr>
			          		<th><div align="right"><kul:htmlAttributeLabel attributeEntry="${narrativeAttributes.updateUser}" /></div></th>
			                <td align="left" valign="middle">
			                	<kul:htmlControlAttribute property="document.developmentProposalList[0].narrative[${status.index}].uploadUserFullName" readOnly="true" attributeEntry="${narrativeAttributes.updateUser}" />
							</td>
			          		<th><div align="right"><kul:htmlAttributeLabel attributeEntry="${narrativeAttributes.emailAddress}" /></div></th>
			                <td align="left" valign="middle">
			                	<kul:htmlControlAttribute property="document.developmentProposalList[0].narrative[${status.index}].emailAddress" attributeEntry="${narrativeAttributes.emailAddress}" />
							</td>
			          	</tr>
			          	<tr>
			          		<th><div align="right"><kul:htmlAttributeLabel attributeEntry="${narrativeAttributes.updateTimestamp}" /></div></th>
			                <td align="left" valign="middle">
			                	<kul:htmlControlAttribute property="document.developmentProposalList[0].narrative[${status.index}].timestampDisplay" readOnly="true" attributeEntry="${narrativeAttributes.updateTimestamp}" />
							</td>
			          		<th><div align="right"><kul:htmlAttributeLabel attributeEntry="${narrativeAttributes.phoneNumber}" /></div></th>
			                <td align="left" valign="middle">
			                	<kul:htmlControlAttribute property="document.developmentProposalList[0].narrative[${status.index}].phoneNumber" attributeEntry="${narrativeAttributes.phoneNumber}" />
							</td>
			          	</tr>
			          	<tr>
			          		<th><div align="right"><kul:htmlAttributeLabel attributeEntry="${narrativeAttributes.comments}" /></div></th>
			                <td align="left" valign="middle">
			                	<kul:htmlControlAttribute property="document.developmentProposalList[0].narrative[${status.index}].comments" attributeEntry="${narrativeAttributes.comments}" />
			                	<c:set var="textAreaFieldName" value="document.developmentProposalList[0].narrative[${status.index}].comments" />
							</td>
			          		<th><div align="right"><kul:htmlAttributeLabel attributeEntry="${narrativeAttributes.moduleTitle}" /></div></th>
			                <td align="left" valign="middle">
			                	<kul:htmlControlAttribute property="document.developmentProposalList[0].narrative[${status.index}].moduleTitle" attributeEntry="${narrativeAttributes.moduleTitle}" />
			                	<c:set var="textAreaFieldName" value="document.developmentProposalList[0].narrative[${status.index}].moduleTitle" />
							</td>
			          	</tr>
			          	<tr>
							<td colspan=4>
								<div align="center">
									<c:if test="${(downloadAttachment) }">
										<div style="display: inline;">
												<html:image styleId="downloadProposalAttachment.line${status.index}"  property="methodToCall.downloadProposalAttachment.line${status.index}.anchor${currentTabIndex}"
																src='${ConfigProperties.kra.externalizable.images.url}tinybutton-view.gif' styleClass="tinybutton"
																onclick="javascript: openNewWindow('${action}','downloadProposalAttachment','${status.index}',${KualiForm.formKey},'${KualiForm.document.sessionDocument}'); return false" />
										</div>
									</c:if>
									<c:if test="${replaceAttachment}">
										<div style="display: inline;" id="replaceAttachmentDiv${status.index}">
												<html:image styleId="replaceProposalAttachment.line${status.index}" 
																onclick="javascript: showHide('fileDiv${status.index}','replaceDiv${status.index}') ;
																					 showHide('attachmentStatusDiv${status.index}','updateStatusDiv${status.index}') ;
																					 showHide('saveNewAttachmentDiv${status.index}','replaceAttachmentDiv${status.index}') ; return false"  
																src='${ConfigProperties.kew.externalizable.images.url}tinybutton-edit1.gif' styleClass="tinybutton" />
										</div>
								    </c:if>
								    <c:if test="${deleteAttachment}">
										<div style="display: inline;">
												<html:image styleId="deleteProposalAttachment.line${status.index}" property="methodToCall.deleteProposalAttachment.line${status.index}.anchor${currentTabIndex}"
												            src='${ConfigProperties.kra.externalizable.images.url}tinybutton-delete1.gif' styleClass="tinybutton"/>
										</div>
									</c:if>
									<div style="display: inline;">
									    <html:image styleId="getProposalAttachmentRights.line${status.index}" property="methodToCall.getProposalAttachmentRights.line${status.index}.anchor${currentTabIndex}"
											        src='${ConfigProperties.kra.externalizable.images.url}tinybutton-vieweditrights.gif' styleClass="tinybutton"
											        onclick="javascript: proposalAttachmentRightsPop('${status.index}',${KualiForm.formKey},'${KualiForm.document.sessionDocument}');return false"/>
									</div>
									<c:if test="${replaceAttachment}">
									    <div id="cancelAttachmentEdit${status.index}" style="display: inline;">
										    <html:image styleId="getProposalAttachmentRights.line${status.index}"
													    onclick="javascript: showHide('replaceDiv${status.index}', 'fileDiv${status.index}') ;
													    					 showHide('updateStatusDiv${status.index}, 'attachmentStatusDiv${status.index}') ;
													    					 showHide('replaceAttachmentDiv${status.index}','saveNewAttachmentDiv${status.index}') ; return false"
												        src='${ConfigProperties.kra.externalizable.images.url}tinybutton-cancel.gif' styleClass="tinybutton"/>
										</div>
									</c:if>
									<c:if test="${replaceAttachment}">
										<div style="display: none;" id="saveNewAttachmentDiv${status.index}">
												<html:image styleId="replaceProposalAttachment.line${status.index}" 
																src='${ConfigProperties.kew.externalizable.images.url}tinybutton-save.gif' styleClass="tinybutton"
																property="methodToCall.replaceProposalAttachment.line${status.index}.anchor${currentTabIndex};return false" />
										</div>
								    </c:if>
								</div>
			                </td>
			            </tr>
			          </table>
			       </div>
			     </kul:innerTab>
			   </c:if>
        	</c:forEach>
        	</div>
        	</td>
        	</tr>
        	</c:if>
        	
            
        </table>
    </div>
</kul:tabTop>

<% endTime = System.currentTimeMillis();
               LOG.info("JSP Narrative Time = " + (endTime - startTime)); %>
