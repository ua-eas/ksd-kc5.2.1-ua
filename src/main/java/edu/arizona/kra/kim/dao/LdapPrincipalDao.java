/**
 * Copyright 2005-2014 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.arizona.kra.kim.dao;

import java.util.List;
import java.util.Map;

import org.kuali.rice.kim.api.identity.entity.EntityDefault;


/**
 * Interface class with public methods for accessing LDAP data store for entity information
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface LdapPrincipalDao extends org.kuali.rice.kim.dao.LdapPrincipalDao {
	
	/**
	 * This method allows IdentityService access this functionality, which creates the
	 * possibility for a service to go to LDAP instead of KIM tables.
	 * 
	 * @param searchCriteria A map of properties/values that will be used in an LDAP search.
	 * @param unbounded This is moot, since the LDAP service uses a global search result limit.
	 * @return a list of results from LDAP determined by the passed in criteria.
	 */
    List<EntityDefault> lookupEntityDefault(Map<String,String> searchCriteria, boolean unbounded);
}
