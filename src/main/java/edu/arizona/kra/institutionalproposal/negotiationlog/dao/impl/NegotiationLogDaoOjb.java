/*
 * Copyright 2005-2014 The Kuali Foundation
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
package edu.arizona.kra.institutionalproposal.negotiationlog.dao.impl;

import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.accesslayer.LookupException;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

import edu.arizona.kra.institutionalproposal.negotiationlog.dao.NegotiationLogDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class NegotiationLogDaoOjb extends PlatformAwareDaoBaseOjb implements NegotiationLogDao {
    
    public Integer findMaxNegotiationLogId() throws SQLException, LookupException {
        Integer maxNegotiationLogId = 0;
        Statement stmt = null;
        PersistenceBroker pbInstance = getPersistenceBroker(true);
        try {
            stmt = pbInstance.serviceConnectionManager().getConnection().createStatement();
            
            ResultSet rs = stmt.executeQuery("select max(NEGOTIATION_LOG_ID) from negotiation_log");
            if ( rs.next() ){
                maxNegotiationLogId = rs.getInt(1);
            }
        } catch (SQLException e) {
            throw e;
        } 
        return maxNegotiationLogId;
    }
    
    public List<Integer> findNegotiationLogIds(Integer startNegotiationLogId, int maxNumberOfResults, boolean closed) throws SQLException, LookupException{
        ArrayList<Integer> results = new ArrayList<Integer>();
        Statement stmt = null;
        PersistenceBroker pbInstance = getPersistenceBroker(true);
        try {
            stmt = pbInstance.serviceConnectionManager().getConnection().createStatement();
            int endNegotiationLogId = startNegotiationLogId + maxNumberOfResults;
            char closedFlag = closed?'Y':'N';
            ResultSet rs = stmt.executeQuery("select NEGOTIATION_LOG_ID from negotiation_log WHERE CLOSED_FLAG='"+closedFlag+"' AND NEGOTIATION_LOG_ID BETWEEN "
                    +startNegotiationLogId+" AND "+endNegotiationLogId);
            while ( rs.next() ){
                results.add( rs.getInt(1) );
            }
        } catch (SQLException e) {
            throw e;
        } 
        return results;
    }
 
}
