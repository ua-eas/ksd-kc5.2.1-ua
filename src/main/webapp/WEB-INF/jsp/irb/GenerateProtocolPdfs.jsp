<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="GenerateProtocolPdfsForm" scope="request" type="edu.arizona.kra.irb.pdf.action.GenerateProtocolPdfsForm"/>
<html>
    <head>
        <title>Generate Protocol PDFs Status</title>
    </head>
    <body>
        <h1>Generate Protocol PDFs Status</h1>
        <h4>
            PDF worker started ok: <c:out value="${GenerateProtocolPdfsForm.pdfWorkerStartedOk}"/>
        </h4>
    </body>
</html>
