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
package edu.arizona.kra.budget.distributionincome;

import static org.kuali.rice.kns.util.KNSGlobalVariables.getAuditErrorMap;

import java.sql.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kra.budget.BudgetDecimal;
import org.kuali.kra.budget.core.Budget;
import org.kuali.kra.budget.core.BudgetParent;
import org.kuali.kra.budget.distributionincome.BudgetUnrecoveredFandA;
import org.kuali.kra.budget.document.BudgetDocument;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.infrastructure.KeyConstants;
import org.kuali.rice.kns.util.AuditCluster;
import org.kuali.rice.kns.util.AuditError;
import org.kuali.rice.krad.document.Document;

@SuppressWarnings("deprecation")
public class CustomBudgetUnrecoveredFandAAuditRule extends  org.kuali.kra.budget.distributionincome.BudgetUnrecoveredFandAAuditRule {


    @SuppressWarnings("rawtypes")
	@Override
	public boolean processRunAuditBusinessRules(Document document) {

        Budget budget = ((BudgetDocument)document).getBudget();
        if (getAuditErrorMap().containsKey(BUDGET_UNRECOVERED_F_AND_A_ERROR_KEY)) {
            List auditErrors = ((AuditCluster) getAuditErrorMap().get(BUDGET_UNRECOVERED_F_AND_A_ERROR_KEY)).getAuditErrorList();
            auditErrors.clear();
        }

        // Returns if unrecovered f and a is not applicable
        if (!budget.isUnrecoveredFandAApplicable()) {
            return true;
        }
        
        List<BudgetUnrecoveredFandA> unrecoveredFandAs = budget.getBudgetUnrecoveredFandAs();
        boolean retval = true;
        
        // Forces full allocation of unrecovered f and a
        if (budget.getUnallocatedUnrecoveredFandA().isGreaterThan(BudgetDecimal.ZERO) && budget.isUnrecoveredFandAEnforced()) {
            retval = false;
            if (unrecoveredFandAs.size() ==0) {
                getAuditErrors().add(new AuditError("document.budget.budgetUnrecoveredFandA",
                        KeyConstants.AUDIT_ERROR_BUDGET_DISTRIBUTION_UNALLOCATED_NOT_ZERO,
                        Constants.BUDGET_DISTRIBUTION_AND_INCOME_PAGE + "." + Constants.BUDGET_UNRECOVERED_F_AND_A_PANEL_ANCHOR,
                        params));
                
            }            
            for (int i=0;i<unrecoveredFandAs.size(); i++) {
                getAuditErrors().add(new AuditError("document.budget.budgetUnrecoveredFandA["+i+"].amount",
                        KeyConstants.AUDIT_ERROR_BUDGET_DISTRIBUTION_UNALLOCATED_NOT_ZERO,
                        Constants.BUDGET_DISTRIBUTION_AND_INCOME_PAGE + "." + Constants.BUDGET_UNRECOVERED_F_AND_A_PANEL_ANCHOR,
                        params));
            }
        } 

        Integer fiscalYear = null;
        
        int i=0;
        int j=0;
        BudgetParent budgetParent = ((BudgetDocument) document).getParentDocument().getBudgetParent();
        Date projectStartDate = budgetParent.getRequestedStartDateInitial();
        Date projectEndDate = budgetParent.getRequestedEndDateInitial();

        // Forces inclusion of source account
        boolean duplicateEntryFound = false;
        for (BudgetUnrecoveredFandA unrecoveredFandA : unrecoveredFandAs) {
            
            fiscalYear = unrecoveredFandA.getFiscalYear();
            
            if (null == fiscalYear || fiscalYear.intValue() <= 0) {
                retval = false;
                getAuditErrors().add(new AuditError("document.budget.budgetUnrecoveredFandA["+i+"].fiscalYear",
                                                    KeyConstants.AUDIT_ERROR_BUDGET_DISTRIBUTION_FISCALYEAR_MISSING,
                                                    Constants.BUDGET_DISTRIBUTION_AND_INCOME_PAGE + "." + Constants.BUDGET_UNRECOVERED_F_AND_A_PANEL_ANCHOR,
                                                    params));
            }
            
            if (fiscalYear != null && (fiscalYear < projectStartDate.getYear() + YEAR_CONSTANT || fiscalYear > projectEndDate.getYear() + YEAR_CONSTANT)) {
                getAuditWarnings().add(new AuditError("document.budget.budgetUnrecoveredFandA["+i+"].fiscalYear", 
                                                      KeyConstants.AUDIT_WARNING_BUDGET_DISTRIBUTION_FISCALYEAR_INCONSISTENT, 
                                                      Constants.BUDGET_DISTRIBUTION_AND_INCOME_PAGE + "." + Constants.BUDGET_UNRECOVERED_F_AND_A_PANEL_ANCHOR, 
                                                      params));
            }
            
            if(!duplicateEntryFound) {
                j=0;
                for (BudgetUnrecoveredFandA unrecoveredFandAForComparison : unrecoveredFandAs) {
                    if(i != j && unrecoveredFandA.getFiscalYear() != null && unrecoveredFandAForComparison.getFiscalYear() != null && 
                            unrecoveredFandA.getFiscalYear().intValue() == unrecoveredFandAForComparison.getFiscalYear().intValue() &&
                            unrecoveredFandA.getApplicableRate().equals( unrecoveredFandAForComparison.getApplicableRate()) && 
                            unrecoveredFandA.getOnCampusFlag().equalsIgnoreCase(unrecoveredFandAForComparison.getOnCampusFlag()) && 
                            StringUtils.equalsIgnoreCase(unrecoveredFandA.getSourceAccount(), unrecoveredFandAForComparison.getSourceAccount()) &&
                            unrecoveredFandA.getAmount().equals( unrecoveredFandAForComparison.getAmount())) {
                        retval = false;
                        getAuditErrors().add(new AuditError("document.budget.budgetUnrecoveredFandA["+i+"]",
                                KeyConstants.AUDIT_ERROR_BUDGET_DISTRIBUTION_DUPLICATE_UNRECOVERED_FA,
                                Constants.BUDGET_DISTRIBUTION_AND_INCOME_PAGE + "." + Constants.BUDGET_UNRECOVERED_F_AND_A_PANEL_ANCHOR,
                                params));
                        duplicateEntryFound = true;
                        break;
                    }
                    j++;
                }
            }
            
            i++;
        }
        return retval;
    }

}
