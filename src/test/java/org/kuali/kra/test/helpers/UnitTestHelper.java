package org.kuali.kra.test.helpers;

import org.kuali.kra.bo.Unit;
import org.kuali.kra.service.UnitService;
import org.kuali.kra.test.fixtures.UnitFixture;
import org.kuali.rice.krad.service.BusinessObjectService;

public class UnitTestHelper extends TestHelper {

	public Unit createUnit(UnitFixture unitFixture) {
		
		// If it already exists, return it
		Unit unit = getUnit(unitFixture);
		if(unit != null) {
			return unit;
		}
		
		// Does not exist -- create, persist, and return it
		BusinessObjectService businessObjectService = getService(BusinessObjectService.class);
		unit = buildUnit(unitFixture);
		return businessObjectService.save(unit);

	}
	
	private Unit buildUnit(UnitFixture unitFixture) {
		Unit unit = new Unit();
		unit.setActive(true);
		unit.setVersionNumber(0L);
		unit.setUnitNumber(unitFixture.getUnitNumber());
		unit.setUnitName(unitFixture.getUnitName());
		unit.setParentUnitNumber(unitFixture.getParentUnitNumber());
		return unit;
	}

	private Unit getUnit(UnitFixture unitFixture) {
		return getService(UnitService.class).getUnit(unitFixture.getUnitNumber());
	}

}
