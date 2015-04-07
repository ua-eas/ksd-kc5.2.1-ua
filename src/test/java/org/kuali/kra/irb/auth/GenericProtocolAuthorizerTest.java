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
package org.kuali.kra.irb.auth;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.kra.infrastructure.TaskName;
import org.kuali.kra.irb.Protocol;
import org.kuali.kra.irb.ProtocolDocument;
import org.kuali.kra.irb.actions.ProtocolStatus;
import org.kuali.kra.irb.actions.submit.ProtocolActionService;
import org.kuali.kra.irb.actions.submit.ProtocolSubmission;
import org.kuali.kra.irb.actions.submit.ProtocolSubmissionType;
import org.kuali.kra.irb.test.ProtocolFactory;
import org.kuali.kra.service.KraAuthorizationService;
import org.kuali.kra.test.fixtures.PermissionFixture;
import org.kuali.kra.test.fixtures.PersonFixture;
import org.kuali.kra.test.fixtures.RoleFixture;
import org.kuali.kra.test.fixtures.UnitFixture;
import org.kuali.kra.test.helpers.PermissionTestHelper;
import org.kuali.kra.test.helpers.RolePermissionTestHelper;
import org.kuali.kra.test.helpers.RoleTestHelper;
import org.kuali.kra.test.helpers.UnitTestHelper;
import org.kuali.kra.test.infrastructure.KcUnitTestBase;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kim.impl.permission.PermissionBo;
import org.kuali.rice.kim.impl.role.RoleBo;
import org.kuali.rice.kim.impl.role.RoleMemberBo;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.util.GlobalVariables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GenericProtocolAuthorizerTest extends KcUnitTestBase {
    
    private static final String VALID_TASK_NAME = GenericProtocolAuthorizer.TERMINATE_PROTOCOL;
    private static final String INVALID_TASK_NAME = "foobar";
    
    private KraAuthorizationService kraAuthorizationService;
    private ProtocolActionService protocolActionService;
    
    //private Mockery context;
    
    private GenericProtocolAuthorizer auth;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        
        getBusinessObjectService().deleteMatching(RoleMemberBo.class, new HashMap<String, Object>());
        
        RoleTestHelper roleTestHelper = new RoleTestHelper();
        RoleBo role1 = roleTestHelper.createRole(RoleFixture.CREATE_PROTOCOL);
        RoleBo role2 = roleTestHelper.createRole(RoleFixture.MAINTAIN_PROTOCOL);
   
        PermissionTestHelper permissionTestHelper = new PermissionTestHelper();
        PermissionBo permission1 = permissionTestHelper.createPermission(PermissionFixture.CREATE_PROTOCOL_DOCUMENT);
        PermissionBo permission2 = permissionTestHelper.createPermission(PermissionFixture.MAINTAIN_PROTOCOL_SUBMISSIONS);
        
        RolePermissionTestHelper rolePermissionTestHelper = new RolePermissionTestHelper();
        rolePermissionTestHelper.createRolePermission(permission1.getId(), role1.getId());
        rolePermissionTestHelper.createRolePermission(permission2.getId(), role2.getId());
        
        Person woods = KraServiceLocator.getService(PersonService.class).getPerson(PersonFixture.UAR_TEST_003.getPrincipalId());
        roleTestHelper.addPersonToRole(woods, RoleFixture.CREATE_PROTOCOL);
        roleTestHelper.addPersonToRole(woods, RoleFixture.MAINTAIN_PROTOCOL);
        
        GlobalVariables.setUserSession(new UserSession(woods.getPrincipalName()));
        
        UnitTestHelper unitTestHelper = new UnitTestHelper();
        unitTestHelper.createUnit(UnitFixture.TEST_1);
        
        auth = new GenericProtocolAuthorizer();
        kraAuthorizationService = KraServiceLocator.getService(KraAuthorizationService.class);
        protocolActionService = KraServiceLocator.getService(ProtocolActionService.class);
        auth.setKraAuthorizationService(kraAuthorizationService);
        auth.setProtocolActionService(protocolActionService);
        //context = new JUnit4Mockery();
    }

    @After
    public void tearDown() throws Exception {
        auth = null;
        kraAuthorizationService = null;
        protocolActionService = null;
        GlobalVariables.setUserSession(null);
        //context = null;
        super.tearDown();
    }

    @Test
    public void testValidCloseEnrollment() throws Exception {
        Protocol prot = getBaseProtocol(ProtocolStatus.ACTIVE_OPEN_TO_ENROLLMENT, ProtocolSubmissionType.REQUEST_TO_CLOSE_ENROLLMENT);
        ProtocolTask task = new ProtocolTask(TaskName.GENERIC_PROTOCOL_ACTION, prot);
        auth.setGenericTaskName(GenericProtocolAuthorizer.CLOSE_ENROLLMENT_PROTOCOL);
        assertTrue(auth.isAuthorized(GlobalVariables.getUserSession().getPrincipalId(), task));
    }
    
    @Test
    public void testValidDataAnalysisOnly() throws Exception {
        Protocol prot = getBaseProtocol(ProtocolStatus.ACTIVE_OPEN_TO_ENROLLMENT, ProtocolSubmissionType.REQUEST_FOR_DATA_ANALYSIS_ONLY);
        ProtocolTask task = new ProtocolTask(TaskName.GENERIC_PROTOCOL_ACTION, prot);
        auth.setGenericTaskName(GenericProtocolAuthorizer.PERMIT_DATA_ANALYSIS);
        assertTrue(auth.isAuthorized(GlobalVariables.getUserSession().getPrincipalId(), task));
    }
    
    @Test
    public void testValidSuspend() throws Exception {
        Protocol prot = getBaseProtocol(ProtocolStatus.ACTIVE_OPEN_TO_ENROLLMENT, ProtocolSubmissionType.REQUEST_FOR_SUSPENSION);
        ProtocolTask task = new ProtocolTask(TaskName.GENERIC_PROTOCOL_ACTION, prot);
        auth.setGenericTaskName(GenericProtocolAuthorizer.SUSPEND_PROTOCOL);
        assertTrue(auth.isAuthorized(GlobalVariables.getUserSession().getPrincipalId(), task));
    }
    
    @Test
    public void testValidClose() throws Exception {
        Protocol prot = getBaseProtocol(ProtocolStatus.ACTIVE_OPEN_TO_ENROLLMENT, ProtocolSubmissionType.REQUEST_TO_CLOSE);
        ProtocolTask task = new ProtocolTask(TaskName.GENERIC_PROTOCOL_ACTION, prot);
        auth.setGenericTaskName(GenericProtocolAuthorizer.CLOSE_PROTOCOL);
        assertTrue(auth.isAuthorized(GlobalVariables.getUserSession().getPrincipalId(), task));
    }
    
    @Test
    public void testValidReOpen() throws Exception {
        Protocol prot = getBaseProtocol(ProtocolStatus.ACTIVE_CLOSED_TO_ENROLLMENT, ProtocolSubmissionType.REQUEST_TO_REOPEN_ENROLLMENT);
        ProtocolTask task = new ProtocolTask(TaskName.GENERIC_PROTOCOL_ACTION, prot);
        auth.setGenericTaskName(GenericProtocolAuthorizer.REOPEN_PROTOCOL);
        assertTrue(auth.isAuthorized(GlobalVariables.getUserSession().getPrincipalId(), task));
    }
    
    @Test
    public void testInValidReOpen() throws Exception {
        Protocol prot = getBaseProtocol(ProtocolStatus.ACTIVE_CLOSED_TO_ENROLLMENT, ProtocolSubmissionType.REQUEST_TO_REOPEN_ENROLLMENT);
        ProtocolTask task = new ProtocolTask(TaskName.GENERIC_PROTOCOL_ACTION, prot);
        auth.setGenericTaskName(GenericProtocolAuthorizer.REOPEN_PROTOCOL);
        assertFalse(auth.isAuthorized(GlobalVariables.getUserSession().getPrincipalId() + "xyz", task));
    }
    
    @Test
    public void testValidTerminate() throws Exception {
        Protocol prot = getBaseProtocol(ProtocolStatus.ACTIVE_OPEN_TO_ENROLLMENT, ProtocolSubmissionType.REQUEST_FOR_TERMINATION);
        ProtocolTask task = new ProtocolTask(TaskName.GENERIC_PROTOCOL_ACTION, prot);
        auth.setGenericTaskName(GenericProtocolAuthorizer.TERMINATE_PROTOCOL);
        assertTrue(auth.isAuthorized(GlobalVariables.getUserSession().getPrincipalId(), task));
    }
    
    @SuppressWarnings({"unchecked", "rawtypes"})
    private Protocol getBaseProtocol(String protocolStatusCode, String submissionType) throws Exception{
        
        ProtocolDocument pd = ProtocolFactory.createProtocolDocument("123", new Integer(1));
        pd.getProtocol().setProtocolStatusCode(protocolStatusCode);
        ProtocolSubmission ps = new ProtocolSubmission();
        ps.setProtocol(pd.getProtocol());
        ps.setCommitteeId("789");
        ps.setScheduleId("456");
        ps.setSubmissionStatusCode(submissionType);
        List<ProtocolSubmission> protocolSubmissions = new ArrayList<ProtocolSubmission>();
        protocolSubmissions.add(ps);        
        pd.getProtocol().setProtocolSubmission(ps);
        pd.getProtocol().setProtocolSubmissions((List)protocolSubmissions);
        pd.getProtocol().setLeadUnitNumber(UnitFixture.TEST_1.getUnitNumber());
        return pd.getProtocol();     
    }

    @Test
    public void testValidSetGenericTaskName() {
        boolean validTest = true;
        try {
            auth.setGenericTaskName(VALID_TASK_NAME);
        } catch (IllegalArgumentException iae) {
            validTest = false;
        }
        assertTrue(validTest);
    }
    
    @Test
    public void testInValidSetGenericTaskName() {
        boolean validTest = true;
        try {
            auth.setGenericTaskName(INVALID_TASK_NAME);
        } catch (IllegalArgumentException iae) {
            validTest = false;
        }
        assertFalse(validTest);
    }
}