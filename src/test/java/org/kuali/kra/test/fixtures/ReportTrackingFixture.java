package org.kuali.kra.test.fixtures;

import java.sql.Date;

import java.util.Calendar;

public enum ReportTrackingFixture {
	REPORT_TRACKING_9999001( 9999001L, "4", "4", 0, "9999001L" ),
	REPORT_TRACKING_9999002( 9999002L, "4", "4", -29, "9999002L" ),
	REPORT_TRACKING_9999003( 9999003L, "4", "4", -30, "9999003L" ),
	REPORT_TRACKING_9999004( 9999004L, "4", "4", -59, "9999004L" ),
	REPORT_TRACKING_9999005( 9999005L, "4", "4", -60, "9999005L" ),
	REPORT_TRACKING_9999006( 9999006L, "4", "4", 10, "9999006L" ),
	REPORT_TRACKING_9999007( 9999007L, "4", "4", 30, "9999007L" ),
	REPORT_TRACKING_9999008( 9999008L, "4", "4", 60, "9999008L" ),
	REPORT_TRACKING_9999009( 9999009L, "4", "4", 61, "9999009L" ),
	REPORT_TRACKING_9999010( 9999010L, "4", "4", -40, "9999010L" );

	private final Long awardReportTermId;
	private final String reportClassCode;
	private final String frequencyBaseCode;
	private final Date dueDate;
	private final String objectId;

	private ReportTrackingFixture(Long awardReportTermId, String reportClassCode, String frequencyBaseCode, Integer dueDateOffset, String objectId) {
		this.awardReportTermId = awardReportTermId;
		this.reportClassCode = reportClassCode;
		this.frequencyBaseCode = frequencyBaseCode;
		Calendar newDate = Calendar.getInstance();
		newDate.add( Calendar.DAY_OF_YEAR, dueDateOffset );
		Date dueDate = new Date( newDate.getTime().getTime() );
		this.dueDate = dueDate;
		this.objectId = objectId;
	}

	public Long getAwardReportTermId() {
		return awardReportTermId;
	}

	public String getReportClassCode() {
		return reportClassCode;
	}

	public String getFrequencyBaseCode() {
		return frequencyBaseCode;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public String getObjectId() {
		return objectId;
	}

}
