package org.kuali.kra.test.helpers;

import org.kuali.kra.bo.Unit;
import org.kuali.kra.service.UnitService;
import org.kuali.kra.test.fixtures.LeadUnitFixture;
import org.kuali.rice.krad.service.BusinessObjectService;

public class LeadUnitTestHelper extends TestHelper {

	public Unit createLeadUnit( LeadUnitFixture unitFixture ) {

		// If it already exists, return it
		Unit unit = getLeadUnit( unitFixture );
		if ( unit != null ) {
			return unit;
		}

		// Does not exist -- create, persist, and return it
		BusinessObjectService businessObjectService = getService( BusinessObjectService.class );
		unit = buildLeadUnit( unitFixture );
		return businessObjectService.save( unit );

	}

	private Unit buildLeadUnit( LeadUnitFixture unitFixture ) {
		Unit unit = new Unit();
		unit.setActive( true );
		unit.setVersionNumber( 0L );
		unit.setUnitNumber( unitFixture.getKey() );
		unit.setUnitName( unitFixture.getValue() );
		return unit;
	}

	private Unit getLeadUnit( LeadUnitFixture unitFixture ) {
		return getService( UnitService.class ).getUnit( unitFixture.getKey() );
	}

}
