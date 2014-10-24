package edu.arizona.kra.protocol;

import java.util.HashMap;
import java.util.Map;

import org.kuali.kra.bo.CustomAttributeDocValue;
import org.kuali.kra.bo.CustomAttributeDocument;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.protocol.ProtocolDocumentBase;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.KRADPropertyConstants;

public class ProtocolCustomDataServiceImpl implements ProtocolCustomDataService {

	protected BusinessObjectService businessObjectService;
	
	
	@Override
	public void copyCustomDataAttributeValues(ProtocolDocumentBase src, ProtocolDocumentBase dest) {
    	for (Map.Entry<String, CustomAttributeDocument> entry: src.getCustomAttributeDocuments().entrySet()) {
            // Find the attribute value
            CustomAttributeDocument customAttributeDocument = entry.getValue();
            Map<String, Object> primaryKeys = new HashMap<String, Object>();
            primaryKeys.put(KRADPropertyConstants.DOCUMENT_NUMBER, src.getDocumentNumber());
            primaryKeys.put(Constants.CUSTOM_ATTRIBUTE_ID, customAttributeDocument.getCustomAttributeId());
            CustomAttributeDocValue customAttributeDocValue = (CustomAttributeDocValue)getBusinessObjectService().findByPrimaryKey(CustomAttributeDocValue.class, primaryKeys);
            
            // Store a new CustomAttributeDocValue using the new document's document number
            if (customAttributeDocValue != null) {
                CustomAttributeDocValue newDocValue = new CustomAttributeDocValue();
                newDocValue.setDocumentNumber(dest.getDocumentNumber());
                newDocValue.setCustomAttributeId(customAttributeDocument.getCustomAttributeId().longValue());
                newDocValue.setValue(customAttributeDocValue.getValue());
                getBusinessObjectService().save(newDocValue);
            }
        }
    	dest.refreshReferenceObject("customDataList");
	}


	protected BusinessObjectService getBusinessObjectService() {
		return businessObjectService;
	}


	public void setBusinessObjectService(BusinessObjectService businessObjectService) {
		this.businessObjectService = businessObjectService;
	}


}
