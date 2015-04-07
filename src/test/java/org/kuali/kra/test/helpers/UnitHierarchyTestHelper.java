package org.kuali.kra.test.helpers;

import java.util.ArrayList;
import java.util.List;
import org.kuali.kra.bo.Unit;
import org.kuali.rice.krad.service.BusinessObjectService;


public class UnitHierarchyTestHelper extends TestHelper {
	
	/**
	 * Creates a hierarchy that is 'depth' levels deep
	 * @param leaf - the lowest Unit in the hierarchy 
	 * @param depth - the depth of the hierarchy (including the leaf)
	 */
	public void createUnitHierarchy(Unit leaf, int depth) {

		Unit parent = buildUnit("TEST0");
		leaf.setParentUnitNumber(parent.getUnitNumber());
		List<Unit> units = new ArrayList<Unit>();
		units.add(leaf);
		units.add(parent);
		for(int i = 1; i < depth - 1; i++)
		{
			Unit unit = buildUnit("TEST" + i);
			parent.setParentUnitNumber(unit.getUnitNumber());
			parent = unit;
			units.add(unit);
		}
		
		BusinessObjectService businessObjectService = getService(BusinessObjectService.class);
		
		for(int i = units.size() - 1; i > 0; i--)
		{
			businessObjectService.save(units.get(i));
		}
		businessObjectService.save(leaf);
	}
	
	/**
	 * Makes numSubunits Units with parentUnitNumber as the parent unit number
	 * @param parentUnitNumber - the unit number of the Unit for which subunits are being made
	 * @param numSubunits - the number of sub-units to be made
	 */
	public void makeSubunit(String parentUnitNumber, int numSubunits) {
		
		BusinessObjectService businessObjectService = getService(BusinessObjectService.class);

		for(int i = 0; i < numSubunits; i++)
		{
			Unit unit = buildUnit("subunit" + i);
			unit.setParentUnitNumber(parentUnitNumber);
			businessObjectService.save(unit);
		}
		
	}

	private Unit buildUnit(String name) {
		Unit unit = new Unit();
		unit.setActive(true);
		unit.setVersionNumber(0L);
		unit.setUnitNumber(name);
		unit.setUnitName(name);
		return unit;
	}
}
