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
import java.util.List;
import java.util.Map;

import org.apache.ojb.broker.accesslayer.LookupException;
import org.kuali.rice.krad.dao.LookupDao;

import edu.arizona.kra.proposaldevelopment.ProposalDevelopmentRoutingState;

/**
 * Proposal Development Routing State Dasbhoard Search DAO interface.
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
    
}