package edu.arizona.kra.irb.service.impl;

import edu.arizona.kra.irb.dao.UaProtocolDao;
import edu.arizona.kra.irb.service.ProtocolSecurityService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kra.bo.Unit;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.kra.infrastructure.PermissionConstants;
import org.kuali.kra.irb.ProtocolFinderDao;
import org.kuali.kra.protocol.ProtocolBase;
import org.kuali.kra.service.UnitService;
import org.kuali.rice.kim.api.permission.PermissionService;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static edu.arizona.kra.irb.ProtocolConstants.*;


/**
 * Created by nataliac on 1/3/18.
 */
public class ProtocolSecurityServiceImpl implements ProtocolSecurityService {
    private static final Logger LOG = LoggerFactory.getLogger(ProtocolCustomLookupableHelperServiceImpl.class);

    UaProtocolDao protocolDao;
    UnitService unitService;


    protected static final String[] PRIVILEGED_IRB_ROLES = new String[] { IRB_ADMINISTRATOR_ROLE_NAME };

    private List<String> privilegedProtocolRolesIds = null;
    private List<String> roleIdsWithProtocolViewPermission = null;



    public boolean hasViewPermission(String userId){
        PermissionService permService = KimApiServiceLocator.getPermissionService();
        return permService.hasPermission(userId, Constants.MODULE_NAMESPACE_PROTOCOL, PermissionConstants.VIEW_PROTOCOL);
    }

    public Collection<String> findProtocolNumbersForPersonnelRole(String userId){
        LOG.debug("findProtocolNumbersForPersonnelRole userId="+userId);
        Collection<String> results = new ArrayList<>();
        try {
            results = getProtocolDao().getProtocolNumbersForPersonnelRole(userId);
        } catch (Exception e){
            LOG.error("Exception at findProtocolNumbersForPersonnelRole", e);
        }
        LOG.debug("findProtocolNumbersForPersonnelRole results size="+results.size());
        return results;
    }

    private List<String> getPrivilegedProtocolRolesIds(){

        if ( privilegedProtocolRolesIds == null){
            privilegedProtocolRolesIds = new ArrayList<String>();
            RoleService roleService = KimApiServiceLocator.getRoleService();

            for(String roleDesc : PRIVILEGED_IRB_ROLES) {
                String roleId = roleService.getRoleIdByNamespaceCodeAndName(IRB_ROLE_NAMESPACE, roleDesc);
                if(!StringUtils.isBlank(roleId)) {
                    privilegedProtocolRolesIds.add(roleId);
                }
            }
        }
        return privilegedProtocolRolesIds;
    }

    private List<String> getRoleIdsWithProtocolViewPermission(){
        if (roleIdsWithProtocolViewPermission == null ){
            roleIdsWithProtocolViewPermission = new ArrayList<String>();
            PermissionService permissionService = KimApiServiceLocator.getPermissionService();

            roleIdsWithProtocolViewPermission = permissionService.getRoleIdsForPermission(Constants.MODULE_NAMESPACE_PROTOCOL, PermissionConstants.VIEW_PROTOCOL);
        }
        return roleIdsWithProtocolViewPermission;
    }


    public boolean userHasPrivilegedProtocolRoles(String userId){
        RoleService roleService = KimApiServiceLocator.getRoleService();

        final Map<String,String> NO_QUALIFIERS = new HashMap<String, String>();
        return roleService.principalHasRole(userId, getPrivilegedProtocolRolesIds(), NO_QUALIFIERS, false);
    }


    public boolean userHasUnrestrictedViewPermission(String userId){
        boolean hasUnrestrictedViewPermission = getProtocolDao().hasUnqualifiedRoles(userId, getRoleIdsWithProtocolViewPermission());
        if (!hasUnrestrictedViewPermission){
            hasUnrestrictedViewPermission =  getProtocolDao().hasViewPermissionOnAllUnitHierarchy(userId, getRoleIdsWithProtocolViewPermission());
        }
        return hasUnrestrictedViewPermission;
    };


