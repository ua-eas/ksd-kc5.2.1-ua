package org.kuali.kra.test.helpers;

import org.kuali.kra.test.fixtures.RoleFixture;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.kim.impl.role.RoleBo;
import org.kuali.rice.krad.service.BusinessObjectService;


/**
 * A class the helps tests create and persist Roles from fixtures.
 */
public class RoleTestHelper extends TestHelper {

	public RoleBo createRole (RoleFixture roleFixture) {
		RoleBo role = new RoleBo();
		BusinessObjectService businessObjectService = getService(BusinessObjectService.class);
		role = buildRole(roleFixture);

		return businessObjectService.save(role);
	}
	
	public void addPersonToRole(Person person, RoleFixture role) {
    	getService(RoleService.class).assignPrincipalToRole(
    			person.getPrincipalId(),
    			role.getNamespaceCode(),
    			role.getRoleName(),
    			role.getQualifications());
    			role.getKimTypId();
    			role.isActive();
    }

	private RoleBo buildRole (RoleFixture roleFixture) {
		RoleBo role = new RoleBo();
		role.setId(roleFixture.getRoleId());
		role.setNamespaceCode(roleFixture.getNamespaceCode());
		role.setName(roleFixture.getRoleName());
		role.setKimTypeId(roleFixture.getKimTypId());
		role.setActive(roleFixture.isActive());
		return role;
	}
}
