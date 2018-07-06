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


import edu.arizona.kra.proposaldevelopment.bo.ProposalDevelopmentRoutingState;
import edu.arizona.kra.proposaldevelopment.bo.SPSReviewer;
import edu.arizona.kra.proposaldevelopment.dao.PropDevRoutingStateDao;
import edu.arizona.kra.proposaldevelopment.lookup.PropDevRouteStopValueFinder;
import edu.arizona.kra.util.DBConnection;
import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.accesslayer.LookupException;
import org.kuali.rice.krad.dao.impl.LookupDaoOjb;
import org.kuali.rice.krad.util.GlobalVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

import static edu.arizona.kra.proposaldevelopment.PropDevRoutingStateConstants.*;


/**
 * Proposal Development Routing State Dashboard Search DAO Ojb implementation.
 * @author nataliac
 */

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

        try (DBConnection dbc = new DBConnection(this.getPersistenceBroker(true)) ){

            ResultSet rs = dbc.executeQuery(sqlQuery, null);

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
                    rtState.setDeadlineType( getDeadlineTypeLabel(rs.getString(COL_DEADLINE_TYPE)));
                    rtState.setProposalDocumentNumber( documentNumber );
                    rtState.setInitiatorPrincipalName( rs.getString(COL_DOC_INITIATOR));
                    rtState.setProposalTitle( rs.getString(COL_PROPOSAL_TITLE));
                    rtState.setSponsorName( rs.getString(COL_SPONSOR_NAME));
                    rtState.setSponsorCode( rs.getString(COL_SPONSOR_CODE));
                    rtState.setSponsorDeadlineDate( rs.getDate(COL_SPONSOR_DEADLINE_DATE));
                    rtState.setSponsorDeadlineTime( rs.getString(COL_SPONSOR_DEADLINE_TIME));
                    rtState.setSponsorDeadlineDateTime( addTimestampToDate(rtState.getSponsorDeadlineDate(), rtState.getSponsorDeadlineTime()));
                    rtState.setPrincipalInvestigatorName( rs.getString(COL_PRINCIPAL_INVESTIGATOR));
                    rtState.setLeadUnitNumber( rs.getString(COL_LEAD_UNIT));
                    rtState.setLeadUnitName( rs.getString(COL_LEAD_UNIT_NAME));
                    rtState.setLeadCollege("");
                    String ordExpedited = rs.getString(COL_ORD_EXP);
                    rtState.setORDExpedited(YES.equalsIgnoreCase(ordExpedited));
                    if ( rs.getTimestamp(COL_FPR) != null ) {
                        rtState.setFinalProposalReceivedTime(rs.getTimestamp(COL_FPR));
                        rtState.setFinalProposalReceived(true);
                    } else {
                        rtState.setFinalProposalReceived(false);
                    }
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
        }

        LOG.debug("getPropDevRoutingState: no of unfiltered results={}.",results.size());

        //perform additional filtering on the results if the user specified a workflow unit
        if ( !results.isEmpty() && searchCriteria.containsKey( WORKFLOW_UNIT ) && StringUtils.isNotEmpty(searchCriteria.get(WORKFLOW_UNIT))) {
            String workflowUnit = searchCriteria.get( WORKFLOW_UNIT );
            List<String> workFlowUnits = findWorkflowUnitNumbers( workflowUnit );
            List<String> proposalNumbers = findProposalsForWorkflowUnits(workFlowUnits);

            List<ProposalDevelopmentRoutingState> filteredResults = new ArrayList<ProposalDevelopmentRoutingState>();
            for (ProposalDevelopmentRoutingState rtState:results){
                if ( proposalNumbers.contains( rtState.getProposalNumber()) ){
                    filteredResults.add(rtState);
                }
            }
            LOG.debug("getPropDevRoutingState: filtered results={}.",filteredResults.size());
            return filteredResults;
        }
        return results;

    }


    private boolean isAdHocNode(String nodeAnnotation){
        return ( StringUtils.isNotEmpty(nodeAnnotation) && nodeAnnotation.startsWith(NODE_NAME_ADHOC));
    }

    /**
     * Method that builds the main query for searching for in route proposals
     * @param searchCriteria
     * @return
     * @throws SQLException
     * @throws LookupException
     */
    private String buildSqlQuery(Map<String, String> searchCriteria) throws SQLException, LookupException {
        StringBuilder query = new StringBuilder(SQL_LOOKUP);

        if ( searchCriteria.containsKey(ROUTE_UNIT_NBR) && StringUtils.isNotEmpty(searchCriteria.get(ROUTE_UNIT_NBR))){
            String routeUnitNumber = searchCriteria.get(ROUTE_UNIT_NBR);

            query.insert(annotationCriteriaOffset, LEAD_UNIT_ANNOT_CRITERIA+routeUnitNumber+"%'");
        }

        if ( searchCriteria.containsKey(PROPOSAL_PERSON_NAME) && StringUtils.isNotEmpty(searchCriteria.get(PROPOSAL_PERSON_NAME))){
            String ppName = searchCriteria.get(PROPOSAL_PERSON_NAME).replaceAll("[\"?]", "");
            query.append(PROPOSAL_PERSON_NAME_CRITERIA);
            query.append(StringUtils.lowerCase(ppName.replaceAll("[*]", "%")));
            query.append("%')");
        } 

        if ( searchCriteria.containsKey(LEAD_COLLEGE) && StringUtils.isNotEmpty(searchCriteria.get(LEAD_COLLEGE))){
            String leadCollege = searchCriteria.get(LEAD_COLLEGE);
            query.append(LEAD_COLLEGE_CRITERIA);
            query.append(leadCollege);
            query.append(LEAD_COLLEGE_CRITERIA_CONT);
        }

        for ( String searchKey: SEARCH_QUERIES.keySet()){
            if (searchCriteria.containsKey(searchKey) && StringUtils.isNotEmpty(searchCriteria.get(searchKey))){
                query.append(SEARCH_QUERIES.get(searchKey));
                query.append(searchCriteria.get(searchKey));
                query.append("'"); 
            }
        }

        for ( String searchKey: SEARCH_QUERIES_LIKE.keySet()){
            if (searchCriteria.containsKey(searchKey) && StringUtils.isNotEmpty(searchCriteria.get(searchKey))){
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

    private String getDeadlineTypeLabel(String deadlineType){
        if (StringUtils.isNotEmpty(deadlineType)){
            if ( DEADLINE_TYPE_POSTMARK.equalsIgnoreCase(deadlineType) )
                return  DEADLINE_TYPE_POSTMARK_LABEL;
            else if (DEADLINE_TYPE_RECEIPT.equalsIgnoreCase(deadlineType))
                return DEADLINE_TYPE_RECEIPT_LABEL;
            else if (DEADLINE_TYPE_TARGET.equalsIgnoreCase(deadlineType))
                return DEADLINE_TYPE_TARGET_LABEL;
        }
        return DEADLINE_TYPE_NONE_LABEL;
    }

    @Override
    public Boolean getORDExpedited(String proposalNumber) throws SQLException, LookupException {
        LOG.debug("getORDExpedited proposalNumber={}",proposalNumber);

        if (StringUtils.isEmpty(proposalNumber)){
            throw new IllegalArgumentException("PropDevRoutingStateDaoOjb: getORDExpedited with a null proposalNumber!");
        }

        Boolean result = Boolean.FALSE;
        Object[] params = new Object[] { proposalNumber };
        try (DBConnection dbc = new DBConnection(this.getPersistenceBroker(true)) ){

            ResultSet rs = dbc.executeQuery(ORD_EXPEDITED_QUERY, params);
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

        try {
            //first 'remove' last active version of the ORDExpedited by setting its cur_ind to 0
            String curIndQuery = CUR_IND_UPDATE_STMT.replace("$tablename", ORD_EXP_TABLE_NAME);
            Object[] params = new Object[] { proposalNumber };
            try (DBConnection dbc = new DBConnection(this.getPersistenceBroker(true)) ) {
                dbc.executeUpdate(curIndQuery, params);
            }

            //insert updated new value for ORDExpedited with cur_ind=1
            params = new Object[] { proposalNumber, ordExp?YES:NO, GlobalVariables.getUserSession().getPrincipalName() };

            try (DBConnection dbc = new DBConnection(this.getPersistenceBroker(true)) ) {
                dbc.executeUpdate(ADD_ORD_EXPEDITED_QUERY, params);
            }

        } catch (SQLException sqle) {
            LOG.error("SQLException: " + sqle.getMessage(), sqle);
            throw sqle;
        } catch (LookupException le) {
            LOG.error("LookupException: " + le.getMessage(), le);
            throw le;
        }

        LOG.debug("setORDExpedited:Finished");
    }


    @Override
    public String getSPSReviewer(String proposalNumber) throws SQLException, LookupException {
        LOG.debug("getSPSReviewer proposalNumber={}",proposalNumber);

        if (StringUtils.isEmpty(proposalNumber)){
            throw new IllegalArgumentException("PropDevRoutingStateDaoOjb: getSPSReviewer with a null proposalNumber!");
        }
        String spsReviewerId = null;
        Object[] params = new Object[] { proposalNumber };
        try (DBConnection dbc = new DBConnection(this.getPersistenceBroker(true)) ){

            ResultSet rs = dbc.executeQuery(SPS_REVIEWER_QUERY, params);
            if (rs.next()) {
                spsReviewerId = rs.getString(COL_SPS_REVIEWER_ID);
            }

        } catch (SQLException sqle) {
            LOG.error("SQLException: " + sqle.getMessage(), sqle);
            throw sqle;
        } catch (LookupException le) {
            LOG.error("LookupException: " + le.getMessage(), le);
            throw le;
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

        try {
            //first 'remove' last active version of the spsReviewer by setting its cur_ind to 0
            String curIndQuery = CUR_IND_UPDATE_STMT.replace("$tablename", SPS_REV_TABLE_NAME);
            Object[] params = new Object[] { proposalNumber };

            try (DBConnection dbc = new DBConnection(this.getPersistenceBroker(true)) ) {
                dbc.executeUpdate(curIndQuery, params);
            }

            //insert updated new value for sps reviewer with cur_ind=1
            params = new Object[] { proposalNumber, kcPersonId, fullName, GlobalVariables.getUserSession().getPrincipalName() };

            try (DBConnection dbc = new DBConnection(this.getPersistenceBroker(true)) ) {
                dbc.executeUpdate(ADD_SPS_REVIEWER_QUERY, params);
            }

        } catch (SQLException sqle) {
            LOG.error("SQLException: " + sqle.getMessage(), sqle);
            throw sqle;
        } catch (LookupException le) {
            LOG.error("LookupException: " + le.getMessage(), le);
            throw le;
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
            query.deleteCharAt(query.length()-1);
            query.append(")");
            LOG.debug("findSPSReviewers: query={}",query);

            try (DBConnection dbc = new DBConnection(this.getPersistenceBroker(true)) ){
                ResultSet rs = dbc.executeQuery(query.toString(), null);
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
            }
        }       
        LOG.debug("findSPSReviewers: Finished result size={}.",result.size());
        return result;
    }


    /**
     * Searches the proposals associated with the given list of unitNumbers in either the cost sharing part of the budget or
     * by the proposal persons that are assigned a role in the proposal
     * and returns a list of unique proposalNumbers corresponding to those proposals
     *
     * @param workflowUnits - list of unitNumbers
     * @return
     * @throws SQLException
     * @throws LookupException
     */
    protected List<String> findProposalsForWorkflowUnits(List<String> workflowUnits) throws SQLException, LookupException{
        LOG.debug("findProposalsForWorkflowUnits: workflowUnits {}.",workflowUnits);
        List<String> proposalNumbers = new ArrayList<String>();
        if ( workflowUnits != null && !workflowUnits.isEmpty()){
            //create the subquery containing unitNumbers
            StringBuffer unitNumbersList = new StringBuffer("(");
            for (String unitNumber: workflowUnits) {
                unitNumbersList.append("'"+unitNumber+"',");
            }
            //remove last hanging comma
            unitNumbersList.deleteCharAt(unitNumbersList.length()-1);
            unitNumbersList.append(") ");

            StringBuffer query = new StringBuffer(WORKFLOW_UNITS_PROPOSALS_QUERY);
            query.append(unitNumbersList);
            query.append(WORKFLOW_UNITS_PROPOSALS_QUERY_CONT);
            query.append(unitNumbersList);
            query.append(WORKFLOW_UNITS_PROPOSALS_QUERY_FIN);

            try (DBConnection dbc = new DBConnection(this.getPersistenceBroker(true)) ){
                ResultSet rs = dbc.executeQuery(query.toString(), null);
                while (rs.next()){
                    proposalNumbers.add( rs.getString(1) );
                }
            } catch (SQLException sqle) {
                LOG.error("SQLException: " + sqle.getMessage(), sqle);
                throw sqle;
            } catch (LookupException le) {
                LOG.error("LookupException: " + le.getMessage(), le);
                throw le;
            }
        }
        LOG.debug("findProposalsForWorkflowUnits: Finished results size={}.",proposalNumbers.size());
        LOG.debug("PROPOSAL NUMBERS: {}",proposalNumbers.toString());
        return proposalNumbers;
    }

    /**
     * Searches in the Unit table all the units hierarchically associated with the giver unitNumber (parent units and children units)
     * and returns a list of unique unitNumbers corresponding to that hierarchy (which will also include the original unitNumber ofcourse)
     *
     * @param unitNumber
     * @return
     * @throws SQLException
     * @throws LookupException
     */
    protected List<String> findWorkflowUnitNumbers(String unitNumber) throws SQLException, LookupException {
        LOG.debug("findWorkflowUnitNumbers: unitNumber {}.",unitNumber);
        List<String> unitNumbers = new ArrayList<String>();
        if ( StringUtils.isNotEmpty(unitNumber)){
            //there should two unitNumber parameters in the query - no error here...
            Object[] params = new Object[] { unitNumber, unitNumber };
            try (DBConnection dbc = new DBConnection(this.getPersistenceBroker(true)) ){
                ResultSet rs = dbc.executeQuery(WORKFLOW_UNIT_HIERARCHY_QUERY, params);
                while (rs.next()){
                    unitNumbers.add( rs.getString(1) );
                }
            } catch (SQLException sqle) {
                LOG.error("SQLException: " + sqle.getMessage(), sqle);
                throw sqle;
            } catch (LookupException le) {
                LOG.error("LookupException: " + le.getMessage(), le);
                throw le;
            }
        }
        LOG.debug("findWorkflowUnitNumbers: Finished results size={}.",unitNumbers.size());
        LOG.debug("WORKFLOW UNITS: {}",unitNumbers.toString());
        return unitNumbers;
    }


    private static java.sql.Date addTimestampToDate(java.sql.Date date, String time){
        if ( StringUtils.isEmpty( time )) {
            time = "5:00 PM";
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
            Date temp = sdf.parse(time);

            Calendar cal = Calendar.getInstance(); // creates calendar
            cal.setTime(date);
            cal.add(Calendar.HOUR_OF_DAY, temp.getHours());
            cal.add(Calendar.MINUTE, temp.getMinutes());

            return new java.sql.Date(cal.getTimeInMillis());

        } catch (Exception e){
            LOG.info("Exception parsing time: "+time);
        }
        return date;

    }

}