package edu.arizona.kra.irb.associateworkflow;

import java.util.Arrays;
import java.util.List;

public class ProtocolAssociateWorkflowConstants {
	
	public static final String ASSOCIATE_CUSTOM_ATTRIBUTE_NAME = "Associate";
	
	public static final String TRACKING_COMMENT_CUSTOM_ATTRIBUTE_NAME = "Tracking Comments";
	
	public static final List<String> MULTISELECT_FIELDS = Arrays.asList( 
			"protocolStatusCode",
			"submissionTypeCode",
			"submissionStatusCode",
			"committeeId");
	
}
