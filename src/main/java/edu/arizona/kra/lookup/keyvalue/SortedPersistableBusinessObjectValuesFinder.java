package edu.arizona.kra.lookup.keyvalue;

import java.util.Collections;
import java.util.List;

import org.kuali.kra.lookup.keyvalue.KeyValueComparator;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.keyvalues.PersistableBusinessObjectValuesFinder;

/**
 * This class is used to create a sorted list of key/value pairs
 * for any persistable business object.  It specifically does not
 * include "select" as its first element like other ValuesFinders.
 * 
 */
public class SortedPersistableBusinessObjectValuesFinder extends PersistableBusinessObjectValuesFinder<PersistableBusinessObject> {

	private static final long serialVersionUID = 2961645267860584209L;

	@Override
	public List<KeyValue> getKeyValues() {
		List<KeyValue> list = super.getKeyValues();
		
		Collections.sort(list, new KeyValueComparator());
		return list;
	}
}
