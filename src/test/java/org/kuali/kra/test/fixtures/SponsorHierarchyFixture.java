package org.kuali.kra.test.fixtures;

public enum SponsorHierarchyFixture {
	
	TEST_1("NASA", "010152");
	
	private String hierarchyName;
	private String sponsorCode;
	
	private SponsorHierarchyFixture(String hierarchyName, String sponsorCode) {
		this.hierarchyName = hierarchyName;
		this.sponsorCode = sponsorCode;
	}
	
	public String getHierarchyName() {
		return hierarchyName;
	}
	public String getSponsorCode() {
		return sponsorCode;
	}
}