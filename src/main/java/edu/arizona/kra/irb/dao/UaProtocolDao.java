package edu.arizona.kra.irb.dao;

import org.apache.ojb.broker.accesslayer.LookupException;
import org.kuali.kra.irb.ProtocolDao;

import java.sql.SQLException;
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
    public Collection<String> getProtocolNumbersForPersonnelRole(String userId) throws SQLException, LookupException;


    /**
     * Returns the qualifiers for the roles that have view protocol permission that the given user is a member of
     * @param userId
     * @return Collection<String>
     */
    public Map<String,String> getQualifiersForMemberAndAttributeName(List<String> roleIds, String userId, String qualifierName);

    /**
     * Returns the protocol numbers that have the leading unit in the leadUnits list.
     * @param leadUnits
     * @return Collection<String>
     */
    public Collection<String> getProtocolNumbersWithLeadUnits(Collection<String> leadUnits);

}
