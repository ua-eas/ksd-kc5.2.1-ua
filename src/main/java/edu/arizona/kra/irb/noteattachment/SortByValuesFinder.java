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
package edu.arizona.kra.irb.noteattachment;

import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;

import java.util.List;

/**
 * 
 * This add a new sort for Amendment/Renewal Number for the fields used to sort protocol attachments.
 */
public class SortByValuesFinder extends org.kuali.kra.irb.noteattachment.SortByValuesFinder {

    @Override
    public List<KeyValue> getKeyValues() {
        List<KeyValue> keyValues = super.getKeyValues();
        keyValues.add(new ConcreteKeyValue("ARNO", new String("Amend/Renewal Number")));

        return keyValues;
    }

}
