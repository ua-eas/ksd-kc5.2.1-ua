package edu.arizona.kra.irb.pdf;

public class EfsAttachment {
    private String efsPath;
    private String md5hash;

    public EfsAttachment(String efsPath, String md5hash) {
        this.efsPath = efsPath;
        this.md5hash = md5hash;
    }

    public String getEfsPath() {
        return efsPath;
    }

    public String getMd5hash() {
        return md5hash;
    }
}
