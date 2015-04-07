package org.kuali.kra.test.fixtures;

public enum ValidCeRateTypeFixture {
	OK_VALID_CE_RATE_TYPE( "10", "1", "4210" );
	
	//TODO: Each of these values are foriegn keys from another table, and should
	//TODO: probably have their own fixtures.
	private final String rateClassCode;
	private final String rateTypeCode;
	private final String costElement;

	private ValidCeRateTypeFixture(String rateClassCode, String rateTypeCode, String costElement) {
		this.rateClassCode = rateClassCode;
		this.rateTypeCode = rateTypeCode;
		this.costElement = costElement;
	}

	public String getRateClassCode() {
		return rateClassCode;
	}

	public String getRateTypeCode() {
		return rateTypeCode;
	}

	public String getCostElement() {
		return costElement;
	}

}
