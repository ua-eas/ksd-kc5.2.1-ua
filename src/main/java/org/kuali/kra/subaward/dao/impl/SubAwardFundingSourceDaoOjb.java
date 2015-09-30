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
package org.kuali.kra.subaward.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.accesslayer.LookupException;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kra.award.home.Award;
import org.kuali.kra.subaward.bo.SubAward;
import org.kuali.kra.subaward.bo.SubAwardFundingSource;
import org.kuali.kra.subaward.dao.SubAwardFundingSourceDao;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;
import org.kuali.rice.krad.service.util.OjbCollectionAware;
import org.springframework.dao.DataAccessException;




public class SubAwardFundingSourceDaoOjb extends PlatformAwareDaoBaseOjb implements OjbCollectionAware, SubAwardFundingSourceDao {


    private static final Log LOG = LogFactory.getLog(SubAwardFundingSourceDaoOjb.class);


    private static final String SQL_SUBAWARDS =  "select SUBAWARD.SUBAWARD_ID, SUBAWARD.SUBAWARD_CODE, status from SUBAWARD join (select unique SUBAWARD_CODE sc, h.SEQ_OWNER_SEQ_NUMBER seq, h.VERSION_STATUS status from SUBAWARD_FUNDING_SOURCE sfs JOIN VERSION_HISTORY h on sfs.SUBAWARD_CODE = h.SEQ_OWNER_VERSION_NAME_VALUE where AWARD_NUMBER=? and ACTV_IND='Y'and h.SEQ_OWNER_CLASS_NAME='org.kuali.kra.subaward.bo.SubAward' and (h.VERSION_STATUS='ACTIVE' or h.VERSION_STATUS='PENDING')) a on SUBAWARD.SUBAWARD_CODE = a.sc and SUBAWARD.SEQUENCE_NUMBER = a.seq order by SUBAWARD_ID";
    private static final String SQL_AWARDS =  "select AWARD_ID, AWARD_NUMBER, AWARD_SEQUENCE_STATUS from AWARD join (select unique AWARD_NUMBER an, h.SEQ_OWNER_SEQ_NUMBER seq from SUBAWARD_FUNDING_SOURCE sfs JOIN VERSION_HISTORY h on sfs.AWARD_NUMBER = h.SEQ_OWNER_VERSION_NAME_VALUE where sfs.SUBAWARD_CODE=? and ACTV_IND='Y'and h.SEQ_OWNER_CLASS_NAME='org.kuali.kra.award.home.Award' and  (h.VERSION_STATUS='ACTIVE' or h.VERSION_STATUS='PENDING')) a on AWARD.AWARD_NUMBER = a.an and AWARD.SEQUENCE_NUMBER = a.seq and (AWARD.AWARD_SEQUENCE_STATUS = 'ACTIVE' or AWARD.AWARD_SEQUENCE_STATUS = 'PENDING') order by AWARD_ID";
    
    private static final String AWARD_ID = "AWARD_ID";
    private static final String AWARD_NUMBER = "AWARD_NUMBER";
    private static final String SUBAWARD_CODE = "SUBAWARD_CODE";

    private static final String ACTIVE = "ACTIVE";
    private static final String PENDING = "PENDING";    

