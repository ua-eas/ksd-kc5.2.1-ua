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
package org.kuali.kra.subaward.web.struts.action;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kra.test.infrastructure.KcUnitTestBase;

public class SubAwardActionTest extends KcUnitTestBase{

    SubAwardAction subAwardAction;
    public static final String MOCK_FORWARD_STRING = "FORWARD_STRING";
    public static final String MOCK_DOC_ID_REQUEST_PARAMETER = "21";
    public static final String MOCK_EXPECTED_RESULT_STRING = "FORWARD_STRING?docId=21";

    /**
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        subAwardAction = new SubAwardAction();
    }

    /**
     *
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        subAwardAction = null;
    }

    /**
     * 
     * This test tests the SubAwardAction.buildForwardStringForActionListCommand method.
     * @throws Exception
     */
    @Test
    public void testBuildForwardStringForActionListCommand() throws Exception {        
        Assert.assertEquals(
                subAwardAction.buildForwardStringForActionListCommand(MOCK_FORWARD_STRING, 
                        MOCK_DOC_ID_REQUEST_PARAMETER),MOCK_EXPECTED_RESULT_STRING);     
    }

}
