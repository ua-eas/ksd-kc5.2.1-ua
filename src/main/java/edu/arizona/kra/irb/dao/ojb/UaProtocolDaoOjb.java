package edu.arizona.kra.irb.dao.ojb;

import edu.arizona.kra.irb.dao.UaProtocolDao;
import edu.arizona.kra.irb.service.impl.ProtocolCustomLookupableHelperServiceImpl;
import edu.arizona.kra.util.DBConnection;
import org.apache.ojb.broker.accesslayer.LookupException;
import org.kuali.kra.irb.Protocol;
import org.kuali.kra.irb.actions.submit.ProtocolSubmission;
import org.kuali.kra.irb.personnel.ProtocolPerson;
import org.kuali.kra.irb.personnel.ProtocolUnit;
import org.kuali.kra.irb.protocol.funding.ProtocolFundingSource;
import org.kuali.kra.irb.protocol.location.ProtocolLocation;
import org.kuali.kra.irb.protocol.research.ProtocolResearchArea;
import org.kuali.kra.protocol.CriteriaFieldHelper;
import org.kuali.kra.protocol.ProtocolBase;
import org.kuali.kra.protocol.ProtocolDaoOjbBase;
import org.kuali.kra.protocol.ProtocolLookupConstants;
import org.kuali.kra.protocol.actions.ProtocolActionBase;
import org.kuali.kra.protocol.actions.submit.ProtocolSubmissionBase;
import org.kuali.kra.protocol.personnel.ProtocolPersonBase;
import org.kuali.kra.protocol.personnel.ProtocolUnitBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static edu.arizona.kra.irb.ProtocolConstants.*;


/**
 *
 * This class is the UofA implementation for ProtocolDao interface.
 * Created by nataliac on 12/12/17.
 */
public class UaProtocolDaoOjb extends ProtocolDaoOjbBase<Protocol> implements UaProtocolDao {
    private static final Logger LOG = LoggerFactory.getLogger(ProtocolCustomLookupableHelperServiceImpl.class);

    /**
     * The APPROVED_SUBMISSION_STATUS_CODE contains the status code of approved protocol submissions (i.e. 203).
     */
    private static final Collection<String> APPROVED_SUBMISSION_STATUS_CODES = Arrays.asList(new String[] {PROTOCOL_STATUS_EXEMPT});

    /**
     * The ACTIVE_PROTOCOL_STATUS_CODES contains the various active status codes for a protocol.
     */
    private static final Collection<String> ACTIVE_PROTOCOL_STATUS_CODES = Arrays.asList(new String[] {PROTOCOL_STATUS_ACTIVE_OPEN_TO_ENROLLMENT, PROTOCOL_STATUS_ACTIVE_CLOSED_TO_ENROLLMENT, PROTOCOL_STATUS_ACTIVE_DATA_ANALYSIS_ONLY});

    /**
     * The REVISION_REQUESTED_PROTOCOL_ACTION_TYPE_CODES contains the protocol action codes for the protocol revision requests.
     */
    private static final Collection<String> REVISION_REQUESTED_PROTOCOL_ACTION_TYPE_CODES = Arrays.asList(new String[] {PROTOCOL_ACTION_REVISIONS_REQUIRED, PROTOCOL_ACTION_MINOR_REVISIONS_REQUIRED});

    /**
     * The REVISION_REQUESTED_PROTOCOL_STATUS_CODES contains the various status codes for protocol revision requests.
     */
    private static final Collection<String> REVISION_REQUESTED_PROTOCOL_STATUS_CODES = Arrays.asList(new String[] {PROTOCOL_STATUS_MINOR_REVISIONS_REQUIRED, PROTOCOL_STATUS_REVISIONS_REQUIRED});


    /**
     * The PENDING_AMENDMENT_RENEWALS_STATUS_CODES contains the various status codes for protocol amendment/renewals.
     */
    private static final Collection<String> PENDING_AMENDMENT_RENEWALS_STATUS_CODES = Arrays.asList(new String[]{PROTOCOL_STATUS_PENDING_IN_PROGRESS, PROTOCOL_STATUS_SUBMITTED, PROTOCOL_STATUS_MINOR_REVISIONS_REQUIRED,
            PROTOCOL_STATUS_DEFERRED, PROTOCOL_STATUS_REVISIONS_REQUIRED, PROTOCOL_STATUS_AMENDMENT_PROGRESS, PROTOCOL_STATUS_RENEWAL_PROGRESS});

    @Override
    protected Collection<String> getApprovedSubmissionStatusCodesHook() {
        return APPROVED_SUBMISSION_STATUS_CODES;
    }

    @Override
    protected Collection<String> getActiveProtocolStatusCodesHook() {
        return ACTIVE_PROTOCOL_STATUS_CODES;
    }

    @Override
    protected Collection<String> getRevisionRequestedProtocolActionTypeCodesHook() {
        return REVISION_REQUESTED_PROTOCOL_ACTION_TYPE_CODES;
    }

