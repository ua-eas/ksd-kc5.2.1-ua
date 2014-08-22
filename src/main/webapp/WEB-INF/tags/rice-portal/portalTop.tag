<%--
 Copyright 2005-2009 The Kuali Foundation
 
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

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>UAccess Research</title>
<c:forEach items="${fn:split(ConfigProperties.portal.css.files, ',')}" var="cssFile">
	<c:if test="${fn:length(fn:trim(cssFile)) > 0}">
        <link href="${pageContext.request.contextPath}/${fn:trim(cssFile)}" rel="stylesheet" type="text/css" />
	</c:if>
</c:forEach>
<c:forEach items="${fn:split(ConfigProperties.portal.javascript.files, ',')}" var="javascriptFile">
	<c:if test="${fn:length(fn:trim(javascriptFile)) > 0}">
        <script type="text/javascript" src="${ConfigProperties.application.url}/${fn:trim(javascriptFile)}"></script>
	</c:if>
</c:forEach> 

<script type="text/javascript" >
if (top.location != self.location) {
	top.location = self.location;
}
</script>

</head>
<body>

<c:if test="${fn:trim(ConfigProperties.environment) != fn:trim(ConfigProperties.production.environment.code)}" >

<div id="red-bar">
	<span id="environment-info">environment: <c:out value="${ConfigProperties.environment}"/></span>
	<c:set var="backboorEnabled" value="<%=org.kuali.rice.coreservice.framework.CoreFrameworkServiceLocator.getParameterService().getParameterValueAsBoolean(org.kuali.rice.kew.api.KewApiConstants.KEW_NAMESPACE, org.kuali.rice.krad.util.KRADConstants.DetailTypes.BACKDOOR_DETAIL_TYPE, org.kuali.rice.kew.api.KewApiConstants.SHOW_BACK_DOOR_LOGIN_IND)%>"/>
      <c:if test="${backboorEnabled}">
        <html:form action="/backdoorlogin.do" method="post" styleId="backdoor-form">
          <input name="backdoorId" type="text" class="searchbox" size="10" title="Enter your backdoor ID here.">
          <input name="imageField" type="hidden" value="backdoor" />
          <input name="methodToCall" type="hidden" value="login" />
          <a class="portal_link" onclick="document.getElementById('backdoor-form').submit();">
		    <span class="icon-enter"></span>
		  </a>
        </html:form>
      </c:if> 
</div>

</c:if>


<div id="header" title="<c:out value="${ConfigProperties.portal.title}"/>"> 
    <h1><c:out value="${ConfigProperties.portal.title}"/></h1>
  </div>
  
