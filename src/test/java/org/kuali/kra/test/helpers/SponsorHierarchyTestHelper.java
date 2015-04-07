package org.kuali.kra.test.helpers;

import java.sql.Timestamp;

import org.kuali.kra.bo.SponsorHierarchy;
import org.kuali.kra.test.fixtures.PersonFixture;
import org.kuali.kra.test.fixtures.SponsorHierarchyFixture;
import org.kuali.rice.krad.service.BusinessObjectService;

public class SponsorHierarchyTestHelper extends TestHelper {

	public SponsorHierarchy createSponsorHierarchy(SponsorHierarchyFixture sponsorHierarchyFixture) {
		
		BusinessObjectService businessObjectService = getService(BusinessObjectService.class);
		SponsorHierarchy sponsorHierarchy = buildRolodex(sponsorHierarchyFixture);
		
		return businessObjectService.save(sponsorHierarchy); 
	}

	private SponsorHierarchy buildRolodex(SponsorHierarchyFixture sponsorHierarchyFixture) {
		SponsorHierarchy sponsorHierarchy = new SponsorHierarchy();
		
		sponsorHierarchy.setSponsorCode(sponsorHierarchyFixture.getSponsorCode());
		sponsorHierarchy.setHierarchyName(sponsorHierarchyFixture.getHierarchyName());
		sponsorHierarchy.setUpdateTimestamp(new Timestamp(System.currentTimeMillis()));
		sponsorHierarchy.setUpdateUser(PersonFixture.UAR_TEST_001.getPrincipalName());
		sponsorHierarchy.setVersionNumber(0L);
		return sponsorHierarchy;
	}
}
