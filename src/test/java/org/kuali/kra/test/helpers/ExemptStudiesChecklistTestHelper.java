package org.kuali.kra.test.helpers;

import org.kuali.kra.irb.actions.submit.ExemptStudiesCheckListItem;
import org.kuali.kra.test.fixtures.ExemptStudiesCheckListFixture;
import org.kuali.rice.krad.service.BusinessObjectService;

public class ExemptStudiesChecklistTestHelper extends TestHelper{
	
	public ExemptStudiesCheckListItem createExemptStudiesChecklist(ExemptStudiesCheckListFixture exemptStudiesChecklistFixture) {
		ExemptStudiesCheckListItem checkListItem = getExemptStudiesCheckListItem(exemptStudiesChecklistFixture);
		if(checkListItem != null) {
			return checkListItem;
		}
		
		checkListItem = buildExemptStudiesCheckListItem(exemptStudiesChecklistFixture);
		return getService(BusinessObjectService.class).save(checkListItem);
	}

	private ExemptStudiesCheckListItem buildExemptStudiesCheckListItem(ExemptStudiesCheckListFixture exemptStudiesChecklistFixture) {
		ExemptStudiesCheckListItem checkListItem = new ExemptStudiesCheckListItem();
		checkListItem.setExemptStudiesCheckListCode(exemptStudiesChecklistFixture.getCode());
		checkListItem.setDescription(exemptStudiesChecklistFixture.getDescription());
		return checkListItem;
	}

	private ExemptStudiesCheckListItem getExemptStudiesCheckListItem(ExemptStudiesCheckListFixture exemptStudiesChecklistFixture) {
		return getService(BusinessObjectService.class).findBySinglePrimaryKey(ExemptStudiesCheckListItem.class, exemptStudiesChecklistFixture.getCode());
	}
	
}
