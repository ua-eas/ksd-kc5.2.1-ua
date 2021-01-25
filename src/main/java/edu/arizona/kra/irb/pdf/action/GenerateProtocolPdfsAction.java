package edu.arizona.kra.irb.pdf.action;

import edu.arizona.kra.irb.pdf.ProtocolPdfWriterService;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kra.infrastructure.KraServiceLocator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GenerateProtocolPdfsAction extends Action {

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {

        ProtocolPdfWriterService protocolPdfWriterService = KraServiceLocator.getService(ProtocolPdfWriterService.class);
        boolean pdfWorkerStartedOk = protocolPdfWriterService.generateActiveProtocolPdfsToDisk();

        GenerateProtocolPdfsForm generateProtocolPdfsForm = (GenerateProtocolPdfsForm) form;
        generateProtocolPdfsForm.setPdfWorkerStartedOk(pdfWorkerStartedOk);

        return mapping.findForward("pdfWorkerStarted");
    }
}
