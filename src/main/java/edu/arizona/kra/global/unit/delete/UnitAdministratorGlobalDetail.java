package edu.arizona.kra.global.unit.delete;

import java.util.HashMap;
import java.util.Map;

import org.kuali.kra.bo.KcPerson;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.kra.service.KcPersonService;
import org.kuali.rice.krad.bo.GlobalBusinessObjectDetailBase;

import edu.arizona.kra.global.unit.UnitAdministratorPrimaryKey;

/**
 * This class is an instance variable(list) inside UnitAdministratorDeleteGlobal.
 * 
 * This class encapsulates the PK's for one UnitAdministrator.
 *
 */
public class UnitAdministratorGlobalDetail extends GlobalBusinessObjectDetailBase {
	private static final long serialVersionUID = 4642810936457709004L;
	
	private String unitNumber;
	private String personId; // aka, uaId, e.g.: 104454139762
	private String unitAdministratorTypeCode;

	transient private KcPerson person;
	transient private KcPersonService kcPersonService;



	public UnitAdministratorGlobalDetail() {
		// Have to set this since framework is not, and OJB needs it
		if(versionNumber == null) {
			setVersionNumber(new Long(1L));
		}
	}

	public Map<String, Object> getPrimaryKeyToValueMap() {
		Map<String, Object> primaryKeyToValueMap = new HashMap<String, Object>();
		primaryKeyToValueMap.put(UnitAdministratorPrimaryKey.PERSON_ID.getValue(), getPersonId());
		primaryKeyToValueMap.put(UnitAdministratorPrimaryKey.UNIT_ADMIN_TYPE_CODE.getValue(), getUnitAdministratorTypeCode());
		primaryKeyToValueMap.put(UnitAdministratorPrimaryKey.UNIT_NUMBER.getValue(), getUnitNumber());
		return primaryKeyToValueMap;
	}
	
	
	public KcPerson getPerson() {
		if(person == null && personId != null || personId != null) {
			if (person != null && person.getPersonId().equals(personId)) {
				return person;
			}
			person = getKcPersonService().getKcPersonByPersonId(personId);
		}
        return person;
    }


	protected KcPersonService getKcPersonService() {
        if (this.kcPersonService == null) {
            this.kcPersonService = KraServiceLocator.getService(KcPersonService.class);
        }
        return this.kcPersonService;
    }


	public String getPersonId() {
		return personId;
	}


	public void setPersonId(String principalName) {
		this.personId = principalName;
	}


	public String getUnitAdministratorTypeCode() {
		return unitAdministratorTypeCode;
	}


	public void setUnitAdministratorTypeCode(String unitAdminTypeCode) {
		this.unitAdministratorTypeCode = unitAdminTypeCode;
	}

	public String getUnitNumber() {
		return unitNumber;
	}

	public void setUnitNumber(String unitNumber) {
		this.unitNumber = unitNumber;
	}

}
