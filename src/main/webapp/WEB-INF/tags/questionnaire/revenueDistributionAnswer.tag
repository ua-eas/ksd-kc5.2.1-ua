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

<%@ attribute name="bean" required="true" type="org.kuali.kra.proposaldevelopment.questionnaire.ProposalDevelopmentQuestionnaireHelper" %>
<%@ attribute name="property" required="true" %>



 <c:set var="questionIndex" value="${bean.revenueDistributionQuestionIndex}"/>
 <c:if test="${bean.revenueDistributionQuestionIndex > 0}">
     <c:if test="${not bean.oldQuestionnaireVersion}">
         <c:set var="answerHeaderIndex" value="${bean.revenueDistributionAnswerHeaderIndex}"/>
         <c:if test="${bean.answerHeaders[answerHeaderIndex]!=null and  bean.answerHeaders[answerHeaderIndex].answers!=null and bean.answerHeaders[answerHeaderIndex].answers[questionIndex]!= null }">
             <c:set var="answer" value="${bean.answerHeaders[answerHeaderIndex].answers[questionIndex]}" />
             <div class="questionnaireContent">
                 <table class="content_table question">
                     <tr>
                         <td class="content_questionnaire">
                             <div class="Qdiv" >
                                 <div class="Qquestiondiv">
                                     <span class="Qquestion">${answer.question.question}</span>
                                 </div>
                                 <c:choose>
                                     <c:when test = "${answer.answer == 'Y'}" >
                                         Yes
                                     </c:when>
                                     <c:when test = "${answer.answer == 'N'}" >
                                         No
                                     </c:when>
                                     <c:otherwise>
                                         N/A
                                     </c:otherwise>
                                 </c:choose>
                             </div>
                         </td>
                     </tr>
                 </table>
             </div>
         </c:if>
     </c:if>
     <c:if test="${bean.oldQuestionnaireVersion }">
         <c:set var="proposalYnq" value="${KualiForm.document.developmentProposalList[0].proposalYnqs[questionIndex]}" />
         <div class="questionnaireContent">
             <table class="content_table question">
                 <tr>
                     <td class="content_questionnaire">
                         <div class="Qdiv" >
                             <div class="Qquestiondiv">
                                 <span class="Qquestion">${proposalYnq.ynq.description}</span>
                             </div>
                             <c:choose>
                                 <c:when test = "${proposalYnq.answer == 'Y'}" >
                                     Yes
                                 </c:when>
                                 <c:when test = "${proposalYnq.answer == 'N'}" >
                                     No
                                 </c:when>
                                 <c:otherwise>
                                     N/A
                                 </c:otherwise>
                             </c:choose>
                         </div>
                     </td>
                 </tr>
             </table>
         </div>
    </c:if>
</c:if>
