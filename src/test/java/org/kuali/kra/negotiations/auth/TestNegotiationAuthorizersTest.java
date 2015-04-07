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
package org.kuali.kra.negotiations.auth;


import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kra.authorization.ApplicationTask;
import org.kuali.kra.bo.Unit;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.kra.infrastructure.TaskName;
import org.kuali.kra.negotiations.bo.Negotiation;
import org.kuali.kra.negotiations.bo.NegotiationAgreementType;
import org.kuali.kra.negotiations.bo.NegotiationAssociationType;
import org.kuali.kra.negotiations.bo.NegotiationStatus;
import org.kuali.kra.negotiations.bo.NegotiationUnassociatedDetail;
import org.kuali.kra.negotiations.document.NegotiationDocument;
import org.kuali.kra.service.TaskAuthorizationService;
import org.kuali.kra.test.fixtures.PersonFixture;
import org.kuali.kra.test.fixtures.RoleFixture;
import org.kuali.kra.test.helpers.RoleTestHelper;
import org.kuali.kra.test.infrastructure.KcUnitTestBase;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.krad.service.BusinessObjectService;


public class TestNegotiationAuthorizersTest extends KcUnitTestBase {
    
    TaskAuthorizationService taskAuthorizationService;
    BusinessObjectService businessObjectService;  
    Person quickstart;
    Person jtester;
    Person woods;
    Person ospAdmin;
    Person negotiator;
    

    @Before
    public void setUp() throws Exception {
        super.setUp();
    	
    	taskAuthorizationService = KraServiceLocator.getService(TaskAuthorizationService.class);
        businessObjectService = KraServiceLocator.getService(BusinessObjectService.class);

        PersonService personService = getService(PersonService.class);
        quickstart = personService.getPersonByPrincipalName((PersonFixture.UAR_TEST_001.getPrincipalName()));
        jtester = personService.getPersonByPrincipalName((PersonFixture.UAR_TEST_002.getPrincipalName()));
        woods = personService.getPersonByPrincipalName((PersonFixture.UAR_TEST_003.getPrincipalName()));
        ospAdmin = personService.getPersonByPrincipalName((PersonFixture.UAR_TEST_004.getPrincipalName()));
        negotiator = personService.getPersonByPrincipalName((PersonFixture.UAR_TEST_005.getPrincipalName()));

    	// Set permissions
        RoleTestHelper roleHelper = new RoleTestHelper();
    	roleHelper.addPersonToRole(quickstart, RoleFixture.NEGOTIATION_ADMIN);
    	roleHelper.addPersonToRole(ospAdmin, RoleFixture.NEGOTIATION_INVESTIGATORS);
    	roleHelper.addPersonToRole(jtester, RoleFixture.NEGOTIATION_CREATOR);
    	roleHelper.addPersonToRole(negotiator, RoleFixture.NEGOTIATION_NEGOTIATOR);

    }

    @After
    public void tearDown() throws Exception {
        taskAuthorizationService = null;
        businessObjectService = null;
        quickstart = null;
        jtester = null;
        woods = null;
        ospAdmin = null;
    }

    @Test
    public void testCreateNegotiationAuthorizer() throws WorkflowException {
        ApplicationTask task = new ApplicationTask(TaskName.NEGOTIATION_CREATE_NEGOTIATION);

        boolean retVal = taskAuthorizationService.isAuthorized(quickstart.getPrincipalId(), task);
        assertTrue(retVal);
        
        retVal = taskAuthorizationService.isAuthorized(jtester.getPrincipalId(), task);
        assertTrue(retVal);

        retVal = taskAuthorizationService.isAuthorized(woods.getPrincipalId(), task);
        assertFalse(retVal);

        retVal = taskAuthorizationService.isAuthorized(ospAdmin.getPrincipalId(), task);
        assertFalse(retVal);
    }
    
    @Test
    public void testModifyNegotiationAuthorizer()  throws WorkflowException {
        NegotiationDocument negotiationDoc = getNewNegotiationWithUnassociatedDetail();
        NegotiationTask task = new NegotiationTask(TaskName.NEGOTIATION_MODIFIY_NEGOTIATION, negotiationDoc);

        boolean retVal = taskAuthorizationService.isAuthorized(quickstart.getPrincipalId(), task);
        assertTrue(retVal);
        
        retVal = taskAuthorizationService.isAuthorized(jtester.getPrincipalId(), task);
        assertTrue(retVal);
        
        retVal = taskAuthorizationService.isAuthorized(woods.getPrincipalId(), task);
        assertFalse(retVal);

        retVal = taskAuthorizationService.isAuthorized(ospAdmin.getPrincipalId(), task);
        assertFalse(retVal);
    }
    
