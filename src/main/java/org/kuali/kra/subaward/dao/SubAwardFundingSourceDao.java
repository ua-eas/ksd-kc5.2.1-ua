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
package org.kuali.kra.subaward.dao;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.apache.ojb.broker.accesslayer.LookupException;
import org.kuali.kra.award.home.Award;
import org.kuali.kra.subaward.bo.SubAward;
import org.kuali.kra.subaward.bo.SubAwardFundingSource;
import org.springframework.dao.DataAccessException;

public interface SubAwardFundingSourceDao {
    
    /**
    *
    * This method finds all the linked Awards through SFS for a SubAward
    * It returns only the 'Active' versions of the corresponding Awards. 
    * Each linked Award is returned only once.
    * @return List<SubAward>
    */
    Collection<Award> getLinkedAwards(SubAward subAward) throws SQLException, LookupException;
    
    
    /**
     * 
    * This method finds all the linked SubAward Ids through SubAwardFundingSource for an Award
    * It returns only the 'Active' versions of the corresponding SubAwards (no 'Pending', 'Archived' etc).
    * Each linked SubAward is returned only once.
    * @return List<SubAward>
    */
    List<String> getLinkedSubAwardsIds(Award award) throws SQLException, LookupException;

    
    /**
     * Updates all the SFSs with the same AwardNumber and SubawardCode to be inactive
     * 
     * */
    void deleteSubAwardFundingSource(SubAwardFundingSource sfs) throws SQLException, LookupException;


    /**
     * Updates all the SFSs with the same AwardNumber and SubawardCode to be inactive
     * 
     * */
    void deleteSubAwardFundingSource(String awardNumber, String subAwardcode) throws DataAccessException;


    

}
