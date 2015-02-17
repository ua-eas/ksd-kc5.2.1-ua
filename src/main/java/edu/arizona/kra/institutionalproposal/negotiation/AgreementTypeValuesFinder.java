package edu.arizona.kra.institutionalproposal.negotiation;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.core.util.KeyLabelPair;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;

public class AgreementTypeValuesFinder extends KeyValuesBase {

	@Override
	public List<KeyLabelPair> getKeyValues() {
		List<KeyLabelPair> result = new ArrayList<KeyLabelPair>();
		for (AgreementType type : AgreementType.values()) {
			result.add(new KeyLabelPair(type.name(), type.getDesc()));
		}
		return result;
	}

}
