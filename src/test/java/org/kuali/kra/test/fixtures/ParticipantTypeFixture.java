package org.kuali.kra.test.fixtures;

public enum ParticipantTypeFixture {

	KEY_VALUE_000( "", "select" ),
	KEY_VALUE_001( "1", "Children" ),
	KEY_VALUE_002( "2", "Decisionally impaired" ),
	KEY_VALUE_003( "3", "Employees" ),
	KEY_VALUE_004( "4", "Prisoners" ),
	KEY_VALUE_005( "5", "Pregnant women" ),
	KEY_VALUE_006( "6", "Fetuses" ),
	KEY_VALUE_007( "7", "Students" ),
	KEY_VALUE_008( "8", "Students - minors" ),
	KEY_VALUE_009( "9", "Wards of the state" ),
	KEY_VALUE_010( "10", "Other" ),
	KEY_VALUE_011( "11", "#11 Other" ),
	KEY_VALUE_012( "12", "#12 Other" ),
	KEY_VALUE_013( "13", "#13 Other" ),
	KEY_VALUE_014( "14", "#14 Other" ),
	KEY_VALUE_015( "15", "#15 Other" ),
	KEY_VALUE_016( "16", "#16 Other" ),
	KEY_VALUE_017( "17", "#17 Other" ),
	KEY_VALUE_018( "18", "#18 Other" ),
	KEY_VALUE_019( "19", "#19 Other" ),
	KEY_VALUE_020( "20", "#20 Other" );
	private final String key;
	private final String value;

	private ParticipantTypeFixture(String key, String value) {
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
