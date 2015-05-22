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

<c:set var="proposalDevelopmentAttributes" value="${DataDictionary.DevelopmentProposal.attributes}" />
<c:set var="proposalChangedDataAttributes" value="${DataDictionary.BudgetChangedData.attributes}" />
<c:set var="proposalColumnsToAlterAttributes" value="${DataDictionary.BudgetColumnsToAlter.attributes}" />
<c:set var="action" value="proposalDevelopmentActions" />

     <%-- Need to register the lookup methodToCall that can be generated by the js --%>
     <c:forEach items="${KualiForm.budgetDataOverrideMethodToCalls}" var = "mtc" varStatus = "status">
           	${kfunc:registerEditableProperty(KualiForm, mtc)}
	 </c:forEach>
	        
 
 <kul:tab tabTitle="Budget Data Override" defaultOpen="${openFlag}" tabErrorKey="newBudgetChangedData.*">
         
	<div class="tab-container" align="center">
	 <kra:section permission="alterProposalData">  
    	<h3>
    		<span class="subhead-left">Budget Data Override</span>
    		<span class="subhead-right"><kul:help businessObjectClassName="org.kuali.kra.proposaldevelopment.bo.BudgetChangedData" altText="help"/></span>
        </h3>
        
        
		 <table cellpadding="0" cellspacing="0" summary="">
			<input type="hidden" name="document.developmentProposalList[0].proposalNumber" id="document.developmentProposalList[0].proposalNumber" value="${KualiForm.document.developmentProposalList[0].proposalNumber}" />
			<input type="hidden" name="document.budgetDocumentVersions[0].documentNumber" id="document.budgetDocumentVersions[0].documentNumber" value="${KualiForm.document.budgetDocumentVersions[0].documentNumber}" />
			<input type="hidden" name="newBudgetChangedData.editableColumn.lookupReturn" id="newBudgetChangedData.editableColumn.lookupReturn"  value="${KualiForm.newBudgetChangedData.editableColumn.lookupReturn}" />
			<input type="hidden" name="newBudgetChangedData.editableColumn.dataType" id="newBudgetChangedData.editableColumn.dataType" value="${KualiForm.newBudgetChangedData.editableColumn.dataType}" />
			<input type="hidden" name="newBudgetChangedData.editableColumn.hasLookup" id="newBudgetChangedData.editableColumn.hasLookup" value="${KualiForm.newBudgetChangedData.editableColumn.hasLookup}" />
			<input type="hidden" name="newBudgetChangedData.editableColumn.lookupClass" id="newBudgetChangedData.editableColumn.lookupClass" value="${KualiForm.newBudgetChangedData.editableColumn.lookupClass}" />
			<input type="hidden" name="newBudgetChangedData.editableColumn.lookupPkReturn" id="newBudgetChangedData.editableColumn.lookupPkReturn"  value="${KualiForm.newBudgetChangedData.editableColumn.lookupPkReturn}" />
			<input type="hidden" name="newBudgetChangedData.editableColumn.columnName" id="newBudgetChangedData.editableColumn.columnName" value="${KualiForm.newBudgetChangedData.editableColumn.columnName}" />
			<input type="hidden" name="imageUrl" id="imageUrl"  value="${ConfigProperties.kr.externalizable.images.url}" /> 
			<input type="hidden" name="tabIndex" id="tabIndex" value="${KualiForm.nextArbitrarilyHighIndex}" />
		    <c:set var="textAreaFieldName" value="newBudgetChangedData.comments" />
			
			<tr>
				<th align="right" valign="middle"><kul:htmlAttributeLabel attributeEntry="${proposalChangedDataAttributes.columnName}" noColon="false" /></th>
				<td align="left" valign="middle">				
					<kul:htmlControlAttribute readOnly="false" property="newBudgetChangedData.columnName" attributeEntry="${proposalChangedDataAttributes.columnName}" onchange="updateBudgetOtherFields(this,'document.developmentProposalList[0].proposalNumber','document.budgetDocumentVersions[0].documentNumber',updateBudgetOtherFields_Callback);" styleClass="fixed-size-select"/>
					</td>
					<%-- <c:if test = "{newBudgetChangedData.editableColumn.datatype=="boolean"}" --%>
			</tr>
		       	<tr>
		                <th align="right" valign="middle"><kul:htmlAttributeLabel attributeEntry="${proposalChangedDataAttributes.oldDisplayValue}" noColon="false" /></th>
		                <td align="left" valign="middle">
		                
					<kul:htmlControlAttribute readOnly="false" property="newBudgetChangedData.oldDisplayValue" attributeEntry="${proposalChangedDataAttributes.oldDisplayValue}" />
				</td>
		        </tr>
		       	<tr>
		        	<th align="right" valign="middle"><kul:htmlAttributeLabel attributeEntry="${proposalChangedDataAttributes.displayValue}" noColon="false" /></th>
		        	<td align="left" valign="middle">
					<kul:htmlControlAttribute readOnly="false" property="newBudgetChangedData.displayValue" attributeEntry="${proposalChangedDataAttributes.displayValue}" />
				</td>
			</tr>
			<tr>
				<th align="right" valign="middle"><kul:htmlAttributeLabel attributeEntry="${proposalChangedDataAttributes.changedValue}" noColon="false" /></th>
				<td align="left" valign="middle">
					<div nowrap>
						<kul:htmlControlAttribute readOnly="false" property="newBudgetChangedData.changedValue" attributeEntry="${proposalChangedDataAttributes.changedValue}" />
							<div id="changedValueBudgetExtraBody" >
			                   		<c:if test="${not empty KualiForm.newBudgetChangedData.editableColumn.lookupClass and KualiForm.newBudgetChangedData.editableColumn.lookupClass != 'null'}">
				                   		<c:if test="${not empty KualiForm.newBudgetChangedData.editableColumn.lookupPkReturn and KualiForm.newBudgetChangedData.editableColumn.lookupPkReturn != 'null'}">
				                   		<c:if test="${not empty KualiForm.newBudgetChangedData.editableColumn.lookupReturn and KualiForm.newBudgetChangedData.editableColumn.lookupReturn != 'null'}">
											<kul:lookup boClassName="${KualiForm.newBudgetChangedData.editableColumn.lookupClass}" fieldConversions="${KualiForm.newBudgetChangedData.editableColumn.lookupPkReturn}:newBudgetChangedData.changedValue,${KualiForm.newBudgetChangedData.editableColumn.lookupReturn}:newBudgetChangedData.displayValue" anchor="${tabKey}"/>
										</c:if>
										</c:if>
									</c:if>
									<c:if test="${KualiForm.newBudgetChangedData.editableColumn.dataType == 'DATE'}">
									
						                <img src="${ConfigProperties.kr.externalizable.images.url}cal.gif" id="newBudgetChangedData.changedValue_datepicker" style="cursor: pointer;"
						                     title="Date selector" alt="Date selector"
						                     onmouseover="this.style.backgroundColor='red';" onmouseout="this.style.backgroundColor='transparent';" />
						                     <script type="text/javascript">
							                  Calendar.setup(
							                          {
							                            inputField : "newBudgetChangedData.changedValue", // ID of the input field
							                            ifFormat : "%m/%d/%Y", // the date format
							                            button : "newBudgetChangedData.changedValue_datepicker" // ID of the button
							                          }
							                  );
							                
							               </script>	 
									</c:if>
									<%-- <c:if test="${KualiForm.newBudgetChangedData.editableColumn.dataType == 'boolean'}">
									 <kul:htmlControlAttribute readOnly="false" property="newBudgetChangedData.flag" attributeEntry="${proposalChangedDataAttributes.flag}" onchange="updateFlag(this);" />
									 </c:if> --%>
			                  </div>
			                  
			                  <c:if test="${fn:length(ErrorPropertyList) > 0}" >
				                  <c:forEach items="${ErrorPropertyList}" var="key">
				                  	<c:if test="${fn:startsWith(key, 'newBudgetChangedData.')}" >
		              					<c:set var="overrideDataError" value="true"/>
		              				</c:if>
		              			  </c:forEach>
	              			  </c:if>
					</div>
				</td>
			</tr>
			<tr>
		         <th align="right" valign="middle"><kul:htmlAttributeLabel attributeEntry="${proposalChangedDataAttributes.comments}" noColon="false" /></th>
		         <td align="left" valign="middle">
					<kul:htmlControlAttribute readOnly="false" property="newBudgetChangedData.comments" attributeEntry="${proposalChangedDataAttributes.comments}" />
				</td>
		    </tr>
			<tr>
				<td align="center" colspan="2">
				<div align="center">
					<html:image property="methodToCall.addProposalBudgetChangedData.anchor${tabKey}"
									src='${ConfigProperties.kra.externalizable.images.url}tinybutton-edit1.gif' styleClass="tinybutton"/>
				</div>
		                </td>
			</tr>
		 </table>
        </kra:section>
        
       
        <c:if test="${fn:length(KualiForm.document.developmentProposalList[0].budgetChangeHistory) > 0}">
	        <br>
	        <h3>
	    		<span class="subhead-left">Budget Change History</span>
	    		<span class="subhead-right"><kul:help businessObjectClassName="org.kuali.kra.proposaldevelopment.bo.ProposalChangedData" altText="help"/></span>
	        </h3>
	        
		    <table cellpadding=0 cellspacing=0 summary="">
		     	<tr>
	                  <th>Original Value</th>
	                  <th>New Value</th>
	                  <th>Change Date</th>
	                  <th>Changed By</th>
	                  <th>Explanation</th>
	            </tr>
	            
	                
	        	<c:forEach var="changedDataListByColumn" items="${KualiForm.document.developmentProposalList[0].budgetChangeHistory}" varStatus="status">
	        		<tr><td colspan="5" class="tab-subhead" >${status.current.key}</td></tr>
	
		        	<c:forEach var="budgetChangedData" items="${status.current.value}" varStatus="innerStatus">
			        	<tr>
			        		<td align="left" valign="middle" class="infoline"> 
				                <div align="center">
								<c:out value="${budgetChangedData.oldDisplayValue}" />
				                &nbsp;</div>
							</td>
			                <td align="left" valign="middle" class="infoline">
				                <div align="center"><c:out value="${budgetChangedData.displayValue}" />&nbsp;</div>
			                </td>
			                <td align="left" valign="middle" class="infoline">
			                	<div align="center">
			                		<fmt:formatDate value="${budgetChangedData.updateTimestamp}" type="both" dateStyle="short" timeStyle="short" />
			                	&nbsp;</div>
			                </td>
			                <td align="left" valign="middle" class="infoline">
			                	<div align="center"><c:out value="${budgetChangedData.updateUser}" />&nbsp;</div>
			                </td>
			                <td align="left" valign="middle" class="infoline">
			                	<div align="center"><c:out value="${budgetChangedData.comments}" />&nbsp;</div>
			                </td>
							 <td align="left" valign="middle" class="infoline">
			                	<div align="left">&nbsp;</div>
			                </td>	
		   				</tr>
	        		</c:forEach>
	        	</c:forEach>
	      	   </table>
      	   
      	</c:if>

    </div> 
</kul:tab>

