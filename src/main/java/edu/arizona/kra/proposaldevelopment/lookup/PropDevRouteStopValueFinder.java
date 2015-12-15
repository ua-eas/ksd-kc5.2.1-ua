/*
 * Copyright 2005-2015 The Kuali Foundation
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
package edu.arizona.kra.proposaldevelopment.lookup;

import java.util.ArrayList;

import java.util.List;

import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.uif.control.UifKeyValuesFinderBase;
import static edu.arizona.kra.proposaldevelopment.PropDevRoutingStateConstants.*;

/**
 * Values to populate the Node Name dropdown for the Proposal Development Dashboard
 * @author nataliac
 */
public class PropDevRouteStopValueFinder extends UifKeyValuesFinderBase {
    private static final long serialVersionUID = -348552528513204757L;
    private static KeyValue keyValueSelect = new ConcreteKeyValue("", NODE_NAME_ALL);


    @Override
    public List<KeyValue> getKeyValues() {     
        List<KeyValue> keyValueList = new ArrayList<KeyValue>();

        keyValueList.add(keyValueSelect);
        keyValueList.add(new ConcreteKeyValue(NODE_KEY_HIERARCHY, NODE_NAME_COLLEGE) );
        keyValueList.add(new ConcreteKeyValue(NODE_KEY_LEAD_UNIT, NODE_NAME_LEAD_UNIT) );
        keyValueList.add(new ConcreteKeyValue(NODE_KEY_PI, NODE_NAME_PI) );
        keyValueList.add(new ConcreteKeyValue(NODE_KEY_INITIATED, NODE_NAME_INITIATED) );
        keyValueList.add(new ConcreteKeyValue(NODE_KEY_SPS_APPROVE, NODE_NAME_SPS_APPROVE) );
        return keyValueList;
        
    }
}
