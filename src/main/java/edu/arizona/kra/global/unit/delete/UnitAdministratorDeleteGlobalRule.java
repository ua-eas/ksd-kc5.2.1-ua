package edu.arizona.kra.global.unit.delete;

import java.util.List;
import java.util.Map;

import org.kuali.kra.bo.UnitAdministrator;
import org.kuali.kra.infrastructure.KeyConstants;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.util.GlobalVariables;

import edu.arizona.kra.global.unit.UnitAdministratorGlobalRuleBase;
import edu.arizona.kra.global.unit.UnitAdministratorPropertyConstants;

@SuppressWarnings( "deprecation" )
public class UnitAdministratorDeleteGlobalRule extends UnitAdministratorGlobalRuleBase {

	/*
	 * This corresponds to the UnitAdministratorDeleteGlobalMaintenanceDocument, the framework will places the error
	 * message next to the UI element that has this property name.
	 */

	@Override
	protected boolean processCustomSaveDocumentBusinessRules( MaintenanceDocument document ) {

		UnitAdministratorDeleteGlobal unitAdministratorDeleteGlobal = (UnitAdministratorDeleteGlobal) super.getNewBo();
		boolean success = true;
		success &= validateUnitAdministratorsExist( unitAdministratorDeleteGlobal.getAllUnitAdministratorPrimaryKeys() );
		success &= validateAllUnitAdministrators( unitAdministratorDeleteGlobal.getAllUnitAdministratorPrimaryKeys(), UnitAdministratorPropertyConstants.UNIT_ADMIN_DELETE_GLOBAL_PROPERTY_NAME );
		return success;
	}

	protected boolean validateUnitAdministratorsExist( List<Map<String, Object>> allUnitAdministrators ) {
		for ( Map<String, Object> primaryKeyMap : allUnitAdministrators ) {
			UnitAdministrator unitAdministrator = getBoService().findByPrimaryKey( UnitAdministrator.class, primaryKeyMap );
			if ( unitAdministrator == null ) {
				GlobalVariables.getMessageMap().putError( UnitAdministratorPropertyConstants.UNIT_ADMIN_DELETE_GLOBAL_PROPERTY_NAME, KeyConstants.ERROR_UNIT_ADMIN_NONEXISTENT, primaryKeyMap.toString() );
				return false;
			}
		}
		return true;
	}
}
