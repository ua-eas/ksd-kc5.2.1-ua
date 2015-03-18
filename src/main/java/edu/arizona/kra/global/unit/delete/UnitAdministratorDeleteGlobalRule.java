package edu.arizona.kra.global.unit.delete;

import java.util.Map;

import org.kuali.kra.bo.UnitAdministrator;
import org.kuali.kra.infrastructure.KeyConstants;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.util.GlobalVariables;

import edu.arizona.kra.global.unit.UnitAdministratorPrimaryKey;

@SuppressWarnings("deprecation")
public class UnitAdministratorDeleteGlobalRule extends UnitAdministratorGlobalRuleBase {

	/*
	 * This corresponds to the UnitAdministratorDeleteGlobalMaintenanceDocument,
	 * the framework will places the error message next to the UI element
	 * that has this property name.
	 */
	private static final String UNIT_ADMIN_DELETE_GLOBAL_DETAIL_PROPERTY_NAME = "unitAdministratorDeleteGlobal";


	
	
	@Override
	protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
		
		UnitAdministratorDeleteGlobal unitAdministratorDeleteGlobal = (UnitAdministratorDeleteGlobal) super.getNewBo();
		
        int errorCount = 0;
        for (Map<String, Object> primaryKeyMap : unitAdministratorDeleteGlobal.getAllUnitAdministratorPrimaryKeys()) {
        		
        		// Check to ensure this record doesn't exist
        		UnitAdministrator unitAdministrator = unitAdministratorDeleteGlobal.getUnitAdministratorByPrimaryKey(primaryKeyMap);        		
        		if(unitAdministrator == null) {
        			GlobalVariables.getMessageMap().putError(UNIT_ADMIN_DELETE_GLOBAL_DETAIL_PROPERTY_NAME,
        					                                 KeyConstants.ERROR_UNIT_ADMIN_NONEXISTENT,
        					                                 primaryKeyMap.toString());
        			errorCount++;
        		}
        		
        		String personId = (String)primaryKeyMap.get(UnitAdministratorPrimaryKey.PERSON_ID.getValue());
        		errorCount += validatePersonId(personId, UNIT_ADMIN_DELETE_GLOBAL_DETAIL_PROPERTY_NAME);
        		
        		String unitAdminTypeCode = (String)primaryKeyMap.get(UnitAdministratorPrimaryKey.UNIT_ADMIN_TYPE_CODE.getValue());
        		errorCount += validateUnitAdminTypeCode(unitAdminTypeCode, UNIT_ADMIN_DELETE_GLOBAL_DETAIL_PROPERTY_NAME);
        		
        		String unitNumber = (String)primaryKeyMap.get(UnitAdministratorPrimaryKey.UNIT_NUMBER.getValue());
        		errorCount += validateUnitNumber(unitNumber, UNIT_ADMIN_DELETE_GLOBAL_DETAIL_PROPERTY_NAME);
        }

		return errorCount > 0 ? false : true;
	}


}
