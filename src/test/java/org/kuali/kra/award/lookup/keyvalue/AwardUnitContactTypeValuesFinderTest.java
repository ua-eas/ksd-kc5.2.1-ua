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
package org.kuali.kra.award.lookup.keyvalue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kra.bo.UnitAdministratorType;
import org.kuali.kra.test.infrastructure.KcUnitTestBase;
import org.kuali.rice.core.api.util.KeyValue;

import java.util.List;

public class AwardUnitContactTypeValuesFinderTest extends KcUnitTestBase {

    protected AwardUnitContactTypeValuesFinder valuesFinder;
    
    @Before
    public void setUp() throws Exception {
        super.setUp();
        valuesFinder = new AwardUnitContactTypeValuesFinder();
    }
    
    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }
    
    @Test
    public void testValuesFinder() throws Exception {
        List<KeyValue> keyLabels = valuesFinder.getKeyValues();
        assertFalse(keyLabels.isEmpty());
        for (KeyValue pair : keyLabels) {
            UnitAdministratorType contactType = getBusinessObjectService().findBySinglePrimaryKey(UnitAdministratorType.class, pair.getKey());
            assertEquals("U", contactType.getDefaultGroupFlag());
        }
    }
}
