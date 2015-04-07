package org.kuali.kra.test.fixtures;

public enum OrgFixture {

	ONE("Test Organization 1", "999991", 1),
	TWO("Test Organization 2", "999992", 2);
	
	private String orgName;
	private String orgId;
	private int contactAddressId;

	private OrgFixture (String orgName, String orgId, int contactAddressId) {
		this.orgName = orgName;
		this.orgId = orgId;
		this.contactAddressId = contactAddressId;
	}

	public String getOrgName() {
		return orgName;
	}

	public String getOrgId() {
		return orgId;
	}

	public int getContactAddressId() {
		return contactAddressId;
	}

}
