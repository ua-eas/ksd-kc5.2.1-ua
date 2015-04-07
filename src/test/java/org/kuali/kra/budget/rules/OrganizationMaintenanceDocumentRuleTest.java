/*
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.osedu.org/licenses/ECL-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kra.budget.rules;

import java.sql.Date;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kra.bo.Organization;
import org.kuali.kra.bo.OrganizationYnq;
import org.kuali.kra.bo.Ynq;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.kra.maintenance.MaintenanceRuleTestBase;
import org.kuali.kra.rules.OrganizationMaintenanceDocumentRule;
import org.kuali.kra.test.fixtures.OrgFixture;
import org.kuali.kra.test.fixtures.PersonFixture;
import org.kuali.kra.test.fixtures.RoleFixture;
import org.kuali.kra.test.helpers.OrgTestHelper;
import org.kuali.kra.test.helpers.RoleTestHelper;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kns.document.MaintenanceDocument;

@SuppressWarnings( "deprecation" )
public class OrganizationMaintenanceDocumentRuleTest extends MaintenanceRuleTestBase {
    private OrganizationMaintenanceDocumentRule rule = null;

    Person quickstart;
    
    @Before
    public void setUp() throws Exception {
        super.setUp();
        rule = new OrganizationMaintenanceDocumentRule();
        PersonService personService = getService(PersonService.class);
        quickstart = personService.getPersonByPrincipalName(PersonFixture.UAR_TEST_001.getPrincipalName());
        
        RoleTestHelper roleTestHelper = new RoleTestHelper();
        roleTestHelper.addPersonToRole(quickstart, RoleFixture.MODIFY_ORGANIZATIONS);
        
        OrgTestHelper orgTestHelper = new OrgTestHelper();
        orgTestHelper.createOrg(OrgFixture.ONE);
    }

    @After
    public void tearDown() throws Exception {
        rule = null;
        super.tearDown();
    }

    @Test
    public void testOK() throws Exception {

        Organization organization = new Organization();

        organization.setOrganizationId(OrgFixture.ONE.getOrgId());
        organization.setOrganizationName(OrgFixture.ONE.getOrgName());
        organization.setContactAddressId(OrgFixture.ONE.getContactAddressId());
        organization.setOrganizationYnqs(setupOrganizationYnq(organization, "test",KraServiceLocator.getService(DateTimeService.class).getCurrentSqlDate()));
        MaintenanceDocument organizationDocument = newMaintDoc(organization);
        assertTrue(rule.processCustomRouteDocumentBusinessRules(organizationDocument));
        assertTrue(rule.processCustomApproveDocumentBusinessRules(organizationDocument));
    }

    @Test
    public void testMissingReviewDate() throws Exception {

        Organization organization = new Organization();

        organization.setOrganizationId(OrgFixture.ONE.getOrgId());
        organization.setOrganizationName(OrgFixture.ONE.getOrgName());
        organization.setContactAddressId(OrgFixture.ONE.getContactAddressId());
        organization.setOrganizationYnqs(setupOrganizationYnq(organization, "test", null));
        MaintenanceDocument organizationDocument = newMaintDoc(organization);
        assertFalse(rule.processCustomRouteDocumentBusinessRules(organizationDocument));
        assertFalse(rule.processCustomApproveDocumentBusinessRules(organizationDocument));
    }
    
    @Test
    public void testMissingExplanation() throws Exception {

        Organization organization = new Organization();

        organization.setOrganizationId(OrgFixture.ONE.getOrgId());
        organization.setOrganizationName(OrgFixture.ONE.getOrgName());
        organization.setContactAddressId(OrgFixture.ONE.getContactAddressId());
        organization.setOrganizationYnqs(setupOrganizationYnq(organization, "", KraServiceLocator.getService(DateTimeService.class).getCurrentSqlDate()));
        MaintenanceDocument organizationDocument = newMaintDoc(organization);
        assertFalse(rule.processCustomRouteDocumentBusinessRules(organizationDocument));
        assertFalse(rule.processCustomApproveDocumentBusinessRules(organizationDocument));
    }

    private List<OrganizationYnq> setupOrganizationYnq(Organization organization, String explanation, Date reviewDate) {
    	
    	Map<String, Object> criteria = new HashMap<String, Object>();
    	criteria.put("questionType", "O");
    	Collection<Ynq> ynqs = getBusinessObjectService().findMatching(Ynq.class, criteria);
        List<OrganizationYnq> organizationYnqs = organization.getOrganizationYnqs();
        for (Ynq ynq : ynqs) {
            OrganizationYnq organizationYnq = new OrganizationYnq();
            organizationYnq.setYnq(ynq);
            organizationYnq.setQuestionId(ynq.getQuestionId());
            organizationYnq.setAnswer("Y");
            organizationYnq.setExplanation(explanation);
            organizationYnq.setReviewDate(reviewDate);
            if (StringUtils.isNotBlank(organization.getOrganizationId())) {
                organizationYnq.setOrganizationId(organization.getOrganizationId()); 
            }
            organizationYnqs.add(organizationYnq);
        }
        return organizationYnqs;
    }
}
