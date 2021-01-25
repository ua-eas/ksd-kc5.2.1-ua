package edu.arizona.kra.irb.pdf.action;

import org.apache.struts.action.ActionForm;

public class GenerateProtocolPdfsForm extends ActionForm {
    private boolean pdfWorkerStartedOk;

    public boolean isPdfWorkerStartedOk() {
        return pdfWorkerStartedOk;
    }

    public void setPdfWorkerStartedOk(boolean pdfWorkerStartedOk) {
        this.pdfWorkerStartedOk = pdfWorkerStartedOk;
    }
}
