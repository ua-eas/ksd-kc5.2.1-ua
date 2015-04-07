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
public class FinEntitiesDataMatrixMaintenanceDocumentRuleTest extends MaintenanceRuleTestBase {
    
    private static final String COLUMN_SORT_ID_FIELD_NAME = "columnSortId";
    private static final String GROUP_ID_FIELD_NAME = "dataGroupId";
    
    private static final Integer GROUP_ID_1 = 1;
    private static final Integer SORT_ID_1 = 1;
    private static final String COLUMN_NAME = "Test";
    private static final String COLUMN_NAME_1 = "Test1";
    
    private FinEntitiesDataMatrixMaintenanceDocumentRule rule;
    
    private Mockery context = new JUnit4Mockery();
    
    @Before
    public void setUp() throws Exception {
        super.setUp();
        
        RoleTestHelper roleTestHelper = new RoleTestHelper();
        roleTestHelper.createRole(RoleFixture.COI_MAINTAINER);
        Person jtester = KraServiceLocator.getService(PersonService.class).getPerson(PersonFixture.UAR_TEST_002.getPrincipalId());
        roleTestHelper.addPersonToRole(jtester, RoleFixture.COI_MAINTAINER);
        
        GlobalVariables.setUserSession(new UserSession(jtester.getPrincipalName()));
        
        rule = new FinEntitiesDataMatrixMaintenanceDocumentRule();
    }

    @After
    public void tearDown() throws Exception {
        rule = null;
        
        super.tearDown();
    }
    
    @Test
    public void testUniqueOK() throws Exception {
        MaintenanceDocument finEntitiesDataMatrixMaintenanceDocument = getFinEntitiesDataMatrixMaintenanceDocument(COLUMN_NAME, SORT_ID_1, GROUP_ID_1);
        rule.setBusinessObjectService(getMockBusinessObjectService());
       
        assertTrue(rule.processCustomRouteDocumentBusinessRules(finEntitiesDataMatrixMaintenanceDocument));
        assertTrue(rule.processCustomApproveDocumentBusinessRules(finEntitiesDataMatrixMaintenanceDocument));
    }
    
    @Test
    public void testUniqueNotOK() throws Exception {
        rule.setBusinessObjectService(getMockBusinessObjectService1());
        MaintenanceDocument finEntitiesDataMatrixMaintenanceDocument = getFinEntitiesDataMatrixMaintenanceDocument(COLUMN_NAME_1, SORT_ID_1, GROUP_ID_1);
        
        assertFalse(rule.processCustomRouteDocumentBusinessRules(finEntitiesDataMatrixMaintenanceDocument));
        assertFalse(rule.processCustomApproveDocumentBusinessRules(finEntitiesDataMatrixMaintenanceDocument));
    }
    
    
    private MaintenanceDocument getFinEntitiesDataMatrixMaintenanceDocument(String columnName, Integer sortId, Integer groupId) throws Exception {
        FinEntitiesDataMatrix finEntitiesDataMatrix = new FinEntitiesDataMatrix();
        finEntitiesDataMatrix.setColumnName(columnName);
        finEntitiesDataMatrix.setColumnSortId(sortId);
        finEntitiesDataMatrix.setDataGroupId(groupId);
        
        return newMaintDoc(finEntitiesDataMatrix);
    }
    
    
    private BusinessObjectService getMockBusinessObjectService() {
        final BusinessObjectService service = context.mock(BusinessObjectService.class);
        
        context.checking(new Expectations() {{
            Map<String, Object> fieldValues1 = new HashMap<String, Object>();
            fieldValues1.put(COLUMN_SORT_ID_FIELD_NAME,  SORT_ID_1);
            
            FinEntitiesDataMatrix finEntitiesDataMatrix = new FinEntitiesDataMatrix();
            finEntitiesDataMatrix.setColumnName(COLUMN_NAME);
            finEntitiesDataMatrix.setDataGroupId(GROUP_ID_1);
            allowing(service).findMatching(FinEntitiesDataMatrix.class, fieldValues1);
            will(returnValue(Collections.singleton(finEntitiesDataMatrix)));
        }});
        
        context.checking(new Expectations() {{
            Map<String, Object> fieldValues = new HashMap<String, Object>();
            fieldValues.put(GROUP_ID_FIELD_NAME, GROUP_ID_1);
            
            allowing(service).countMatching(FinEntitiesDataGroup.class, fieldValues);
            will(returnValue(1));
        }});
        
        return service;
    }
    private BusinessObjectService getMockBusinessObjectService1() {
        final BusinessObjectService service = context.mock(BusinessObjectService.class);
        
        context.checking(new Expectations() {{
            Map<String, Object> fieldValues1 = new HashMap<String, Object>();
            fieldValues1.put(COLUMN_SORT_ID_FIELD_NAME,  SORT_ID_1);
            
            FinEntitiesDataMatrix finEntitiesDataMatrix = new FinEntitiesDataMatrix();
            finEntitiesDataMatrix.setColumnName(COLUMN_NAME);
            finEntitiesDataMatrix.setDataGroupId(GROUP_ID_1);
            allowing(service).findMatching(FinEntitiesDataMatrix.class, fieldValues1);
            will(returnValue(Collections.singleton(finEntitiesDataMatrix)));
        }});
        
        context.checking(new Expectations() {{
            Map<String, Object> fieldValues = new HashMap<String, Object>();
            fieldValues.put(GROUP_ID_FIELD_NAME, GROUP_ID_1);
            
            allowing(service).countMatching(FinEntitiesDataGroup.class, fieldValues);
            will(returnValue(0));
        }});
        
        return service;
    }

}
