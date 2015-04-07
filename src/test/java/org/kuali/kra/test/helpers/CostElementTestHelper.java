package org.kuali.kra.test.helpers;

import java.sql.Timestamp;
import org.kuali.kra.budget.core.CostElement;
import org.kuali.kra.test.fixtures.CostElementFixture;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * A class that helps test cases create and persist a CostElement from a
 * CostElementFixture to a DB.
 *
 */
public class CostElementTestHelper extends TestHelper {
	
	public CostElement createCostElement(CostElementFixture costElementFixture) {
		
		CostElement costElement;
		BusinessObjectService businessObjectService = getService(BusinessObjectService.class);
		costElement = buildCostElement(costElementFixture);
		return businessObjectService.save(costElement);
	}
	
	private CostElement buildCostElement(CostElementFixture costElementFixture) {
		CostElement costElement = new CostElement();
		costElement.setCostElement(costElementFixture.getCostElement());
		costElement.setDescription(costElementFixture.getDescription());
		costElement.setOnOffCampusFlag(true);
		costElement.setUpdateTimestamp(new Timestamp(System.currentTimeMillis()));
		costElement.setUpdateUser(costElementFixture.getUpdateUser());
		costElement.setVersionNumber(0l);
		return costElement;
	}
}
