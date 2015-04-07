package org.kuali.kra.test.helpers;

import org.kuali.kra.bo.Sponsor;
import org.kuali.kra.service.SponsorService;
import org.kuali.kra.test.fixtures.SponsorFixture;
import org.kuali.rice.krad.service.BusinessObjectService;

public class SponsorTestHelper extends TestHelper {
	
	public Sponsor createSponsor(SponsorFixture sponsorFixture) {
		
		// If it already exists, return it
		Sponsor sponsor = getSponsor(sponsorFixture);
		if(sponsor != null) {
			return sponsor;
		}
		
		// Does not exist -- create, persist, and return it
		BusinessObjectService businessObjectService = getService(BusinessObjectService.class);
		sponsor = buildSponsor(sponsorFixture);
		return businessObjectService.save(sponsor);

	}
	
	private Sponsor buildSponsor(SponsorFixture sponsorFixture) {
		Sponsor sponsor = new Sponsor();
		sponsor.setSponsorCode(sponsorFixture.getSponsorCode());
		sponsor.setSponsorName(sponsorFixture.getSponsorName());
		sponsor.setSponsorTypeCode(sponsorFixture.getSponsorTypeCode());
		sponsor.setRolodexId(sponsorFixture.getRolodexId());
		sponsor.setOwnedByUnit(sponsorFixture.getOwnedByUnit());
		sponsor.setActive(true);
		sponsor.setVersionNumber(0L);
		return sponsor;
	}

	private Sponsor getSponsor(SponsorFixture sponsorFixture) {
		return getService(SponsorService.class).getSponsor(sponsorFixture.getSponsorCode());
	}
}
