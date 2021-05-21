package edu.arizona.kra.irb.pdf.thread;

public class BatchResult {
    private long numSuccess;
    private long numFailed;


    public long getTotalProcessed() {
        return numSuccess + numFailed;
    }


    public void incrementSuccess() {
        numSuccess++;
    }

    public long getNumSuccess() {
        return numSuccess;
    }


    public void incrementFailed() {
        numFailed++;
    }

    public long getNumFailed() {
        return numFailed;
    }

}
