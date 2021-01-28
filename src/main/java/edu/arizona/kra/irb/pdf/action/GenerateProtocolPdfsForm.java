package edu.arizona.kra.irb.pdf.action;

import edu.arizona.kra.irb.pdf.ProtocolPdfJobInfo;
import org.apache.struts.action.ActionForm;

public class GenerateProtocolPdfsForm extends ActionForm {
    private ProtocolPdfJobInfo protocolPdfJobInfo;

    public void setProtocolPdfJobInfo(ProtocolPdfJobInfo protocolPdfJobInfo) {
        this.protocolPdfJobInfo = protocolPdfJobInfo;
    }

    public int getTotalNumberProtocols() {
        return protocolPdfJobInfo.getTotalNumberProtocols();
    }

    public String getStartFromDate() {
        return protocolPdfJobInfo.getStartFromDate();
    }

    public String getEndToDate() {
        return protocolPdfJobInfo.getEndToDate();
    }

    public boolean isJobStartedOk() {
        return protocolPdfJobInfo.isJobStartedOk();
    }

    public String getOutputDir() {
        return protocolPdfJobInfo.getOutputDir();
    }
}
