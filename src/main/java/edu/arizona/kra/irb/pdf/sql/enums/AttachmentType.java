package edu.arizona.kra.irb.pdf.sql.enums;

public enum AttachmentType {
    CV("CV"),
    DataCollectionTools("Data Collection Tools"),
    DataMonitoringPlan("Data Monitoring Plan"),
    DrugDeviceInfo("Drug/Device info"),
    GeneralCorrespondence("General Correspondence"),
    GrantContracts("Grant/Contracts"),
    HSPPFormsCorrespondence("HSPP Forms/Correspondence"),
    InformedConsentPHIForms("Informed Consent/PHI Forms"),
    Other("Other"),
    OtherApprovalsAndAuthorizations("Other Approvals and Authorizations"),
    ParticipantMaterial("Participant Material"),
    Privacy("Privacy"),
    Protocol("Protocol"),
    RecruitmentMaterial("Recruitment Material"),
    RegulatoryDocumentation("Regulatory Documentation");



    private final String description;


    AttachmentType(String description) {
        this.description = description;
    }


    public String getDescription() {
        return description;
    }


    public static AttachmentType getByDescription(String description) {
        for (AttachmentType attachmentType : AttachmentType.values()) {
            if (attachmentType.getDescription().equals(description)) {
                return attachmentType;
            }
        }

        // If we're here, this is an unexpected attachment type
        throw new RuntimeException("Unkown attachment type: " + description);
    }
}
