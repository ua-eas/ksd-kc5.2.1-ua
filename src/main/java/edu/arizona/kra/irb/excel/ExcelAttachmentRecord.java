package edu.arizona.kra.irb.excel;

import java.util.ArrayList;
import java.util.List;

public class ExcelAttachmentRecord {
    private String id;
    private String destType;
    private String protocolNumber;
    private String huronDestination;
    private int destAttIsSet;
    private String uiFilename;
    private String sftpPath;
    private String category;
    private String versionId;
    private String isProcessed;
    private String error;
    private String documentId;


    /*
     * All of these except last four are set to `not null` in the schmema definition
     */
    public List<String> getColumnValues() {
        List<String> columnValues = new ArrayList<>();
        columnValues.add(getId());
        columnValues.add(getDestType());
        columnValues.add(getProtocolNumber());
        columnValues.add(getHuronDestination());
        columnValues.add(Integer.toString(getDestAttIsSet()));
        columnValues.add(getUiFilename());
        columnValues.add(getSftpPath());
        columnValues.add(getCategory());
        columnValues.add("");//getVersionId() unused
        columnValues.add("");//getIsProcessed() unused
        columnValues.add("");//getError() unused
        columnValues.add("");//getDocumentId() unused

        return columnValues;
    }


    // Column A: Set our own protocol_attachment_protocol.pa_protocol_id or protocol_correspondence.id
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Column B: Hard-coded to "_IRBSubmission" per spec
    public String getDestType() {
        return destType;
    }

    public void setDestType(String destType) {
        this.destType = destType;
    }

    // Column C: This should always be the 'parent' protocol, since no ammendment/revision data is being migrated
    public String getProtocolNumber() {
        return protocolNumber;
    }

    public void setProtocolNumber(String protocolNumber) {
        this.protocolNumber = protocolNumber;
    }

    // Column D: This is one of four spots and determines where in the huron smart form the document gets listed
    public String getHuronDestination() {
        return huronDestination;
    }

    public void setHuronDestination(String huronDestination) {
        this.huronDestination = huronDestination;
    }

    // Column E: Hardcoded to 1 ber spec
    public int getDestAttIsSet() {
        return destAttIsSet;
    }

    public void setDestAttrIsSet(int desAttIsSet) {
        this.destAttIsSet = desAttIsSet;
    }

    // Column F: This is the 'friendly' filename, and will be displayed via the UI; spec has 5
    // cases for how this should be formed.
    public String getUiFilename() {
        return uiFilename;
    }

    public void setUiFilename(String uiFilename) {
        this.uiFilename = uiFilename;
    }

    // Column G: This will be "$fileId-$dbFilename" to guaruntee uniqueness on the file system
    public String getSftpPath() {
        return sftpPath;
    }

    public void setSftpPath(String sftpPath) {
        this.sftpPath = sftpPath;
    }

    // Column H: ???: TBD, spec doesn't have any info on this column
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    // Column I: Leave blank
    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    // Column J: Leave blank
    public String getIsProcessed() {
        return isProcessed;
    }

    public void setIsProcessed(String isProcessed) {
        this.isProcessed = isProcessed;
    }

    // Column K: Leave blank
    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    // Column L: Leave blank
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}
