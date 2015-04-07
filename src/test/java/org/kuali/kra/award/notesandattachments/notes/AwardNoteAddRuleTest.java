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

import org.junit.Test;
import org.kuali.kra.award.notesandattachments.notes.AwardNoteEventBase.ErrorType;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.rules.TemplateRuleTest;

public class AwardNoteAddRuleTest {

    @Test
    public void testOK() {
        new TemplateRuleTest<AwardNoteAddEvent, AwardNoteAddRule>() {
            @Override
            protected void prerequisite() {

                AwardNotepad awardNotepad = new AwardNotepad();
                awardNotepad.setNoteTopic("test");
                event = new AwardNoteAddEvent(Constants.EMPTY_STRING, null, awardNotepad, ErrorType.HARDERROR);
                rule = new AwardNoteAddRule();
                expectedReturnValue = true;
            }
        };


    }

    @Test
    public void testNotOK() {
        new TemplateRuleTest<AwardNoteAddEvent, AwardNoteAddRule>() {
            @Override
            protected void prerequisite() {

                AwardNotepad awardNotepad = new AwardNotepad();
                awardNotepad.setNoteTopic("");
                event = new AwardNoteAddEvent(Constants.EMPTY_STRING, null, awardNotepad, ErrorType.HARDERROR);
                rule = new AwardNoteAddRule();
                expectedReturnValue = false;
            }
        };


    }

}
