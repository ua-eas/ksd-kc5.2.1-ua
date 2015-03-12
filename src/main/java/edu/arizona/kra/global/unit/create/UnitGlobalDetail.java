package edu.arizona.kra.global.unit.create;

import java.util.HashMap;
import java.util.Map;

import org.kuali.rice.krad.bo.GlobalBusinessObjectDetailBase;

import edu.arizona.kra.global.unit.UnitAdministratorPrimaryKey;

/**
 * This class is a child instance variable of UnitAdministratorCreateGlobal.
 * 
 * This class encapsulates a unitNumber, one of UnitAdministrator PK's.
 */
public class UnitGlobalDetail extends GlobalBusinessObjectDetailBase {
	private static final long serialVersionUID = 7539577676956377579L;
	
	private String unitNumber;


	public UnitGlobalDetail () {
		// Have to set this since framework is not, and OJB needs it;
		// also, setting this in the getter does not work, since OJB
		// is accessing the field directly.
		if (getVersionNumber() == null) {
			setVersionNumber(new Long(1L));
		}
	}



	/**
	 * This method returns one of the three primary keys for UnitAdministrator:
	 * 1. unitNumber
	 * 
	 * @return The unitNumber that will be used to associate with a UnitAdministrator
	 */
	public Map<String, Object> getPrimaryKeyToValueMap() {
		Map<String, Object> primaryKeyToValueMap = new HashMap<String, Object>();
		primaryKeyToValueMap.put(UnitAdministratorPrimaryKey.UNIT_NUMBER.getValue(), getUnitNumber());
		return primaryKeyToValueMap;
	}


	public String getUnitNumber() {
		return unitNumber;
	}


	public void setUnitNumber(String unitNumber) {
		this.unitNumber = unitNumber;
	}


}
