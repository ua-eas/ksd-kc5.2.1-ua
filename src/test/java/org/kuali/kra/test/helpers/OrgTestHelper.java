package org.kuali.kra.test.helpers;

import org.kuali.kra.bo.Organization;
import org.kuali.kra.service.OrganizationService;
import org.kuali.kra.test.fixtures.OrgFixture;
import org.kuali.rice.krad.service.BusinessObjectService;

public class OrgTestHelper extends TestHelper {
	
	public Organization createOrg(OrgFixture orgFixture) {
		
		Organization org = getOrg(orgFixture);
		if(org != null) {
			return org;
		}
		
		BusinessObjectService businessObjectService = getService(BusinessObjectService.class);
		org = buildOrg(orgFixture);

		return businessObjectService.save(org);		
	}
	
	
	private Organization buildOrg(OrgFixture orgFixture) {
		Organization org = new Organization();
		org.setOrganizationName(orgFixture.getOrgName());
		org.setOrganizationId(orgFixture.getOrgId());
		org.setContactAddressId(orgFixture.getContactAddressId());
		return org;
	}
	
	
	private Organization getOrg(OrgFixture orgFixture) {
		return getService(OrganizationService.class).getOrganization(orgFixture.getOrgId());
	}
}
