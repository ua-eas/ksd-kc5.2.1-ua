package edu.arizona.kra.subaward.bo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.kuali.kra.award.home.ContactUsage;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.kra.lookup.keyvalue.PrefixValuesFinder;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.PersistableBusinessObjectValuesFinder;
import org.kuali.rice.krad.service.KeyValuesService;

public class CustomSubAwardContactProjectRolesValuesFinder extends PersistableBusinessObjectValuesFinder {

	private static final String KEY_VALUES_SERVICE_NAME = "keyValuesService";
    
    class PBOComparator implements Comparator
    {    
        public int compare(Object kv1, Object kv2 )
        {    
            try
            {
                String desc1 = ((KeyValue)kv1).getValue();
                String desc2 = ((KeyValue)kv2).getValue();
                if (desc1 == null)
                {
                    desc1 = "";
                }
                if (desc2 == null)
                {
                    desc2 = "";
                }
                return desc1.compareTo(desc2);  
            }
            catch (Exception e)
            {
                return 0;
            }
        }
        
    }
    /**
     * Build the list of KeyValues using the key (keyAttributeName) and
     * label (labelAttributeName) of the list of all business objects found
     * for the BO class specified along with a "select" entry.
     * 
     * {@inheritDoc}
     */
    @Override
    public List<KeyValue> getKeyValues(){
    	
    	Collection<ContactUsage> contactUsageList = ((KeyValuesService)KraServiceLocator.getService(KEY_VALUES_SERVICE_NAME)).findAll(ContactUsage.class);
    	
        List<KeyValue> labels;
        List<KeyValue> returnLabels = new ArrayList<KeyValue>();
        
        labels = super.getKeyValues();
        Collections.sort(labels, new PBOComparator());
        
         
        for(KeyValue label : labels) {
        	for(ContactUsage contactUsage : contactUsageList) {
        		if(contactUsage.getContactTypeCode().equalsIgnoreCase(label.getKey())) {
        			returnLabels.add(new ConcreteKeyValue(label.getKey(), label.getValue()));
        		}
        	}
        }
        
        returnLabels.add(0, new ConcreteKeyValue(PrefixValuesFinder.getPrefixKey(), PrefixValuesFinder.getDefaultPrefixValue()));    
        
        return returnLabels;
    }
}
