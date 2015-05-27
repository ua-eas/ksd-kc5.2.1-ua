package edu.arizona.kra.global.unit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kra.bo.Unit;
import org.kuali.kra.bo.UnitAdministratorType;
import org.kuali.kra.infrastructure.KeyConstants;
import org.kuali.kra.rules.KraMaintenanceDocumentRuleBase;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.util.GlobalVariables;

@SuppressWarnings( "deprecation" )
public class UnitAdministratorGlobalRuleBase extends KraMaintenanceDocumentRuleBase {

	public enum UnitAdministatorGlobalAction {
		CREATE, DELETE
	}

	protected boolean validateAllUnitAdministrators( List<Map<String, Object>> allUnitAdministrators, String propertyName ) {
		boolean success = true;
		if ( allUnitAdministrators.size() == 0 || allUnitAdministrators == null ) {
			GlobalVariables.getMessageMap().putError( propertyName, KeyConstants.ERROR_UNIT_ADMIN_NO_DETAILS );
			return false;
		}

		for ( Map<String, Object> primaryKeyMap : allUnitAdministrators ) {
			String personId = (String) primaryKeyMap.get( UnitAdministratorPrimaryKey.PERSON_ID.getValue() );
			success &= validatePersonId( personId, propertyName );

			String unitAdminTypeCode = (String) primaryKeyMap.get( UnitAdministratorPrimaryKey.UNIT_ADMIN_TYPE_CODE.getValue() );
			success &= validateUnitAdminTypeCode( unitAdminTypeCode, propertyName );

			String unitNumber = (String) primaryKeyMap.get( UnitAdministratorPrimaryKey.UNIT_NUMBER.getValue() );
			success &= validateUnitNumber( unitNumber, propertyName );
		}
		return success;
	}

	protected boolean validatePersonId( String personId, String propertyName ) {
		Person person = getPersonService().getPerson( personId );
		if ( person == null ) {
			Map<String, Object> primaryKeyMap = new HashMap<String, Object>();
			primaryKeyMap.put( UnitAdministratorPrimaryKey.PERSON_ID.getValue(), personId );
			GlobalVariables.getMessageMap().putError( propertyName, KeyConstants.ERROR_UNIT_ADMIN_PERSON_ID, primaryKeyMap.toString() );
			return false;
		}
		return true;
	}

	protected boolean validateUnitAdminTypeCode( String unitAdminTypeCode, String propertyName ) {
		String key = UnitAdministratorPrimaryKey.UNIT_ADMIN_TYPE_CODE.getValue();
		Map<String, Object> primaryKeyMap = new HashMap<String, Object>();
		primaryKeyMap.put( key, unitAdminTypeCode );
		int boCount = getBoCount( UnitAdministratorType.class, primaryKeyMap );
		if ( boCount < 1 ) {
			GlobalVariables.getMessageMap().putError( propertyName, KeyConstants.ERROR_UNIT_ADMIN_TYPE_CODE, primaryKeyMap.toString() );
			return false;
		}
		return true;
	}

	protected boolean validateUnitNumber( String unitNumber, String propertyName ) {
		String key = UnitAdministratorPrimaryKey.UNIT_NUMBER.getValue();
		Map<String, Object> primaryKeyMap = new HashMap<String, Object>();
		primaryKeyMap.put( key, unitNumber );
		int boCount = getBoCount( Unit.class, primaryKeyMap );
		if ( boCount < 1 ) {
			GlobalVariables.getMessageMap().putError( propertyName, KeyConstants.ERROR_UNIT_NUMBER, primaryKeyMap.toString() );
			return false;
		}
		return true;
	}

	private int getBoCount( Class<? extends PersistableBusinessObjectBase> clazz, Map<String, Object> primaryKeyMap ) {
		return getBoService().countMatching( clazz, primaryKeyMap );
	}

}
