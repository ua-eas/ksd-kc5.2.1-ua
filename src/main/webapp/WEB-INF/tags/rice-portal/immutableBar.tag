<%@ tag import="org.kuali.rice.krad.util.KRADConstants" %>
<%@ tag import="java.util.Enumeration" %>
<%--
~ Copyright 2006-2011 The Kuali Foundation
~
~ Licensed under the Educational Community License, Version 2.0 (the "License");
~ you may not use this file except in compliance with the License.
~ You may obtain a copy of the License at
~
~ http://www.opensource.org/licenses/ecl2.php
~
~ Unless required by applicable law or agreed to in writing, software
~ distributed under the License is distributed on an "AS IS" BASIS,
~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
~ See the License for the specific language governing permissions and
~ limitations under the License.
--%>

<%@ include file="/rice-portal/jsp/sys/riceTldHeader.jsp"%>

<div class="header2">
  <div class="header2-left-focus">
    <div class="breadcrumb-focus">
    	<portal:portalLink displayTitle="false" title='Action List' url='${ConfigProperties.kew.url}/ActionList.do'>
   			<span class="icon-list"></span>
        	<span class="icon-link-text">action list</span>
   		</portal:portalLink>
    	<portal:portalLink displayTitle="false" title='Document Search' url='${ConfigProperties.workflow.documentsearch.base.url}'>
    		<span class="icon-search"></span>
    		<span class="icon-link-text">doc search</span>
		</portal:portalLink>
		<a class="portal_link" href="${ConfigProperties.bi.reporting.url}" title="UAccess Analytics" target="_BLANK">
    		<span class="icon-bars"></span>
    		<span class="icon-link-text">analytics</span>
		</a>
     </div>
  </div>
</div>
<div id="login-info">
	<c:set var="invalidUserMsg" value="Invalid username" />
	<c:choose>
		<c:when test="${empty UserSession.loggedInUserPrincipalName}">
			<strong>You are not logged in.</strong>
		</c:when>
		<c:otherwise>
			<span class="login-user-info">Logged in User:${UserSession.loggedInUserPrincipalName}
			<c:if test="${UserSession.backdoorInUse}">
				Impersonating User:${UserSession.principalName}
			</c:if>
			<c:if test="${param.invalidUser}">
				Impersonating User:${invalidUserMsg}
			</c:if>
			</span>
			<html:form action="/logout.do" method="post" styleId="logout-form">
				<input type="hidden" name="imageField" value="logout">
				<a class="portal_link" onclick="document.getElementById('logout-form').submit();">
					<span class="icon-exit"></span>
    				<span class="icon-link-text">logout</span>
				</a>
			</html:form>
		</c:otherwise>
	</c:choose>
</div>
