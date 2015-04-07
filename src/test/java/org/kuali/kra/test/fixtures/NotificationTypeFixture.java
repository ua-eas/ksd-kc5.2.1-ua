package org.kuali.kra.test.fixtures;

public enum NotificationTypeFixture {
	NOTIFICATION_TYPE_014001( 10153L, "1", "401", true ),
	NOTIFICATION_TYPE_014002( 10155L, "1", "402", true ),
	NOTIFICATION_TYPE_014003( 10157L, "1", "403", true ),
	NOTIFICATION_TYPE_014004( 10159L, "1", "404", true ), ;

	private final long notificationTypeId;
	private final String moduleCode;  // FIXME: Fixture for CoeusModule.class
	private final String actionCode;  // FIXME: ActionCode? also used in ReportTrackingNotificationFixture
	private final boolean active;

	private NotificationTypeFixture(long notificationTypeId, String moduleCode, String actionCode, boolean active) {
		this.notificationTypeId = notificationTypeId;
		this.moduleCode = moduleCode;
		this.actionCode = actionCode;
		this.active = active;
	}

	public long getNotificationTypeId() {
		return notificationTypeId;
	}

	public String getModuleCode() {
		return moduleCode;
	}

	public String getActionCode() {
		return actionCode;
	}

	public boolean getActive() {
		return active;
	}
}
