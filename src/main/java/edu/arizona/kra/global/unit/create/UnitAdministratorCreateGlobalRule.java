package edu.arizona.kra.global.unit.create;

import java.util.Map;

import org.kuali.kra.bo.UnitAdministrator;
import org.kuali.kra.infrastructure.KeyConstants;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.util.GlobalVariables;

import edu.arizona.kra.global.unit.UnitAdministratorPrimaryKey;
import edu.arizona.kra.global.unit.delete.UnitAdministratorGlobalRuleBase;

@SuppressWarnings("deprecation")
public class UnitAdministratorCreateGlobalRule extends UnitAdministratorGlobalRuleBase {
	
	/*
	 * These corresponds to the UnitAdministratorCreateGlobalMaintenanceDocument,
	 * the framework will place the error messages next to the UI element
	 * that has this property name.
	 */
	private static final String UNIT_ADMIN_CREATE_GLOBAL_PROPERTY_NAME = "unitAdministratorCreateGlobal";
	private static final String UNIT_ADMIN_CREATE_UNIT_CREATE_PROPERTY_NAME = "unitCreateGlobal";


	
	@Override
	protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
		
		UnitAdministratorCreateGlobal unitAdministratorCreateGlobal = (UnitAdministratorCreateGlobal) super.getNewBo();
		
        int errorCount = 0;
        for (Map<String, Object> primaryKeyMap : unitAdministratorCreateGlobal.getAllUnitAdministratorPrimaryKeys()) {
        		// Check to ensure this record doesn't exist
        		UnitAdministrator unitAdministrator = unitAdministratorCreateGlobal.getUnitAdministratorByPrimaryKey(primaryKeyMap);        		
        		if(unitAdministrator != null) {
        			GlobalVariables.getMessageMap().putError(UNIT_ADMIN_CREATE_GLOBAL_PROPERTY_NAME, KeyConstants.ERROR_UNIT_ADMIN_DUPLICATE, primaryKeyMap.toString());
        			errorCount++;
        		}
        		
        		String personId = (String)primaryKeyMap.get(UnitAdministratorPrimaryKey.PERSON_ID.getValue());
        		errorCount += validatePersonId(personId, UNIT_ADMIN_CREATE_GLOBAL_PROPERTY_NAME);
        		
        		String unitAdminTypeCode = (String)primaryKeyMap.get(UnitAdministratorPrimaryKey.UNIT_ADMIN_TYPE_CODE.getValue());
        		errorCount += validateUnitAdminTypeCode(unitAdminTypeCode, UNIT_ADMIN_CREATE_GLOBAL_PROPERTY_NAME);
        		
        		String unitNumber = (String)primaryKeyMap.get(UnitAdministratorPrimaryKey.UNIT_NUMBER.getValue());
        		errorCount += validateUnitNumber(unitNumber, UNIT_ADMIN_CREATE_UNIT_CREATE_PROPERTY_NAME);
        }

		return errorCount > 0 ? false : true;
	}
	
}
