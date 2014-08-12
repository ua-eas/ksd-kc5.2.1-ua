<%--
 Copyright 2006-2009 The Kuali Foundation
 
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
<%@ include file="/rice-portal/jsp/sys/riceTldHeader.jsp"%>

<%@ attribute name="channelTitle" required="true" %>
<%@ attribute name="visible" required="false" type="java.lang.Boolean" %>

<c:if test="${visible == null}"><c:set var="visible" value="${true}" /></c:if>

<c:choose>
	<c:when test="${visible}">
		<div class="portlet">
	</c:when>
	<c:otherwise>
		<div class="portlet hidden">
	</c:otherwise>
</c:choose>
			<div class="header">
				<div class="portlet-title">
					<h2 class="portlet-title">${channelTitle}</h2>
				</div>
			</div>
			<div class="chan-contain">