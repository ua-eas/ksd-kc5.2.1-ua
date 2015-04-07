package org.kuali.kra.test.helpers;

import java.util.Random;

import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.rice.kim.impl.role.RolePermissionBo;
import org.kuali.rice.krad.service.BusinessObjectService;

public class RolePermissionTestHelper extends TestHelper {

	public RolePermissionBo createRolePermission(String permissionId, String roleId) {
		RolePermissionBo rolePermission = new RolePermissionBo();
		rolePermission = buildRolePermission(permissionId, roleId);		
		return KraServiceLocator.getService(BusinessObjectService.class).save(rolePermission);
	}
	
	private RolePermissionBo buildRolePermission(String permissionId, String roleId) {
		RolePermissionBo rolePermission = new RolePermissionBo();
		rolePermission.setId(getRandomId());
		rolePermission.setRoleId(roleId);
		rolePermission.setPermissionId(permissionId);
		rolePermission.setActive(true);
		return rolePermission;
	}

	/*
	 * Returns a random string of length 8
	 */
	private String getRandomId() {
		StringBuilder sb = new StringBuilder();
		Random rand = new Random();
		for (int i = 0; i < 8; i++) {
			sb.append((char) (rand.nextInt((126 - 33) + 1) + 33)); // ascii values 33-126
		}
		return sb.toString();
	}

}
