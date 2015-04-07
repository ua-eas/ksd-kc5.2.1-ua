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
package org.kuali.kra.subaward.document.authorization;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.kra.infrastructure.TaskName;
import org.kuali.kra.service.TaskAuthorizationService;
import org.kuali.kra.subaward.bo.SubAward;
import org.kuali.kra.subaward.document.SubAwardDocument;
import org.kuali.kra.test.fixtures.PersonFixture;
import org.kuali.kra.test.fixtures.RoleFixture;
import org.kuali.kra.test.helpers.RoleTestHelper;
import org.kuali.kra.test.infrastructure.KcUnitTestBase;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;

public class TestSubAwardAuthorizersTest extends KcUnitTestBase {
    
    TaskAuthorizationService taskAuthorizationService;
    Person quickstart;
    Person jtester;
    Person woods;
    Person ospAdmin;
    

    @Before
    public void setUp() throws Exception {
    	super.setUp();
        taskAuthorizationService = KraServiceLocator.getService(TaskAuthorizationService.class);
        
        PersonService personService = getService(PersonService.class);
        quickstart = personService.getPersonByPrincipalName((PersonFixture.UAR_TEST_001.getPrincipalName()));
        jtester = personService.getPersonByPrincipalName((PersonFixture.UAR_TEST_002.getPrincipalName()));
        woods = personService.getPersonByPrincipalName((PersonFixture.UAR_TEST_003.getPrincipalName()));
        ospAdmin = personService.getPersonByPrincipalName((PersonFixture.UAR_TEST_004.getPrincipalName()));
        
        RoleTestHelper roleHelper = new RoleTestHelper();
    	roleHelper.addPersonToRole(quickstart, RoleFixture.SUBAWARD_MODIFIER);
    	roleHelper.addPersonToRole(ospAdmin, RoleFixture.SUPER_USER);
    	roleHelper.addPersonToRole(jtester, RoleFixture.SUPER_USER);
    	roleHelper.addPersonToRole(woods, RoleFixture.SUPER_USER);
    }

    @After
    public void tearDown() throws Exception {
        taskAuthorizationService = null;
        quickstart = null;
        jtester = null;
        woods = null;
        ospAdmin = null;
    }
    
  
  
    
    @Test
    public void testModifySubAuthorizer()  throws WorkflowException {
        
        SubAwardDocument subAwardDoc = getSubAwardDocument(); 
        
        SubAwardTask task = new SubAwardTask(TaskName.MODIFY_SUBAWARD, subAwardDoc);

        boolean retVal = taskAuthorizationService.isAuthorized(quickstart.getPrincipalId(), task);
        assertTrue(retVal);
        
        retVal = taskAuthorizationService.isAuthorized(jtester.getPrincipalId(), task);
        assertFalse(retVal);
        
        retVal = taskAuthorizationService.isAuthorized(woods.getPrincipalId(), task);
        assertFalse(retVal);
        
        retVal = taskAuthorizationService.isAuthorized(ospAdmin.getPrincipalId(), task);
        assertFalse(retVal);
    }
    
   
   
    
  @Test
    public void testViewSubAuthorizer()  throws WorkflowException{
        SubAwardDocument subAwardDoc = getSubAwardDocument(); 
        SubAwardTask task = new SubAwardTask(TaskName.VIEW_SUBAWARD,  subAwardDoc);
        boolean retVal = taskAuthorizationService.isAuthorized(quickstart.getPrincipalId(), task);
        assertTrue(retVal);
        
        retVal = taskAuthorizationService.isAuthorized(jtester.getPrincipalId(), task);
        assertFalse(retVal);
        
        retVal = taskAuthorizationService.isAuthorized(woods.getPrincipalId(), task);
        assertFalse(retVal);
        
        retVal = taskAuthorizationService.isAuthorized(ospAdmin.getPrincipalId(), task);
        assertFalse(retVal);
    }
    
    private SubAwardDocument getSubAwardDocument() throws WorkflowException {
        SubAwardDocument document = (SubAwardDocument) getDocumentService().getNewDocument(SubAwardDocument.class);
        document.getDocumentHeader().setDocumentDescription("SubcontractDocumentTest test doc");
        SubAward subAward = new SubAward();
        subAward.setOrganizationId("000001");
        subAward.setSubAwardTypeCode(229);
        subAward.setSubAwardCode("7687");
        subAward.setStatusCode(123);
        subAward.setPurchaseOrderNum("111");
        subAward.setRequisitionerId("1");
        
        document.setSubAward(subAward);
        getDocumentService().saveDocument(document);
        return document;
    }
    
   
}