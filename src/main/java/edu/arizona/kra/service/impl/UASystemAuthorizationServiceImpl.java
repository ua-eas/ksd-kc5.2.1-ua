package edu.arizona.kra.service.impl;


import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.kra.service.impl.SystemAuthorizationServiceImpl;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.role.RoleService;


/**
 * Porting fix from IU for avoiding retrieving derived roles for dealing with role accumulation
 * (See Foundation Jira: https://jira.kuali.org/browse/KRACOEUS-6458 )
 * Created by nataliac on 10/30/17.
 */
public class UASystemAuthorizationServiceImpl extends SystemAuthorizationServiceImpl{
    private static final Logger LOG = Logger.getLogger(UASystemAuthorizationServiceImpl.class);

    private RoleService roleManagementService;

    /** UITSRA-1840:
     * Derived roles are being returned and they shouldn't be. There should be a fix in 5.1.1,
     * at which point this class can be removed (and removed from CustomCoreSpringBeans.xml)
     */

    @Override
    public List<Role> getRoles(String namespaceCode) {
        LOG.debug("ENTER: UASystemAuthorizationServiceImpl: getRoles for namespaceCode="+namespaceCode);
        List<Role> unfilteredRoles = super.getRoles(namespaceCode);
        List<Role> filteredRoles = new ArrayList<Role>();
        for(Role role : unfilteredRoles) {
            if(!roleManagementService.isDerivedRole(role.getId())) {
                filteredRoles.add(role);
            }
        }
        LOG.debug("EXIT: UASystemAuthorizationServiceImpl: getRoles for namespaceCode="+namespaceCode);
        return filteredRoles;
    }

    public void setRoleManagementService(RoleService roleManagementService) {
        super.setRoleManagementService(roleManagementService);
        this.roleManagementService = roleManagementService;
    }

}
