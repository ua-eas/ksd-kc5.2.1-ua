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
package org.kuali.kra.service.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kra.bo.CustomAttribute;
import org.kuali.kra.bo.CustomAttributeDocValue;
import org.kuali.kra.bo.CustomAttributeDocument;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.kra.proposaldevelopment.document.ProposalDevelopmentDocument;
import org.kuali.kra.service.CustomAttributeService;
import org.kuali.kra.test.fixtures.CustomAttributeDocumentFixture;
import org.kuali.kra.test.fixtures.CustomAttributeFixture;
import org.kuali.kra.test.helpers.CustomAttributeDocumentTestHelper;
import org.kuali.kra.test.infrastructure.KcUnitTestBase;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.util.GlobalVariables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class tests CustomAttributeServiceImpl.
 */
public class CustomAttributeServiceImplTest extends KcUnitTestBase {


    private Map<String, CustomAttributeDocument> testCustomAttributeDocuments;
    private CustomAttributeDocument customAttributeDocument1;
    private CustomAttributeDocument customAttributeDocument2;

    private DocumentService documentService = null;
    private CustomAttributeService customAttributeService = null;
    
    private static final String TEST_DOCUMENT_TYPE_CODE = "PRDV";

    @Before
    public void setUp() throws Exception {
        super.setUp();
        
        testCustomAttributeDocuments = new HashMap<String, CustomAttributeDocument>();
        getBusinessObjectService().deleteMatching(CustomAttributeDocument.class, new HashMap<String, String>());
        
        CustomAttributeDocumentTestHelper customAttributeDocumentTestHelper = new CustomAttributeDocumentTestHelper();
        customAttributeDocument1 = customAttributeDocumentTestHelper.createCustomAttributeDocuments(CustomAttributeFixture.CUSTOM_ATTRIBUTE_1, CustomAttributeDocumentFixture.CUSTOM_ATTRIBUTE_DOCUMENT_1);
        customAttributeDocument2 = customAttributeDocumentTestHelper.createCustomAttributeDocuments(CustomAttributeFixture.CUSTOM_ATTRIBUTE_2, CustomAttributeDocumentFixture.CUSTOM_ATTRIBUTE_DOCUMENT_2);
        
        testCustomAttributeDocuments.put("1", customAttributeDocument1);
        testCustomAttributeDocuments.put("2", customAttributeDocument2);
        
        documentService = KRADServiceLocatorWeb.getDocumentService();
        customAttributeService = KraServiceLocator.getService(CustomAttributeService.class);
    }

    @After
    public void tearDown() throws Exception {
        GlobalVariables.setUserSession(null);
        testCustomAttributeDocuments = null;
        documentService = null;
        customAttributeService = null;
        super.tearDown();
    }


    @Test public void testGetDefaultCustomAttributesForDocumentType() throws Exception {
        Map<String, CustomAttributeDocument>customAttributeDocuments = customAttributeService.getDefaultCustomAttributeDocuments(TEST_DOCUMENT_TYPE_CODE, new ArrayList<CustomAttributeDocValue>());
        assertNotNull(customAttributeDocuments);
        assertEquals(testCustomAttributeDocuments.size(), customAttributeDocuments.size());

        for(Map.Entry<String, CustomAttributeDocument> testCustomAttributeDocumentEntry: testCustomAttributeDocuments.entrySet()) {
            CustomAttributeDocument testCustomAttributeDocument = testCustomAttributeDocumentEntry.getValue();
            CustomAttributeDocument customAttributeDocument = customAttributeDocuments.get(testCustomAttributeDocument.getCustomAttributeId().toString());
            assertNotNull(customAttributeDocument);
            assertEquals(testCustomAttributeDocument.getDocumentTypeName(), customAttributeDocument.getDocumentTypeName());
            assertEquals(testCustomAttributeDocument.isRequired(), customAttributeDocument.isRequired());
            CustomAttribute testCustomAttribute = testCustomAttributeDocument.getCustomAttribute();
            CustomAttribute customAttribute = customAttributeDocument.getCustomAttribute();

            assertEquals(testCustomAttribute.getId(), customAttribute.getId());
            assertEquals(testCustomAttribute.getName(), customAttribute.getName());
            assertEquals(testCustomAttribute.getLabel(), customAttribute.getLabel());
            assertEquals(testCustomAttribute.getDataTypeCode(), customAttribute.getDataTypeCode());
            assertEquals(testCustomAttribute.getDataLength(), customAttribute.getDataLength());
            assertEquals(testCustomAttribute.getGroupName(), customAttribute.getGroupName());
            assertEquals(testCustomAttribute.getLookupClass(), customAttribute.getLookupClass());
            assertEquals(testCustomAttribute.getLookupReturn(), customAttribute.getLookupReturn());
        }
    }

