/*
 * Copyright 2005-2015 The Kuali Foundation
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
package edu.arizona.kra.proposaldevelopment.service;

import java.util.List;
import java.util.Map;

import org.apache.ojb.broker.accesslayer.LookupException;
import org.kuali.kra.bo.KcPerson;
import org.kuali.rice.krad.exception.AuthorizationException;

import edu.arizona.kra.proposaldevelopment.bo.ProposalDevelopmentRoutingState;
import edu.arizona.kra.proposaldevelopment.bo.SPSReviewer;

/**
 * Proposal Development Routing State Service used mainly in the Lookup and for setting/getting ORDExpedited and SPSRevierwer for PropDev
 * @author nataliac
 */
public interface PropDevRoutingStateService {

    /**
     * Returns the search results for the Proposal Development Routing State Dashboard
     * @param searchCriteria
     * @return
     */
    public List<ProposalDevelopmentRoutingState> findPropDevRoutingState(Map<String, String> searchCriteria);


    /**
     * Returns True if the Proposal indicated by ProposalNumber is ORDExpedited, False otherwise
     * @param String proposalNumber
     * @return Boolean
     * @throws LookupException 
     */
    public Boolean getORDExpedited(String proposalNumber) throws LookupException;

    /**
     * Sets the ORDExpedited indicator for the Proposal indicated by ProposalNumber. 
     * The service will check if the currentUser is not authorized to change this, will throw a AuthorizationException.
     * @param String proposalNumber
     * @param Boolean ordExp 
     * @param String currentUser
     * @return void
     */
    public void setORDExpedited(String proposalNumber, Boolean ordExp) throws AuthorizationException, IllegalArgumentException;

    /**
     * Returns the KCPerson who is the SPS Reviewer for the Proposal indicated by ProposalNumber - or null if none found.
     * @param String proposalNumber
     * @return KcPerson
     * @throws LookupException 
     */
    public KcPerson getSPSReviewer(String proposalNumber) throws LookupException;

    /**
     * Sets the SPS Reviewer for the Proposal indicated by ProposalNumber to be the KCPerson indicated by kcPersonId.
     * The service will check if the currentUser is not authorized to change this, will throw a AuthorizationException.
     * @param String proposalNumber
     * @param String kcPersonId - a valid ID for the user in the system who will be an SPS Reviewer. If no such person is found, will throw IllegalArgumentException
     * @param String currentUser
     * @return void
     */
    public void setSPSReviewer(String proposalNumber, String kcPersonId) throws AuthorizationException, IllegalArgumentException;


    /**
     * Returns the list of SPSReviewer beans containing principalId and FullNames for the users that have the SPSReviewer role
     * @return
     * @throws LookupException 
     */
    public List<SPSReviewer> findSPSReviewers() throws LookupException;


}
