package org.kuali.kra.test.fixtures;

public enum SponsorFixture {
	
	ASU("010152", "Arizona State University", "6", 1, "000001"),
	AZ_STATE("010159", "State of Arizona", "1", 1, "000001");

	private String sponsorCode; // Six chars
	private String sponsorName;
	private String sponsorTypeCode;
	private int rolodexId;
	private String ownedByUnit;

	private SponsorFixture (String sponsorCode, String sponsorName, String sponsorTypeCode, int rolodexId, String ownedByUnit) {
		this.sponsorCode = sponsorCode;
		this.sponsorName = sponsorName;
		this.sponsorTypeCode = sponsorTypeCode;
		this.rolodexId = rolodexId;
		this.ownedByUnit = ownedByUnit;
	}

	public String getSponsorCode() {
		return sponsorCode;
	}

	public String getSponsorName() {
		return sponsorName;
	}

	public String getSponsorTypeCode() {
		return sponsorTypeCode;
	}

	public int getRolodexId() {
		return rolodexId;
	}

	public String getOwnedByUnit() {
		return ownedByUnit;
	}

}
