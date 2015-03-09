package edu.arizona.kra.institutionalproposal.negotiationlog;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

public class LocationValuesFinder extends KeyValuesBase {

	@Override
	public List<KeyValue> getKeyValues() {
		List<KeyValue> result = new ArrayList<KeyValue>();
		for (Location type : Location.values()) {
			result.add(new ConcreteKeyValue(type.name(), type.getDesc()));
		}
		return result;
	}

}
