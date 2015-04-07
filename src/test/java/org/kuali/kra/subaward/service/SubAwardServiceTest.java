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
package org.kuali.kra.subaward.service;

import org.apache.commons.lang.time.DateUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.kra.test.infrastructure.KcUnitTestBase;

import java.sql.Date;

public class SubAwardServiceTest extends KcUnitTestBase {
    SubAwardService subAwardService;
    @Before
    public void setUp() throws Exception {
        subAwardService = KraServiceLocator.getService(SubAwardService.class);
    }

    @After
    public void tearDown() throws Exception {
        subAwardService = null;
    }

    @Test
    public void testGetFollowupDateDefaultLength() {
        String checkVal = subAwardService.getFollowupDateDefaultLength();
        assertEquals("6W", checkVal);
    }

    @Test
    public void testGetCalculatedFollowupDate() {
        Date checkDate = subAwardService.getCalculatedFollowupDate(new Date(2012, 1, 1));
        Date expectedDate = new Date(DateUtils.addWeeks(new Date(2012, 1, 1), 6).getTime());
        assertEquals(expectedDate, checkDate);
    }
    
    @Test
    public void testGetFollowupDateDefaultLengthInDays() {
        int followUpDays = subAwardService.getFollowupDateDefaultLengthInDays();
        int expectedFollowUpDays = 6 * 7;
        assertEquals(expectedFollowUpDays, followUpDays);
        
    }
}