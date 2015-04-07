package org.kuali.kra.test.helpers;

import org.kuali.kra.irb.actions.submit.ExpeditedReviewCheckListItem;
import org.kuali.kra.test.fixtures.ExpeditedReviewCheckListFixture;
import org.kuali.rice.krad.service.BusinessObjectService;



public class ExpeditedReviewCheckListTestHelper extends TestHelper{

	public ExpeditedReviewCheckListItem createExpeditedReviewCheckList(ExpeditedReviewCheckListFixture expeditedReviewCheckListFixture) {
		ExpeditedReviewCheckListItem checkListItem = getExpeditedReviewCheckListItem(expeditedReviewCheckListFixture);
		if(checkListItem != null) {
			return checkListItem;
		}
		
		checkListItem = buildExpeditedReviewCheckListItem(expeditedReviewCheckListFixture);
		return getService(BusinessObjectService.class).save(checkListItem);
	}

	private ExpeditedReviewCheckListItem buildExpeditedReviewCheckListItem(ExpeditedReviewCheckListFixture expeditedReviewCheckListFixture) {
		ExpeditedReviewCheckListItem checkListItem = new ExpeditedReviewCheckListItem();
		checkListItem.setExpeditedReviewCheckListCode(expeditedReviewCheckListFixture.getCode());
		checkListItem.setDescription(expeditedReviewCheckListFixture.getDescription());
		return checkListItem;
	}

	private ExpeditedReviewCheckListItem getExpeditedReviewCheckListItem(ExpeditedReviewCheckListFixture expeditedReviewCheckListFixture) {
		return getService(BusinessObjectService.class).findBySinglePrimaryKey(ExpeditedReviewCheckListItem.class, expeditedReviewCheckListFixture.getCode());
	}

}
