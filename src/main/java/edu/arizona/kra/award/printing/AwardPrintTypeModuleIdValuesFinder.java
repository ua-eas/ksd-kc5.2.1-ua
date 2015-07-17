package edu.arizona.kra.award.printing;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.uif.control.UifKeyValuesFinderBase;

@SuppressWarnings( "serial" )
public class AwardPrintTypeModuleIdValuesFinder extends UifKeyValuesFinderBase {

	 @Override
	    public List<KeyValue> getKeyValues() {
	        List<KeyValue> KeyValues = new ArrayList<KeyValue>();

	        KeyValues.add(new ConcreteKeyValue("", "select"));
	        for (AwardPrintTypeModuleIdConstants printTypeModuleIdConstants : AwardPrintTypeModuleIdConstants.values()) {
	            KeyValues.add(new ConcreteKeyValue(printTypeModuleIdConstants.getCode(), printTypeModuleIdConstants.getDescription()));
	        }
	        
	        return KeyValues; 
	    }
}
