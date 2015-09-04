package edu.arizona.kra.award.home;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kra.award.document.AwardDocument;
import org.kuali.kra.award.home.Award;
import org.kuali.kra.award.notesandattachments.attachments.AwardAttachment;
import org.kuali.kra.service.VersionException;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.service.BusinessObjectService;

public class AwardServiceImpl extends org.kuali.kra.award.home.AwardServiceImpl {
	/*
	 * Note that we are creating a second reference to the boservice here
	 * because, in 5.2.1, the parent class has the businessObjectService
	 * instance variable set to private with no getter.
	 */
	protected BusinessObjectService businessObjectServiceChild;

	protected BusinessObjectService getBusinessObjectServiceChild() {
		return businessObjectServiceChild;
	}

	public void setBusinessObjectServiceChild(
			BusinessObjectService businessObjectServiceChild) {
		this.businessObjectServiceChild = businessObjectServiceChild;
	}

	@Override
	protected void incrementVersionNumberIfCanceledVersionsExist(Award award) {
		BusinessObjectService boService = getBusinessObjectServiceChild();
		List<Award> awardList = (List<Award>) boService.findMatching(
				Award.class, getHashMap(award.getAwardNumber()));

		// Assuming that there will be an Award Number and non-zero records from
		// Award table
		Integer maxSequenceNumber = 1;
		for (Award aw : awardList) {
			if (aw.getSequenceNumber() > maxSequenceNumber) {
				maxSequenceNumber = aw.getSequenceNumber();
			}
		}
		award.setSequenceNumber(maxSequenceNumber + 1);
	}

	@Override
	protected Map<String, String> getHashMap(String awardNumber) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("awardNumber", awardNumber);
		return map;
	}
	
	
	

    public AwardDocument createNewAwardVersion(AwardDocument awardDocument) throws VersionException, WorkflowException {
        AwardDocument newVersion = super.createNewAwardVersion(awardDocument);
        synchSubAwardFundingSources(awardDocument, newVersion);
        return newVersion;
    }
    
    protected void synchSubAwardFundingSources(AwardDocument oldAwardDocument, AwardDocument newAwardDocument){
        
    }
	
	
	

}
