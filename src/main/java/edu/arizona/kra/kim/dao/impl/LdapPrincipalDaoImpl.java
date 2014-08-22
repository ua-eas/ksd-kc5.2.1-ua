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
package edu.arizona.kra.kim.dao.impl;

import java.util.List;
import java.util.Map;

import org.kuali.rice.kim.api.identity.entity.EntityDefault;

import edu.arizona.kra.kim.dao.LdapPrincipalDao;

/**
 * Integrated Data Access via LDAP to EDS. Provides implementation to interface method
 * for using Spring-LDAP to communicate with EDS.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LdapPrincipalDaoImpl extends org.kuali.rice.kim.dao.impl.LdapPrincipalDaoImpl implements LdapPrincipalDao { 

	public List<EntityDefault> lookupEntityDefault(Map<String,String> searchCriteria, boolean unbounded) {
        Map<String, Object> criteria = getLdapLookupCriteria(searchCriteria);
        List<EntityDefault> results = search(EntityDefault.class, criteria);
        return results;
    }

}
