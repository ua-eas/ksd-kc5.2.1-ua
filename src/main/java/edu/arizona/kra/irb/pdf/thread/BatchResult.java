package edu.arizona.kra.irb.pdf.thread;

import java.util.List;

public class BatchResult {
    private long numSuccess;
    private List<String> failedProtocolNumbers;


    public BatchResult() {
        numSuccess = 0;
    }


    public long getTotalProcessed() {
        return numSuccess + failedProtocolNumbers.size();
    }


    public void incrementSuccess() {
        numSuccess++;
    }


    public long getNumSuccess() {
        return numSuccess;
    }


    public void addFailed(String protocolNumber) {
        failedProtocolNumbers.add(protocolNumber);
    }


    public long getNumFailed() {
        return failedProtocolNumbers.size();
    }


    public List<String> getFailedProtocolNumbers() {
        return failedProtocolNumbers;
    }

}

