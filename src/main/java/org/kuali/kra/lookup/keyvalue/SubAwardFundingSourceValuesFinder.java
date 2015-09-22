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
package org.kuali.kra.lookup.keyvalue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.kra.krad.migration.FormViewAwareUifKeyValuesFinderBase;
import org.kuali.kra.subaward.bo.SubAward;
import org.kuali.kra.subaward.bo.SubAwardFundingSource;
import org.kuali.kra.subaward.document.SubAwardDocument;
import org.kuali.kra.subaward.service.SubAwardService;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;


public class SubAwardFundingSourceValuesFinder extends FormViewAwareUifKeyValuesFinderBase {
    
    @Override
    public List<KeyValue> getKeyValues() {
        SubAward subaward = ((SubAwardDocument)getDocument()).getSubAward();
        
        Collection<SubAwardFundingSource> fundingSources = (Collection<SubAwardFundingSource>) KraServiceLocator
                .getService(SubAwardService.class).getActiveSubAwardFundingSources(subaward);
        List<KeyValue> keyValues = new ArrayList<KeyValue>();
        for (SubAwardFundingSource sfs : fundingSources) {
                keyValues.add(new ConcreteKeyValue(subaward.getSubAwardCode()+":"+sfs.getAwardNumber(),"Award:"+sfs.getAwardNumber()));
        }
        if(keyValues.size() == 0){
            keyValues.add(0, new ConcreteKeyValue("", "No Funding Source has been added to this Subaward"));
        }
        return keyValues;
    }

}
