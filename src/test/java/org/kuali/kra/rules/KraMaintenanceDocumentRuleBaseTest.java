/*
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.osedu.org/licenses/ECL-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kra.rules;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kra.budget.core.CostElement;
import org.kuali.kra.maintenance.MaintenanceRuleTestBase;
import org.kuali.kra.test.fixtures.CostElementFixture;
import org.kuali.kra.test.fixtures.PersonFixture;
import org.kuali.kra.test.helpers.CostElementTestHelper;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.util.GlobalVariables;

import java.util.HashMap;
import java.util.Map;

public class KraMaintenanceDocumentRuleBaseTest extends MaintenanceRuleTestBase {
    private KraMaintenanceDocumentRuleBase rule = null;
    Person quickstart;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        rule = new KraMaintenanceDocumentRuleBase();
        PersonService personService = getService(PersonService.class);
        quickstart = personService.getPersonByPrincipalName((PersonFixture.UAR_TEST_001.getPrincipalName()));
        GlobalVariables.setUserSession(new UserSession(quickstart.getPrincipalName()));
        
        CostElementTestHelper costElementTestHelper = new CostElementTestHelper();
        costElementTestHelper.createCostElement(CostElementFixture.TEST_1);
    }

    @After
    public void tearDown() throws Exception {
        rule = null;
        super.tearDown();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
    public void testOK() throws Exception {

        
        Map pkMap = new HashMap();
        pkMap.put("costElement", CostElementFixture.TEST_1.getCostElement());
        assertTrue(rule.checkExistenceFromTable(CostElement.class, pkMap, "costElement", "Cost Element"));
    }

    /**
     * 
     * This method to test rate type does not exist.
     * @throws Exception
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
    public void testNotExist() throws Exception {

        Map pkMap = new HashMap();
        pkMap.put("costElement", "1x");
        assertFalse(rule.checkExistenceFromTable(CostElement.class, pkMap, "costElement", "Cost Element"));

    }


}
