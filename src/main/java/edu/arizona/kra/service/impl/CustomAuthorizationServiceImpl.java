package edu.arizona.kra.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kra.common.permissions.Permissionable;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.infrastructure.PermissionAttributes;
import org.kuali.kra.service.impl.KraAuthorizationServiceImpl;

import edu.arizona.kra.service.CustomAuthorizationService;

public class CustomAuthorizationServiceImpl extends KraAuthorizationServiceImpl implements CustomAuthorizationService {
    
    @Override
    public boolean hasPermissionOnPropDev(String userId, String proposalNumber, String unitNumber, String permissionName){
//        boolean userHasPermission = false;
//        Map<String, String> qualifiedRoleAttributes = new HashMap<String, String>();
//        qualifiedRoleAttributes.put(Permissionable.PROPOSAL_KEY, proposalNumber);
//        
//        Map<String, String> permissionAttributes = PermissionAttributes.getAttributes(permissionName);
//        
//        
//        if(StringUtils.isNotEmpty(proposalNumber)) {
//            userHasPermission = permissionService.isAuthorized(userId, Constants.MODULE_NAMESPACE_PROPOSAL_DEVELOPMENT, permissionName, qualifiedRoleAttributes); 
//        }
//        if (!userHasPermission && StringUtils.isNotEmpty(unitNumber)) {
//            userHasPermission = unitAuthorizationService.hasPermission(userId, unitNumber, Constants.MODULE_NAMESPACE_PROPOSAL_DEVELOPMENT, permissionName);
//        }
//        
//        
//        if(StringUtils.isNotEmpty(permissionable.getDocumentNumberForPermission())) {
//            userHasPermission = permissionService.isAuthorized(userId, Constants.MODULE_NAMESPACE_PROPOSAL_DEVELOPMENT, permissionName, qualifiedRoleAttributes); 
//        }
//        if (!userHasPermission && StringUtils.isNotEmpty(unitNumber)) {
//            userHasPermission = unitAuthorizationService.hasPermission(userId, unitNumber, Constants.MODULE_NAMESPACE_PROPOSAL_DEVELOPMENT, permissionName);
//        }
//        return userHasPermission;
        return true;
    }

}
