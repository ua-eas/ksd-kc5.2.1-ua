package org.kuali.kra.kim.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kra.bo.UnitAdministrator;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.kra.kim.bo.KcKimAttributes;
import org.kuali.kra.service.UnitService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.role.RoleMembership;
import org.kuali.rice.kim.framework.role.RoleTypeService;
import org.kuali.rice.kns.kim.role.DerivedRoleTypeServiceBase;

/**
 * 
 * @author chrisdenne
 *
 */

@SuppressWarnings("deprecation")
public class UnitApproverDerivedRoleTypeServiceImpl extends DerivedRoleTypeServiceBase implements RoleTypeService {
	
    private UnitService unitService;
    private ConfigurationService kualiConfigurationService;
    private List<String> approverCodeList;    
    

    public boolean hasApplicationRole(String principalId, List<String> groupIds, String namespaceCode, String roleName, Map<String, String> qualification) {
        String unitNumber = qualification.get(KcKimAttributes.UNIT_NUMBER);

        if (StringUtils.isNotBlank(unitNumber)) {
            List<UnitAdministrator> unitAdministrators = unitService.retrieveUnitAdministratorsByUnitNumber(unitNumber);
            for (UnitAdministrator unitAdministrator : unitAdministrators) {
                if (unitAdministrator.getPersonId().equals(principalId) &&
                	getUnitApproverCodes().contains(unitAdministrator.getUnitAdministratorTypeCode())	) {
                    return true;
                }
            }
        }
        
        return false;
    }

    /**
     * 
     * @param unitService
     */
    public void setUnitService(UnitService unitService) {
        this.unitService = unitService;
    }

    public List<RoleMembership> getRoleMembersFromApplicationRole(String namespaceCode, String roleName, Map<String, String> qualification) {
        String unitNumber = qualification.get(KcKimAttributes.UNIT_NUMBER);
        List<RoleMembership> members = new ArrayList<RoleMembership>();
        if (StringUtils.isNotBlank(unitNumber)) {
            List<UnitAdministrator> unitAdministrators = unitService.retrieveUnitAdministratorsByUnitNumber(unitNumber);
            for ( UnitAdministrator unitAdministrator : unitAdministrators ) {
                if ( StringUtils.isNotBlank(unitAdministrator.getPersonId()) && getUnitApproverCodes().contains(unitAdministrator.getUnitAdministratorTypeCode()) ) {
                	RoleMembership.Builder roleMembershipBuilder = RoleMembership.Builder.create(null, null, unitAdministrator.getPersonId(), KimConstants.KimGroupMemberTypes.PRINCIPAL_MEMBER_TYPE, null);
                    members.add(roleMembershipBuilder.build());
                }
            }
        }
            
        return members;
    }
    
    protected List<String> getUnitApproverCodes() {
    	if (approverCodeList==null) {	    	
	    	// TODO will change below to parameter instead of property accessed via keyConsts.
	    	String listString = getKualiConfigurationService().getPropertyValueAsString("1,2,3");
	    	String[] arrayString = StringUtils.split(listString, ',');
	    	approverCodeList = Arrays.asList(arrayString);
    	}
    	return approverCodeList;
    }

	protected ConfigurationService getKualiConfigurationService() {
		if ( kualiConfigurationService == null ) {
			kualiConfigurationService = KraServiceLocator.getService(ConfigurationService.class);
		}
		return this.kualiConfigurationService;
	}
	
}
