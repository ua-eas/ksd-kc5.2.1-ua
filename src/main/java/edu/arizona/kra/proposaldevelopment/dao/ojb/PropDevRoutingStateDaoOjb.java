/*
 * Copyright 2005-2016 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.osedu.org/licenses/ECL-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.arizona.kra.proposaldevelopment.dao.ojb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;


import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.accesslayer.LookupException;
import org.kuali.rice.krad.dao.impl.LookupDaoOjb;
import org.kuali.rice.krad.util.GlobalVariables;
import org.springframework.transaction.annotation.Transactional;
import org.springmodules.orm.ojb.OjbFactoryUtils;

import static edu.arizona.kra.proposaldevelopment.PropDevRoutingStateConstants.*;
import edu.arizona.kra.proposaldevelopment.bo.ProposalDevelopmentRoutingState;
import edu.arizona.kra.proposaldevelopment.bo.SPSReviewer;
import edu.arizona.kra.proposaldevelopment.dao.PropDevRoutingStateDao;
import edu.arizona.kra.proposaldevelopment.lookup.PropDevRouteStopValueFinder;


/**
 * Proposal Development Routing State Dashboard Search DAO Ojb implementation.
 * @author nataliac
 */
@SuppressWarnings("unchecked")
public class PropDevRoutingStateDaoOjb extends LookupDaoOjb implements PropDevRoutingStateDao {
    private static final Logger LOG = LoggerFactory.getLogger(PropDevRoutingStateDaoOjb.class);
    private static final PropDevRouteStopValueFinder nodeNameFinder = new PropDevRouteStopValueFinder();  

    @Override
    public List<ProposalDevelopmentRoutingState> getPropDevRoutingState(Map<String, String> searchCriteria) throws SQLException, LookupException {        
        List<ProposalDevelopmentRoutingState> results = new ArrayList<ProposalDevelopmentRoutingState>();
        HashSet<String> resultDocNumbers = new HashSet<String>();
        LOG.debug("getPropDevRoutingState searchCriteria={}",searchCriteria);

        String sqlQuery = buildSqlQuery(searchCriteria);
        boolean displayAdHocNodes = (searchCriteria.containsKey(ROUTE_STOP_NAME) && StringUtils.isEmpty(searchCriteria.get(ROUTE_STOP_NAME)));

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        PersistenceBroker broker = null;
        try {
            broker = this.getPersistenceBroker(true);
            conn = broker.serviceConnectionManager().getConnection();
            ps = conn.prepareStatement(sqlQuery);
            rs = ps.executeQuery();

            while (rs.next()) { 
                String documentNumber = rs.getString(COL_DOCUMENT_NUMBER);
                String annotation = rs.getString(COL_ANNOTATION);
                //avoid displaying more than one result row per document
                if ( !resultDocNumbers.contains(documentNumber)){
                    //skip AdHoc nodes if the searchCriteria has a specific NodeName
                    if ( isAdHocNode(annotation) && !displayAdHocNodes ){
                        continue;
                    }
                    ProposalDevelopmentRoutingState rtState = new ProposalDevelopmentRoutingState();
                    rtState.setRouteStopDate( rs.getTimestamp(COL_STOP_DATE));
                    rtState.setRouteStopName( getRouteStopLabel(rs.getString(COL_NODE_NAME), annotation));
                    rtState.setProposalNumber( rs.getString(COL_PROPOSAL_NUMBER));
                    rtState.setProposalDocumentNumber( documentNumber );
                    rtState.setProposalTitle( rs.getString(COL_PROPOSAL_TITLE));
                    rtState.setSponsorName( rs.getString(COL_SPONSOR_NAME));
                    rtState.setSponsorCode( rs.getString(COL_SPONSOR_CODE));
                    rtState.setSponsorDeadlineDate( rs.getDate(COL_SPONSOR_DEADLINE_DATE));
                    rtState.setSponsorDeadlineTime( rs.getString(COL_SPONSOR_DEADLINE_TIME));
                    rtState.setPrincipalInvestigatorName( rs.getString(COL_PRINCIPAL_INVESTIGATOR));
                    rtState.setLeadUnitNumber( rs.getString(COL_LEAD_UNIT));
                    rtState.setLeadUnitName( rs.getString(COL_LEAD_UNIT_NAME));
                    rtState.setLeadCollege("");
                    String ordExpedited = rs.getString(COL_ORD_EXP);
                    rtState.setORDExpedited(YES.equalsIgnoreCase(ordExpedited));
                    String fpr = rs.getString(COL_FPR);
                    rtState.setFinalProposalReceived(YES.equalsIgnoreCase(fpr));
                    rtState.setSPSPersonId(rs.getString(COL_SPS_REVIEWER_ID));
                    rtState.setSPSReviewerName(rs.getString(COL_SPS_REVIEWER_NAME));
                    results.add(rtState);
                    resultDocNumbers.add(rtState.getProposalDocumentNumber());
                }

            }

        } catch (SQLException sqle) {
            LOG.error("SQLException: " + sqle.getMessage(), sqle);
            throw sqle;
        } catch (LookupException le) {
            LOG.error("LookupException: " + le.getMessage(), le);
            throw le;
        } finally {
            closeDatabaseObjects(rs, ps, conn, broker);
        }

        LOG.debug("getPropDevRoutingState: no of results={}.",results.size());
        return results;

    }