    @Override
    protected Collection<String> getRevisionRequestedProtocolStatusCodesHook() {
        return REVISION_REQUESTED_PROTOCOL_STATUS_CODES;
    }

    @Override
    protected Collection<String> getPendingAmendmentRenewalsProtocolStatusCodesHook() {
        return PENDING_AMENDMENT_RENEWALS_STATUS_CODES;
    }

    @Override
    protected Class<? extends ProtocolActionBase> getProtocolActionBOClassHoook() {
        return org.kuali.kra.irb.actions.ProtocolAction.class;
    }

    @Override
    protected void initRoleListsHook(List<String> investigatorRoles, List<String> personRoles) {
        investigatorRoles.add(ROLE_PRINCIPAL_INVESTIGATOR);
        investigatorRoles.add(ROLE_CO_INVESTIGATOR);

        personRoles.add(ROLE_STUDY_PERSONNEL);
        personRoles.add(ROLE_CORRESPONDENT_ADMINISTRATOR);
        personRoles.add(ROLE_CORRESPONDENT_CRC);
    }



    @Override
    protected Class<? extends ProtocolBase> getProtocolBOClassHook() {
        return Protocol.class;
    }

    @Override
    protected Class<? extends ProtocolPersonBase> getProtocolPersonBOClassHook() {
        return ProtocolPerson.class;
    }

    @Override
    protected Class<? extends ProtocolUnitBase> getProtocolUnitBOClassHook() {
        return ProtocolUnit.class;
    }

    @Override
    protected Class<? extends ProtocolSubmissionBase> getProtocolSubmissionBOClassHook() {
        return ProtocolSubmission.class;
    }

    @Override
    protected List<CriteriaFieldHelper> getCriteriaFields() {
        List<CriteriaFieldHelper> criteriaFields = new ArrayList<CriteriaFieldHelper>();

        criteriaFields.add(new CriteriaFieldHelper(ProtocolLookupConstants.Property.KEY_PERSON,
                ProtocolLookupConstants.Property.PERSON_NAME,
                ProtocolPerson.class));
        criteriaFields.add(new CriteriaFieldHelper(ProtocolLookupConstants.Property.INVESTIGATOR,
                ProtocolLookupConstants.Property.PERSON_NAME,
                ProtocolPerson.class));
        criteriaFields.add(new CriteriaFieldHelper(ProtocolLookupConstants.Property.FUNDING_SOURCE,
                ProtocolLookupConstants.Property.FUNDING_SOURCE_NUMBER,
                ProtocolFundingSource.class));
        criteriaFields.add(new CriteriaFieldHelper(ProtocolLookupConstants.Property.PERFORMING_ORGANIZATION_ID,
                ProtocolLookupConstants.Property.ORGANIZATION_ID,
                ProtocolLocation.class));
        criteriaFields.add(new CriteriaFieldHelper(ProtocolLookupConstants.Property.RESEARCH_AREA_CODE,
                ProtocolLookupConstants.Property.RESEARCH_AREA_CODE,
                ProtocolResearchArea.class));
        return criteriaFields;
    }


    public Collection<String> getProtocolNumbersForPersonnelRole(String userId) {
        List<String> protocolNumbers = new ArrayList<>();

        Object[] params = new Object[] { userId };
        try (DBConnection dbc = new DBConnection(this.getPersistenceBroker(true)) ){
            ResultSet rs = dbc.executeQuery(PROTOCOL_NUMBERS_FOR_PERSONNEL_QUERY, params);
            while (rs.next()){
                protocolNumbers.add( rs.getString(1) );
            }
        } catch (SQLException sqle) {
            LOG.error("SQLException: " + sqle.getMessage(), sqle);
        } catch (LookupException le) {
            LOG.error("LookupException: " + le.getMessage(), le);
        }

        LOG.debug("PROTOCOL NUMBERS: {}",protocolNumbers.toString());

        return protocolNumbers;

    }

    public boolean hasUnqualifiedRoles(String userId, List<String> roleIds) {
        LOG.debug("ENTER hasUnqualifiedRoles userid="+userId );
        StringBuffer sqlQuery = new StringBuffer(ROLE_WITHOUT_QUALIFIERS_FOR_MEMBER_QUERY_PART1);
        for (String roleId:roleIds){
            sqlQuery.append("'"+roleId+"',");
        }
        sqlQuery.deleteCharAt(sqlQuery.length()-1);
        sqlQuery.append(ROLE_WITHOUT_QUALIFIERS_FOR_MEMBER_QUERY_PART2);
        LOG.debug(" hasUnqualifiedRoles sqlQuery="+sqlQuery );
        Object[] params = new Object[] { userId };
        try (DBConnection dbc = new DBConnection(this.getPersistenceBroker(true)) ){
            ResultSet rs = dbc.executeQuery(sqlQuery.toString(), params);
            if (rs.next()){
                LOG.info("User "+ userId+" has unqualified role: "+ rs.getString(1));
                return true;
            }
        } catch (SQLException sqle) {
            LOG.error("SQLException: " + sqle.getMessage(), sqle);
        } catch (Exception le) {
            LOG.error("Exception: " + le.getMessage(), le);
        }

        return false;
    }

