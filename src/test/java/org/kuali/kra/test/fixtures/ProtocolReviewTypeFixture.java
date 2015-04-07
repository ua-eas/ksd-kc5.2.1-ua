package org.kuali.kra.test.fixtures;

public enum ProtocolReviewTypeFixture {
	
	FULL("1", "Full", true),
	EXPEDITED("2", "Expedited", true),
	EXEMPT("3", "Exempt", true),
	LIMITED("4", "Limited/Single Use", true),
	NOT_REQUIRED("5", "IRB Review not required", true),
	RESPONSE("6", "Response", true),
	FYI("7", "FYI", true),
	DEFERRAL_OF_OVERSIGHT("8", "Deferral of IRB Oversight", true),
	PENDING_DETERMINATION("9", "Pending IRB Determination", true),
	DETERMINATION_118("10", "118 Determination", true);
	
	
	private String code;
	private String description;
	private boolean globalFlag;

	private ProtocolReviewTypeFixture (String code, String description, boolean globalFlag) {
		this.code = code;
		this.description = description;
		this.globalFlag = globalFlag;
	}

	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}

	public boolean getGlobalFlag() {
		return globalFlag;
	}

}
