/*
 * Copyright 2005-2014 The Kuali Foundation
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
package edu.arizona.kra.irb.actions.correction;

import org.kuali.kra.irb.actions.ActionHelper;

public class CustomAdminCorrectionBean extends org.kuali.kra.irb.actions.correction.AdminCorrectionBean {
	private static final long serialVersionUID = -7246448632911841016L;

	/**
     * UA implementation constructor to default 2 modules
     * to always being enabled. This ensures the original
     * Protocol can be "Admin Corrected" even after an open
     * Ammendment has been created.
     * 
     * @param actionHelper Reference back to the action helper for this bean
     */
    public CustomAdminCorrectionBean(ActionHelper actionHelper) {
        super(actionHelper);
        super.setAddModifyAttachmentsEnabled(true);
        super.setOthersEnabled(true);
    }

    /**
     * UA implementation to not consider "Other" and "Notes/Attachment"
     * modules when deciding if an amendment/renewal is outstanding.
     */
    @Override
    public boolean isAmendmentRenewalOutstanding() {
        return !(getGeneralInfoEnabled() &&  
            getFundingSourceEnabled() && 
            getProtocolReferencesEnabled() && 
            getProtocolOrganizationsEnabled() && 
            getSubjectsEnabled() && 
            getAreasOfResearchEnabled() && 
            getSpecialReviewEnabled() && 
            getProtocolPersonnelEnabled() && 
            getProtocolPermissionsEnabled() &&
            getQuestionnaireEnabled());
    }

}
