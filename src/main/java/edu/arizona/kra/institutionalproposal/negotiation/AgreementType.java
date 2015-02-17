package edu.arizona.kra.institutionalproposal.negotiation;

public enum AgreementType {

	ATA("AZ Telemedicine Agreement"), CDA("CDA"), CTA("Clinical Trial Agreement"), 
	CTF("Clinical Training Affiliation"), HS("Health Sciences"), MOU("MOU Non-Sponsored"), 
	MTA("MTA"), PA("Preceptor Agreement"), SS("Sales & Service"),	SPA("Site Preceptor Agreement"), 
	SRA("Site Rotation Agreement"),	TAA("Training Affiliation Agreement"), OT("Other");
	
	AgreementType(String desc) {
		this.desc = desc;
	}
	
	private String desc;
	public String getDesc() {
		return desc;
	}
	
}
