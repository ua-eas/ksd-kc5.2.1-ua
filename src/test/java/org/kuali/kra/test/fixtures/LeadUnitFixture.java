package org.kuali.kra.test.fixtures;

public enum LeadUnitFixture {

	KEY_VALUE_000( "", "select" ),
	KEY_VALUE_001( "000001", "University" ),
	KEY_VALUE_002( "IN-CARD", "CARDIOLOGY" ),
	KEY_VALUE_003( "IN-CARR", "CARDIOLOGY RECHARGE CTR" ),
	KEY_VALUE_004( "BL-IIDC", "IND INST ON DISABILITY/COMMNTY" );

	private final String key;
	private final String value;

	private LeadUnitFixture(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

}