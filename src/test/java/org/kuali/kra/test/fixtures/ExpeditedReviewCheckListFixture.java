package org.kuali.kra.test.fixtures;

public enum ExpeditedReviewCheckListFixture {
	
	ONE("1", "Expedited 1"),
	TWO("2", "Expedited 2");
	
	private String code;
	private String description;
	
	
	private ExpeditedReviewCheckListFixture (String code, String description) {
		this.code = code;
		this.description = description;
	}


	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}
	
}
