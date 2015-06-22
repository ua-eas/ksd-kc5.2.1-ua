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
package org.kuali.kra.award.contacts;

import org.kuali.kra.award.home.ContactRole;
import org.kuali.kra.award.home.ContactType;
import org.kuali.kra.award.home.ContactUsage;
import org.kuali.kra.bo.CoeusModule;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.service.BusinessObjectService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class finds Award Unit Contact Project Roles.
 */
public class AwardSponsorContactProjectRolesValuesFinder extends AwardContactsProjectRoleValuesFinder {

    /**
     * {@inheritDoc}
     * @see org.kuali.kra.award.contacts.AwardContactsProjectRoleValuesFinder#getKeyValues()
     */
    @Override
    @SuppressWarnings("unchecked")
    public List getKeyValues() {
        return buildKeyValues(getKeyValuesService().findAllOrderBy(getRoleType(), "description", true));
    }
    
    @Override
    protected Class<? extends ContactRole> getRoleType() {
        return ContactType.class;
    }
    
    @Override
    protected List<KeyValue> buildKeyValues(Collection<? extends ContactRole> contactRoles) {
    	
    	 //first find the contact usage objects associated with the award module
        BusinessObjectService boService = KraServiceLocator.getService(BusinessObjectService.class);
        Map<String, String> criteriaMap = new HashMap<String, String>();
        criteriaMap.put("moduleCode", CoeusModule.AWARD_MODULE_CODE);
        
        Collection<ContactUsage> contactUsageList = (List<ContactUsage>) boService.findMatching( ContactUsage.class, criteriaMap);
    	
    	
        List<KeyValue> keyValues = new ArrayList<KeyValue>();
        addEmptyKeyValuePair(keyValues);
        for (ContactRole role : contactRoles) {
        	for (ContactUsage contactUsage : contactUsageList) {
        		if(contactUsage.getContactTypeCode().equalsIgnoreCase(role.getRoleCode())) {
        			keyValues.add(new ConcreteKeyValue(role.getRoleCode(), role.getRoleDescription()));
        			break;
        		}
        	}
        }
        return keyValues;
    }
}
