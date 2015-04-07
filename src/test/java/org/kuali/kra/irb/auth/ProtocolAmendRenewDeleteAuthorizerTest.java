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

import org.junit.Test;
import org.kuali.kra.infrastructure.PermissionConstants;
import org.kuali.kra.infrastructure.TaskName;
import org.kuali.kra.irb.ProtocolDocument;
import org.kuali.kra.irb.actions.ProtocolStatus;

/**
 * Test the Protocol/Amendment/Renewal Delete Authorizer.
 */
public class ProtocolAmendRenewDeleteAuthorizerTest extends ProtocolAuthorizerTestBase {
    
    @Test
    public void testHasPermission() throws Exception {
        runStatusAuthorizerTest(PROTOCOL_NUMBER, ProtocolStatus.IN_PROGRESS, true, true);
    }
    
    @Test
    public void testNoPermission() throws Exception {
        runStatusAuthorizerTest(PROTOCOL_NUMBER, ProtocolStatus.IN_PROGRESS, false, false);
    }
    
    @Test
    public void testNotInProgress() throws Exception {
        runStatusAuthorizerTest(PROTOCOL_NUMBER, ProtocolStatus.SUBMITTED_TO_IRB, true, false);
    }
    
    @Override
    protected ProtocolAuthorizer createProtocolAuthorizer(ProtocolDocument protocolDocument, boolean hasPermission, boolean isActionAllowed, boolean isInWorkflow) {
        ProtocolAuthorizer authorizer = new ProtocolAmendRenewDeleteAuthorizer();
        authorizer.setKraAuthorizationService(buildKraAuthorizationService(protocolDocument, PermissionConstants.DELETE_PROTOCOL, hasPermission));
        return authorizer;
    }
    
    @Override
    protected String getTaskName() {
        return TaskName.PROTOCOL_AMEND_RENEW_DELETE;
    }
    
}