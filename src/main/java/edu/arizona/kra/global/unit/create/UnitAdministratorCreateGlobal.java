package edu.arizona.kra.global.unit.create;

import java.util.ArrayList;
import java.util.HashMap;
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

import edu.arizona.kra.global.unit.UnitAdministratorPrimaryKey;

/**
 * This class is the code backend for the UnitAdministratorCreateGlobalMaintenanceDocument.
 * 
 * Essentially, this is fancy encapsulation for UnitAdinistrator PK's of:
 * 1. personId
 * 2. unitAdministratorTypeCode
 * 3. unitNumber
 */
public class UnitAdministratorCreateGlobal  extends PersistableBusinessObjectBase implements GlobalBusinessObject {
	private static final long serialVersionUID = -2693025407153108598L;

	// Our instance variables
	private String documentNumber;
	private List<UnitAdminTypeAndPersonGlobalDetail> unitAdminTypeAndPersonGlobalDetails;
	private List<UnitGlobalDetail> unitGlobalDetails;

	

	public UnitAdministratorCreateGlobal() {
		// Empty for OJB
	}


	/**
	 * This method produces a list of UnitAdministrator objects to
	 * persist to the datastore.
	 * 
	 * This functionality has the following relationship:
	 * - One personId
	 * - One unitAdminType
	 * - One or more unitNumbers
	 * 
	 * For each unique (personId, unitAdminType, unitNumber), there will be one unique record
	 * in the UnitAdminstrator table.
	 * 
	 * Additionally, this method will set an error and return null if the details match an existing
	 * UnitAministrator record.
	 * 
	 * @return A list of UnitAdministrator that will be persisted to the datastore. 
	 */
	@Override
	public List<PersistableBusinessObject> generateGlobalChangesToPersist() {

        List<PersistableBusinessObject> persistables = new ArrayList<PersistableBusinessObject>();;
        for (Map<String, Object> primaryKeyMap : getAllUnitAdministratorPrimaryKeys()) {

        		// Check to ensure this record doesn't exist
        		UnitAdministrator unitAdministrator = getUnitAdministratorByPrimaryKey(primaryKeyMap);        		
        		if(unitAdministrator != null) {
        			GlobalVariables.getMessageMap().putError("unitAdministratorCreateGlobal", "Record already exists for UnitAdministrator: " + primaryKeyMap);
        			return null;
        		}

        		unitAdministrator = createUnitAdminstrator(primaryKeyMap);
        		persistables.add(unitAdministrator);
        }

        return persistables;
	}


	// Only need to set three fields, the framework will set the remainder.
	private UnitAdministrator createUnitAdminstrator(Map<String, Object> primaryKeyMap) {
		UnitAdministrator unitAdministrator = new UnitAdministrator();
		unitAdministrator.setPersonId((String)primaryKeyMap.get(UnitAdministratorPrimaryKey.PERSON_ID.getValue()));
		unitAdministrator.setUnitAdministratorTypeCode((String)primaryKeyMap.get(UnitAdministratorPrimaryKey.UNIT_ADMIN_TYPE_CODE.getValue()));
		unitAdministrator.setUnitNumber((String)primaryKeyMap.get(UnitAdministratorPrimaryKey.UNIT_NUMBER.getValue()));
		return unitAdministrator;
	}


	/**
	 * This class should never delete/deactivate UnitAdminstrator.
	 * 
	 * @return Always returns null.
	 */
	@Override
	public List<PersistableBusinessObject> generateDeactivationsToPersist() {
		return null;
	}


