/*
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.osedu.org/licenses/ECL-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.arizona.kra.budget.document;

import org.kuali.kra.award.budget.AwardBudgeCostTotalAuditRule;
import org.kuali.kra.award.budget.AwardBudgetBudgetTypeAuditRule;
import org.kuali.kra.award.budget.AwardBudgetCostLimitAuditRule;
import org.kuali.kra.award.budget.document.AwardBudgetDocument;
import org.kuali.kra.budget.distributionincome.BudgetCostShareAuditRule;
import org.kuali.kra.budget.document.BudgetDocument;
import org.kuali.kra.budget.nonpersonnel.BudgetExpensesAuditRule;
import org.kuali.kra.budget.parameters.BudgetPeriodAuditRule;
import org.kuali.kra.budget.personnel.BudgetPersonnelAuditRule;
import org.kuali.kra.budget.rates.BudgetRateAuditRule;
import org.kuali.kra.rules.ActivityTypeAuditRule;
import org.kuali.kra.rules.ResearchDocumentBaseAuditRule;
import org.kuali.rice.krad.document.Document;

import edu.arizona.kra.budget.distributionincome.CustomBudgetUnrecoveredFandAAuditRule;


public class CustomBudgetDocumentRule extends  org.kuali.kra.budget.document.BudgetDocumentRule {

    /**
     * @see org.kuali.rice.krad.rules.rule.DocumentAuditRule#processRunAuditBusinessRules(org.kuali.rice.krad.document.Document)
     */
    @SuppressWarnings("rawtypes")
	public boolean processRunAuditBusinessRules(Document document) {
        boolean retval = true;
        
        // This was super.processRunAuditBusinessRules(document), but we extended, so now we need super's super, which is:
        retval &= new ResearchDocumentBaseAuditRule().processRunAuditBusinessRules(document);

        retval &= new BudgetPeriodAuditRule().processRunAuditBusinessRules(document);
        
        retval &= new BudgetExpensesAuditRule().processRunAuditBusinessRules(document);
        
        retval &= new BudgetPersonnelAuditRule().processRunPersonnelAuditBusinessRules(document);

        retval &= new BudgetRateAuditRule().processRunAuditBusinessRules(document);
        
        // Skipping D and I audits for Awards Budgets since we've temporarily removed the Award D&I tab
        if (!(document instanceof AwardBudgetDocument)) {
        
        	// This is the main reason this class was extended... we need to use
        	// CustomBudgetUnrecoveredFandAAuditRule in order to allow for absence of source account
            retval &= new CustomBudgetUnrecoveredFandAAuditRule().processRunAuditBusinessRules(document);
        
            retval &= new BudgetCostShareAuditRule().processRunAuditBusinessRules(document);

        }
        
        retval &= new ActivityTypeAuditRule().processRunAuditBusinessRules(document);

        if (!Boolean.valueOf(((BudgetDocument)document).getParentDocument().getProposalBudgetFlag())){
            retval &= new AwardBudgetBudgetTypeAuditRule().processRunAuditBusinessRules(document);
            retval &= new AwardBudgeCostTotalAuditRule().processRunAuditBusinessRules(document);
            retval &= new AwardBudgetCostLimitAuditRule().processRunAuditBusinessRules(document);
        }
        if (retval) {
            processRunAuditBudgetVersionRule(((BudgetDocument) document).getParentDocument());
        }
        
        return retval;
    }

}
