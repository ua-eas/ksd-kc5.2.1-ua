package edu.arizona.kra.irb.pdf;

public class ProtocolPdfJobInfo {
    private final int totalNumberProtocols;
    private final String startFromDate;
    private final String endToDate;
    private final boolean jobStartedOk;
    private final String outputDir;


    public ProtocolPdfJobInfo(int totalNumberProtocols, String startFromDate, String endToDate, boolean jobStartedOk, String outputDir) {
        this.totalNumberProtocols = totalNumberProtocols;
        this.startFromDate = startFromDate;
        this.endToDate = endToDate;
        this.jobStartedOk = jobStartedOk;
        this.outputDir = outputDir;
    }

    public int getTotalNumberProtocols() {
        return totalNumberProtocols;
    }

    public String getStartFromDate() {
        return startFromDate;
    }

    public String getEndToDate() {
        return endToDate;
    }

    public boolean isJobStartedOk() {
        return jobStartedOk;
    }

    public String getOutputDir() {
        return outputDir;
    }
}
