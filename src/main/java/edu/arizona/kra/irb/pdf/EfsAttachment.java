package edu.arizona.kra.irb.pdf;

public class EfsAttachment {
    private final String efsPath;
    private final int bytesLength;


    public EfsAttachment(String efsPath, int bytesLength) {
        this.efsPath = efsPath;
        this.bytesLength = bytesLength;
    }


    public String getEfsPath() {
        return efsPath;
    }


    public int getBytesLength() {
        return bytesLength;
    }
}
