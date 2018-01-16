package edu.arizona.kra.irb.service;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by nataliac on 1/3/18.
 */
public interface ProtocolSecurityService {

    /**
     * Returns true if the user has the Protocol View Permission or 
     * @param userId
     * @return
     */
    public boolean hasViewPermission(String userId);


    /**
     * Returns the protocol numbers the given user is listed on as Personnel (all should have view access)
     * @param userId
     * @return Collection<String>
     */
    public Collection<String> findProtocolNumbersForPersonnelRole(String userId);

    /**
     * Returns the list of protocol numbers for the roles that user has and are qualified with a protocol number
     * @param userId
     * @return Collection<String>
     */
    public Collection<String> findProtocolNumbersForProtocolQualifiedRoles(String userId);


    /**
     * Returns the protocol numbers where user is listed on permissions page
     * @param userId
     * @return Collection<String>
     */
    public Collection<String> findProtocolNumbersByUserPermissions(String userId);

    /**
     * Finds the Protocol Numbers for the Protocols that have the lead units within the list of units that qualify the user's roles that have
     * view protocol permission
     * @param userId
     * @return Collection<String>
     */
    public Collection<String> findProtocolNumbersForUnitQualifiedRoles(String userId);


    /**
     * Finds the Lead Unit Numbers for the Protocols that have the lead units within the list of units that qualify the user's roles that have
     * view protocol permission
     * @param userId
     * @return Collection<String>
     */
    public HashSet<String> findUnitsForUnitQualifiedRoles(String userId);

    /**
     * Check if the user has any priviledged Protocol roles,
     * @param userId
     * @return
     */
    public boolean userHasPrivilegedProtocolRoles(String userId);



}
