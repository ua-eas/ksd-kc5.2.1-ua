package edu.arizona.kra.irb.pdf.thread;

import java.util.List;

public class PdfBatch {
    private final List<String> protocolNumbers;
    private final String bucketPath;

    public PdfBatch(List<String> protocolNumbers, String bucketPath) {
        this.protocolNumbers = protocolNumbers;
        this.bucketPath = bucketPath;
    }

    public List<String> getProtocolNumbers() {
        return protocolNumbers;
    }

    public String getBucketPath() {
        return bucketPath;
    }
}
