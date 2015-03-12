package edu.arizona.kra.global.unit.delete;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kra.bo.UnitAdministrator;
import org.kuali.rice.kns.maintenance.KualiGlobalMaintainableImpl;
import org.kuali.rice.krad.bo.GlobalBusinessObjectDetail;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.maintenance.MaintenanceLock;

import edu.arizona.kra.global.DelimiterConstants;
import edu.arizona.kra.global.unit.UnitAdministratorPrimaryKey;

/**
 * This class is used by the framework to generate locks
 * for the UnitAdministrationDeleteGlobal class and its
 * children. These methods are from the Maintainable interface.
 */
@SuppressWarnings("deprecation")
public class UnitAdminDeleteGlobalMaintainableImpl extends KualiGlobalMaintainableImpl {
	private static final long serialVersionUID = -5289542451038756040L;
	
	
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
		UnitAdministratorDeleteGlobal unitAdministratorDeleteGlobal = (UnitAdministratorDeleteGlobal)getBusinessObject();
		
        for (GlobalBusinessObjectDetail adminDetail : unitAdministratorDeleteGlobal.getUnitAdministratorGlobalDetails()) {
        	UnitAdministratorGlobalDetail unitAdminGlobalDetail = (UnitAdministratorGlobalDetail)adminDetail;
        	
            MaintenanceLock maintenanceLock = new MaintenanceLock();

            StringBuffer lockKeyBuffer = new StringBuffer();
            lockKeyBuffer.append(UnitAdministrator.class.getName() + DelimiterConstants.AFTER_CLASS.getValue());
            lockKeyBuffer.append(UnitAdministratorPrimaryKey.PERSON_ID.getValue() + DelimiterConstants.AFTER_FIELDNAME);
            lockKeyBuffer.append(unitAdminGlobalDetail.getPersonId() + DelimiterConstants.AFTER_VALUE);
            lockKeyBuffer.append(UnitAdministratorPrimaryKey.UNIT_ADMIN_TYPE_CODE.getValue() + DelimiterConstants.AFTER_FIELDNAME);
            lockKeyBuffer.append(unitAdminGlobalDetail.getUnitAdministratorTypeCode() + DelimiterConstants.AFTER_VALUE);
            lockKeyBuffer.append(UnitAdministratorPrimaryKey.UNIT_NUMBER.getValue() + DelimiterConstants.AFTER_FIELDNAME);
            lockKeyBuffer.append(unitAdminGlobalDetail.getUnitNumber() + DelimiterConstants.AFTER_VALUE);

            maintenanceLock.setDocumentNumber(unitAdministratorDeleteGlobal.getDocumentNumber());
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
