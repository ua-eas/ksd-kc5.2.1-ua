package edu.arizona.kra.global.unit.create;

import java.util.HashMap;
import java.util.Map;

import org.kuali.kra.bo.KcPerson;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.kra.service.KcPersonService;
import org.kuali.rice.krad.bo.GlobalBusinessObjectDetailBase;

import edu.arizona.kra.global.unit.UnitAdministratorPrimaryKey;

/**
 * This class is a child instance variable of UnitAdministratorCreateGlobal.
 * 
 * This class encapsulates UnitAdministrator PK's of:
 * 1. personId
 * 2. unitAdministratorTypeCode
 *
 */
public class UnitAdminTypeAndPersonGlobalDetail extends GlobalBusinessObjectDetailBase {
	private static final long serialVersionUID = -9205279192165904750L;

	private String personId; // aka, uaId, e.g.: 104454139762
	private String unitAdministratorTypeCode;

	transient private KcPerson person;
	transient private KcPersonService kcPersonService;



	public UnitAdminTypeAndPersonGlobalDetail() {
		// Have to set this since framework is not, and OJB needs it
		if(versionNumber == null) {
			setVersionNumber(new Long(1L));
		}
	}


	/**
	 * This method returns two of the three prinamry keys for UnitAdministrator:
	 * 1. principalName
	 * 2. unitAdministratorTypeCode
	 * 
	 * @return
	 */
	public Map<String, Object> getPrimaryKeyToValueMap() {
		Map<String, Object> primaryKeyToValueMap = new HashMap<String, Object>();
		primaryKeyToValueMap.put(UnitAdministratorPrimaryKey.PERSON_ID.getValue(), getPersonId());
		primaryKeyToValueMap.put(UnitAdministratorPrimaryKey.UNIT_ADMIN_TYPE_CODE.getValue(), getUnitAdministratorTypeCode());
		return primaryKeyToValueMap;
	}
	
	
	public KcPerson getPerson() {
		if(person == null && personId != null) {
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

}
