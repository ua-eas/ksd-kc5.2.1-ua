package edu.arizona.kra.global.unit.delete;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kra.bo.UnitAdministrator;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.rice.krad.bo.GlobalBusinessObject;
import org.kuali.rice.krad.bo.GlobalBusinessObjectDetail;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * This class is the code backend for the UnitAdministratorDeleteGlobalMaintenanceDocument.
 * 
 * Essentially, this is fancy encapsulation for UnitAdinistrator PK's of:
 * 1. personId
 * 2. unitAdministratorTypeCode
 * 3. unitNumber
 */
public class UnitAdministratorDeleteGlobal extends PersistableBusinessObjectBase implements GlobalBusinessObject {
	private static final long serialVersionUID = 3581436099011466129L;

	private String documentNumber;
	private List<UnitAdministratorGlobalDetail> unitAdministratorGlobalDetails;
	
	transient private BusinessObjectService businessObjectService;
	


	public UnitAdministratorDeleteGlobal() {
		// Empty for OJB
	}



	@Override
	public List<PersistableBusinessObject> generateGlobalChangesToPersist() {
		// We don't want to change/create, only deactivate
		return null;
	}


	/**
	 * We are cheating here; since the framework only sets an "active" flag to false,
	 * we will perform the actual UnitAdministrator deletion in this method.
	 * 
	 * The UNIT_ADMINISTRATOR table is one of the few tables in a kuali app which
	 * does not have an active flag -- KC workflow is to delete these records.
	 * 
	 * To keep the framework happy, we'll return null, which will be a no-op
	 * further down the execution path.
	 * 
	 * @return Always returns null. 
	 */
	@Override
	public List<PersistableBusinessObject> generateDeactivationsToPersist() {
		
		// Since we are going to delete, we want to verify all records exist first
		List<UnitAdministrator> unitAdministrators = new ArrayList<UnitAdministrator>();;
        for (Map<String, Object> primaryKeyMap : getAllUnitAdministratorPrimaryKeys()) {
        		// Check to ensure this record doesn't exist
        		UnitAdministrator unitAdministrator = getUnitAdministratorByPrimaryKey(primaryKeyMap);        		
        		if(unitAdministrator == null) {
        			GlobalVariables.getMessageMap().putError("unitAdministratorDeleteGlobal", "Record does not exist for UnitAdministrator: " + primaryKeyMap);
        			return null;
        		}
        		unitAdministrators.add(unitAdministrator);
        }
        
        // If we've made it here, all records exist, and its safe to do deletions
        getBusinessObjectService().delete(unitAdministrators);

        return null;
	}


	protected List<Map<String, Object>> getAllUnitAdministratorPrimaryKeys() {
		List<Map<String, Object>> primaryKeysList = new ArrayList<Map<String, Object>>();
		for(GlobalBusinessObjectDetail adminDetail : getUnitAdministratorGlobalDetails()) {
			UnitAdministratorGlobalDetail unitAdminDetail = (UnitAdministratorGlobalDetail)adminDetail;
			primaryKeysList.add(unitAdminDetail.getPrimaryKeyToValueMap());
		}
		return primaryKeysList;
	}
	
	
	public List<? extends GlobalBusinessObjectDetail> getUnitAdministratorGlobalDetails() {
		if(unitAdministratorGlobalDetails == null) {
			unitAdministratorGlobalDetails = new ArrayList<UnitAdministratorGlobalDetail>();
		}
		return unitAdministratorGlobalDetails;
	}


	public void setUnitAdministratorGlobalDetails(List<UnitAdministratorGlobalDetail> unitAdministratorGlobalDetails) {
		this.unitAdministratorGlobalDetails = unitAdministratorGlobalDetails;
	}
	
	
	public BusinessObjectService getBusinessObjectService() {
		if(businessObjectService == null) {
			businessObjectService = KraServiceLocator.getService(BusinessObjectService.class); 
		}
		return businessObjectService;
	}


	protected UnitAdministrator getUnitAdministratorByPrimaryKey(Map<String, Object> primaryKeyMap) {
		return getBusinessObjectService().findByPrimaryKey(UnitAdministrator.class, primaryKeyMap);
	}


	@Override
	public String getDocumentNumber() {
		return documentNumber;
	}


	@Override
	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}

	
	@Override
	public boolean isPersistable() {

		if(StringUtils.isBlank(getDocumentNumber())) {
			return false;
		}

        for (GlobalBusinessObjectDetail unitAdminDetail : getUnitAdministratorGlobalDetails()) {
            if (!getPersistenceStructureService().hasPrimaryKeyFieldValues(unitAdminDetail)) {
                return false;
            }
        }

        return true;
	}
	

	@Override
	public List<? extends GlobalBusinessObjectDetail> getAllDetailObjects() {
		return getUnitAdministratorGlobalDetails();
	}
	
	
	@Override
	public Long getVersionNumber() {
		// Doing this since framework was not setting this in KC, and this is a non-nullable field
		if(super.getVersionNumber() == null) {
			super.setVersionNumber(new Long(1L));
		}
		return super.getVersionNumber();
	}

	@SuppressWarnings( { "unchecked", "rawtypes" } )
	@Override
	public List buildListOfDeletionAwareLists() {
		List managedLists = super.buildListOfDeletionAwareLists();
		managedLists.add( this.unitAdministratorGlobalDetails );
		return managedLists;
	}
}
