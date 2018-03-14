package edu.arizona.kra.irb.dao;

import org.kuali.kra.irb.ProtocolDao;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by nataliac on 1/8/18.
 */
public interface UaProtocolDao extends ProtocolDao {


    /**
     * Returns the protocol numbers the given user is listed on as Personnel (all should have view access)
     * @param userId
     * @return Collection<String>
     */
    public Collection<String> getProtocolNumbersForPersonnelRole(String userId);


    /**
     * Returns true if the users is a member of any of the given roles but without further qualifiers for that membership
     * @param userId
     * @param roleIds - list of roles to look for
     *
     * @return boolean
     */
    public boolean hasUnqualifiedRoles(String userId, List<String> roleIds);

    /**
     * Returns true if the users has 'View Protocol' for root unit - University of Arizona - descending hierarchy.
     * This means he can practically see any protocol because all the lead units are included.
     * @param userId
     * @param roleIds - list of roles to look for
     *
     * @return boolean
     */
    public boolean hasViewPermissionOnAllUnitHierarchy(String userId, List<String> roleIds);

    /**
     * Returns the qualifiers for the roles that have view protocol permission that the given user is a member of
     * @param userId
     * @param roleIds - list of roles to look for
     * @param qualifierName
     *
     * @return Collection<String>
     */
    public Map<String,String> getQualifiersForMemberAndAttributeName(List<String> roleIds, String userId, String qualifierName);

    /**
     * Returns the protocol numbers that have the leading unit in the leadUnits list.
     * @param leadUnits
     *
     * @return Collection<String>
     */
    public Collection<String> getProtocolNumbersWithLeadUnits(Collection<String> leadUnits);

}
