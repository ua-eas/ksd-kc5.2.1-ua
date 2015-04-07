package org.kuali.kra.test.helpers;

import org.kuali.kra.test.fixtures.PermissionFixture;
import org.kuali.rice.kim.impl.permission.PermissionBo;
import org.kuali.rice.krad.service.BusinessObjectService;

public class PermissionTestHelper extends TestHelper {

	public PermissionBo createPermission(PermissionFixture permissionFixture) {
		
		PermissionBo permission = new PermissionBo();
		BusinessObjectService businessObjectService = getService(BusinessObjectService.class);
		permission = buildPermission(permissionFixture);

		return businessObjectService.save(permission);		
	}
	
	
	private PermissionBo buildPermission(PermissionFixture permissionFixture) {
		PermissionBo permission = new PermissionBo();
		permission.setId(permissionFixture.getPermId());
		permission.setNamespaceCode(permissionFixture.getNamespaceCode());
		permission.setName(permissionFixture.getName());
		permission.setActive(permissionFixture.isActive());
		return permission;
	}
	
}