    @SuppressWarnings("unchecked")
    @Override
    public Collection<Award> getLinkedAwards(SubAward subAward) throws SQLException, LookupException {
        
        List<String> awardIdsList = getLinkedAwardsIds(subAward);
        if ( !awardIdsList.isEmpty() ){
            Criteria criteria = new Criteria();
            criteria.addColumnIn(AWARD_ID, awardIdsList);
            return (Collection<Award>)getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(Award.class, criteria));
        }
        return new ArrayList<Award>(0);
    }


    @Override
    public List<String> getLinkedSubAwardsIds(Award award) throws SQLException, LookupException {
        if ( award == null) {
            throw new IllegalArgumentException("SubAwardFundingSourceDaoOjb: getLinkedSubAwardsIds with a null Award!");
        }
        LOG.debug("getLinkedSubAwardsCodes awardNumber="+award.getAwardNumber()+" awardId="+award.getAwardId());
        List<String> result = new ArrayList<String>();

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        PersistenceBroker broker=null;
        try {
            broker = this.getPersistenceBroker(true);
            conn = broker.serviceConnectionManager().getConnection();
            ps = conn.prepareStatement(SQL_SUBAWARDS);
            ps.setString(1, award.getAwardNumber());
            rs = ps.executeQuery();
            //eliminate duplicate results
            List<Row> rows = eliminateDuplicates(rs);

            for ( Row row: rows){
                result.add(row.getId());
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

        return result;
    }


    protected List<String> getLinkedAwardsIds(SubAward subAward) throws SQLException, LookupException {
        if ( subAward == null) {
            throw new IllegalArgumentException("SubAwardFundingSourceDaoOjb: getLinkedAwardsIds with a null SubAward!");
        }
        LOG.debug("getLinkedAwardsIds subAward code="+subAward.getSubAwardCode()+" subAwardId="+subAward.getSubAwardId());
        List<String> result = new ArrayList<String>();

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        PersistenceBroker broker=null;
        try {
            broker = this.getPersistenceBroker(true);
            conn = broker.serviceConnectionManager().getConnection();
            ps = conn.prepareStatement(SQL_AWARDS);
            ps.setString(1, subAward.getSubAwardCode());
            rs = ps.executeQuery();
            //eliminate duplicate results
            List<Row> rows = eliminateDuplicates(rs);

            for ( Row row: rows){
                result.add(row.getId());
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

        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void deleteSubAwardFundingSource(SubAwardFundingSource sfs) throws DataAccessException {
        deleteSubAwardFundingSource(sfs.getAwardNumber(), sfs.getSubAwardCode());
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void deleteSubAwardFundingSource(String awardNumber, String subAwardcode) throws DataAccessException {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(AWARD_NUMBER, awardNumber);
        criteria.addEqualTo(SUBAWARD_CODE, subAwardcode);

        Collection<SubAwardFundingSource> results = (Collection<SubAwardFundingSource>) getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(SubAwardFundingSource.class, criteria));
        for (SubAwardFundingSource subAwardFS:results){
            subAwardFS.setActive(false);
            getPersistenceBrokerTemplate().store(subAwardFS);
        }
    }


    protected void closeDatabaseObjects(ResultSet rs, PreparedStatement ps, Connection conn, PersistenceBroker broker) {
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
        
        if (broker != null) {
            this.releasePersistenceBroker(broker);
        }
    }

 
     
    private List<Row> eliminateDuplicates(ResultSet rs) throws SQLException{
        List<Row> rows = new ArrayList<Row>();
        while (rs.next()) {
            //Result set structure: 1=ID 2=AwardNumber or SubawardCode 3=Status
            Row currentRow = new Row(rs.getString(1),rs.getString(2), rs.getString(3));

            //eliminate duplicate results
            boolean duplicate = false;
            for ( Row row: rows){
                if ( currentRow.hasSameIdentifier(row) ){
                    if ( currentRow.isActive() && row.isPending() ){
                        //replace the pending version in the results with the actual active version of the award/subaward
                        row.replaceWith(currentRow);
                    }
                    duplicate = true;
                    break;
                }
            }
            if (!duplicate){
                rows.add(currentRow);
            }
        }
        return rows;
    }

    private class Row {
        private String id;
        private String identifier;
        private String status;

        public Row(String id, String identifier, String status){
            this.id=new String(id);
            this.identifier=new String(identifier);
            this.status=new String(status);
        }

        public String getId() {
            return id;
        }

        public String getIdentifier() {
            return identifier;
        }

        public String getStatus() {
            return status;
        }


        public boolean hasSameIdentifier(Row otherRow){
            if ( this.identifier == null || otherRow.identifier==null ){
                return false;
            }
            return this.identifier.equalsIgnoreCase(otherRow.getIdentifier());
        }

        public boolean isActive(){
            return ACTIVE.equalsIgnoreCase(status);
        }

        public boolean isPending(){
            return PENDING.equalsIgnoreCase(status);
        }

        public void replaceWith(Row otherRow){
            id=otherRow.getId();
            identifier=otherRow.getIdentifier();
            status=otherRow.getStatus();
        }

    }

}