    @Test public void testGetDefaultCustomAttributesForDocumentTypeNullDocument() throws Exception {
        Map<String, CustomAttributeDocument>customAttributeDocuments = customAttributeService.getDefaultCustomAttributeDocuments(TEST_DOCUMENT_TYPE_CODE, new ArrayList<CustomAttributeDocValue>());
        assertNotNull(customAttributeDocuments);
        assertNotNull(testCustomAttributeDocuments);
        assertEquals(testCustomAttributeDocuments.size(), customAttributeDocuments.size());

        for(Map.Entry<String, CustomAttributeDocument> testCustomAttributeDocumentEntry: testCustomAttributeDocuments.entrySet()) {
            CustomAttributeDocument testCustomAttributeDocument = testCustomAttributeDocumentEntry.getValue();
            CustomAttributeDocument customAttributeDocument = customAttributeDocuments.get(testCustomAttributeDocument.getCustomAttributeId().toString());
            assertNotNull(customAttributeDocument);
            assertEquals(testCustomAttributeDocument.getDocumentTypeName(), customAttributeDocument.getDocumentTypeName());
            assertEquals(testCustomAttributeDocument.isRequired(), customAttributeDocument.isRequired());
            CustomAttribute testCustomAttribute = testCustomAttributeDocument.getCustomAttribute();
            CustomAttribute customAttribute = customAttributeDocument.getCustomAttribute();

            assertEquals(testCustomAttribute.getId(), customAttribute.getId());
            assertEquals(testCustomAttribute.getName(), customAttribute.getName());
            assertEquals(testCustomAttribute.getLabel(), customAttribute.getLabel());
            assertEquals(testCustomAttribute.getDataTypeCode(), customAttribute.getDataTypeCode());
            assertEquals(testCustomAttribute.getDataLength(), customAttribute.getDataLength());
            assertEquals(testCustomAttribute.getGroupName(), customAttribute.getGroupName());
            assertEquals(testCustomAttribute.getLookupClass(), customAttribute.getLookupClass());
            assertEquals(testCustomAttribute.getLookupReturn(), customAttribute.getLookupReturn());
        }
    }

    @Test public void testGetDefaultCustomAttributesFromNewDocument() throws Exception {
        ProposalDevelopmentDocument document = (ProposalDevelopmentDocument) documentService.getNewDocument(ProposalDevelopmentDocument.class);
        document.initialize();

        Map<String, CustomAttributeDocument>customAttributeDocuments = document.getCustomAttributeDocuments();
        assertNotNull(customAttributeDocuments);
        assertNotNull(testCustomAttributeDocuments);
        assertEquals(testCustomAttributeDocuments.size(), customAttributeDocuments.size());

        for(Map.Entry<String, CustomAttributeDocument> testCustomAttributeDocumentEntry: testCustomAttributeDocuments.entrySet()) {
            CustomAttributeDocument testCustomAttributeDocument = testCustomAttributeDocumentEntry.getValue();
            CustomAttributeDocument customAttributeDocument = customAttributeDocuments.get(testCustomAttributeDocument.getCustomAttributeId().toString());
            assertNotNull(customAttributeDocument);
            assertEquals(testCustomAttributeDocument.getDocumentTypeName(), customAttributeDocument.getDocumentTypeName());
            assertEquals(testCustomAttributeDocument.isRequired(), customAttributeDocument.isRequired());
            CustomAttribute testCustomAttribute = testCustomAttributeDocument.getCustomAttribute();
            CustomAttribute customAttribute = customAttributeDocument.getCustomAttribute();

            assertEquals(testCustomAttribute.getId(), customAttribute.getId());
            assertEquals(testCustomAttribute.getName(), customAttribute.getName());
            assertEquals(testCustomAttribute.getLabel(), customAttribute.getLabel());
            assertEquals(testCustomAttribute.getDataTypeCode(), customAttribute.getDataTypeCode());
            assertEquals(testCustomAttribute.getDataLength(), customAttribute.getDataLength());
            assertEquals(testCustomAttribute.getGroupName(), customAttribute.getGroupName());
            assertEquals(testCustomAttribute.getLookupClass(), customAttribute.getLookupClass());
            assertEquals(testCustomAttribute.getLookupReturn(), customAttribute.getLookupReturn());
        }
    }


