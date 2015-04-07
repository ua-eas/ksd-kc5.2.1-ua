package org.kuali.kra.test.fixtures;

public enum ExemptStudiesCheckListFixture {
	
	ONE("1", "Exempt 1"),
	TWO("2", "Exempt 2");
	
	private String code;
	private String description;
	
	
	private ExemptStudiesCheckListFixture (String code, String description) {
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
