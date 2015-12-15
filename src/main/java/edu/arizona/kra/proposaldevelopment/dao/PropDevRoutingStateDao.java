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
package edu.arizona.kra.proposaldevelopment.dao;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.ojb.broker.accesslayer.LookupException;
import org.kuali.rice.krad.dao.LookupDao;
import edu.arizona.kra.proposaldevelopment.bo.ProposalDevelopmentRoutingState;
import edu.arizona.kra.proposaldevelopment.bo.SPSReviewer;

/**
 * Proposal Development Routing State Dasbhoard Search DAO interface.
 * @author nataliac
 */
public interface PropDevRoutingStateDao extends LookupDao {

    /**
     * Searches the DB for PropDev Routing status based on the query criteria in fieldValues 
     * 
     * @return  A list of ProposalDevelopmentRoutingState.
     * @throws LookupException 
     * @throws SQLException 
     */
    public List<ProposalDevelopmentRoutingState> getPropDevRoutingState(Map<String, String> searchCriteria) throws SQLException, LookupException;

    /**
     * Returns True if the Proposal indicated by ProposalNumber is ORDExpedited, False otherwise
     * @param String proposalNumber
     * @return Boolean
     * @throws LookupException 
     * @throws SQLException 
     */
    public Boolean getORDExpedited(String proposalNumber) throws SQLException, LookupException;

    /**
     * Sets the ORDExpedited indicator for the Proposal indicated by ProposalNumber. 
     * The service will check if the currentUser is not authorized to change this, will throw a AuthorizationException.
     * @param String proposalNumber
     * @param Boolean ordExp 
     * @param String currentUser
     * @return void
     * @throws LookupException 
     * @throws SQLException 
     */
    public void setORDExpedited(String proposalNumber, Boolean ordExp) throws SQLException, LookupException;

    /**
     * Returns the KCPersonId who is the SPS Reviewer for the Proposal indicated by ProposalNumber - or null if none found.
     * @param String proposalNumber
     * @return String personId for the SPSReviewer
     * @throws LookupException 
     * @throws SQLException 
     */
    public String getSPSReviewer(String proposalNumber) throws SQLException, LookupException;

    /**
     * Sets the SPS Reviewer for the Proposal indicated by ProposalNumber to be the KCPerson indicated by kcPersonId.
     * 
     * @param String proposalNumber
     * @param String kcPersonId - a valid ID for the user
     * @param String fullName - full name of the SPS Reviewer
     * @return void
     * @throws LookupException 
     * @throws SQLException 
     */
    public void setSPSReviewer(String proposalNumber, String kcPersonId, String fullName) throws SQLException, LookupException;

    /**
     * Returns a list of SPSReviewer stub objects containing just the id and the full name for the provided list of principalIds
     * @param Collection<String> principalIds
     * @return List<SPSReviewer>
     * @throws LookupException 
     * @throws SQLException 
     */
    public List<SPSReviewer> findSPSReviewers(Collection<String> principalIds) throws SQLException, LookupException;


}