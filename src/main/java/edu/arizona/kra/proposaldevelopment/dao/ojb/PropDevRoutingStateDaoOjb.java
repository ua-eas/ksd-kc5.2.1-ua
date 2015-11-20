/*
 * Copyright 2005-2015 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.arizona.kra.proposaldevelopment.dao.ojb;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.accesslayer.LookupException;
import org.kuali.rice.krad.dao.impl.LookupDaoOjb;

import static edu.arizona.kra.proposaldevelopment.PropDevRoutingStateConstants.*;
import edu.arizona.kra.proposaldevelopment.ProposalDevelopmentRoutingState;
import edu.arizona.kra.proposaldevelopment.dao.PropDevRoutingStateDao;
import edu.arizona.kra.proposaldevelopment.lookup.PropDevRouteStopValueFinder;


/**
 * Proposal Development Routing State Dashboard Search DAO Ojb implementation.
 */
public class PropDevRoutingStateDaoOjb extends LookupDaoOjb implements PropDevRoutingStateDao {
    private static final Log LOG = LogFactory.getLog(PropDevRoutingStateDaoOjb.class);
    private static final PropDevRouteStopValueFinder nodeNameFinder = new PropDevRouteStopValueFinder();  
    
    @Override
    public List<ProposalDevelopmentRoutingState> getPropDevRoutingState(Map<String, String> searchCriteria) throws SQLException, LookupException {        
        List<ProposalDevelopmentRoutingState> results = new ArrayList<ProposalDevelopmentRoutingState>();
        HashSet<String> resultDocNumbers = new HashSet<String>();
        LOG.debug("getPropDevRoutingState searchCriteria="+searchCriteria.toString());
  
        String sqlQuery = buildSqlQuery(searchCriteria);
        boolean displayAdHocNodes = (searchCriteria.containsKey(ROUTE_STOP_NAME) && StringUtils.isEmpty(searchCriteria.get(ROUTE_STOP_NAME)));
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            PersistenceBroker broker = this.getPersistenceBroker(true);

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
            closeDatabaseObjects(rs, ps, conn);
        }
        
        LOG.debug("getPropDevRoutingState results="+results.size());
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
        
        LOG.debug("getPropDevRoutingState sqlQuery="+query);
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
        query.append(targetDate);
        if ( isEndDate ){
            query.append(END_DATE_TIME);
            query.append(END_DATE_FORMAT_STR);
        }
        else {
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
    
    
    protected void closeDatabaseObjects(ResultSet rs, PreparedStatement ps, Connection conn) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException ex) {
                LOG.warn("Failed to close ResultSet.", ex);
            }
        }
        if ( ps != null ){
            try {
                ps.close();
            } catch (SQLException ex) {
                LOG.warn("Failed to close PreparedStatement.", ex);
            }
        }
        if ( conn != null ){
            try {
                conn.close();
            } catch (SQLException ex) {
                LOG.warn("Failed to close Connection.", ex);
            }
        }
    }

}