package edu.arizona.kra.irb.pdf.sql.enums;


public enum Category {
    CV("CV"),
    DataCollectionTool("Data Collection Tool"),
    DataMonitoringPlan("Data Monitoring Plan"),
    Other("Other"),
    HSPPForm("HSPP Form"),
    ParticipantMaterial("Participant Material"),
    RegulatoryDocumentation("Regulatory Documentation");


    private final String description;


    Category(String description) {
        this.description = description;
    }


    public String getDescription() {
        return description;
    }


    public static Category getByAttachmentType(AttachmentType attachmentType) {
        switch (attachmentType) {
            case CV:
                return CV;
            case DataCollectionTools:
                return DataCollectionTool;
            case DataMonitoringPlan:
                return DataMonitoringPlan;
            case HSPPFormsCorrespondence:
                return HSPPForm;
            case ParticipantMaterial:
                return ParticipantMaterial;
            case RegulatoryDocumentation:
                return RegulatoryDocumentation;
            case DrugDeviceInfo:
            case GeneralCorrespondence:
            case GrantContracts:
            case InformedConsentPHIForms:
            case OtherApprovalsAndAuthorizations:
            case Other:
            case Privacy:
            case Protocol:
            case RecruitmentMaterial:
                return Other;
            default:
                throw new RuntimeException(
                        "Unregistered AttachmentType, need to add another case statement!: " + attachmentType.getDescription());

        }
    }
}
