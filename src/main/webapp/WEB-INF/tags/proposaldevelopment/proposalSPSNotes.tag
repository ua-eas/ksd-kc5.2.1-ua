<%--
 Copyright 2005-2015 The Kuali Foundation
 
 Licensed under the Educational Community License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl2.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<%@ attribute name="noteType" required="false" type="java.lang.Enum"%>
<%@ attribute name="displayTopicFieldInNotes" required="false"%>
<%@ attribute name="transparentBackground" required="false"%>
<%@ attribute name="defaultOpen" required="false" description="Whether the tab for the notes is rendered as open." %>

<c:set var="documentTypeName" value="${KualiForm.docTypeName}" />
<c:set var="canEditSPSNote" value="${KualiForm.canEditSPSRestrictedNotes}" />
<c:set var="documentEntry" value="${DataDictionary[documentTypeName]}" />
<c:set var="tabTitle" value="SPS Restricted Notes" />
<c:set var="SPSNotesAttributes" value="${DataDictionary.SPSRestrictedNote.attributes}" />
<c:set var="newSPSRestrictedNote" value="${KualiForm.newSPSRestrictedNote}" />
<c:set var="SPSRestrictedNotes" value="${KualiForm.SPSRestrictedNotes}" />

<c:if test="${empty displayTopicFieldInNotes}">
    <c:set var="displayTopicFieldInNotes" value="${documentEntry.displayTopicFieldInNotes}" />
</c:if>


<kul:tab tabTitle="${tabTitle}" defaultOpen="${!empty SPSRestrictedNotes or (not empty defaultOpen and defaultOpen)}" tabErrorKey="${Constants.DOCUMENT_NOTES_ERRORS}" tabItemCount="${fn:length(SPSRestrictedNotes)}" transparentBackground="${transparentBackground}">

    <div class="tab-container" align=center id="G4">
        <p align=left><jsp:doBody />
        <h3>${tabTitle}
            <span class="subhead-right"><kul:help parameterNamespace="KC-PD" parameterDetailType="Document" parameterName="proposalDevelopmentnotesHelpUrl" altText="help" /></span>
        </h3>
        <table cellpadding="0" cellspacing="0" class="datatable" summary="view/add notes">
            <tbody>
                <tr>
                    <th><div align="left">&nbsp;</div></th>
                    <kul:htmlAttributeHeaderCell attributeEntry="${SPSNotesAttributes.createdDate}" hideRequiredAsterisk="true" scope="col" align="left"/>
                    <kul:htmlAttributeHeaderCell attributeEntry="${SPSNotesAttributes.authorId}" hideRequiredAsterisk="true" scope="col" align="left"/>
                   
                    <c:if test="${displayTopicFieldInNotes eq true}">
                        <th><div align="center">
                                <kul:htmlAttributeLabel attributeEntry="${SPSNotesAttributes.noteTopic}" />
                            </div></th>
                    </c:if>
                    <th><div align="center">
                            <kul:htmlAttributeLabel attributeEntry="${SPSNotesAttributes.noteText}" />
                        </div></th>
                    <th><div align="center">
                            <kul:htmlAttributeLabel attributeEntry="${SPSNotesAttributes.proposalReceivedDate}" />
                        </div></th>
                    <th><div align="center">
                            <kul:htmlAttributeLabel attributeEntry="${SPSNotesAttributes.proposalReceivedTime}" />
                        </div></th>
                    <c:if test="${canEditSPSNote eq true}">
                        <kul:htmlAttributeHeaderCell literalLabel="Actions" scope="col" />
                    </c:if>
                </tr>

                <c:if test="${canEditSPSNote eq true}">
                    <tr class="addline">
                        <kul:htmlAttributeHeaderCell literalLabel="add:" scope="row" />
                        <td class="infoline">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
                        <td class="infoline">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>

                        <c:if test="${displayTopicFieldInNotes eq true}">
                            <td class="infoline"><kul:htmlControlAttribute property="newSPSRestrictedNote.noteTopic" attributeEntry="${SPSNotesAttributes.noteTopic}" forceRequired="${SPSNotesAttributes.noteTopic.required}" readOnly="false" /></td>
                        </c:if>

                        <td class="infoline"><kul:htmlControlAttribute property="newSPSRestrictedNote.noteText" attributeEntry="${SPSNotesAttributes.noteText}" forceRequired="${SPSNotesAttributes.noteText.required}" readOnly="false" /></td>
                        <td class="infoline"><kul:htmlControlAttribute property="newSPSRestrictedNote.proposalReceivedDate" attributeEntry="${SPSNotesAttributes.proposalReceivedDate}"
                                forceRequired="${SPSNotesAttributes.proposalReceivedDate.required}" readOnly="false" datePicker="true" /></td>
                        <td class="infoline"><kul:htmlControlAttribute property="newSPSRestrictedNote.proposalReceivedTime" attributeEntry="${SPSNotesAttributes.proposalReceivedTime}"
                                forceRequired="${SPSNotesAttributes.proposalReceivedTime.required}" readOnly="false" /> </td>

                        <td class="infoline">
                            <div align="center">
                                <html:image property="methodToCall.insertSPSNote" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" alt="Add SPS Note" title="Add SPS Note" styleClass="tinybutton addButton" />
                            </div>
                        </td>
                    </tr>
                </c:if>

                <c:forEach var="SPSNote" items="${KualiForm.SPSRestrictedNotes}" varStatus="status">
                    <c:set var="authorUniversalIdentifier" value="${SPSNote.authorId}" />
                    <tr>
                        <th width="5%" class="infoline" rowspan="1"><c:out value="${status.index+1}" /></th>

                        <td class="datacell center"><kul:htmlControlAttribute property="SPSRestrictedNotes[${status.index}].createdDate" readOnly="true" attributeEntry="${SPSNotesAttributes.createdDate}" /></td>

                        <td class="datacell center"><kul:htmlControlAttribute property="SPSRestrictedNotes[${status.index}].authorName" readOnly="true" attributeEntry="${SPSNotesAttributes.authorId}" /></td>
                        <c:if test="${displayTopicFieldInNotes eq true}">
                            <td class="datacell center"><kul:htmlControlAttribute property="SPSRestrictedNotes[${status.index}].noteTopic" readOnly="true" attributeEntry="${SPSNotesAttributes.noteTopic}" /></td>
                        </c:if>

                        <td class="datacell center"><kul:htmlControlAttribute property="SPSRestrictedNotes[${status.index}].noteText" readOnly="true" attributeEntry="${SPSNotesAttributes.noteText}" /></td>
                        <td class="datacell center"><kul:htmlControlAttribute property="SPSRestrictedNotes[${status.index}].proposalReceivedDate" readOnly="true" attributeEntry="${SPSNotesAttributes.proposalReceivedDate}" /></td>
                        <td class="datacell center"><kul:htmlControlAttribute property="SPSRestrictedNotes[${status.index}].proposalReceivedTime" readOnly="true" attributeEntry="${SPSNotesAttributes.proposalReceivedTime}" /></td>
                        <c:if test="${canEditSPSNote eq true}">
                            <td class="infoline">
                                <div align="center">
                                    <html:image property="methodToCall.deleteSPSNote.line${status.index}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif" title="Delete Note" alt="Delete Note" styleClass="tinybutton" />
                                </div>
                            </td>
                        </c:if>
                    </tr>
                </c:forEach>

            </tbody>
        </table>
    </div>
</kul:tab>
