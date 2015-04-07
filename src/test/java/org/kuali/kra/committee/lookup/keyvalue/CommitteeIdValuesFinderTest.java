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
package org.kuali.kra.committee.lookup.keyvalue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.kuali.kra.committee.bo.Committee;
import org.kuali.kra.common.committee.bo.CommitteeBase;
import org.kuali.kra.test.infrastructure.KcUnitTestBase;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;

@SuppressWarnings("rawtypes")
public class CommitteeIdValuesFinderTest extends KcUnitTestBase {

    private static final String CMT_1_ID = "c1";
    private static final String CMT_2_ID = "c2";
    private static final String CMT_3_ID = "c3";
    private static final String CMT_4_ID = "c4";
    
    private static final String C1_LATEST_NAME = "c1Latest";
    private static final String C2_LATEST_NAME = "c2Latest";
    private static final String C3_LATEST_NAME = "c3Latest";
    private static final String C4_LATEST_NAME = "c4Latest";
    
    /**
     * This method is to look for IRB specific committee.
     */
	@Test
    public void testGetActiveCommittees(){
    	
    	// Gather all rows, and use PK to determine number of active committees
    	Set<String> uniqueCommitteeNames = new HashSet<String>();
    	Collection<Committee> committeeRows = getBusinessObjectService().findAll(Committee.class);
    	for(Committee committee : committeeRows) {
    		uniqueCommitteeNames.add(committee.getCommitteeName());
    	}
    	
    	
        CommitteeIdValuesFinder finder = new CommitteeIdValuesFinder();
        finder.setBusinessObjectService(getBusinessObjectService());
        List<CommitteeBase> results = finder.getActiveCommittees();
        Assert.assertEquals(uniqueCommitteeNames.size(), results.size());
    }

    // NOTE: this method tests with an empty exclusion list of committees, 
    // it's not quite clear in what context(s) would this list be non-empty
    @Test
    public void testGetKeyValues(){
        Committee committee1 = new Committee();
        committee1.setCommitteeId(CMT_1_ID);
        committee1.setCommitteeName(CommitteeIdValuesFinderTest.C1_LATEST_NAME);
        
        Committee committee2 = new Committee();
        committee2.setCommitteeId(CMT_2_ID);
        committee2.setCommitteeName(CommitteeIdValuesFinderTest.C2_LATEST_NAME);
        
        Committee committee3 = new Committee();
        committee3.setCommitteeId(CMT_3_ID);
        committee3.setCommitteeName(C3_LATEST_NAME);
        
        Committee committee4 = new Committee();
        committee4.setCommitteeId(CMT_4_ID);
        committee4.setCommitteeName(C4_LATEST_NAME);
        
        final List<CommitteeBase> activeCommittees = new ArrayList<CommitteeBase>();
        activeCommittees.add(committee1);
        activeCommittees.add(committee2);
        activeCommittees.add(committee3);
        activeCommittees.add(committee4);
        
        KeyValue klp0 = new ConcreteKeyValue("", "select");
        KeyValue klp1 = new ConcreteKeyValue(CMT_1_ID, C1_LATEST_NAME);
        KeyValue klp2 = new ConcreteKeyValue(CMT_2_ID, C2_LATEST_NAME);
        KeyValue klp3 = new ConcreteKeyValue(CMT_3_ID, C3_LATEST_NAME);
        KeyValue klp4 = new ConcreteKeyValue(CMT_4_ID, C4_LATEST_NAME);
       
        // create an anonymous instance of the finder overriding the
        // getActiveCommittees() method, with our mock
        CommitteeIdValuesFinder finder = new CommitteeIdValuesFinder(){
			private static final long serialVersionUID = -4474244366618062358L;

			@Override
            public List<CommitteeBase> getActiveCommittees() {
                return activeCommittees;
            }
        };
        List<KeyValue> results = finder.getKeyValues();
        
        Assert.assertEquals(5, results.size());
        Assert.assertTrue(results.contains(klp0));
        Assert.assertTrue(results.contains(klp1));
        Assert.assertTrue(results.contains(klp2));
        Assert.assertTrue(results.contains(klp3));
        Assert.assertTrue(results.contains(klp4));
        
    }
    
}
