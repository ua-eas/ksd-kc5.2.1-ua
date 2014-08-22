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
package edu.arizona.kra.institutionalproposal.rules;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.institutionalproposal.document.InstitutionalProposalDocument;
import org.kuali.kra.institutionalproposal.home.InstitutionalProposalUnrecoveredFandA;
import org.kuali.kra.institutionalproposal.rules.InstitutionalProposalSaveUnrecoveredFandARuleEvent;
import org.kuali.kra.institutionalproposal.rules.InstitutionalProposalUnrecoveredFandARuleImpl;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;

/**
 * This class...
 */
public class CustomInstitutionalProposalDocumentRule extends org.kuali.kra.institutionalproposal.rules.InstitutionalProposalDocumentRule {
    
    /**
    *
    * process Cost Share business rules.
    * @param awardDocument
    * @return
    */
    @Override
    protected boolean processUnrecoveredFandABusinessRules(Document document) {
        boolean valid = true;
        MessageMap errorMap = GlobalVariables.getMessageMap();
        InstitutionalProposalDocument institutionalProposalDocument = (InstitutionalProposalDocument) document;
        int i = 0;
        List<InstitutionalProposalUnrecoveredFandA> institutionalProposalUnrecoveredFandAs = 
                                    institutionalProposalDocument.getInstitutionalProposal().getInstitutionalProposalUnrecoveredFandAs();
        errorMap.addToErrorPath(DOCUMENT_ERROR_PATH);
        errorMap.addToErrorPath(IP_ERROR_PATH);

        for (InstitutionalProposalUnrecoveredFandA institutionalProposalUnrecoveredFandA : institutionalProposalUnrecoveredFandAs) {
        	
        	// UA customization: Remove requirement for Source Account
        	String source = institutionalProposalUnrecoveredFandA.getSourceAccount();
        	if(StringUtils.isBlank(source)){
        		continue;
        	}

            String errorPath = "institutionalProposalUnrecoveredFandAs[" + i + Constants.RIGHT_SQUARE_BRACKET;
            errorMap.addToErrorPath(errorPath);
            InstitutionalProposalSaveUnrecoveredFandARuleEvent event = new InstitutionalProposalSaveUnrecoveredFandARuleEvent(errorPath, 
                                                                                institutionalProposalDocument, 
                                                                                institutionalProposalUnrecoveredFandA);
            valid &= new InstitutionalProposalUnrecoveredFandARuleImpl().processSaveInstitutionalProposalUnrecoveredFandABusinessRules(event);
            errorMap.removeFromErrorPath(errorPath);
            i++;
        }

        errorMap.removeFromErrorPath(IP_ERROR_PATH);
        errorMap.removeFromErrorPath(DOCUMENT_ERROR_PATH);

        return valid;
    }

}
