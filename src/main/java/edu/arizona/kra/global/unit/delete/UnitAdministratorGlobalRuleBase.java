package edu.arizona.kra.global.unit.delete;

import java.util.HashMap;
import java.util.Map;

import org.kuali.kra.bo.Unit;
import org.kuali.kra.bo.UnitAdministratorType;
import org.kuali.kra.infrastructure.KeyConstants;
import org.kuali.kra.rules.KraMaintenanceDocumentRuleBase;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.util.GlobalVariables;

import edu.arizona.kra.global.unit.UnitAdministratorPrimaryKey;

@SuppressWarnings("deprecation")
public class UnitAdministratorGlobalRuleBase extends KraMaintenanceDocumentRuleBase {

	

	protected int validatePersonId(String personId, String propertyName) {
		int errorCount = 0;
		Person person = getPersonService().getPerson(personId);
		if (person == null) {
			errorCount++;
			Map<String, Object> primaryKeyMap = new HashMap<String, Object>();
			primaryKeyMap.put(UnitAdministratorPrimaryKey.PERSON_ID.getValue(), personId);
			GlobalVariables.getMessageMap().putError(propertyName, KeyConstants.ERROR_UNIT_ADMIN_PERSON_ID, primaryKeyMap.toString());
		}
		return errorCount;
	}

	protected int validateUnitAdminTypeCode(String unitAdminTypeCode, String propertyName) {
		int errorCount = 0;
		String key = UnitAdministratorPrimaryKey.UNIT_ADMIN_TYPE_CODE.getValue();
		Map<String, Object> primaryKeyMap = new HashMap<String, Object>();
		primaryKeyMap.put(key, unitAdminTypeCode);
		int boCount = getBoCount(UnitAdministratorType.class, primaryKeyMap);
		if (boCount < 1) {
			errorCount++;
			GlobalVariables.getMessageMap().putError(propertyName, KeyConstants.ERROR_UNIT_ADMIN_TYPE_CODE, primaryKeyMap.toString());
		}
		return errorCount;
	}


	protected int validateUnitNumber(String unitNumber, String propertyName) {
		int errorCount = 0;
		String key = UnitAdministratorPrimaryKey.UNIT_NUMBER.getValue();
		Map<String, Object> primaryKeyMap = new HashMap<String, Object>();
		primaryKeyMap.put(key, unitNumber);
		int boCount = getBoCount(Unit.class, primaryKeyMap);
		if (boCount < 1) {
			errorCount++;
			GlobalVariables.getMessageMap().putError(propertyName, KeyConstants.ERROR_UNIT_NUMBER, primaryKeyMap.toString());
		}
		return errorCount;
	}

	
	private int getBoCount(Class<? extends PersistableBusinessObjectBase> clazz, Map<String, Object> primaryKeyMap) {
		return getBoService().countMatching(clazz, primaryKeyMap);
	}


}
