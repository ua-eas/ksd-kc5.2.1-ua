package edu.arizona.kra.irb.associateworkflow;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.kuali.kra.bo.CustomAttributeDocValue;

public interface ProtocolAssociateWorkflowSearchDao {
	
	public List<ProtocolAssociateWorkflowSearch> getProtocolAssociateWorkflowResults(Map<String, String> fieldValues, 
			boolean filterChairReviewComplete, String associate);

	public Collection<CustomAttributeDocValue> getCustomAttributeValuesForDocs(Collection<String> documentNumbers, String customAttributeFieldName);
}
