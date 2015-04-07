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
package org.kuali.kra.proposaldevelopment.lookup.keyvalue;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.core.api.util.KeyValue;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class ProposalColumnsValuesFinderTest {
    
    private ProposalColumnsValuesFinder finder = null;

    @Before
    public void setUp() throws Exception {
        finder = new ProposalColumnsValuesFinder();
    }
    
    @Test
    public void testProposalColumnsValuesFinderRuns() {
        List<KeyValue> values = finder.getKeyValues();
        assertTrue(values.size() > 0);
    }

    @After
    public void tearDown() throws Exception {
    }

}
