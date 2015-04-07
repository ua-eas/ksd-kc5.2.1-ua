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
package org.kuali.kra.rules;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kra.bo.Unit;
import org.kuali.kra.infrastructure.KeyConstants;
import org.kuali.kra.maintenance.MaintenanceRuleTestBase;
import org.kuali.kra.test.fixtures.OrgFixture;
import org.kuali.kra.test.fixtures.UnitFixture;
import org.kuali.kra.test.helpers.OrgTestHelper;
import org.kuali.kra.test.helpers.UnitTestHelper;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.util.ErrorMessage;
import org.kuali.rice.krad.util.GlobalVariables;
import org.springframework.util.AutoPopulatingList;

@SuppressWarnings("deprecation")
public class UnitMaintenanceDocumentRuleTest extends MaintenanceRuleTestBase {
    private UnitMaintenanceDocumentRule rule = null;


    @Before
    public void setUp() throws Exception {
        super.setUp();
        rule = new UnitMaintenanceDocumentRule();
        
        UnitTestHelper unitTestHelper = new UnitTestHelper();
        unitTestHelper.createUnit(UnitFixture.TEST_3);
        
        OrgTestHelper orgTestHelper = new OrgTestHelper();
        orgTestHelper.createOrg(OrgFixture.ONE);
    }

    @After
    public void tearDown() throws Exception {
        rule = null;
        super.tearDown();
    }

    @Test
    public void testOK() throws Exception {

        Unit unit=new Unit();
        String unitNumber= UnitFixture.TEST_3.getUnitName();
        unit.setUnitName(unitNumber);
        unit.setUnitNumber(UnitFixture.TEST_3.getUnitNumber());
        unit.setParentUnitNumber(UnitFixture.TEST_3.getUnitNumber());
        unit.setOrganizationId(OrgFixture.ONE.getOrgId());
        MaintenanceDocument unitmaintenancedocument = newMaintDoc(unit);
        assertTrue(rule.processCustomApproveDocumentBusinessRules(unitmaintenancedocument));
    }

    @SuppressWarnings("rawtypes")
	@Test
    public void testMoveUnitOwnDescendant() throws Exception{
        Unit unit=new Unit();
        unit.setUnitName(UnitFixture.TEST_3.getUnitName());
        unit.setUnitNumber(UnitFixture.TEST_3.getUnitNumber());
        unit.setParentUnitNumber(UnitFixture.TEST_3.getParentUnitNumber());
        unit.setOrganizationId(OrgFixture.ONE.getOrgId());
        MaintenanceDocument unitmaintenancedocument = newMaintDoc(unit);
        assertFalse(rule.processCustomRouteDocumentBusinessRules(unitmaintenancedocument));
        AutoPopulatingList errors = GlobalVariables.getMessageMap().getMessages("ddocument.newMaintainableObject.parentUnitNumber");
        errors = GlobalVariables.getMessageMap().getMessages("document.newMaintainableObject.parentUnitNumber");
        assertTrue(errors.size() == 1);
        ErrorMessage message = (ErrorMessage) errors.get(0);
        assertEquals(message.getErrorKey(), KeyConstants.MOVE_UNIT_OWN_DESCENDANTS);
    }

}


