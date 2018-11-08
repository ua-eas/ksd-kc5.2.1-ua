/*
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kra.negotiations.web.struts.action;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.negotiations.document.NegotiationDocument;
import org.kuali.kra.negotiations.web.struts.form.NegotiationForm;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.util.KRADConstants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 
 * This class represents the Struts Action for Medusa page (NegotiationMedusa.jsp)
 */
public class NegotiationMedusaAction extends NegotiationAction {

    @Override
    public ActionForward docHandler(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        NegotiationForm negotiationForm = (NegotiationForm) form;
            if (negotiationForm.getDocument().getDocumentNumber() == null) {
                //if we are entering this from the search results
                loadDocumentInForm(request, negotiationForm);
            }
            negotiationForm.getMedusaBean().setMedusaViewRadio("0");
            negotiationForm.getMedusaBean().setModuleName("neg");
            negotiationForm.getMedusaBean().setModuleIdentifier(negotiationForm.getNegotiationDocument().getNegotiation().getNegotiationId());
            negotiationForm.getMedusaBean().generateParentNodes();

            return mapping.findForward(Constants.MAPPING_AWARD_MEDUSA_PAGE);
    }

    public ActionForward refreshView(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        return mapping.findForward(Constants.MAPPING_AWARD_BASIC);
    }



    protected void loadDocumentInForm(HttpServletRequest request, NegotiationForm negotiationForm)
            throws WorkflowException {
        String docIdRequestParameter = request.getParameter(KRADConstants.PARAMETER_DOC_ID);
        NegotiationDocument retrievedDocument = (NegotiationDocument) KRADServiceLocatorWeb.getDocumentService().getByDocumentHeaderId(docIdRequestParameter);
        negotiationForm.setDocument(retrievedDocument);
        request.setAttribute(KRADConstants.PARAMETER_DOC_ID, docIdRequestParameter);
    }
}
