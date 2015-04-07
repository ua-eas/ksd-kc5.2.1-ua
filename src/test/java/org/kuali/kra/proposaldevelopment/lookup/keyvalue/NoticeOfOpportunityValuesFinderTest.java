/*
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.osedu.org/licenses/ECL-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.kuali.kra.proposaldevelopment.lookup.keyvalue;

import java.util.ArrayList;
import java.util.List;


import org.junit.Before;
import org.kuali.kra.keyvalue.ValuesFinderTestBase;
import org.kuali.kra.test.fixtures.NoticeOfOpportunityFixture;
import org.kuali.kra.test.helpers.NoticeOfOpportunityTestHelper;
import org.kuali.rice.core.api.util.KeyValue;

/**
 * This class tests NoticeOfOpportunityValuesFinder.
 */
public class NoticeOfOpportunityValuesFinderTest extends ValuesFinderTestBase {


	@Before
	@Override
	public void setUp() {
		NoticeOfOpportunityTestHelper helper = new NoticeOfOpportunityTestHelper();
		for ( NoticeOfOpportunityFixture notice : NoticeOfOpportunityFixture.values() ) {
			if ( !notice.getKey().equals( "" ) ) {
				helper.createNoticeOfOpportunity( notice );
			}
		}
	}

	@Override
	protected Class<NoticeOfOpportunityValuesFinder> getTestClass() {
		return NoticeOfOpportunityValuesFinder.class;
	}

	@Override
	protected List<KeyValue> getKeyValues() {
		List<KeyValue> keyValue = new ArrayList<KeyValue>();
		for ( NoticeOfOpportunityFixture notice : NoticeOfOpportunityFixture.values() ) {
			keyValue.add( createKeyValue( notice.getKey(), notice.getValue() ) );
		}
		return keyValue;
	}

}
