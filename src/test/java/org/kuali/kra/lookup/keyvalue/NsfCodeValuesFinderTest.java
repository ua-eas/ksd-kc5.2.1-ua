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
package org.kuali.kra.lookup.keyvalue;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kra.keyvalue.PersistableBusinessObjectValuesFinderTestBase;
import org.kuali.kra.test.fixtures.NsfCodeFixture;
import org.kuali.kra.test.helpers.NsfCodeValuesFinderTestHelper;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.PersistableBusinessObjectValuesFinder;

/**
 * This class tests NsfCodeValuesFinder.
 */
public class NsfCodeValuesFinderTest extends PersistableBusinessObjectValuesFinderTestBase {

	public NsfCodeValuesFinderTest() {
		setValuesFinderClass( PersistableBusinessObjectValuesFinder.class );
		setBusinessObjectClass( org.kuali.kra.bo.NsfCode.class );
		setKeyAttributeName( "nsfCode" );
		setLabelAttributeName( "description" );
		setIncludeKeyInDescription( false );
	}

	@Before
	@Override
	public void setUp() throws Exception {

		NsfCodeValuesFinderTestHelper helper = new NsfCodeValuesFinderTestHelper();
		for ( NsfCodeFixture fixture : NsfCodeFixture.values() ) {
			helper.createNsfCode( fixture );
		}
		super.setUp();
	}

	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	public void testGetKeyValues() throws Exception {
		super.testGetKeyValues();
	}

	@Override
	protected void addKeyValues() {
		testKeyValues.addAll( getKeyValues() );
	}

	private List<KeyValue> getKeyValues()
	{
		List<KeyValue> keyValues = new ArrayList<KeyValue>();

		for ( NsfCodeFixture fixture : NsfCodeFixture.values() ) {
			keyValues.add( createKeyValue( fixture ) );
		}
		return keyValues;
	}

	private KeyValue createKeyValue( NsfCodeFixture fixture ) {
		return new ConcreteKeyValue( fixture.getCode(), fixture.getValue() );
	}

}
