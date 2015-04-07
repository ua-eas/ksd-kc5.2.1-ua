package org.kuali.kra.test.fixtures;

public enum CostElementFixture {
	
	TEST_1("422311", "Test 1", "KRABI");
	
	String costElement;
	String description;
	String updateUser;
	
	private CostElementFixture(String costElement, String description, String updateUser) {
		this.costElement = costElement;
		this.description = description;
		this.updateUser = updateUser;
	}

	public String getCostElement() {
		return costElement;
	}

	public String getDescription() {
		return description;
	}

	public String getUpdateUser() {
		return updateUser;
	}
}
