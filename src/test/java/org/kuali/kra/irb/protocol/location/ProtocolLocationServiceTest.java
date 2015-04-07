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
package org.kuali.kra.irb.protocol.location;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kra.irb.Protocol;
import org.kuali.kra.test.fixtures.OrgFixture;
import org.kuali.kra.test.helpers.OrgTestHelper;
import org.kuali.kra.test.infrastructure.KcUnitTestBase;


public class ProtocolLocationServiceTest extends KcUnitTestBase{
    protected static final String NEW_ORGANIZATION_VALUE =  "999991";
    
    @Before
    public void setUp() throws Exception {
    	super.setUp();
    	OrgTestHelper orgTestHelper = new OrgTestHelper();
    	orgTestHelper.createOrg(OrgFixture.ONE);
    }
    
    @After
    public void tearDown() throws Exception {
    	super.tearDown();
    }
    
    /**
     * This method is to add a new protocol location
     * @throws Exception
     */
    @Test
    public void testAddProtocolLocation() throws Exception {
        
        ProtocolLocationService service  = new ProtocolLocationServiceImpl();
        
        @SuppressWarnings("serial")
		Protocol protocol = new Protocol(){
            @Override
            public void refreshReferenceObject(String referenceObjectName) {}
            
        };
        
        service.addProtocolLocation(protocol, getNewProtocolLocation() );

        assertEquals(1, protocol.getProtocolLocations().size());
        
    }

    /**
     * This method is to get a new protocol location data
     * @return ProtocolLocation
     */
    private ProtocolLocation getNewProtocolLocation() {
        ProtocolLocation protocolLocation = new ProtocolLocation();
        protocolLocation.setOrganizationId(NEW_ORGANIZATION_VALUE);
        return protocolLocation;
        
    }

}