    @Test
    public void testCreateActivitiesAuthorizer() throws WorkflowException {
        NegotiationDocument negotiationDoc = getNewNegotiationWithUnassociatedDetail();
        NegotiationTask task = new NegotiationTask(TaskName.NEGOTIATION_CREATE_ACTIVITIES, negotiationDoc);
        boolean retVal = taskAuthorizationService.isAuthorized(quickstart.getPrincipalId(), task);
        assertTrue(retVal);
        
        retVal = taskAuthorizationService.isAuthorized(jtester.getPrincipalId(), task);
        assertTrue(retVal);

        retVal = taskAuthorizationService.isAuthorized(negotiator.getPrincipalId(), task);
        assertTrue(retVal);
                
        retVal = taskAuthorizationService.isAuthorized(woods.getPrincipalId(), task);
        assertFalse(retVal);
        
        retVal = taskAuthorizationService.isAuthorized(ospAdmin.getPrincipalId(), task);
        assertFalse(retVal);
    }
        
    
    @Test
    public void testModifyActivitiesAuthorizer() throws WorkflowException {
        NegotiationDocument negotiationDoc = getNewNegotiationWithUnassociatedDetail();
        NegotiationTask task = new NegotiationTask(TaskName.NEGOTIATION_MODIFY_ACTIVITIES, negotiationDoc);
        boolean retVal = taskAuthorizationService.isAuthorized(quickstart.getPrincipalId(), task);
        assertTrue(retVal);
        
        retVal = taskAuthorizationService.isAuthorized(jtester.getPrincipalId(), task);
        assertFalse(retVal);

        retVal = taskAuthorizationService.isAuthorized(negotiator.getPrincipalId(), task);
        assertTrue(retVal);
        
        retVal = taskAuthorizationService.isAuthorized(woods.getPrincipalId(), task);
        assertFalse(retVal);
        
        retVal = taskAuthorizationService.isAuthorized(ospAdmin.getPrincipalId(), task);
        assertFalse(retVal);
    }
    
    @Test
    public void testViewNegotiationUnRestrictedAuthorizer()  throws WorkflowException{

        NegotiationDocument negotiationDoc = getNewNegotiationWithUnassociatedDetail();
        NegotiationTask task = new NegotiationTask(TaskName.NEGOTIATION_VIEW_NEGOTIATION_UNRESTRICTED, negotiationDoc);

        boolean retVal = taskAuthorizationService.isAuthorized(quickstart.getPrincipalId(), task);
        assertTrue(retVal);

        retVal = taskAuthorizationService.isAuthorized(jtester.getPrincipalId(), task);
        assertTrue(retVal);
        
        retVal = taskAuthorizationService.isAuthorized(woods.getPrincipalId(), task);
        assertFalse(retVal);

        /*
         * FIXME: UofA does not have a role with just permission 1272, we would need to
         *        implement a Permission fixture to insert this, and then add permission to
         *        a new role and insert that, and finally add new role to role fixture
         */
        //retVal = taskAuthorizationService.isAuthorized(ospAdmin.getPrincipalId(), task);
        //assertTrue(retVal);

    }

    @Test
    public void testViewNegotiationAuthorizer()  throws WorkflowException{
    	
        NegotiationDocument negotiationDoc = getNewNegotiationWithUnassociatedDetail();
        NegotiationTask task = new NegotiationTask(TaskName.NEGOTIATION_VIEW_NEGOTIATION,  negotiationDoc);

        boolean retVal = taskAuthorizationService.isAuthorized(quickstart.getPrincipalId(), task);
        assertTrue(retVal);

        retVal = taskAuthorizationService.isAuthorized(jtester.getPrincipalId(), task);
        assertTrue(retVal);

        retVal = taskAuthorizationService.isAuthorized(woods.getPrincipalId(), task);
        assertFalse(retVal);
        
        retVal = taskAuthorizationService.isAuthorized(ospAdmin.getPrincipalId(), task);
        assertTrue(retVal);

    }
    
    @SuppressWarnings({"rawtypes", "deprecation", "unchecked"})
	private NegotiationDocument getNewNegotiationWithUnassociatedDetail() throws WorkflowException {
    	
        NegotiationDocument document = (NegotiationDocument) getDocumentService().getNewDocument(NegotiationDocument.class);
        Negotiation negotiation = document.getNegotiation();
        
        NegotiationStatus status = (NegotiationStatus)businessObjectService.findAll(NegotiationStatus.class).iterator().next();
        NegotiationAgreementType agreementType = (NegotiationAgreementType)businessObjectService.findAll(NegotiationAgreementType.class).iterator().next();
        
        Map primaryKey = new HashMap();
        primaryKey.put("code", "NO");
        NegotiationAssociationType associationType = (NegotiationAssociationType)businessObjectService.findMatching(NegotiationAssociationType.class, primaryKey).iterator().next();
        
        negotiation.setNegotiationAgreementType(agreementType);
        negotiation.setNegotiationAssociationType(associationType);
        negotiation.setNegotiationAssociationTypeId(associationType.getId());
        negotiation.setNegotiationStatus(status);
        
        java.sql.Date testEndDate = new java.sql.Date(2011, 12, 31);
        java.sql.Date testStartDate = new java.sql.Date(2009, 12, 31);
        
        negotiation.setAnticipatedAwardDate(testEndDate);
        negotiation.setNegotiationEndDate(testEndDate);
        negotiation.setNegotiationStartDate(testStartDate);
        negotiation.setDocumentFolder("document folder");
        negotiation.setDocumentNumber("123321");
        negotiation.setNegotiatorPersonId(negotiator.getPrincipalId());
        
        negotiation.setNegotiatorName(negotiator.getPrincipalName());
        
        this.businessObjectService.save(negotiation);
        
        NegotiationUnassociatedDetail detail = new NegotiationUnassociatedDetail();
        detail.setNegotiation(negotiation);
        detail.setNegotiationId(negotiation.getNegotiationId());
        detail.setTitle("super cool title");
        
        primaryKey = new HashMap();
        primaryKey.put("unit_number", "000001");
        Unit unit = (Unit) this.businessObjectService.findByPrimaryKey(Unit.class, primaryKey);
        
        detail.setLeadUnit(unit);
        detail.setLeadUnitNumber(unit.getUnitNumber());
        
        this.businessObjectService.save(detail);
        
        negotiation.setAssociatedDocumentId(detail.getNegotiationId().toString());
        negotiation.setUnAssociatedDetail(detail);
        
        this.businessObjectService.save(negotiation);
        return document;
    }

}