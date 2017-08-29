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
package org.kuali.kra.subaward.service;

import org.kuali.kra.award.home.Award;
import org.kuali.kra.bo.versioning.VersionStatus;
import org.kuali.kra.service.VersionException;
import org.kuali.kra.subaward.bo.SubAward;
import org.kuali.kra.subaward.bo.SubAwardFundingSource;
import org.kuali.kra.subaward.document.SubAwardDocument;
import org.kuali.rice.kew.api.exception.WorkflowException;

import java.sql.Date;
import java.util.Collection;
import java.util.List;

/**
 * This class represents SubAwardService...
 */
public interface SubAwardService {

    /**.
     * Create new version of the Subaward document
     * @param subAwardDocument
     * @return
     * @throws VersionException
     */
    public SubAwardDocument createNewSubAwardVersion(
    SubAwardDocument subAwardDocument) throws VersionException,
    WorkflowException;

    /**.
     * Update the subaward to use the new VersionStatus.
     *  If the version status is ACTIVE, any other
     *  active version of this
     * subAward will be set to ARCHIVED.
     * @param subAward
     * @param status
     */
    public void updateSubAwardSequenceStatus(
    SubAward subAward, VersionStatus status);

    /**
     * This method returns an unused SubAwardCode.
     * @return
     */
    String getNextSubAwardCode();

    /**
     * This method will add AmountInfo details to subaward.
     * @param subAward
     * @return
     */
    public SubAward getAmountInfo(SubAward subAward);

     /**.
      * 
      * This method returns the value of the parameter 'Subaward Follow Up'.
      * @return
      */
     public String getFollowupDateDefaultLength();

     /**
      * 
      * This method calculates a follow date based on
      * getFollowupDateDefaultLength and the passed in baseDate.
      * @param baseDate
      * @return
      */
     public Date getCalculatedFollowupDate(Date baseDate);
     
     /**
      * 
      * This method returns a formatted Date string based on the base date.
      * @param baseDate
      * @return
      */
     public String getCalculatedFollowupDateForAjaxCall(String baseDate);

     /**
      *
      * This method calls getFollowupDateDefaultLength
      *  translates the value into days or weeks,
      *   and then returns the value in days.
      * @return
      */
     public int getFollowupDateDefaultLengthInDays();
     
     
     /**
     *
     * This method returns the Active Version for the SubAward specified with the subAwardCode
     * (using the VersionHistory service)
     */
     SubAward getActiveSubAward(Long subAwardCode);

     /**
     *
     * This method finds all the linked SubAwards through SubAwardFundingSource for an Award
     * It returns only the 'Active' versions of the corresponding SubAwards. Each linked Subaward is returned only once.
     * @return List<SubAward>
     */
     public Collection<SubAward> getLinkedSubAwards(Award award);

    /**
     *
     * This method finds all the linked SubAward Ids through SubAwardFundingSource for an Award identified by the given awardNumber
     * It returns only the 'Active' or 'Pending' versions of the corresponding SubAwards (no 'Archived' etc).
     * Each linked SubAward is returned only once.
     * @return List<SubAward>
     */
    public Collection<String> getLinkedSubAwardsIds(String awardNumber);
     
     /**
     *
     * This method finds all the linked Awards through SFS for a SubAward
     * It returns only the 'Active' versions of the corresponding Awards. Each linked Award is returned only once.
     * @return List<SubAward>
     */
     public Collection<Award> getLinkedAwards(SubAward subAward);
     
     /**
     *
     * This method finds all the SubAwardFundingSources in the DB marked with the same SubAward code and Award Number and
     * sets the active flag to false for all of them.
     * 
     */
     public void deleteSubAwardFundingSource(SubAwardFundingSource sfs);
     
     /**
     *
     * This method finds all the SubAwardFundingSources in the DB marked with the same SubAward code and Award Number and
     * sets the active flag to false for all of them.
     * 
     */
     public void deleteSubAwardFundingSource(String awardNumber, String subAwardCode);
     
     /**
     *
     * This method returns all the ACTIVE SubAwardFundingSources related to a certain Award by AwardNumber
     */
     public Collection<SubAwardFundingSource> getActiveSubAwardFundingSources(Award award);
     
     
     /**
     *
     * This method returns all the ACTIVE SubAwardFundingSources related to a certain SubAward by SubAwardCode
     * 
     */
     public Collection<SubAwardFundingSource> getActiveSubAwardFundingSources(SubAward subAward);

     
     
}