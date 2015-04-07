package org.kuali.kra.test.fixtures;


public enum NoticeOfOpportunityFixture {

	KEY_VALUE_000( "", "select" ),
	KEY_VALUE_001( "1", "Federal Solicitation" ),
	KEY_VALUE_002( "2", "Unsolicited" ),
	KEY_VALUE_003( "3", "Verbal Request for Proposal" ),
	KEY_VALUE_004( "4", "SBIR Solicitation" ),
	KEY_VALUE_005( "5", "STTR Solicitation" ),
	KEY_VALUE_006( "6", "Non-Federal Solicitation" ),
	KEY_VALUE_007( "7", "Internal" );

	private final String key;
	private final String value;

	private NoticeOfOpportunityFixture(String key, String value) {
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
