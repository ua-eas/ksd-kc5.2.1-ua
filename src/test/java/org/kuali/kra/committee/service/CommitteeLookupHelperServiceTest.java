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
package org.kuali.kra.committee.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kra.committee.bo.Committee;
import org.kuali.kra.committee.document.CommitteeDocument;
import org.kuali.kra.committee.lookup.CommitteeLookupableHelperServiceImpl;
import org.kuali.kra.test.fixtures.PermissionFixture;
import org.kuali.kra.test.fixtures.PersonFixture;
import org.kuali.kra.test.fixtures.RoleFixture;
import org.kuali.kra.test.fixtures.UnitFixture;
import org.kuali.kra.test.helpers.PermissionTestHelper;
import org.kuali.kra.test.helpers.PersonTestHelper;
import org.kuali.kra.test.helpers.RolePermissionTestHelper;
import org.kuali.kra.test.helpers.RoleTestHelper;
import org.kuali.kra.test.infrastructure.KcUnitTestBase;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kim.impl.role.RoleBo;
import org.kuali.rice.kim.impl.role.RoleMemberBo;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;

@SuppressWarnings("deprecation")
public class CommitteeLookupHelperServiceTest extends KcUnitTestBase {
    
    private static final int NUMBER_LOOKUP_CRITERIA_FIELDS = 9;
    private static final String EDIT_URL = "../committeeCommittee.do?committeeId=100&docTypeName=CommitteeDocument&methodToCall=docHandler&command=initiate";
    
    private CommitteeLookupableHelperServiceImpl committeeLookupableHelperServiceImpl;
    
    Person quickstart;
    RoleBo role1;


    @Before
    public void setUp() throws Exception {
        super.setUp();
        
        committeeLookupableHelperServiceImpl = new CommitteeLookupableHelperServiceImpl();
        committeeLookupableHelperServiceImpl.setBusinessObjectClass(Committee.class);
        
        getBusinessObjectService().deleteMatching(RoleMemberBo.class, new HashMap<String, Object>());

        
        PersonTestHelper personTestHelper = new PersonTestHelper();
        personTestHelper.createPerson(PersonFixture.UAR_TEST_007);
        PersonService personService = getService(PersonService.class);
        quickstart = personService.getPersonByPrincipalName(PersonFixture.UAR_TEST_007.getPrincipalName());

        RoleTestHelper roleTestHelper = new RoleTestHelper();
        RolePermissionTestHelper rolePermissionTestHelper = new RolePermissionTestHelper();
        
        PermissionTestHelper permissionTestHelper = new PermissionTestHelper();
        permissionTestHelper.createPermission(PermissionFixture.MODIFY_COMMITTEE);
        

        role1 = roleTestHelper.createRole(RoleFixture.USER);
        roleTestHelper.addPersonToRole(quickstart, RoleFixture.USER);
       
        rolePermissionTestHelper.createRolePermission(PermissionFixture.MODIFY_COMMITTEE.getPermId(), role1.getId());
        
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
        
        committeeLookupableHelperServiceImpl = null;
    }

    /**
     * 
     * This method is to test getrows.  The lookup fields will be updated and make sure 
     * a few of the drop down fields are sert as dropdown_refresh 
     */
    @Test
    public void testGetRows() {
        List<Row> rows = committeeLookupableHelperServiceImpl.getRows();
        assertEquals(rows.size(), NUMBER_LOOKUP_CRITERIA_FIELDS);
        for (Row row : rows) {
            for (Field field : row.getFields()) {
                if (field.getPropertyName().equals("researchAreaCode")) {
                    assertDropDownField(field, "researchAreaCode", "org.kuali.kra.bo.ResearchArea");
                } else if (field.getPropertyName().equals("memberName")) {
                     assertDropDownField(field, "personName", "org.kuali.kra.committee.bo.CommitteeMembership");
                }
            }
        }
    }

    /**
     * 
     * This method to check the 'edit' link is correct
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
    public void testGetCustomActionUrl() {
        List pkNames = new ArrayList();
        pkNames.add("committeeId");
        Committee committee = new Committee();
        committee.setCommitteeId("100");
        committee.setHomeUnitNumber(UnitFixture.TEST_1.getUnitNumber());
        CommitteeDocument document = new CommitteeDocument();
        document.setDocumentNumber("101");
        committee.setCommitteeDocument(document);
        List<HtmlData> actionUrls = committeeLookupableHelperServiceImpl.getCustomActionUrls(committee,pkNames);
        assertEquals(((HtmlData.AnchorHtmlData) actionUrls.get(0)).getHref(), EDIT_URL);                
    
    }

    /*
     * 
     * This method is to set up protocol for get inquiry url test
     * @return
     */
    private void assertDropDownField(Field field, String keyName, String className) {
        assertEquals(field.getFieldConversions(), keyName + ":" + field.getPropertyName());
        assertEquals(field.getLookupParameters(), field.getPropertyName() + ":" + keyName);
        assertEquals(field.getInquiryParameters(), field.getPropertyName() + ":" + keyName);
        assertEquals(field.getQuickFinderClassNameImpl(), className);

    }

}
