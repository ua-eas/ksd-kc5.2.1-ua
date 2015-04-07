package org.kuali.kra.test.helpers;

import org.kuali.kra.bo.CustomAttribute;
import org.kuali.kra.bo.CustomAttributeDocument;
import org.kuali.kra.test.fixtures.CustomAttributeDocumentFixture;
import org.kuali.kra.test.fixtures.CustomAttributeFixture;
import org.kuali.rice.krad.service.BusinessObjectService;

public class CustomAttributeDocumentTestHelper extends TestHelper {

	public CustomAttributeDocument createCustomAttributeDocuments(CustomAttributeFixture customAttributeFixture, CustomAttributeDocumentFixture customAttributeDocumentFixture) {
		BusinessObjectService businessObjectService = getService(BusinessObjectService.class);
		CustomAttribute customAttribute = buildCustomAttribute(customAttributeFixture);
        CustomAttributeDocument customAttributeDocument = buildCustomAttributeDocument(customAttributeDocumentFixture, customAttribute);
        businessObjectService.save(customAttributeDocument);
        return customAttributeDocument;
    }

    private static CustomAttributeDocument buildCustomAttributeDocument(CustomAttributeDocumentFixture customAttributeDocumentFixture, CustomAttribute customAttribute) {
        CustomAttributeDocument customAttributeDocument = new CustomAttributeDocument();
        customAttributeDocument.setCustomAttributeId(customAttribute.getId());
        customAttributeDocument.setDocumentTypeName(customAttributeDocumentFixture.getDocumentTypeCode());
        customAttributeDocument.setRequired(customAttributeDocumentFixture.isRequired());
        customAttributeDocument.setActive(customAttributeDocumentFixture.isActive());

        customAttributeDocument.setCustomAttribute(customAttribute);
        return customAttributeDocument;
    }

   private static CustomAttribute buildCustomAttribute(CustomAttributeFixture customAttributeFixture) {
        CustomAttribute customAttribute = new CustomAttribute();

        customAttribute.setId(customAttributeFixture.getId());
        customAttribute.setName(customAttributeFixture.getName());
        customAttribute.setLabel(customAttributeFixture.getLabel());
        customAttribute.setDataTypeCode(customAttributeFixture.getDataTypeCode());
        customAttribute.setDataLength(customAttributeFixture.getDataLength());
        customAttribute.setGroupName(customAttributeFixture.getGroupName());

        return customAttribute;
    }
}
