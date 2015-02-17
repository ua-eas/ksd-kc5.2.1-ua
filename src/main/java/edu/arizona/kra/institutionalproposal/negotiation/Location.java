package edu.arizona.kra.institutionalproposal.negotiation;

public enum Location {

	SPS("SPS"), ORCA("ORCA");
	
	Location(String desc) {
		this.desc = desc;
	}
	private String desc;
	public String getDesc() {
		return desc;
	}
}
