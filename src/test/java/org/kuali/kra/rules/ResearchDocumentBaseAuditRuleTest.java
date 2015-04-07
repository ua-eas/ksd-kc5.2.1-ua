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
package org.kuali.kra.rules;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kra.bo.CustomAttribute;
import org.kuali.kra.bo.CustomAttributeDocValue;
import org.kuali.kra.bo.CustomAttributeDocument;
import org.kuali.kra.document.ResearchDocumentBase;
import org.kuali.kra.proposaldevelopment.document.ProposalDevelopmentDocument;
import org.kuali.kra.test.infrastructure.KcUnitTestBase;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.util.AuditCluster;
import org.kuali.rice.kns.util.AuditError;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;

/**
 * This class tests the ResearchDocumentBaseAuditRule class
 */
@SuppressWarnings("deprecation")
public class ResearchDocumentBaseAuditRuleTest extends KcUnitTestBase {

    private static final String DEFAULT_PROPOSAL_SPONSOR_CODE = "123456";
    private static final String DEFAULT_PROPOSAL_TITLE = "Project title";
    private static final String DEFAULT_PROPOSAL_ACTIVITY_TYPE = "1";
    private static final String DEFAULT_PROPOSAL_OWNED_BY_UNIT = "000002";
    private static final String PROPOSAL_TYPE_NEW = "1";
    private static final String ERROR_MAP_AUDIT_ENTRY_KEY = "CustomDataProjectInformationErrors";

    private DocumentService documentService = null;
    private ResearchDocumentBaseAuditRule auditRule = null;

    private Date defaultProposalRequestedStartDate = null;
    private Date defaultProposalRequestedEndDate = null;


    @Before
    public void setUp() throws Exception {
        super.setUp();
        documentService = KRADServiceLocatorWeb.getDocumentService();
        auditRule = new ResearchDocumentBaseAuditRule();
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        defaultProposalRequestedStartDate = new Date(dateFormat.parse("08/14/2007").getTime());
        defaultProposalRequestedEndDate = new Date(dateFormat.parse("08/21/2007").getTime());
    }

    @After
    public void tearDown() throws Exception {
        documentService = null;
        auditRule = null;
        defaultProposalRequestedStartDate = null;
        defaultProposalRequestedEndDate = null;
        super.tearDown();
    }

    @Test public void testRequiredCustomAttributeFieldsMissing() throws Exception {
        ProposalDevelopmentDocument document = getNewDocument();

        List<CustomAttribute> requiredFields = new LinkedList<CustomAttribute>();

        Map<String, CustomAttributeDocument> customAttributeDocuments = ((ResearchDocumentBase)document).getCustomAttributeDocuments();
        for (Map.Entry<String, CustomAttributeDocument> customAttributeDocumentEntry: customAttributeDocuments.entrySet()) {
            CustomAttributeDocument customAttributeDocument = customAttributeDocumentEntry.getValue();
            CustomAttribute customAttribute = customAttributeDocument.getCustomAttribute();
            if (customAttributeDocument.isRequired()) {
                requiredFields.add(customAttribute);
            }
            CustomAttributeDocValue newValue = new CustomAttributeDocValue();
            newValue.setCustomAttribute(customAttributeDocument.getCustomAttribute());
            newValue.setCustomAttributeId(customAttributeDocument.getCustomAttributeId().longValue());
            newValue.setValue(null);
            document.getCustomDataList().add(newValue);          
        }

        assertFalse("Audit Rule should produce an audit error", auditRule.processRunAuditBusinessRules(document));
        assertEquals(requiredFields.size(), KNSGlobalVariables.getAuditErrorMap().get(ERROR_MAP_AUDIT_ENTRY_KEY).getAuditErrorList().size());

        int errorIndex = 0;
        for (CustomAttribute customAttribute : requiredFields) {
        	
            AuditCluster auditCluster = (AuditCluster)KNSGlobalVariables.getAuditErrorMap().get("CustomData" + StringUtils.deleteWhitespace(customAttribute.getGroupName()) + "Errors");

            assertEquals(requiredFields.size(), auditCluster.getSize());
            //assertEquals("Custom Data: " + customAttribute.getGroupName(), auditCluster.getLabel());
            assertEquals(customAttribute.getGroupName(), auditCluster.getLabel());
            assertEquals("Validation Errors", auditCluster.getCategory());
            AuditError auditError = (AuditError) auditCluster.getAuditErrorList().get(errorIndex);
            int index = 0;
            for (CustomAttributeDocValue value : document.getCustomDataList()) {
                if (value.getCustomAttributeId().longValue() == customAttribute.getId().longValue()) {
                    break;
                }
                index++;
            }
            assertEquals("customDataHelper.customDataList[" + index + "].value", auditError.getErrorKey());
            assertEquals("customData." + StringUtils.deleteWhitespace(customAttribute.getGroupName()), auditError.getLink());
            assertEquals(customAttribute.getLabel(), auditError.getParams()[0]);
            assertEquals(RiceKeyConstants.ERROR_REQUIRED, auditError.getMessageKey());
            
            errorIndex++;
        }

    }

