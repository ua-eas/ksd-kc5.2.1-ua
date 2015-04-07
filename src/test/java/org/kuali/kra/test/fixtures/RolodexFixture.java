package org.kuali.kra.test.fixtures;

public enum RolodexFixture {
	
	TEST_1(10, "Chris", "Lastname", "Lockheed", "000001"),
	TEST_2(11, "Firstname", "Lastname", "George", "000001");
	
	private int rolodexId;
	private String firstName;
	private String lastName;
	private String organization;
	private String ownedByUnit;
	
	private RolodexFixture(int rolodexId, String firstName, String lastName, String organization, String ownedByUnit) {
		this.rolodexId = rolodexId;
		this.firstName = firstName;
		this.organization = organization;
		this.lastName = lastName;
		this.ownedByUnit = ownedByUnit;
	}
	
	public int getRolodexId() {
		return rolodexId;
	}
	public String getFirstName() {
		return firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public String getOrganization() {
		return organization;
	}
	public String getOwnedByUnit() {
		return ownedByUnit;
	}
}