	/**
	 * 	 UnitAdministrator primary keys are:
	 * - One personId
	 * - One unitAdminType
	 * - One or more unitNumbers
	 * 
	 * The personId and unitAdminType are encapsualted in PersonAndUnitAdminGlobalDetail.
	 * 
	 * The List<unitNumber> are encapsualted in List<UnitGlobalDetail>, by itself, so that more
	 * than one Unit can be chosen via the UI.
	 * 
	 * This means we will need to need to have two loops to account for a UnitAdmin that is against
	 * one Person, one unitAdminType, and multiple Units.
	 * 
	 */
	protected List<Map<String, Object>> getAllUnitAdministratorPrimaryKeys() {
		
        List<Map<String, Object>> primaryKeysList = new ArrayList<Map<String, Object>>();

        for (GlobalBusinessObjectDetail adminDetail : getUnitAdminTypeAndPersonGlobalDetails()) {
        	UnitAdminTypeAndPersonGlobalDetail unitAdminTypeAndPersonGlobalDetail = (UnitAdminTypeAndPersonGlobalDetail)adminDetail;

        	for(GlobalBusinessObjectDetail unitDetail : getUnitGlobalDetails()) {
        		UnitGlobalDetail unitGlobalDetail = (UnitGlobalDetail)unitDetail;
        		
        		Map<String, Object> primaryKeyMap = new HashMap<String, Object>();
        		
        		Map<String, Object> unitAdminPrimaryKeys = unitAdminTypeAndPersonGlobalDetail.getPrimaryKeyToValueMap();
        		primaryKeyMap.putAll(unitAdminPrimaryKeys);

        		Map<String, Object> unitPrimaryKeys = unitGlobalDetail.getPrimaryKeyToValueMap();
        		primaryKeyMap.putAll(unitPrimaryKeys);
        		
        		primaryKeysList.add(primaryKeyMap);

        	}//for

        }//for

        return primaryKeysList;
	}
	
	
	protected UnitAdministrator getUnitAdministratorByPrimaryKey(Map<String, Object> primaryKeyMap) {
		return getBusinessObjectService().findByPrimaryKey(UnitAdministrator.class, primaryKeyMap);
	}


	@Override
	public boolean isPersistable() {

		if(StringUtils.isBlank(getDocumentNumber())) {
			return false;
		}

        for (GlobalBusinessObjectDetail unitAdminDetail : getUnitAdminTypeAndPersonGlobalDetails()) {
            if (!getPersistenceStructureService().hasPrimaryKeyFieldValues(unitAdminDetail)) {
                return false;
            }
        }

        for(UnitGlobalDetail unitGlobalDetail : getUnitGlobalDetails()) {
        	if (!getPersistenceStructureService().hasPrimaryKeyFieldValues(unitGlobalDetail)) {
                return false;
            }
        }

        return true;
	}


	@Override
	public List<? extends GlobalBusinessObjectDetail> getAllDetailObjects() {
		List<GlobalBusinessObjectDetail> allDetailObjects = new ArrayList<GlobalBusinessObjectDetail>();
		allDetailObjects.addAll(getUnitAdminTypeAndPersonGlobalDetails());
		allDetailObjects.addAll(getUnitGlobalDetails());
		return allDetailObjects;
	}


	public List<UnitGlobalDetail> getUnitGlobalDetails() {
		if(unitGlobalDetails == null) {
			// Do this here to prevent OJB woes
			unitGlobalDetails = new ArrayList<UnitGlobalDetail>();
		}
		return unitGlobalDetails;
	}


	public void setUnitGlobalDetails(List<UnitGlobalDetail> unitGlobalDetails) {
		this.unitGlobalDetails = unitGlobalDetails;
	}


	public List<? extends GlobalBusinessObjectDetail> getUnitAdminTypeAndPersonGlobalDetails() {
		if(unitAdminTypeAndPersonGlobalDetails == null) {
			// Do this here to prevent OJB woes
			unitAdminTypeAndPersonGlobalDetails = new ArrayList<UnitAdminTypeAndPersonGlobalDetail>();
		}
		return unitAdminTypeAndPersonGlobalDetails;
	}


	public void setUnitAdminTypeAndPersonGlobalDetails(List<UnitAdminTypeAndPersonGlobalDetail> unitAdminTypeAndPersonGlobalDetails) {
		this.unitAdminTypeAndPersonGlobalDetails = unitAdminTypeAndPersonGlobalDetails;
	}
	
	public BusinessObjectService getBusinessObjectService() {
		return KraServiceLocator.getService(BusinessObjectService.class);
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
	public Long getVersionNumber() {
		// Doing this since framework was not setting this in KC, and this is a non-nullable field
		if(super.getVersionNumber() == null) {
			super.setVersionNumber(new Long(1L));
		}
		return super.getVersionNumber();
	}

}
