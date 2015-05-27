package edu.arizona.kra.global.unit.create;

import java.util.List;
import java.util.Map;

import org.kuali.kra.bo.UnitAdministrator;
import org.kuali.kra.infrastructure.KeyConstants;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.util.GlobalVariables;

import edu.arizona.kra.global.unit.UnitAdministratorGlobalRuleBase;
import edu.arizona.kra.global.unit.UnitAdministratorPropertyConstants;

@SuppressWarnings( "deprecation" )
public class UnitAdministratorCreateGlobalRule extends UnitAdministratorGlobalRuleBase {

	/*
	 * These corresponds to the UnitAdministratorCreateGlobalMaintenanceDocument, the framework will place the error
	 * messages next to the UI element that has this property name.
	 */

	@Override
	protected boolean processCustomSaveDocumentBusinessRules( MaintenanceDocument document ) {

		UnitAdministratorCreateGlobal unitAdministratorCreateGlobal = (UnitAdministratorCreateGlobal) super.getNewBo();
		boolean success = true;
		success &= validateUnitAdministratorsNotExist( unitAdministratorCreateGlobal.getAllUnitAdministratorPrimaryKeys() );
		success &= validateAllUnitAdministrators( unitAdministratorCreateGlobal.getAllUnitAdministratorPrimaryKeys(), UnitAdministratorPropertyConstants.UNIT_ADMIN_CREATE_GLOBAL_PROPERTY_NAME );
		return success;
	}

	protected boolean validateUnitAdministratorsNotExist( List<Map<String, Object>> allUnitAdministrators ) {
		for ( Map<String, Object> primaryKeyMap : allUnitAdministrators ) {
			UnitAdministrator unitAdministrator = getBoService().findByPrimaryKey( UnitAdministrator.class, primaryKeyMap );
			if ( unitAdministrator != null ) {
				GlobalVariables.getMessageMap().putError( UnitAdministratorPropertyConstants.UNIT_ADMIN_CREATE_GLOBAL_PROPERTY_NAME, KeyConstants.ERROR_UNIT_ADMIN_DUPLICATE, primaryKeyMap.toString() );
				return false;
			}
		}
		return true;
	}
}
