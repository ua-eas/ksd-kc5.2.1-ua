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
package org.kuali.kra.meeting;

import org.junit.Test;
import org.kuali.kra.committee.bo.Committee;
import org.kuali.kra.common.committee.document.authorization.CommitteeTaskBase;
import org.kuali.kra.infrastructure.TaskGroupName;
import org.kuali.kra.infrastructure.TaskName;
import org.kuali.kra.service.KraAuthorizationService;
import org.kuali.kra.service.impl.mocks.KraAuthorizationServiceMock;
import org.kuali.kra.test.fixtures.PersonFixture;
import org.kuali.rice.kew.api.exception.WorkflowException;

import static org.junit.Assert.assertEquals;

public class ModifyScheduleAuthorizerTest {
    private static final String USERNAME = PersonFixture.UAR_TEST_001.getPrincipalName();

    @Test
    public void testModifySchedulePermission() throws WorkflowException {
        ModifyScheduleAuthorizer authorizer = new ModifyScheduleAuthorizer();
        
        final KraAuthorizationService kraAuthorizationService = new KraAuthorizationServiceMock(true);
        authorizer.setKraAuthorizationService(kraAuthorizationService);
        
        Committee committee = createCommittee("modifyschedule1", "modify schedule test");
        CommitteeTaskBase<Committee> task = new CommitteeTaskBase<Committee>(TaskGroupName.COMMITTEE, TaskName.VIEW_SCHEDULE, committee) {};
        assertEquals(true, authorizer.isAuthorized(USERNAME, task));
        final KraAuthorizationService kraAuthorizationService1 = new KraAuthorizationServiceMock(false);
        authorizer.setKraAuthorizationService(kraAuthorizationService1);
        assertEquals(false, authorizer.isAuthorized("tdurkin", task));
    }
    
    private Committee createCommittee(String committeeId, String committeeName) {
        Committee committee = new Committee();
        committee.setCommitteeId(committeeId);
        committee.setCommitteeName(committeeName);
        return committee;
    }


}
