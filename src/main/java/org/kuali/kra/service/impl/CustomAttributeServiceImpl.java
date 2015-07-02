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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kra.bo.ArgValueLookup;
import org.kuali.kra.bo.CustomAttributeDataType;
import org.kuali.kra.bo.CustomAttributeDocValue;
import org.kuali.kra.bo.CustomAttributeDocument;
import org.kuali.kra.bo.DocumentCustomData;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.kra.service.CustomAttributeService;
import org.kuali.rice.core.framework.persistence.ojb.conversion.OjbCharBooleanConversion;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.WorkflowDocumentFactory;
import org.kuali.rice.kew.api.document.attribute.WorkflowAttributeDefinition;
import org.kuali.rice.kns.service.BusinessObjectDictionaryService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * This class provides the implementation of the Custom Attribute Service.
 * It provides service methods related to custom attributes.
 */
@SuppressWarnings( { "deprecation", "unchecked", "rawtypes", "unused" } )
public class CustomAttributeServiceImpl implements CustomAttributeService {
	private static class CustomAttributeDocumentConstants {
		public static final String CUSTOM_ATTRIBUTE_ID = "customAttributeId";
		public static final String DOCUMENT_TYPE_CODE = "documentTypeName";
		public static final String IS_REQUIRED = "required";
		public static final String OBJ_ID = "objectId";
		public static final String TYPE_NAME = "typeName";
		public static final String ACTIVE_FLAG = "active";
		public static final String UPDATE_TIMESTAMP = "updateTimestamp";
		public static final String UPDATE_USER = "updateUser";
		public static final String VER_NBR = "versionNumber";
	}

	private static class CustomAttributeDocValueConstants {
		public static final String DOCUMENT_NUMBER = "documentNumber";
		public static final String CUSTOM_ATTRIBUTE_ID = "customAttributeId";
		public static final String OBJ_ID = "objectId";
		public static final String UPDATE_TIMESTAMP = "updateTimestamp";
		public static final String UPDATE_USER = "updateUser";
		public static final String VALUE = "value";
		public static final String VER_NBR = "versionNumber";
	}

    private static final String ARGVALUELOOKUPE_CLASS = "org.kuali.kra.bo.ArgValueLookup";
    private BusinessObjectService businessObjectService;

	@Override
	public Map<String, CustomAttributeDocument> getCustomAttributeDocuments( String documentNumber, String documentTypeCode ) {
		Map<String, CustomAttributeDocument> allCustomAttributeDocuments = getAllCustomAttributeDocuments( documentTypeCode );
		Map<String, CustomAttributeDocument> thisCustomAttributeDocuments = new HashMap<String, CustomAttributeDocument>();
		Map<String, String> fieldValues = new HashMap<String, String>();
		fieldValues.put( CustomAttributeDocValueConstants.DOCUMENT_NUMBER, documentNumber );
		List<CustomAttributeDocValue> docValues = (List<CustomAttributeDocValue>) getBusinessObjectService().findMatching( CustomAttributeDocValue.class, fieldValues );
		if ( docValues.size() > 0 ) {
			for ( CustomAttributeDocValue docValue : docValues ) {
				for ( Entry<String, CustomAttributeDocument> entry : allCustomAttributeDocuments.entrySet() ) {
					String entryId = entry.getValue().getCustomAttributeId().toString();
					String docValueId = docValue.getCustomAttributeId().toString();
					if ( StringUtils.equals( entryId, docValueId ) ) {
						thisCustomAttributeDocuments.put( entryId, entry.getValue() );
					}
				}
			}
		}
		return thisCustomAttributeDocuments;
	}
	
	@Override
	public Map<String, CustomAttributeDocument> getAllCustomAttributeDocuments( String documentTypeCode ) {
		Map<String, String> fieldValues = new HashMap<String, String>();
		fieldValues.put( CustomAttributeDocumentConstants.DOCUMENT_TYPE_CODE, documentTypeCode );
		List<CustomAttributeDocument> customAttributeDocumentList = (List<CustomAttributeDocument>) getBusinessObjectService().findMatching( CustomAttributeDocument.class, fieldValues );
		Map<String, CustomAttributeDocument> allCustomAttributeDocuments = new HashMap<String, CustomAttributeDocument>();
		for ( CustomAttributeDocument customAttributeDocument : customAttributeDocumentList ) {
			String entryId = customAttributeDocument.getCustomAttributeId().toString();
			allCustomAttributeDocuments.put( entryId, customAttributeDocument );
		}
		return allCustomAttributeDocuments;
	}
	
