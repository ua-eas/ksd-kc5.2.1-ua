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
package org.kuali.kra.coi.personfinancialentity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.kra.maintenance.MaintenanceRuleTestBase;
import org.kuali.kra.test.fixtures.PersonFixture;
import org.kuali.kra.test.fixtures.RoleFixture;
import org.kuali.kra.test.helpers.RoleTestHelper;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;

@SuppressWarnings("deprecation")
public class FinEntitiesDataGroupMaintenanceDocumentRuleTest extends MaintenanceRuleTestBase {
    
    private static final String GROUP_SORT_ID_FIELD_NAME = "dataGroupSortId";
    private static final String GROUP_NAME_FIELD_NAME = "dataGroupName";
    
    private static final Integer GROUP_ID_1 = 1;
    private static final Integer GROUP_ID_2 = 2;
    private static final Integer SORT_ID_1 = 1;
   private static final String GROUP_NAME = "Test";
    
    private FinEntitiesDataGroupMaintenanceDocumentRule rule;
    
    private Mockery context = new JUnit4Mockery();
    
    @Before
    public void setUp() throws Exception {
        super.setUp();
        
        RoleTestHelper roleTestHelper = new RoleTestHelper();
        roleTestHelper.createRole(RoleFixture.COI_MAINTAINER);
        Person jtester = KraServiceLocator.getService(PersonService.class).getPerson(PersonFixture.UAR_TEST_002.getPrincipalId());
        roleTestHelper.addPersonToRole(jtester, RoleFixture.COI_MAINTAINER);
        GlobalVariables.setUserSession(new UserSession(jtester.getPrincipalName()));
        
        rule = new FinEntitiesDataGroupMaintenanceDocumentRule();
        rule.setBusinessObjectService(getMockBusinessObjectService());
    }

    @After
    public void tearDown() throws Exception {
        rule = null;
        
        super.tearDown();
    }
    
    @Test
    public void testUniqueOK() throws Exception {
        MaintenanceDocument finEntitiesDataGroupMaintenanceDocument = getFinEntitiesDataGroupMaintenanceDocument(GROUP_ID_1, GROUP_NAME, SORT_ID_1);
        
        assertTrue(rule.processCustomRouteDocumentBusinessRules(finEntitiesDataGroupMaintenanceDocument));
        assertTrue(rule.processCustomApproveDocumentBusinessRules(finEntitiesDataGroupMaintenanceDocument));
    }
    
    @Test
    public void testUniqueNotOK() throws Exception {
        MaintenanceDocument finEntitiesDataGroupMaintenanceDocument = getFinEntitiesDataGroupMaintenanceDocument(GROUP_ID_2, GROUP_NAME, SORT_ID_1);
        
        assertFalse(rule.processCustomRouteDocumentBusinessRules(finEntitiesDataGroupMaintenanceDocument));
        assertFalse(rule.processCustomApproveDocumentBusinessRules(finEntitiesDataGroupMaintenanceDocument));
    }
    
    
    private MaintenanceDocument getFinEntitiesDataGroupMaintenanceDocument(Integer finEntitiesDataGroupId, String groupName, Integer sortId) throws Exception {
        FinEntitiesDataGroup finEntitiesDataGroup = new FinEntitiesDataGroup();
        finEntitiesDataGroup.setDataGroupId(finEntitiesDataGroupId);
        finEntitiesDataGroup.setDataGroupName(groupName);
        finEntitiesDataGroup.setDataGroupSortId(sortId);
        
        return newMaintDoc(finEntitiesDataGroup);
    }
    
    
    private BusinessObjectService getMockBusinessObjectService() {
        final BusinessObjectService service = context.mock(BusinessObjectService.class);
        
        context.checking(new Expectations() {{
            Map<String, Object> fieldValues1 = new HashMap<String, Object>();
            fieldValues1.put(GROUP_SORT_ID_FIELD_NAME, SORT_ID_1);
            
            FinEntitiesDataGroup finEntitiesDataGroup = new FinEntitiesDataGroup();
            finEntitiesDataGroup.setDataGroupId(GROUP_ID_1);
            
            allowing(service).findMatching(FinEntitiesDataGroup.class, fieldValues1);
            will(returnValue(Collections.singleton(finEntitiesDataGroup)));
        }});
        
        context.checking(new Expectations() {{
            Map<String, String> fieldValues = new HashMap<String, String>();
            fieldValues.put(GROUP_NAME_FIELD_NAME, GROUP_NAME);
            FinEntitiesDataGroup finEntitiesDataGroup = new FinEntitiesDataGroup();
            finEntitiesDataGroup.setDataGroupId(GROUP_ID_1);
            
            allowing(service).findMatching(FinEntitiesDataGroup.class, fieldValues);
            will(returnValue(Collections.singleton(finEntitiesDataGroup)));
        }});
        
        return service;
    }

}
