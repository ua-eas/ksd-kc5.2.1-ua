/*
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kra.questionnaire.question;

import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.kra.test.fixtures.PermissionFixture;
import org.kuali.kra.test.fixtures.PersonFixture;
import org.kuali.kra.test.fixtures.RoleFixture;
import org.kuali.kra.test.helpers.PermissionTestHelper;
import org.kuali.kra.test.helpers.RolePermissionTestHelper;
import org.kuali.kra.test.helpers.RoleTestHelper;
import org.kuali.kra.test.infrastructure.KcUnitTestBase;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kim.impl.permission.PermissionBo;
import org.kuali.rice.kim.impl.role.RoleBo;
import org.kuali.rice.kim.impl.role.RolePermissionBo;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.util.GlobalVariables;

public class QuestionAuthorizationServiceTest extends KcUnitTestBase {

	 Person quickstart;
	 Person jtester;
	 Person woods;
	
	 PermissionBo permission1;
	 PermissionBo permission2;

	 RoleBo role1;
	 RoleBo role2;

	
	@Before
	public void setUp() throws Exception {
		super.setUp();
		 // Deleting out an existing permission id for the view questionnaire from
		 // the RolePermission and permission table. Could not create a new
		 // permission with the same name and namespace code
		
		 Map<String, Object> attrs = new HashMap<String, Object>();
		 attrs.put("permissionId", "1081");
		 attrs.put("permissionId", "1080");
		 getBusinessObjectService().deleteMatching(RolePermissionBo.class, attrs);
		 attrs.clear();
		 attrs.put("id", "1081");
		 attrs.put("id", "1080");
		 getBusinessObjectService().deleteMatching(PermissionBo.class, attrs);
		
		 PersonService personService = getService(PersonService.class);
		
		 quickstart = personService.getPersonByPrincipalName(PersonFixture.UAR_TEST_001.getPrincipalName());
		 jtester = personService.getPersonByPrincipalName(PersonFixture.UAR_TEST_002.getPrincipalName());
		 woods = personService.getPersonByPrincipalName(PersonFixture.UAR_TEST_003.getPrincipalName());
		
		 PermissionTestHelper permissionTestHelper = new PermissionTestHelper();
		 permission1 = permissionTestHelper.createPermission(PermissionFixture.VIEW_QUESTION);
		 permission2 = permissionTestHelper.createPermission(PermissionFixture.MODIFY_QUESTION);
		
		 RoleTestHelper roleTestHelper = new RoleTestHelper();
		 role1 = roleTestHelper.createRole(RoleFixture.VIEW_QUESTION);
		 role2 = roleTestHelper.createRole(RoleFixture.MODIFY_QUESTION);

		 roleTestHelper.addPersonToRole(quickstart, RoleFixture.MODIFY_QUESTION);
		 roleTestHelper.addPersonToRole(jtester, RoleFixture.VIEW_QUESTION);
		
		 RolePermissionTestHelper rolePermissionTestHelper = new RolePermissionTestHelper();
		 rolePermissionTestHelper.createRolePermission(permission1.getId(), role1.getId());
		 rolePermissionTestHelper.createRolePermission(permission2.getId(), role2.getId());

	} 
	
	

    @Test
    public void permissionModifyQuestionTest() {
 
        GlobalVariables.setUserSession(new UserSession(quickstart.getPrincipalName()));
        assertTrue(KraServiceLocator.getService(QuestionAuthorizationService.class).hasPermission(PermissionFixture.MODIFY_QUESTION.getName()));
        GlobalVariables.setUserSession(new UserSession(jtester.getPrincipalName()));
        assertFalse(KraServiceLocator.getService(QuestionAuthorizationService.class).hasPermission(PermissionFixture.MODIFY_QUESTION.getName()));

    }
    @Test
    public void permissionViewQuestionTest() {
 
        GlobalVariables.setUserSession(new UserSession(jtester.getPrincipalName()));
        assertTrue(KraServiceLocator.getService(QuestionAuthorizationService.class).hasPermission(PermissionFixture.VIEW_QUESTION.getName()));
        GlobalVariables.setUserSession(new UserSession(woods.getPrincipalName()));
        assertFalse(KraServiceLocator.getService(QuestionAuthorizationService.class).hasPermission(PermissionFixture.VIEW_QUESTION.getName()));

    }
    
}
