package edu.arizona.kra.irb.pdf;


public class PdfProcessingReport {
    private int numProtocolsProcessed;
    private boolean workersStartedOk;
    private boolean processingSuccessful;

    public PdfProcessingReport(int numProtocolsProcessed) {
        this.numProtocolsProcessed = numProtocolsProcessed;
        this.workersStartedOk = false;
        this.processingSuccessful = false;
    }


    public int getNumProtocolsProcessed() {
        return numProtocolsProcessed;
    }

    public void setNumProtocolsProcessed(int numProtocolsProcessed) {
        this.numProtocolsProcessed = numProtocolsProcessed;
    }

    public boolean isWorkersStartedOk() {
        return workersStartedOk;
    }

    public void setWorkersStartedOk(boolean workersStartedOk) {
        this.workersStartedOk = workersStartedOk;
    }

    public boolean isProcessingSuccessful() {
        return processingSuccessful;
    }

    public void setProcessingSuccessful(boolean processingSuccessful) {
        this.processingSuccessful = processingSuccessful;
    }
}