    public Collection<String> findProtocolNumbersForProtocolQualifiedRoles(String userId) {
        Map<String,String> protocolRoleQualifiers = getProtocolDao().getQualifiersForMemberAndAttributeName(getRoleIdsWithProtocolViewPermission(), userId, ROLE_QUALIFIER_PROTOCOL);
        if ( protocolRoleQualifiers!=null && !protocolRoleQualifiers.isEmpty()){
            return protocolRoleQualifiers.values();
        }
        return new ArrayList<String>(0);
    }




    public Collection<String> findProtocolNumbersByUserPermissions(String userId) {
        Set<String> protocolNumbers = new HashSet<String>();
        Map<String,String> protocolRoleQualifiers = getProtocolDao().getQualifiersForMemberAndAttributeName(getRoleIdsWithProtocolViewPermission(), userId, ROLE_QUALIFIER_PROTOCOL);
        if ( protocolRoleQualifiers!=null && !protocolRoleQualifiers.isEmpty()){
            ProtocolFinderDao protocolFinder = KraServiceLocator.getService(ProtocolFinderDao.class);
            // Find the base protocols and their Amendments, Renewals, and FYI etc.
            for ( String baseProtocolNumber: protocolRoleQualifiers.values()) {
                if ( !protocolNumbers.contains(baseProtocolNumber)) {
                    for (ProtocolBase protocol : protocolFinder.findProtocols(baseProtocolNumber)) {
                        protocolNumbers.add(protocol.getProtocolNumber());
                    }
                }
            }
        }
        LOG.debug("findProtocolNumbersByUserPermissions for userId="+userId+ " protocolNumbers = {}",protocolNumbers.toString());
        return protocolNumbers;
    }


    public Collection<String> findProtocolNumbersForUnitQualifiedRoles(String userId) {
        Collection<String> protocolNumbers = new ArrayList<>();

        Collection<String> validUnits = findUnitsForUnitQualifiedRoles(userId);
        if (CollectionUtils.isNotEmpty(validUnits)) {
            protocolNumbers = getProtocolDao().getProtocolNumbersWithLeadUnits(validUnits);
        }
        return protocolNumbers;
    }


    public HashSet<String> findUnitsForUnitQualifiedRoles(String userId){
        //this is a Set to avoid duplicate unit listings
        HashSet<String> validUnits = new HashSet<String>();

        Map<String,String> unitQualifiers = getProtocolDao().getQualifiersForMemberAndAttributeName(getRoleIdsWithProtocolViewPermission(), userId, ROLE_QUALIFIER_UNIT_NUMBER);
        Map<String,String> subunits = getProtocolDao().getQualifiersForMemberAndAttributeName(getRoleIdsWithProtocolViewPermission(), userId, ROLE_QUALIFIER_SUBUNITS);
        for(Map.Entry<String,String> unitNumber : unitQualifiers.entrySet()) {
            validUnits.add(unitNumber.getValue());
            //subunits role qualifier indicates if it descends hierarchy, in which case we need to add all subunits for the crt unit
            String descendsHierarchy = subunits.get(unitNumber.getKey());
            if( ROLE_QUALIFIER_DESCENDS_HIERARCHY.equalsIgnoreCase(descendsHierarchy) ) {
                for(Unit subUnit : getUnitService().getAllSubUnits(unitNumber.getValue())) {
                    validUnits.add(subUnit.getUnitNumber());
                }
            }
        }
        return validUnits;
    }



    public UnitService getUnitService(){
        return this.unitService != null?this.unitService: KraServiceLocator.getService(UnitService.class);
    }


    public UaProtocolDao getProtocolDao() {
        return protocolDao;
    }

    public void setProtocolDao(UaProtocolDao protocolDao) {
        this.protocolDao = protocolDao;
    }

}
