package edu.arizona.kra.subaward.bo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kra.award.home.ContactUsage;
import org.kuali.kra.bo.CoeusModule;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.kra.lookup.keyvalue.PrefixValuesFinder;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.PersistableBusinessObjectValuesFinder;
import org.kuali.rice.krad.service.BusinessObjectService;


public class CustomSubAwardContactProjectRolesValuesFinder extends PersistableBusinessObjectValuesFinder {

    class PBOComparator implements Comparator {    
        public int compare(Object kv1, Object kv2 ) {    
            try {
                String desc1 = ((KeyValue)kv1).getValue();
                String desc2 = ((KeyValue)kv2).getValue();
                
                if (desc1 == null) desc1 = "";
                if (desc2 == null) desc2 = "";
                
                return desc1.compareTo(desc2);  
            }
            catch (Exception e){
                return 0;
            }
        }
    }
    
    /**
     * Build the list of KeyValues for ContactTypes filtering them out by module code as dictated by the contact usage table
     * 
     * {@inheritDoc}
     */
    @Override
    public List<KeyValue> getKeyValues(){
        List<KeyValue> returnLabels = new ArrayList<KeyValue>(); 
        
        //first find the contact usage objects associated with the subawards module
        BusinessObjectService boService = KraServiceLocator.getService(BusinessObjectService.class);
        Map<String, String> criteriaMap = new HashMap<String, String>();
        criteriaMap.put("moduleCode", CoeusModule.SUBCONTRACTS_MODULE_CODE);
        
        List<ContactUsage> contactUsageList = (List<ContactUsage>) boService.findMatching( ContactUsage.class, criteriaMap);

        if ( !contactUsageList.isEmpty() ){
            List<KeyValue> contactTypesKVList = super.getKeyValues();
            
            for(ContactUsage contactUsage : contactUsageList) {
                //filter out contact types associated with subawards
                for(KeyValue contactType : contactTypesKVList) {
                    if( contactType.getKey().equalsIgnoreCase(contactUsage.getContactTypeCode()) ){
                        returnLabels.add(new ConcreteKeyValue(contactType.getKey(), contactType.getValue()));
                        break;
                    }
                }
            }
            
            Collections.sort(returnLabels, new PBOComparator());
        }
        returnLabels.add(0, new ConcreteKeyValue(PrefixValuesFinder.getPrefixKey(), PrefixValuesFinder.getDefaultPrefixValue()));    
        
        return returnLabels;
    }
}
