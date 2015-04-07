package org.kuali.kra.test.fixtures;

public enum MailTypeFixture {
	MAIL_1( "1", "Regular" ),
	MAIL_2( "2", "Expedited" ),
	MAIL_3( "3", "Delivery Service" );

	private final String mailType;
	private final String description;

	private MailTypeFixture(String mailType, String description) {
		this.mailType = mailType;
		this.description = description;
	}

	public String getMailType() {
		return mailType;
	}

	public String getDescription() {
		return description;
	}
}
