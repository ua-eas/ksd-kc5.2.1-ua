package edu.arizona.kra.irb.pdf.sql.enums;

public enum HuronDestination {
    HistoricalDocuments("Historical Documents"),
    ConsentForms("Consent Forms"),
    OtherAttachments("Other Attachments");

    private final String destination;

    public String getDestination() {
        return destination;
    }

    HuronDestination(String destination) {
        this.destination = destination;
    }



}
