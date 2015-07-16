package edu.arizona.kra.global.unit.create;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kuali.kra.bo.UnitAdministrator;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.KualiGlobalMaintainableImpl;
import org.kuali.rice.krad.bo.GlobalBusinessObjectDetail;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.maintenance.MaintenanceLock;

import edu.arizona.kra.global.DelimiterConstants;
import edu.arizona.kra.global.unit.UnitAdministratorPrimaryKey;

/**
 * This class is used by the framework to generate locks
 * for the UnitAdministrationCreateGlobal class and its
 * children. These methods are from the Maintainable interface.
 */
@SuppressWarnings("deprecation")
public class UnitAdminCreateGlobalMaintainableImpl extends KualiGlobalMaintainableImpl {
	private static final long serialVersionUID = -5289542451038756040L;
	
	private static final String KIM_PERSON_LOOKUPABLE_REFRESH_CALLER = "kimPersonLookupable";
	
	@SuppressWarnings( { "rawtypes", "unchecked" } )
	@Override
    public void refresh(String refreshCaller, Map fieldValues, MaintenanceDocument document) {
        super.refresh(refreshCaller, fieldValues, document);
        if (KIM_PERSON_LOOKUPABLE_REFRESH_CALLER.equals(refreshCaller)) {
            String principalId = (String) fieldValues.get(KimConstants.PrimaryKeyConstants.PRINCIPAL_ID);

    		fieldValues.put("unitAdminTypeAndPersonGlobalDetails.personId" , principalId);
    		document.getNewMaintainableObject().populateNewCollectionLines(fieldValues, document, (String) fieldValues.get("methodToCall"));
        }
    }
	
	/**
	 * Build a lock key of the form:
	 * "UnitAdministarator!!personId^^171293712973::unitAdministratorTypeCode^^16::unitNumber^^2001"
	 * 
	 * Do this for every UnitAdministrator in getBusinessObject().getUnitAdministratorDetails();
	 * 
	 * @return A list of locks representing each unique UnitAdministrator represented.
	 */
	@Override
	public List<MaintenanceLock> generateMaintenanceLocks() {
		
		List<MaintenanceLock> maintenanceLocks = new ArrayList<MaintenanceLock>();
		UnitAdministratorCreateGlobal unitAdministratorGlobal = (UnitAdministratorCreateGlobal)getBusinessObject();
		
        for (GlobalBusinessObjectDetail adminDetail : unitAdministratorGlobal.getUnitAdminTypeAndPersonGlobalDetails()) {
        	UnitAdminTypeAndPersonGlobalDetail unitAdminGlobalDetail = (UnitAdminTypeAndPersonGlobalDetail)adminDetail;

            MaintenanceLock maintenanceLock = new MaintenanceLock();
            StringBuffer lockKeyBuffer = new StringBuffer();
            
            // Build first half of key: "UnitAdministarator!!principalName^^foo::unitAdministratorTypeCode^^bar::"
            lockKeyBuffer.append(UnitAdministrator.class.getName() + DelimiterConstants.AFTER_CLASS.getValue());
            lockKeyBuffer.append(UnitAdministratorPrimaryKey.PERSON_ID.getValue() + DelimiterConstants.AFTER_FIELDNAME);
            lockKeyBuffer.append(unitAdminGlobalDetail.getPersonId() + DelimiterConstants.AFTER_VALUE);
            lockKeyBuffer.append(UnitAdministratorPrimaryKey.UNIT_ADMIN_TYPE_CODE.getValue() + DelimiterConstants.AFTER_FIELDNAME);
            lockKeyBuffer.append(unitAdminGlobalDetail.getUnitAdministratorTypeCode() + DelimiterConstants.AFTER_VALUE);
            
            // Build second half of key: "unitNumber::baz"
            for(GlobalBusinessObjectDetail unitDetail : unitAdministratorGlobal.getUnitGlobalDetails()) {
            	UnitGlobalDetail unitGlobalDetail = (UnitGlobalDetail)unitDetail;

            	// One principal can have multiple units being added/removed,
            	// we need a unique key for each tuple: {principalId, unitAdministratorCode, unitNumber}
            	// so keep first half of key separate.
            	String keyFirstHalf = lockKeyBuffer.toString();
            	lockKeyBuffer = new StringBuffer();

            	lockKeyBuffer.append(UnitAdministratorPrimaryKey.UNIT_NUMBER.getValue() + DelimiterConstants.AFTER_FIELDNAME);
                lockKeyBuffer.append(unitGlobalDetail.getUnitNumber() + DelimiterConstants.AFTER_VALUE);
                
                maintenanceLock.setDocumentNumber(unitAdministratorGlobal.getDocumentNumber());
                maintenanceLock.setLockingRepresentation(keyFirstHalf + lockKeyBuffer.toString());

            }

            maintenanceLock.setDocumentNumber(unitAdministratorGlobal.getDocumentNumber());
            maintenanceLock.setLockingRepresentation(lockKeyBuffer.toString());
            maintenanceLocks.add(maintenanceLock);
        }

		return maintenanceLocks;
	}

	@Override
	public Class<? extends PersistableBusinessObject> getPrimaryEditedBusinessObjectClass() {
		return UnitAdministrator.class;
	}

}
