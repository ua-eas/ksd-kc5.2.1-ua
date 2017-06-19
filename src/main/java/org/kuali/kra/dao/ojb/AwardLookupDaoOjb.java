/*
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.kuali.kra.dao.ojb;

import org.kuali.kra.award.home.Award;
import org.kuali.kra.bo.versioning.VersionStatus;
import org.kuali.kra.dao.AwardLookupDao;
import org.kuali.kra.dao.VersionHistoryLookupDao;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.dao.impl.LookupDaoOjb;

import java.util.List;
import java.util.Map;

@SuppressWarnings( "unchecked" )
public class AwardLookupDaoOjb extends LookupDaoOjb implements AwardLookupDao {
	private VersionHistoryLookupDao versionHistoryLookupDao;

	@SuppressWarnings( "rawtypes" )
	@Override
	public List<? extends BusinessObject> getAwardSearchResults( Map fieldValues, boolean usePrimaryKeys ) {
		//only search for 'active' versions of the award, avoiding the post-search filtering
		fieldValues.put("awardSequenceStatus", VersionStatus.ACTIVE.name());

		return versionHistoryLookupDao.getSequenceOwnerSearchResults( Award.class, fieldValues, usePrimaryKeys );
	}

	public void setVersionHistoryLookupDao( VersionHistoryLookupDao versionHistoryLookupDao ) {
		this.versionHistoryLookupDao = versionHistoryLookupDao;
	}

}
