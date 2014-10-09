<%--
 Copyright 2005-2010 The Kuali Foundation

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


<c:set var="attributes" value="${DataDictionary.ProtocolRiskLevel.attributes}" />
<c:set var="readOnly" value="${!KualiForm.protocolHelper.modifyRiskLevels}" />

<kul:tab tabTitle="Risk Levels" defaultOpen="false" tabErrorKey="protocolHelper.newProtocolRiskLevel*,document.protocol.protocolRiskLevels*">
    <div class="tab-container" align="center">

        <h3>
            <span class="subhead-left">Risk Levels</span>
            <span class="subhead-right"><kul:help businessObjectClassName="org.kuali.kra.bo.RiskLevel" altText="help"/></span>
        </h3>

   
        <table class="tab" cellpadding="0" cellspacing="0" summary="">
            
            <%-- Header --%>
            <tr>
                <th><div align="left">&nbsp;</div></th> 
                <kul:htmlAttributeHeaderCell attributeEntry="${attributes.riskLevelCode}" />
                <kul:htmlAttributeHeaderCell attributeEntry="${attributes.dateAssigned}" />
                <kul:htmlAttributeHeaderCell attributeEntry="${attributes.dateInactivated}" />
                <kul:htmlAttributeHeaderCell attributeEntry="${attributes.status}" />
                <kul:htmlAttributeHeaderCell attributeEntry="${attributes.comments}" />
                <c:if test="${!readOnly}">
                    <kul:htmlAttributeHeaderCell literalLabel="Actions" scope="col"/>
                </c:if>
            </tr>
                
            <%-- New data --%>  
            <kra:permission value="${KualiForm.protocolHelper.modifyRiskLevels}">              
            <tr> 
                <th class="infoline">
                    <c:out value="Add:" />
                </th>
                <td align="left" valign="middle">
                    <div align="left">
                        <kul:htmlControlAttribute property="protocolHelper.newProtocolRiskLevel.riskLevelCode" attributeEntry="${attributes.riskLevelCode}" styleClass="fixed-size-200-select" />
                    </div>
                </td>
                <td align="left" valign="middle">
                    <div align="center">
                        <kul:htmlControlAttribute property="protocolHelper.newProtocolRiskLevel.dateAssigned" attributeEntry="${attributes.dateAssigned}" />
                    </div>
                </td>
                <td align="left" valign="middle">
                    <div align="center">
                        <kul:htmlControlAttribute property="protocolHelper.newProtocolRiskLevel.dateInactivated" attributeEntry="${attributes.dateInactivated}" readOnly="true" />
                    </div>
                </td>
                <td align="left" valign="middle">
                    <div align="center">
                        <kul:htmlControlAttribute property="protocolHelper.newProtocolRiskLevel.status" attributeEntry="${attributes.status}" readOnly="true" />
                    </div>
                </td>
                <td align="left" valign="middle">
                    <div align="left">
                        <kul:htmlControlAttribute property="protocolHelper.newProtocolRiskLevel.comments" attributeEntry="${attributes.comments}" />
                    </div>
                </td>
                <td align="left" valign="middle">
                    <div align="center">
                        <html:image property="methodToCall.addRiskLevel.anchor${tabKey}"
                                    src='${ConfigProperties.kra.externalizable.images.url}tinybutton-add1.gif' styleClass="tinybutton"/>
                    </div>
                </td>
            </tr>
              </kra:permission>
               <%-- New data --%>
            
            <%-- Existing data --%>
            <c:forEach var="protocolRiskLevel" items="${KualiForm.document.protocol.protocolRiskLevels}" varStatus="status">
                <c:set var="updateOnly" value="${protocolRiskLevel.persisted}" />
                <tr>
                     <th class="infoline">
                         ${status.index + 1}
                     </th>
                    <td align="left" valign="middle">
                         <div align="left">
                             <kul:htmlControlAttribute property="document.protocol.protocolRiskLevels[${status.index}].riskLevelCode" 
                                                       attributeEntry="${attributes.riskLevelCode}" readOnly="true" />
                         </div>
                    </td>
                    <td align="left" valign="middle">
                         <div align="center">
                             <kul:htmlControlAttribute property="document.protocol.protocolRiskLevels[${status.index}].dateAssigned" 
                                                       attributeEntry="${attributes.dateAssigned}" readOnly="true" />
                         </div>
                    </td>
                    <td align="left" valign="middle">
                         <div align="center">
                             <kul:htmlControlAttribute property="document.protocol.protocolRiskLevels[${status.index}].dateInactivated" 
                                                       attributeEntry="${attributes.dateInactivated}" readOnly="${!protocolRiskLevel.persisted || readOnly}" />
                         </div>
                    </td>
                    <td align="left" valign="middle">
                        <div align="center">
                             <kul:htmlControlAttribute property="document.protocol.protocolRiskLevels[${status.index}].status" 
                                                       attributeEntry="${attributes.status}" readOnly="true" />
                        </div>
                    </td>
                    <td align="left" valign="middle">
                        <div align="left">
                             <%--<kul:htmlControlAttribute property="document.protocol.protocolRiskLevels[${status.index}].comments" 
                                                       attributeEntry="${attributes.comments}" readOnly="true" />--%>
                             <kra:truncateComment textAreaFieldName="document.protocol.protocolRiskLevels[${status.index}].comments" action="protocolProtocolActions" textAreaLabel="${attributes.comments.label}" textValue="${KualiForm.document.protocolList[0].protocolRiskLevels[status.index].comments}" displaySize="200"/>
                        </div>
                    </td>
                    <c:if test="${!readOnly}">
                    <td align="left" valign="middle">
                        <div align="center">
                            <c:choose>
                                <c:when test="${!protocolRiskLevel.persisted}">
                                    <html:image property="methodToCall.deleteRiskLevel.line${status.index}.anchor${tabKey}"
                                                src='${ConfigProperties.kra.externalizable.images.url}tinybutton-delete1.gif' styleClass="tinybutton"/>
                                </c:when>
                                <c:otherwise>
                                    <html:image property="methodToCall.updateRiskLevel.line${status.index}.anchor${tabKey}"
                                                src='${ConfigProperties.kra.externalizable.images.url}tinybutton-update.gif' styleClass="tinybutton"/>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </td>
                     </c:if>
                </tr>
            </c:forEach>
        </table>       
    </div>
</kul:tab>