    @Test public void testGetDefaultCustomAttributesFromSavedDocument() throws Exception {
    	ProposalDevelopmentDocument document = (ProposalDevelopmentDocument) documentService.getNewDocument(ProposalDevelopmentDocument.class);
        document.initialize();

        Map<String, CustomAttributeDocument>customAttributeDocuments = document.getCustomAttributeDocuments();
        assertNotNull(customAttributeDocuments);
        assertNotNull(testCustomAttributeDocuments);
        assertEquals(testCustomAttributeDocuments.size(), customAttributeDocuments.size());

        for(Map.Entry<String, CustomAttributeDocument> testCustomAttributeDocumentEntry: testCustomAttributeDocuments.entrySet()) {
            CustomAttributeDocument testCustomAttributeDocument = testCustomAttributeDocumentEntry.getValue();
            CustomAttributeDocument customAttributeDocument = customAttributeDocuments.get(testCustomAttributeDocument.getCustomAttributeId().toString());
            assertNotNull(customAttributeDocument);
            assertEquals(testCustomAttributeDocument.getDocumentTypeName(), customAttributeDocument.getDocumentTypeName());
            assertEquals(testCustomAttributeDocument.isRequired(), customAttributeDocument.isRequired());
            CustomAttribute testCustomAttribute = testCustomAttributeDocument.getCustomAttribute();
            CustomAttribute customAttribute = customAttributeDocument.getCustomAttribute();

            assertEquals(testCustomAttribute.getId(), customAttribute.getId());
            assertEquals(testCustomAttribute.getName(), customAttribute.getName());
            assertEquals(testCustomAttribute.getLabel(), customAttribute.getLabel());
            assertEquals(testCustomAttribute.getDataTypeCode(), customAttribute.getDataTypeCode());
            assertEquals(testCustomAttribute.getDataLength(), customAttribute.getDataLength());
            assertEquals(testCustomAttribute.getGroupName(), customAttribute.getGroupName());
            assertEquals(testCustomAttribute.getLookupClass(), customAttribute.getLookupClass());
            assertEquals(testCustomAttribute.getLookupReturn(), customAttribute.getLookupReturn());
        }
    }
    
    @SuppressWarnings( "rawtypes" )
	@Test public void testGetLookupReturns() throws Exception {
        List<String> properties = new ArrayList<String>();
        properties.add("degreeCode");
        properties.add("degreeLevel");
        properties.add("description");
        List lookupReturnFields = customAttributeService.getLookupReturns("org.kuali.kra.bo.DegreeType");
        assertEquals(properties.size(), lookupReturnFields.size());

        for(Object returnField : lookupReturnFields) {
            assertTrue(properties.contains(returnField));
        }
    }
    
    @Test
    public void testGetLookupReturnsForAjaxCall() throws Exception {
        String properties = ",degreeCode;Degree Code,degreeLevel;Degree Level,description;Description";
        String lookupReturnFields = customAttributeService.getLookupReturnsForAjaxCall("org.kuali.kra.bo.DegreeType");
        assertEquals(properties,lookupReturnFields);
    }

}
