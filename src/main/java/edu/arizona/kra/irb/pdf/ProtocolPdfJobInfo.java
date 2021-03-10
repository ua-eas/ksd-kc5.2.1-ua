package edu.arizona.kra.irb.pdf;

public class ProtocolPdfJobInfo {
    private final int totalNumberProtocols;
    private final String startFromDate;
    private final String endToDate;
    private final boolean jobStartedOk;


    public ProtocolPdfJobInfo(int totalNumberProtocols, String startFromDate, String endToDate, boolean jobStartedOk) {
        this.totalNumberProtocols = totalNumberProtocols;
        this.startFromDate = startFromDate;
        this.endToDate = endToDate;
        this.jobStartedOk = jobStartedOk;
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

    @Override
    public String toString() {
        return String.format("totalNumberProtocols: %d, startFromDate: %s, endToDate: %s, jobStartedOk: %b",
                totalNumberProtocols, startFromDate, endToDate, jobStartedOk);
    }
}
