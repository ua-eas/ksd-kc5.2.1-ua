/**
 * 
 */
package edu.arizona.kra.irb.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kra.irb.ProtocolForm;
import org.kuali.kra.irb.actions.ProtocolProtocolActionsAction;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * @author shaloo
 *
 */
public class CustomProtocolProtocolActionsAction extends ProtocolProtocolActionsAction {
	
	private static final String HISTORY_SUMMARY_AJAX_FOREWARD = "protocolActionsHistorySummaryAjax";
	
	public ActionForward ajaxLoadVersionHistory(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

    	ProtocolForm protocolForm = (ProtocolForm) form;
    	
    	String docIdRequestParameter = request.getParameter(KRADConstants.PARAMETER_DOC_ID);
        Document retrievedDocument = KRADServiceLocatorWeb.getDocumentService().getByDocumentHeaderId(docIdRequestParameter);
        protocolForm.setDocument(retrievedDocument);
        request.setAttribute(KRADConstants.PARAMETER_DOC_ID, docIdRequestParameter);
        loadDocument(protocolForm);
    	
    	ActionForward forward = protocolActions(mapping, form, request, response);
    	
    	forward = mapping.findForward(HISTORY_SUMMARY_AJAX_FOREWARD);
    	
        return forward;
    }
}