    private boolean isAdHocNode(String nodeAnnotation){
        return ( StringUtils.isNotEmpty(nodeAnnotation) && nodeAnnotation.startsWith(NODE_NAME_ADHOC));
    }

    private String buildSqlQuery(Map<String, String> searchCriteria){
        StringBuilder query = new StringBuilder(SQL_LOOKUP);

        if ( searchCriteria.containsKey(ROUTE_UNIT_NBR) && !StringUtils.isEmpty(searchCriteria.get(ROUTE_UNIT_NBR))){
            String routeUnitNumber = searchCriteria.get(ROUTE_UNIT_NBR);

            query.insert(annotationCriteriaOffset, LEAD_UNIT_ANNOT_CRITERIA+routeUnitNumber+"%'");
        }


        if ( searchCriteria.containsKey(PROPOSAL_PERSON_NAME) && !StringUtils.isEmpty(searchCriteria.get(PROPOSAL_PERSON_NAME))){
            String ppName = searchCriteria.get(PROPOSAL_PERSON_NAME).replaceAll("[\"?]", "");
            query.append(PROPOSAL_PERSON_NAME_CRITERIA);
            query.append(StringUtils.lowerCase(ppName.replaceAll("[*]", "%")));
            query.append("%')"); 
        } 

        if ( searchCriteria.containsKey(LEAD_COLLEGE) && !StringUtils.isEmpty(searchCriteria.get(LEAD_COLLEGE))){
            String leadCollege = searchCriteria.get(LEAD_COLLEGE);
            query.append(LEAD_COLLEGE_CRITERIA);
            query.append(leadCollege);
            query.append(LEAD_COLLEGE_CRITERIA_CONT);
        }

        for ( String searchKey: SEARCH_QUERIES.keySet()){
            if (searchCriteria.containsKey(searchKey) && !StringUtils.isEmpty(searchCriteria.get(searchKey))){
                query.append(SEARCH_QUERIES.get(searchKey));
                query.append(searchCriteria.get(searchKey));
                query.append("'"); 
            }
        }

        for ( String searchKey: SEARCH_QUERIES_LIKE.keySet()){
            if (searchCriteria.containsKey(searchKey) && !StringUtils.isEmpty(searchCriteria.get(searchKey))){
                String value = searchCriteria.get(searchKey).replaceAll("[\"?]", "");
                query.append(SEARCH_QUERIES_LIKE.get(searchKey));
                query.append(StringUtils.lowerCase(value.replaceAll("[*]", "%")));
                query.append("%'"); 
            }
        }

        addSearchDateCriteria(searchCriteria.get(SPONSOR_DATE), SPONSOR_DATE_CRITERIA, query );
        addSearchDateCriteria(searchCriteria.get(ROUTE_STOP_DATE), ROUTE_STOP_DATE_CRITERIA, query );

        query.append(ORDER_CRITERIA);

        LOG.debug("END getPropDevRoutingState sqlQuery={}.",query);
        return query.toString();
    }


    private StringBuilder addSearchDateCriteria(String date, String criteria, StringBuilder query){
        if ( StringUtils.isNotEmpty(date) ){
            if ( date.contains("..")){
                String startDate = date.substring(0, date.lastIndexOf(".."));
                String endDate = date.substring(date.lastIndexOf("..")+2, date.length());
                appendDate(startDate, criteria, query, false);
                appendDate(endDate, criteria, query, true);
            }
            else if (date.contains("=")){
                String targetDate = date.substring(date.lastIndexOf('=')+1, date.length());
                String operation = date.substring(0, 1);
                appendDate(targetDate,criteria, query, operation.equals("<"));
            }
        }
        return query;
    }


    private StringBuilder appendDate(String targetDate, String criteria, StringBuilder query, boolean isEndDate){     
        query.append(criteria);
        if ( isEndDate ){
            query.append("<=");
        }
        else {
            query.append(">=");
        }

        query.append(DATE_QUERY_PREFIX);    

        if ( isEndDate ){
            query.append(END_DATE_TIME);
            query.append(targetDate);
            query.append(END_DATE_FORMAT_STR);
        }
        else {
            query.append(targetDate);
            query.append(DATE_FORMAT_STR);
        }
        return query;
    }

