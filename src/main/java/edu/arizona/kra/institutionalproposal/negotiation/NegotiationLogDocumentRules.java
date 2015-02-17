package edu.arizona.kra.institutionalproposal.negotiation;

import org.apache.commons.lang.StringUtils;
import org.kuali.kra.rules.ErrorReporter;
import org.kuali.kra.rules.KraMaintenanceDocumentRuleBase;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.util.GlobalVariables;

public class NegotiationLogDocumentRules extends KraMaintenanceDocumentRuleBase {

	ErrorReporter errorReporter;
	
	public NegotiationLogDocumentRules() {
		errorReporter = new ErrorReporter();
	}
	
	protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
		boolean result = true;
		GlobalVariables.getMessageMap().addToErrorPath("document.newMaintainableObject");
		
		NegotiationLog negotiation = (NegotiationLog) document.getNewMaintainableObject().getBusinessObject();
		
        getDictionaryValidationService().validateBusinessObject(negotiation);
		
		if (StringUtils.isNotBlank(negotiation.getUnitNumber())) {
			negotiation.refreshReferenceObject("leadUnit");
			if (negotiation.getLeadUnit() == null) {
				errorReporter.reportError("unitNumber", "error.required", "Lead Unit (Lead Unit)");
				result = false;
			}
		}
		
		if (StringUtils.isNotBlank(negotiation.getSponsorCode())) {
			negotiation.refreshReferenceObject("sponsor");
			if (negotiation.getSponsor() == null) {
				errorReporter.reportError("sponsorCode", "error.required", "Sponsor Code (Sponsor Code)");
				result = false;
			}
		}
		
		GlobalVariables.getMessageMap().removeFromErrorPath("document.newMaintainableObject");
		return result;
	}
	
	protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
		return processCustomSaveDocumentBusinessRules(document);
	}
	
	protected boolean processCustomApproveDocumentBusinessRules(MaintenanceDocument document) {
		return processCustomSaveDocumentBusinessRules(document);
	}
}