    @Test public void testRequiredCustomAttributeFieldsPopulated() throws Exception {
        ProposalDevelopmentDocument document = getNewDocument();

        Map<String, CustomAttributeDocument> customAttributeDocuments = ((ResearchDocumentBase)document).getCustomAttributeDocuments();
        for (Map.Entry<String, CustomAttributeDocument> customAttributeDocumentEntry: customAttributeDocuments.entrySet()) {
            CustomAttributeDocument customAttributeDocument = customAttributeDocumentEntry.getValue();
            CustomAttribute customAttribute = customAttributeDocument.getCustomAttribute();
            if (customAttributeDocument.isRequired()) {
                //setting the value to a number since one of the required fields is an numeric field
                //and the audit rule now checks the fields type as well.
                customAttribute.setValue("5");
            }
        }

        assertTrue("Audit Rule shouldn't produce any audit errors", auditRule.processRunAuditBusinessRules(document));
        assertEquals(0, KNSGlobalVariables.getAuditErrorMap().size());
    }

    private ProposalDevelopmentDocument getNewDocument() throws WorkflowException {
        ProposalDevelopmentDocument document = (ProposalDevelopmentDocument) documentService.getNewDocument("ProposalDevelopmentDocument");
        document.initialize();

        setRequiredDocumentFields(document,
                DEFAULT_PROPOSAL_SPONSOR_CODE,
                DEFAULT_PROPOSAL_TITLE,
                defaultProposalRequestedStartDate,
                defaultProposalRequestedEndDate,
                DEFAULT_PROPOSAL_ACTIVITY_TYPE,
                PROPOSAL_TYPE_NEW,
                DEFAULT_PROPOSAL_OWNED_BY_UNIT);

        return document;
    }

    /**
     * This method sets required document fields
     * @param document ProposalDevelopmentDocument to set fields for
     * @param sponsorCode String Sponsor code for the document
     * @param title String title of document
     * @param requestedStartDateInitial String start date
     * @param requestedEndDateInitila String end date
     * @param activityTypeCode String activity type code
     * @param proposalTypeCode String proposal type code
     * @param ownedByUnit String owned by unit
     */
    private void setRequiredDocumentFields(ProposalDevelopmentDocument document, String sponsorCode, String title, Date requestedStartDateInitial, Date requestedEndDateInitial, String activityTypeCode, String proposalTypeCode, String ownedByUnit) {
        document.getDevelopmentProposal().setSponsorCode(sponsorCode);
        document.getDevelopmentProposal().setTitle(title);
        document.getDevelopmentProposal().setRequestedStartDateInitial(requestedStartDateInitial);
        document.getDevelopmentProposal().setRequestedEndDateInitial(requestedEndDateInitial);
        document.getDevelopmentProposal().setActivityTypeCode(activityTypeCode);
        document.getDevelopmentProposal().setProposalTypeCode(proposalTypeCode);
        document.getDevelopmentProposal().setOwnedByUnitNumber(ownedByUnit);
    }

}