    public boolean hasViewPermissionOnAllUnitHierarchy(String userId, List<String> roleIds){
        LOG.debug("ENTER hasViewPermissionOnAllUnitHierarchy roleIds={}"+roleIds.toString()+" userid="+userId);

        StringBuffer sqlQuery = new StringBuffer(ROLE_QUALIFIERS_FOR_ROOT_UNIT_QUERY_PART1);
        for (String roleId:roleIds){
            sqlQuery.append("'"+roleId+"',");
        }
        sqlQuery.deleteCharAt(sqlQuery.length()-1);
        sqlQuery.append(ROLE_QUALIFIERS_FOR_ROOT_UNIT_QUERY_PART2);

        Map<String,String> qualifiers = new HashMap<String, String>();
        Object[] params = new Object[] {userId };
        try (DBConnection dbc = new DBConnection(this.getPersistenceBroker(true)) ){
            ResultSet rs = dbc.executeQuery(sqlQuery.toString(), params);
            if (rs.next()){
                LOG.info("User "+ userId+" has root unit view permission! ");
                return true;
            }
        } catch (SQLException sqle) {
            LOG.error("SQLException: " + sqle.getMessage(), sqle);

        } catch (LookupException le) {
            LOG.error("LookupException: " + le.getMessage(), le);
        }

        return false;
    }



    public Map<String,String> getQualifiersForMemberAndAttributeName(List<String> roleIds, String userId, String qualifierName){
        LOG.debug("ENTER getQualifiersForMemberAndAttributeName roleIds={}"+roleIds.toString()+" userid="+userId +" qualifierName="+qualifierName);
        StringBuffer sqlQuery = new StringBuffer(ROLE_QUALIFIERS_FOR_MEMBER_AND_ATTRIBUTE_QUERY_PART1);
        for (String roleId:roleIds){
            sqlQuery.append("'"+roleId+"',");
        }
        sqlQuery.deleteCharAt(sqlQuery.length()-1);
        sqlQuery.append(ROLE_QUALIFIERS_FOR_MEMBER_AND_ATTRIBUTE_QUERY_PART2);

        Map<String,String> qualifiers = new HashMap<String, String>();
        Object[] params = new Object[] {userId, qualifierName };
        try (DBConnection dbc = new DBConnection(this.getPersistenceBroker(true)) ){
            ResultSet rs = dbc.executeQuery(sqlQuery.toString(), params);
            while (rs.next()){
                qualifiers.put( rs.getString(1), rs.getString(2) );
            }
        } catch (SQLException sqle) {
            LOG.error("SQLException: " + sqle.getMessage(), sqle);

        } catch (LookupException le) {
            LOG.error("LookupException: " + le.getMessage(), le);
        }
        LOG.debug("getQualifiersForMemberAndAttributeName resulted qualifiers : {}", qualifiers.toString());
        return qualifiers;
    }


    public Collection<String> getProtocolNumbersWithLeadUnits(Collection<String> leadUnits) {
        LOG.debug("ENTER getProtocolNumbersWithLeadUnits leadUnits={}"+leadUnits.toString());
        Collection<String> protocolNumbers = new HashSet<>();
        StringBuffer sqlQuery = new StringBuffer(PROTOCOL_NUMBER_WITH_LEAD_UNIT_QUERY);
        for (String unitNumber:leadUnits){
            sqlQuery.append("'"+unitNumber+"',");
        }
        sqlQuery.deleteCharAt(sqlQuery.length()-1);
        sqlQuery.append(")");
        LOG.debug("sqlQuery="+sqlQuery);
        try (DBConnection dbc = new DBConnection(this.getPersistenceBroker(true)) ){
            ResultSet rs = dbc.executeQuery(sqlQuery.toString(), null);
            while (rs.next()){
                String protocolNbr = rs.getString(1);
                // there might be invalid protocol numbers in the unit table, skip them if they dont have at least 10 digits
                if (protocolNbr.length()<10){
                    continue;
                }
                if ( protocolNbr.length()>10)
                    protocolNbr = protocolNbr.substring(0,10);
                protocolNumbers.add( protocolNbr );
            }
        } catch (SQLException sqle) {
            LOG.error("SQLException: " + sqle.getMessage(), sqle);

        } catch (LookupException le) {
            LOG.error("LookupException: " + le.getMessage(), le);
        }
        LOG.debug("getProtocolNumbersWithLeadUnits resulted numbers : {}", protocolNumbers.toString());
        return protocolNumbers;
    }
}
