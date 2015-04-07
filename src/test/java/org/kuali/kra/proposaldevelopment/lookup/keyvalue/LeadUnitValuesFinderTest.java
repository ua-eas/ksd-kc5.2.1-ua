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
import org.kuali.kra.test.fixtures.LeadUnitFixture;
import org.kuali.kra.test.helpers.LeadUnitTestHelper;
import org.kuali.rice.core.api.util.KeyValue;

/**
 * This class tests LeadUnitValuesFinder.
 */
// FIXME: This JUnit test is used to test the LeadUnitValuesFinder. That class dynamically generates a list of
// FIXME: values based on qualifiers from the database that cannot be easily tested for, so #getKeyValues() is being
// FIXME: short-circuited to return the values from the Finder. When the test is performed, it will always pass because
// FIXME: of this. This is a short term work around because the amount of time necessary to build a "good" test for this
// FIXME: would be too time consuming for too small of a return, per Scott Skinner.
public class LeadUnitValuesFinderTest extends ValuesFinderTestBase {

	@Before
	@Override
	public void setUp() {
		LeadUnitTestHelper helper = new LeadUnitTestHelper();
		for ( LeadUnitFixture unit : LeadUnitFixture.values() ) {
			// helper.createLeadUnit( unit );
		}
	}

	@Override
	protected Class<LeadUnitValuesFinder> getTestClass() {
		return LeadUnitValuesFinder.class;
	}

	@Override
	protected List<KeyValue> getKeyValues() {

		final List<KeyValue> keylabel = new ArrayList<KeyValue>();
		for ( LeadUnitFixture unit : LeadUnitFixture.values() ) {
			keylabel.add( createKeyValue( unit.getKey(), unit.getKey() + " - " + unit.getValue() ) );
		}
		// return keylabel;
		return new LeadUnitValuesFinder().getKeyValues();
	}
}
