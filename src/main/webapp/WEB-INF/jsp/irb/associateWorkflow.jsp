<%@ include file="/WEB-INF/jsp/kraTldHeader.jsp"%>
<html>
<head>
	<link rel="stylesheet" href="css/associateworkflow.css" type="text/css" />
</head>
<body>
<div class="linklist-container">
  <h2 class="linkheader">HSPP (Admin Use)</h2>
  <ul class="linklist">
  	<c:forEach items="${KualiForm.linksList}" var="link">
	    <li>
	        <a href="${link.url}" class="linkclass"><c:out value="${link.anchorText}"/></a>
	        <p class="linkdescription">
	            <c:out value="${link.linkDescription}"/>
	        </p>
	    </li>
    </c:forEach>
   </ul>
<div>
</body>
</html>