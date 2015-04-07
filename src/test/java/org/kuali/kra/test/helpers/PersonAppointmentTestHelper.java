package org.kuali.kra.test.helpers;

import org.kuali.kra.bo.PersonAppointment;
import org.kuali.kra.test.fixtures.PersonAppointmentFixture;

public class PersonAppointmentTestHelper extends TestHelper {

	public PersonAppointment createPersonAppointment (PersonAppointmentFixture personAppointmentFixture) {
		
		PersonAppointment personAppointment = buildPersonAppointment(personAppointmentFixture);
		
		return personAppointment;
	}
	
	private PersonAppointment buildPersonAppointment(PersonAppointmentFixture personAppointmentFixture) {
		PersonAppointment personAppointment = new PersonAppointment();
		personAppointment.setAppointmentId(personAppointmentFixture.getAppointmentId());
		personAppointment.setPersonId(personAppointmentFixture.getPersonId());
		personAppointment.setUnitNumber(personAppointmentFixture.getUnitNumber());
		personAppointment.setJobCode(personAppointmentFixture.getJobCode());
		return personAppointment;
	}
	
}
