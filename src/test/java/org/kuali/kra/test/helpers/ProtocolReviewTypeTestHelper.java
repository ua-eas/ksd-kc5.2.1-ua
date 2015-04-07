package org.kuali.kra.test.helpers;

import org.kuali.kra.irb.actions.submit.ProtocolReviewType;
import org.kuali.kra.test.fixtures.ProtocolReviewTypeFixture;
import org.kuali.rice.krad.service.BusinessObjectService;

public class ProtocolReviewTypeTestHelper extends TestHelper {
	
	public ProtocolReviewType createReviewType(ProtocolReviewTypeFixture reviewTypeFixture) {
		ProtocolReviewType reviewType = getReviewType(reviewTypeFixture);
		if(reviewType != null) {
			return reviewType;
		}
		
		reviewType = buildReviewType(reviewTypeFixture);
		return getService(BusinessObjectService.class).save(reviewType);
	}
	
	private ProtocolReviewType buildReviewType(ProtocolReviewTypeFixture reviewTypeFixture) {
		ProtocolReviewType reviewType = new ProtocolReviewType();
		reviewType.setReviewTypeCode(reviewTypeFixture.getCode());
		reviewType.setDescription(reviewTypeFixture.getDescription());
		reviewType.setGlobalFlag(reviewTypeFixture.getGlobalFlag());
		return reviewType;
	}
	
	private ProtocolReviewType getReviewType(ProtocolReviewTypeFixture reviewTypeFixture) {
		return getService(BusinessObjectService.class).findBySinglePrimaryKey(ProtocolReviewType.class, reviewTypeFixture.getCode());
	}
	
	
	
}
