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
package org.kuali.kra.document;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kra.proposaldevelopment.document.ProposalDevelopmentDocument;
import org.kuali.kra.test.fixtures.PersonFixture;
import org.kuali.kra.test.infrastructure.KcUnitTestBase;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.util.GlobalVariables;

import java.sql.Timestamp;
import java.util.Date;

/**
 * This class tests ResearchDocumentBase.
 */
public class ResearchDocumentBaseTest extends KcUnitTestBase {

    @Before
    public void setUp() throws Exception {
        super.setUp();
        GlobalVariables.setUserSession(new UserSession(PersonFixture.UAR_TEST_001.getPrincipalName()));
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
        GlobalVariables.setUserSession(null);
    }

    @Test public void testPrepareForSaveQuickstart() throws Exception {
        ResearchDocumentBase researchDocumentBase = new ProposalDevelopmentDocument();
        assertNull(researchDocumentBase.getUpdateTimestamp());
        assertNull(researchDocumentBase.getUpdateUser());
        researchDocumentBase.prepareForSave();

        assertEquals(PersonFixture.UAR_TEST_001.getPrincipalName(), researchDocumentBase.getUpdateUser());
        Timestamp updateTimestamp = researchDocumentBase.getUpdateTimestamp();
        assertNotNull(researchDocumentBase.getUpdateTimestamp());

        Date currentDate = new Date(System.currentTimeMillis());
        long diff = updateTimestamp.getTime() - currentDate.getTime();

        assertTrue("Should be less than one second difference between dates", diff < 1000);
    }

    @Test public void testPrepareForSaveJtester() throws Exception {
        GlobalVariables.setUserSession(new UserSession(PersonFixture.UAR_TEST_002.getPrincipalName()));

        ResearchDocumentBase researchDocumentBase = new ProposalDevelopmentDocument();
        assertNull(researchDocumentBase.getUpdateTimestamp());
        assertNull(researchDocumentBase.getUpdateUser());
        researchDocumentBase.prepareForSave();

        assertEquals(PersonFixture.UAR_TEST_002.getPrincipalName(), researchDocumentBase.getUpdateUser());
        Timestamp updateTimestamp = researchDocumentBase.getUpdateTimestamp();
        assertNotNull(researchDocumentBase.getUpdateTimestamp());

        Date currentDate = new Date(System.currentTimeMillis());
        long diff = updateTimestamp.getTime() - currentDate.getTime();

        assertTrue("Should be less than one second difference between dates", diff < 1000);
    }

}
