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
package edu.arizona.kra.institutionalproposal.negotiationlog.dao;

import java.sql.SQLException;
import java.util.List;

import org.apache.ojb.broker.accesslayer.LookupException;

public interface NegotiationLogDao {
      
    public Integer findMaxNegotiationLogId() throws SQLException, LookupException;
    
    public List<Integer> findNegotiationLogIds(Integer startNegotiationLogId, int maxNumberOfResults, boolean closed) throws SQLException, LookupException;

}
