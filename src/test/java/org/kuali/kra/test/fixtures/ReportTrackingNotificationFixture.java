package org.kuali.kra.test.fixtures;

public enum ReportTrackingNotificationFixture {

	REPORT_TRACKING_NOTIFICATION_999001( "Test", "401", true, 30, 30, "4" ),
	REPORT_TRACKING_NOTIFICATION_999002( "Test", "402", true, 60, 30, "4" ),
	REPORT_TRACKING_NOTIFICATION_999003( "Test", "403", false, 30, 30, "4" ),
	REPORT_TRACKING_NOTIFICATION_999004( "Test", "404", false, 60, 30, "4" );

	private final String name;
	private final String actionCode;
	private final boolean override;
	private final Integer days;
	private final Integer scope;
	private final String reportClassCode;

	ReportTrackingNotificationFixture(String name, String actionCode, boolean override, Integer days, Integer scope, String reportClassCode) {
		this.name = name;
		this.actionCode = actionCode;
		this.override = override;
		this.days = days;
		this.scope = scope;
		this.reportClassCode = reportClassCode;
	}

	public String getName() {
		return name;
	}

	public String getActionCode() {
		return actionCode;
	}

	public boolean isOverride() {
		return override;
	}

	public Integer getDays() {
		return days;
	}

	public Integer getScope() {
		return scope;
	}

	public String getReportClassCode() {
		return reportClassCode;
	}

}
