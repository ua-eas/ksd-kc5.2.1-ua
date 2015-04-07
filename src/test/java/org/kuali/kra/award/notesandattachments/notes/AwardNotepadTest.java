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
package org.kuali.kra.award.notesandattachments.notes;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kra.award.home.Award;

/**
 * This class tests methods in AwardNotepad.java
 */
public class AwardNotepadTest {

private static final int AWARD_NOTEPAD_ATTRIBUTES_COUNT = 11;
    
    private AwardNotepad awardNotepadBo;
    private Award award = new Award();
    
    /**
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        awardNotepadBo = new AwardNotepad();
        awardNotepadBo.setAward(award);
    }

    /**
     *
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        awardNotepadBo = null;
    }
    
    /**
     * 
     * This method tests that total attributes of Award Business Object 
     * @throws Exception
     */
    @Test
    public void testAwardNotepadBoAttributesCount() throws Exception {              
        Assert.assertEquals(AWARD_NOTEPAD_ATTRIBUTES_COUNT, awardNotepadBo.getClass().getDeclaredFields().length);
    }
}
