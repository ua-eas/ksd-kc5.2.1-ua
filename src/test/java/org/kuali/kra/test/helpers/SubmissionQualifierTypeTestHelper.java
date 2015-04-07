package org.kuali.kra.test.helpers;

import org.kuali.kra.irb.actions.submit.ProtocolSubmissionQualifierType;
import org.kuali.kra.test.fixtures.SubmissionQualifierTypeFixture;
import org.kuali.rice.krad.service.BusinessObjectService;

public class SubmissionQualifierTypeTestHelper extends TestHelper {

	public ProtocolSubmissionQualifierType createSubmissionQualifierType(SubmissionQualifierTypeFixture submissionQualifierTypeFixture) {
		// If it already exists, return it
		ProtocolSubmissionQualifierType type = getSubmissionQualifierType(submissionQualifierTypeFixture);
		if (type != null) {
			return type;
		}

		// Does not exist -- create, persist, and return it
		BusinessObjectService businessObjectService = getService(BusinessObjectService.class);
		type = buildSubmissionQaulifierType(submissionQualifierTypeFixture);
		return businessObjectService.save(type);
	}

	private ProtocolSubmissionQualifierType buildSubmissionQaulifierType(SubmissionQualifierTypeFixture submissionQualifierTypeFixture) {
		ProtocolSubmissionQualifierType type = new ProtocolSubmissionQualifierType();
		type.setSubmissionQualifierTypeCode(submissionQualifierTypeFixture.getSubmissionQualifierTypeCode());
		type.setDescription(submissionQualifierTypeFixture.getDescription());
		return type;
	}

	private ProtocolSubmissionQualifierType getSubmissionQualifierType(SubmissionQualifierTypeFixture submissionQualifierTypeFixture) {
		return getService(BusinessObjectService.class).findBySinglePrimaryKey(ProtocolSubmissionQualifierType.class, submissionQualifierTypeFixture.getSubmissionQualifierTypeCode());
	}

}
