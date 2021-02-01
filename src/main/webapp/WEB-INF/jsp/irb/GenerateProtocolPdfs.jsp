<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="GenerateProtocolPdfsForm" scope="request" type="edu.arizona.kra.irb.pdf.action.GenerateProtocolPdfsForm"/>
<html>
    <head>
        <title>Generate Protocol PDFs Status</title>
    </head>
    <body>
        <h1>Generate Protocol PDFs Status</h1>
        <table>
            <tr>
                <td>Job started ok: </td>
                <td><c:out value="${GenerateProtocolPdfsForm.jobStartedOk}"/></td>
            </tr>
            <tr>
                <td>Protocols to process: </td>
                <td><c:out value="${GenerateProtocolPdfsForm.totalNumberProtocols}"/></td>
            </tr>
            <tr>
                <td>Start from date: </td>
                <td><c:out value="${GenerateProtocolPdfsForm.startFromDate}"/></td>
            </tr>
            <tr>
                <td>End to date: </td>
                <td><c:out value="${GenerateProtocolPdfsForm.endToDate}"/></td>
            </tr>
        </table>
    </body>
</html>
