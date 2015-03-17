package edu.arizona.kra.global.unit.create;

import java.util.Map;

import org.kuali.kra.bo.UnitAdministrator;
import org.kuali.kra.infrastructure.KeyConstants;
import org.kuali.kra.rules.KraMaintenanceDocumentRuleBase;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.util.GlobalVariables;

@SuppressWarnings("deprecation")
public class UnitAdministratorCreateGlobalRule extends KraMaintenanceDocumentRuleBase {
	
	@Override
	protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
		
		UnitAdministratorCreateGlobal unitAdministratorCreateGlobal = (UnitAdministratorCreateGlobal) super.getNewBo();
		
        int errorCount = 0;
        for (Map<String, Object> primaryKeyMap : unitAdministratorCreateGlobal.getAllUnitAdministratorPrimaryKeys()) {
        		// Check to ensure this record doesn't exist
        		UnitAdministrator unitAdministrator = unitAdministratorCreateGlobal.getUnitAdministratorByPrimaryKey(primaryKeyMap);        		
        		if(unitAdministrator != null) {
        			GlobalVariables.getMessageMap().putError("unitAdministratorCreateGlobal", KeyConstants.ERROR_UNIT_ADMIN_DUPLICATE, primaryKeyMap.toString());
        			errorCount++;
        		}
        }
		
		return errorCount > 0 ? false : true;
	}
	
}
