package edu.arizona.kra.global.unit.delete;

import java.util.Map;

import org.kuali.kra.bo.UnitAdministrator;
import org.kuali.kra.infrastructure.KeyConstants;
import org.kuali.kra.rules.KraMaintenanceDocumentRuleBase;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.util.GlobalVariables;

@SuppressWarnings("deprecation")
public class UnitAdministratorDeleteGlobalRule extends KraMaintenanceDocumentRuleBase {
	
	@Override
	protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
		
		UnitAdministratorDeleteGlobal unitAdministratorDeleteGlobal = (UnitAdministratorDeleteGlobal) super.getNewBo();
		
        int errorCount = 0;
        for (Map<String, Object> primaryKeyMap : unitAdministratorDeleteGlobal.getAllUnitAdministratorPrimaryKeys()) {
        		// Check to ensure this record doesn't exist
        		UnitAdministrator unitAdministrator = unitAdministratorDeleteGlobal.getUnitAdministratorByPrimaryKey(primaryKeyMap);        		
        		if(unitAdministrator == null) {
        			GlobalVariables.getMessageMap().putError("unitAdministratorDeleteGlobal", KeyConstants.ERROR_UNIT_ADMIN_NONEXISTENT, primaryKeyMap.toString());
        			errorCount++;
        		}
        }

		return errorCount > 0 ? false : true;
	}
	
}
