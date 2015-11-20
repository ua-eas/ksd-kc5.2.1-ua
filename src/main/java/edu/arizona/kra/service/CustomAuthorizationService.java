package edu.arizona.kra.service;

import org.kuali.kra.service.KraAuthorizationService;

public interface CustomAuthorizationService extends KraAuthorizationService {

    /**
     * Does the user have the given permission for the given Document specified by the documentNumber?
     * @param username 
     * @param String proposalNumber
     * @param String permissionName the name of the Permission
     * @param String unitNumber 
     * @return true if the user has permission; otherwise false
     */
    public boolean hasPermissionOnPropDev(String userId, String proposalNumber, String unitNumber, String permissionName);
}