	@Override
	public Map<String, CustomAttributeDocument> getActiveCustomAttributeDocuments( String documentTypeCode ) {
		Map<String, String> fieldValues = new HashMap<String, String>();
		fieldValues.put( CustomAttributeDocumentConstants.DOCUMENT_TYPE_CODE, documentTypeCode );
		fieldValues.put( CustomAttributeDocumentConstants.ACTIVE_FLAG, OjbCharBooleanConversion.DATABASE_BOOLEAN_TRUE_STRING_REPRESENTATION );
		List<CustomAttributeDocument> customAttributeDocumentList = (List<CustomAttributeDocument>) getBusinessObjectService().findMatching( CustomAttributeDocument.class, fieldValues );
		Map<String, CustomAttributeDocument> activeCustomAttributeDocuments = new HashMap<String, CustomAttributeDocument>();
		for ( CustomAttributeDocument customAttributeDocument : customAttributeDocumentList ) {
			String entryId = customAttributeDocument.getCustomAttributeId().toString();
			activeCustomAttributeDocuments.put( entryId, customAttributeDocument );
		}
		return activeCustomAttributeDocuments;
	}
    
    protected HashSet<Long> getCurrentCustomAttributeIds(List<? extends DocumentCustomData> customDataList) {
        HashSet<Long> customIds = new HashSet<Long>();
        for(DocumentCustomData customData : customDataList) {
            customIds.add(customData.getCustomAttributeId());
        }
        return customIds;
    }

    @Override
    public void setCustomAttributeKeyValue(String documentNumber, Map<String, CustomAttributeDocument> customAttributeDocuments, String attributeName, String networkId) {
        WorkflowDocument workflowDocument = WorkflowDocumentFactory.loadDocument(networkId, documentNumber);
        
        // Not sure to delete all the content, but there is no other options
        workflowDocument.clearAttributeContent();  
        WorkflowAttributeDefinition customDataDef = WorkflowAttributeDefinition.Builder.create(attributeName).build();
        WorkflowAttributeDefinition.Builder refToUpdate = WorkflowAttributeDefinition.Builder.create(customDataDef);
        
        if (customAttributeDocuments != null) {
            for (Map.Entry<String, CustomAttributeDocument> customAttributeDocumentEntry:customAttributeDocuments.entrySet()) {
                CustomAttributeDocument customAttributeDocument = customAttributeDocumentEntry.getValue();
                if (StringUtils.isNotBlank(customAttributeDocument.getCustomAttribute().getValue())) {
                    refToUpdate.addPropertyDefinition(customAttributeDocument.getCustomAttribute().getName(), StringEscapeUtils.escapeXml(customAttributeDocument.getCustomAttribute().getValue()));
                }
            }
        }
        workflowDocument.addAttributeDefinition(refToUpdate.build());
        workflowDocument.saveDocumentData();    
    }

    /**
     * Accessor for <code>{@link BusinessObjectService}</code>
     *
     * @param bos BusinessObjectService
     */
    public void setBusinessObjectService(BusinessObjectService bos) {
        businessObjectService = bos;
    }

    /**
     * Accessor for <code>{@link BusinessObjectService}</code>
     *
     * @return BusinessObjectService
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    @Override
    public CustomAttributeDataType getCustomAttributeDataType(String dataTypeCode) {

        Map<String, String> primaryKeys = new HashMap<String, String>();
        if (StringUtils.isNotEmpty(dataTypeCode)) {
            primaryKeys.put("dataTypeCode", dataTypeCode);
            return (CustomAttributeDataType)businessObjectService.findByPrimaryKey(CustomAttributeDataType.class, primaryKeys);
        }
        return null;
        
    }

    @Override
    public List getLookupReturns(String lookupClass) throws Exception {
        List<String> lookupReturns = new ArrayList<String>();
        if (ARGVALUELOOKUPE_CLASS.equals(lookupClass)) {
            for (ArgValueLookup argValueLookup : (List<ArgValueLookup>) businessObjectService.findAll(ArgValueLookup.class)) {
                if (!lookupReturns.contains(argValueLookup.getArgumentName())) {
                    lookupReturns.add(argValueLookup.getArgumentName());
                }
            }
            Collections.sort(lookupReturns);
        }
        else {
            BusinessObjectDictionaryService businessDictionaryService = (BusinessObjectDictionaryService) KraServiceLocator
                    .getService(Constants.BUSINESS_OBJECT_DICTIONARY_SERVICE_NAME);
            lookupReturns = businessDictionaryService.getLookupFieldNames(Class.forName(lookupClass));
        }
        return lookupReturns;
    }
    
    @Override
    public String getLookupReturnsForAjaxCall(String lookupClass) throws Exception {
        List lookupFieldNames = getLookupReturns(lookupClass);
        String attributeNames="";
        for (Object attributeName : lookupFieldNames) {
            attributeNames += "," + attributeName +";"+ (ARGVALUELOOKUPE_CLASS.equals(lookupClass) ? attributeName : KraServiceLocator.getService(DataDictionaryService.class).getAttributeLabel(lookupClass,attributeName.toString()));
        }
        return attributeNames;
    }
    
}
