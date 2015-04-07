package org.kuali.kra.test.helpers;

import java.sql.Timestamp;
import org.kuali.kra.bo.Rolodex;
import org.kuali.kra.test.fixtures.RolodexFixture;
import org.kuali.rice.krad.service.BusinessObjectService;

public class RolodexTestHelper extends TestHelper {
	
	public Rolodex createRolodex(RolodexFixture rolodexFixture) {

	BusinessObjectService businessObjectService = getService(BusinessObjectService.class);
	Rolodex rolodex = buildRolodex(rolodexFixture);

	return businessObjectService.save(rolodex);

}

	private Rolodex buildRolodex(RolodexFixture rolodexFixture) {
		Rolodex rolodex = new Rolodex();
	
		rolodex.setRolodexId(rolodexFixture.getRolodexId());
		rolodex.setFirstName(rolodexFixture.getFirstName());
		rolodex.setLastName(rolodexFixture.getLastName());
		rolodex.setOrganization(rolodexFixture.getOrganization());
		rolodex.setOwnedByUnit(rolodexFixture.getOwnedByUnit());
		rolodex.setSponsorAddressFlag(false);
		rolodex.setUpdateTimestamp(new Timestamp(System.currentTimeMillis()));
		rolodex.setVersionNumber(0L);
		rolodex.setActive(true);
		return rolodex;
	}
}
