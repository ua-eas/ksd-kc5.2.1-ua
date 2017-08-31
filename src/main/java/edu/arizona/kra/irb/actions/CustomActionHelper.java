/*
 * Copyright 2005-2016 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.arizona.kra.irb.actions;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.kra.irb.actions.ActionHelper;
import org.kuali.kra.irb.actions.amendrenew.ProtocolAmendmentBean;
import org.kuali.kra.irb.actions.correction.AdminCorrectionBean;
import org.kuali.kra.irb.actions.grantexemption.ProtocolGrantExemptionBean;
import org.kuali.kra.irb.onlinereview.ProtocolOnlineReviewService;
import org.kuali.kra.protocol.ProtocolBase;
import org.kuali.kra.protocol.actions.ActionHelperBase;
import org.kuali.kra.protocol.onlinereview.ProtocolOnlineReviewBase;
import org.kuali.rice.kew.actiontaken.ActionTakenValue;
import org.kuali.rice.kew.actiontaken.service.ActionTakenService;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.krad.util.ObjectUtils;

import edu.arizona.kra.irb.CustomProtocolForm;
import edu.arizona.kra.irb.actions.amendrenew.CustomProtocolAmendmentBean;
import edu.arizona.kra.irb.actions.correction.CustomAdminCorrectionBean;


/**
 * The form helper class for the Protocol Actions tab.
 */
public class CustomActionHelper extends org.kuali.kra.irb.actions.ActionHelper {
	private static final long serialVersionUID = -4934851153861075291L;
	
	private transient ActionTakenService actionTakenService;
	private transient ProtocolOnlineReviewService protocolOnlineReviewService;



	/**
     * Constructs an ActionHelper.
     * @param form the protocol form
     * @throws Exception 
     */
    public CustomActionHelper(CustomProtocolForm form) throws Exception {
        super(form);
    }


    @Override
    protected ProtocolAmendmentBean getNewProtocolAmendmentBeanInstanceHook(ActionHelperBase actionHelper) {
        return new CustomProtocolAmendmentBean((ActionHelper) actionHelper);
    }
    
    @Override
    protected AdminCorrectionBean getNewAdminCorrectionBeanInstanceHook(ActionHelperBase actionHelper) {
        return new CustomAdminCorrectionBean((ActionHelper) actionHelper);
    }

    /**
    * Builds an approval date, defaulting to the approval date from the protocol.
    *
    * If the approval date from the protocol is null, or if the protocol is new or a renewal, then if the committee has scheduled a meeting to approve the
    * protocol, sets to the scheduled approval date; otherwise, sets to the current date.
    *
    * @param protocol
    * @return a non-null approval date
    */
    @Override
    protected Date buildApprovalDate(ProtocolBase protocol) {
		Date approvalDate = protocol.getApprovalDate();

		if (approvalDate == null || protocol.isNew() || protocol.isRenewal()) {
			approvalDate = getLatestReviewApprovalDate(protocol);
			if (approvalDate == null) {
				approvalDate = new Date(System.currentTimeMillis());
			}
		}
		
		// This isn't being set by anything after the re-factor from 3.1.1 to 5.2.1
		ProtocolGrantExemptionBean grantExemptionBean = super.getProtocolGrantExemptionBean();
		if(ObjectUtils.isNotNull(grantExemptionBean)){
			// Must do null check, as this method is called by ActionHelperBase#buildProtocolApproveBean()
			// *before* this class has completely intitialized, we only want to set on subsequent calls
			grantExemptionBean.setApprovalDate(approvalDate);
		}
		
		return approvalDate;
    }
    

	private Date getLatestReviewApprovalDate(ProtocolBase protocol) {
		Date lastApprovedReviewDate = null;
		String protocolNumber = protocol.getProtocolNumber();
		List<ProtocolOnlineReviewBase> onlineReviews = getProtocolOnlineReviewService().getProtocolReviews(protocolNumber);

		for (ProtocolOnlineReviewBase onlineReview : onlineReviews) {
			Collection<ActionTakenValue> actionTakenValues = getApprovalActionsForOnlineReview(onlineReview);

			for (ActionTakenValue actionTakenValue : actionTakenValues) {
				boolean performedByReviewer = actionTakenValue.getPrincipalId().equals(onlineReview.getProtocolReviewer().getPersonId());
				if (performedByReviewer) {
					Timestamp actionTimeStamp = actionTakenValue.getActionDate();
					Date actionDate = new Date(actionTimeStamp.getTime());

					if (lastApprovedReviewDate == null || lastApprovedReviewDate.before(actionDate)) {
						// Update greatest.
						lastApprovedReviewDate = actionDate;
					}
				}
			}
		}

		return lastApprovedReviewDate;
	}
	
	
	@SuppressWarnings("unchecked")
	private Collection<ActionTakenValue> getApprovalActionsForOnlineReview(ProtocolOnlineReviewBase protocolOnlineReview) {
		String onlineReviewDocumentNumber = protocolOnlineReview.getProtocolOnlineReviewDocument().getDocumentNumber();
		Collection<ActionTakenValue> actionTakenValues = getActionTakenService().findByDocIdAndAction(onlineReviewDocumentNumber, KewApiConstants.ACTION_TAKEN_APPROVED_CD);
		return actionTakenValues;
	}


	protected ActionTakenService getActionTakenService() {
		if (actionTakenService == null) {
			actionTakenService = (ActionTakenService) KraServiceLocator.getService("enActionTakenService");
		}
		return actionTakenService;
	}


	protected ProtocolOnlineReviewService getProtocolOnlineReviewService() {
		if (protocolOnlineReviewService == null) {
			protocolOnlineReviewService = (ProtocolOnlineReviewService) KraServiceLocator.getService(ProtocolOnlineReviewService.class);
		}
		return protocolOnlineReviewService;
	}


}
