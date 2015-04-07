package org.kuali.kra.test.fixtures;

public enum PersonAppointmentFixture {

	PERSON_APPOINTMENT_1(1, PersonFixture.UAR_TEST_001.getPrincipalId(), UnitFixture.TEST_1.getUnitNumber(), "TEST"),
	PERSON_APPOINTMENT_2(2, PersonFixture.UAR_TEST_002.getPrincipalId(), UnitFixture.TEST_2.getUnitNumber(), "TEST");
	
	private int appointmentId;
	private String personId;
	private String unitNumber;
	private String jobCode;
	
	private PersonAppointmentFixture (int appointmentId, String personId, String unitNumber, String jobCode) {
		this.appointmentId = appointmentId;
		this.personId = personId;
		this.unitNumber = unitNumber;
		this.jobCode = jobCode;
	}

	public int getAppointmentId() {
		return appointmentId;
	}

	public String getPersonId() {
		return personId;
	}

	public String getUnitNumber() {
		return unitNumber;
	}

	public String getJobCode() {
		return jobCode;
	}
	
}