    private String getRouteStopLabel(String routeStopName, String annotation){       
        if (StringUtils.isNotEmpty(routeStopName)){
            if ( isAdHocNode(annotation) ){
                return NODE_NAME_ADHOC;
            }
            String label = nodeNameFinder.getKeyLabel(routeStopName);

            if (StringUtils.isNotEmpty(label)){
                return label;
            }
        }
        return routeStopName;
    }

    @Override
    public Boolean getORDExpedited(String proposalNumber) throws SQLException, LookupException {
        LOG.debug("getORDExpedited proposalNumber={}",proposalNumber);

        if (StringUtils.isEmpty(proposalNumber)){
            throw new IllegalArgumentException("PropDevRoutingStateDaoOjb: getORDExpedited with a null proposalNumber!");
        }

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Boolean result = Boolean.FALSE;
        PersistenceBroker broker = null;
        try {
            broker = this.getPersistenceBroker(true);
            conn = broker.serviceConnectionManager().getConnection();

            ps = conn.prepareStatement(ORD_EXPEDITED_QUERY);
            ps.setString(1, proposalNumber);
            rs = ps.executeQuery();
            if (rs.next()) { 
                String ordExpedited = rs.getString(COL_ORD_EXP);
                if ( YES.equalsIgnoreCase(ordExpedited) ){
                    result = Boolean.TRUE;
                }
            }

        } catch (SQLException sqle) {
            LOG.error("SQLException: " + sqle.getMessage(), sqle);
            throw sqle;
        } catch (LookupException le) {
            LOG.error("LookupException: " + le.getMessage(), le);
            throw le;
        } finally {
            closeDatabaseObjects(rs, ps, conn, broker);
        }
        LOG.debug("getORDExpedited Result={}.",result);
        return result;
    }


    @Override
    @Transactional 
    public void setORDExpedited(String proposalNumber, Boolean ordExp) throws SQLException, LookupException {
        LOG.debug("setORDExpedited proposalNumber={} value={}.",proposalNumber,ordExp);
        if (StringUtils.isEmpty(proposalNumber) || ordExp == null){
            throw new IllegalArgumentException("PropDevRoutingStateDaoOjb: setORDExpedited with null args!");
        }
        String currentUserName = GlobalVariables.getUserSession().getPrincipalName();
        Connection conn = null;
        PreparedStatement ps1 = null, ps2 = null;
        ResultSet rs = null;
        String curIndQuery = CUR_IND_UPDATE_STMT.replace("$tablename", ORD_EXP_TABLE_NAME);
        PersistenceBroker broker = null;
        try {
            broker = this.getPersistenceBroker(true);
            conn = broker.serviceConnectionManager().getConnection();
            //first 'remove' last active version by setting cur_ind to 0
            ps1 = conn.prepareStatement(curIndQuery);
            ps1.setString(1, proposalNumber);
            ps1.executeUpdate();
            //insert new vale with cur_ind=1
            ps2 = conn.prepareStatement(ADD_ORD_EXPEDITED_QUERY);
            ps2.setString(1, proposalNumber);
            ps2.setString(2, ordExp?YES:NO);
            ps2.setString(3, currentUserName);
            ps2.executeUpdate();

        } catch (SQLException sqle) {
            LOG.error("SQLException: " + sqle.getMessage(), sqle);
            throw sqle;
        } catch (LookupException le) {
            LOG.error("LookupException: " + le.getMessage(), le);
            throw le;
        } finally {
            closeDatabaseObjects(rs, ps1, conn, broker);
            if ( ps2 != null ){
                try {
                    ps2.close();
                } catch (SQLException ex) {
                    LOG.warn("Failed to close PreparedStatement.", ex);
                }
            }
        }
        LOG.debug("setORDExpedited:Finished");

    }


    @Override
    public String getSPSReviewer(String proposalNumber) throws SQLException, LookupException {
        LOG.debug("getSPSReviewer proposalNumber={}",proposalNumber);

        if (StringUtils.isEmpty(proposalNumber)){
            throw new IllegalArgumentException("PropDevRoutingStateDaoOjb: getSPSReviewer with a null proposalNumber!");
        }

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String spsReviewerId = null;
        PersistenceBroker broker = null;
        try {

            broker = this.getPersistenceBroker(true);
            conn = broker.serviceConnectionManager().getConnection();

            ps = conn.prepareStatement(SPS_REVIEWER_QUERY);
            ps.setString(1, proposalNumber);
            rs = ps.executeQuery();
            if (rs.next()) { 
                spsReviewerId = rs.getString(COL_SPS_REVIEWER_ID);
            }

        } catch (SQLException sqle) {
            LOG.error("SQLException: " + sqle.getMessage(), sqle);
            throw sqle;
        } catch (LookupException le) {
            LOG.error("LookupException: " + le.getMessage(), le);
            throw le;
        } finally {
            closeDatabaseObjects(rs, ps, conn, broker);
        }
        LOG.debug("getSPSReviewer Result={}.",spsReviewerId);
        return spsReviewerId;
    }


