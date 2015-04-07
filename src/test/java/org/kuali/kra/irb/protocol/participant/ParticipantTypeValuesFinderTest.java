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
package org.kuali.kra.irb.protocol.participant;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.kuali.kra.keyvalue.ValuesFinderTestBase;
import org.kuali.kra.test.fixtures.ParticipantTypeFixture;
import org.kuali.kra.test.helpers.ParticipantTypeValueFinderTestHelper;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * 
 * Test the Participant Type Values Finder.
 *
 */
public class ParticipantTypeValuesFinderTest extends ValuesFinderTestBase {
	
	
	@Before
	@Override
	public void setUp() throws Exception {
		GlobalVariables.clear();
		
		// Needed since when run as suite, other tests aren't cleaning up.
		KNSGlobalVariables.setKualiForm(null);
		
		ParticipantTypeValueFinderTestHelper helper = new ParticipantTypeValueFinderTestHelper();
		for ( ParticipantTypeFixture participantType : ParticipantTypeFixture.values() ) {
			if ( !participantType.getKey().equals( "" ) ) {
				helper.createParticipantType( participantType );
			}
		}
	}

	@Override
	protected Class<ParticipantTypeValuesFinder> getTestClass() {
		return ParticipantTypeValuesFinder.class;
	}

	@Override
	protected List<KeyValue> getKeyValues() {

		final List<KeyValue> keyValue = new ArrayList<KeyValue>();
		for ( ParticipantTypeFixture participantType : ParticipantTypeFixture.values() ) {
			keyValue.add( createKeyValue( participantType.getKey(), participantType.getValue() ) );
		}
		return keyValue;
	}

}
