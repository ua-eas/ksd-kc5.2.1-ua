package edu.arizona.kra.irb.pdf.thread;

import java.util.ArrayList;
import java.util.List;

public class BatchResult {
    private long successCount;
    private final List<String> failedProtocolNumbers;


    public BatchResult() {
        this.successCount = 0;
        this.failedProtocolNumbers = new ArrayList<>();
    }


    public long getTotalProcessed() {
        return successCount + failedProtocolNumbers.size();
    }


    public void incrementSuccess() {
        successCount++;
    }


    public long getSuccessCount() {
        return successCount;
    }


    public void addFailed(String protocolNumber) {
        failedProtocolNumbers.add(protocolNumber);
    }


    public List<String> getFailedProtocolNumbers() {
        return failedProtocolNumbers;
    }

}