    @Override
    @Transactional
    public void setSPSReviewer(String proposalNumber, String kcPersonId, String fullName) throws SQLException, LookupException {
        LOG.debug("setSPSReviewer proposalNumber={}  fullName={}.",proposalNumber,fullName);
        if (StringUtils.isEmpty(proposalNumber) || StringUtils.isEmpty(kcPersonId) ||  StringUtils.isEmpty(fullName)){
            throw new IllegalArgumentException("PropDevRoutingStateDaoOjb: setSPSReviewer with null args!");
        }

        String currentUserName = GlobalVariables.getUserSession().getPrincipalName();
        Connection conn = null;
        PreparedStatement ps1 = null, ps2 = null;
        ResultSet rs = null;
        String curIndQuery = CUR_IND_UPDATE_STMT.replace("$tablename", SPS_REV_TABLE_NAME);
        PersistenceBroker broker = null;
        try {
            broker = this.getPersistenceBroker(true);
            conn = broker.serviceConnectionManager().getConnection();
            //first 'remove' last active version by setting cur_ind to 0
            ps1 = conn.prepareStatement(curIndQuery);
            ps1.setString(1, proposalNumber);
            ps1.executeUpdate();
            //insert new vale with cur_ind=1
            ps2 = conn.prepareStatement(ADD_SPS_REVIEWER_QUERY);
            ps2.setString(1, proposalNumber);
            ps2.setString(2, kcPersonId);
            ps2.setString(3, fullName);
            ps2.setString(4, currentUserName);
            ps2.executeUpdate();

        } catch (SQLException sqle) {
            LOG.error("SQLException: " + sqle.getMessage(), sqle);
            throw sqle;
        } catch (LookupException le) {
            LOG.error("LookupException: " + le.getMessage(), le);
            throw le;
        } finally {
            closeDatabaseObjects(rs, ps1, conn, broker);
            if ( ps2 != null ){
                try {
                    ps2.close();
                } catch (SQLException ex) {
                    LOG.warn("Failed to close PreparedStatement.", ex);
                }
            }
        }
        LOG.debug("setSPSReviewer:Finished.");

    }

    @Override 
    public List<SPSReviewer> findSPSReviewers(Collection<String> principalIds) throws SQLException, LookupException {
        LOG.debug("findSPSReviewers: personIds {}.",principalIds);
        List<SPSReviewer> result = new ArrayList<SPSReviewer>();
        if ( principalIds != null && principalIds.size() > 0){

            StringBuilder query = new StringBuilder(SPS_REVIEWERS_QUERY);
            for (String id:principalIds){
                query.append("'"+id+"',");
            }
            //remove last comma
            query.setLength(query.length()-1);
            query.append(")");
            LOG.debug("findSPSReviewers: query={}",query);
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            PersistenceBroker broker = null;
            try {         
                broker = this.getPersistenceBroker(true);

                conn = broker.serviceConnectionManager().getConnection();
                ps = conn.prepareStatement(query.toString());
                rs = ps.executeQuery();
                while (rs.next()){
                    SPSReviewer person = new SPSReviewer();
                    person.setPrincipalId(rs.getString(COL_SPS_REVIEWER_ID));
                    person.setFullName(rs.getString(COL_SPS_REVIEWER_NAME));
                    result.add(person);
                }
            } catch (SQLException sqle) {
                LOG.error("SQLException: " + sqle.getMessage(), sqle);
                throw sqle;
            } catch (LookupException le) {
                LOG.error("LookupException: " + le.getMessage(), le);
                throw le;
            } finally {
                closeDatabaseObjects(rs, ps, conn, broker);
            }
        }       
        LOG.debug("findSPSReviewers: Finished result size={}.",result.size());
        return result;
    }


    protected void closeDatabaseObjects(ResultSet rs, PreparedStatement ps, Connection conn, PersistenceBroker broker) {
        if (rs != null) {
            try {
                rs.close();
            } catch (Exception ex) {
                LOG.warn("Failed to close ResultSet.", ex);
            }
        }
        if ( ps != null ){
            try {
                ps.close();
            } catch (Exception ex) {
                LOG.warn("Failed to close PreparedStatement.", ex);
            }
        }
        if ( conn != null ){
            try {
                conn.close();
            } catch (Exception ex) {
                LOG.warn("Failed to close Connection.", ex);
            }
        }
        if (broker != null) {
            try {
                OjbFactoryUtils.releasePersistenceBroker(broker, this.getPersistenceBrokerTemplate().getPbKey());
            } catch (Exception e) {
                LOG.error("Failed closing connection: " + e.getMessage(), e);
            }
        }
    }




}