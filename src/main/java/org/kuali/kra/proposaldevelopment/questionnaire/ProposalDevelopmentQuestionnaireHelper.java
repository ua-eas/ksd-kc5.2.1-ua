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
package org.kuali.kra.proposaldevelopment.questionnaire;

import org.kuali.kra.bo.CoeusModule;
import org.kuali.kra.infrastructure.TaskName;
import org.kuali.kra.proposaldevelopment.bo.ProposalYnq;
import org.kuali.kra.proposaldevelopment.document.ProposalDevelopmentDocument;
import org.kuali.kra.proposaldevelopment.document.authorization.ProposalTask;
import org.kuali.kra.proposaldevelopment.web.struts.form.ProposalDevelopmentForm;
import org.kuali.kra.questionnaire.QuestionnaireHelperBase;
import org.kuali.kra.questionnaire.answer.Answer;
import org.kuali.kra.questionnaire.answer.AnswerHeader;
import org.kuali.kra.questionnaire.answer.ModuleQuestionnaireBean;

public class ProposalDevelopmentQuestionnaireHelper extends QuestionnaireHelperBase {

    private static final long serialVersionUID = 8595107639632039291L;

    protected static final String REVENUE_DISTRIBUTION_QUESTION_ID="11018";
    protected static final String OLD_REVENUE_DISTRIBUTION_QUESTION_ID="FA03";

    private ProposalDevelopmentForm proposalDevelopmentForm;


    private int revenueDistributionAnswerHeaderIndex = 0;
    private int revenueDistributionQuestionIndex = 0;
    private boolean oldQuestionnaireVersion = false;


    public ProposalDevelopmentQuestionnaireHelper(ProposalDevelopmentForm form) {
        this.proposalDevelopmentForm = form;
    }
    
    @Override
    public String getModuleCode() {
        return CoeusModule.PROPOSAL_DEVELOPMENT_MODULE_CODE;
    }

    @Override
    public ModuleQuestionnaireBean getModuleQnBean() {
        ProposalDevelopmentDocument propDevDoc = getProposalDevelopmentDocument();
        ModuleQuestionnaireBean moduleQuestionnaireBean = new ProposalDevelopmentModuleQuestionnaireBean(propDevDoc.getDevelopmentProposal());
        return moduleQuestionnaireBean;
    }
    

    protected ProposalDevelopmentDocument getProposalDevelopmentDocument() {
        ProposalDevelopmentDocument document = proposalDevelopmentForm.getProposalDevelopmentDocument();
        if (document == null || document.getDevelopmentProposal() == null) {
            throw new IllegalArgumentException("invalid (null) ProposalDevelopmentDocument in ProposalDevelopmentForm");
        }
        return document;
    }
    
    
    /**
     * Gets the proposalDevelopmentForm attribute. 
     * @return Returns the proposalDevelopmentForm.
     */
    public ProposalDevelopmentForm getProposalDevelopmentForm() {
        return proposalDevelopmentForm;
    }

    /**
     * Sets the proposalDevelopmentForm attribute value.
     * @param proposalDevelopmentForm The proposalDevelopmentForm to set.
     */
    public void setProposalDevelopmentForm(ProposalDevelopmentForm proposalDevelopmentForm) {
        this.proposalDevelopmentForm = proposalDevelopmentForm;
    }

    /**
     * 
     * This method is to set up things for questionnaire page to be displayed.
     */
    public void prepareView() {
        initializePermissions(proposalDevelopmentForm.getProposalDevelopmentDocument());
    }

    /*
     * authorization check.
     */
    private void initializePermissions(ProposalDevelopmentDocument proposalDevelopmentDocument) {
        ProposalTask task = new ProposalTask(TaskName.ANSWER_PROPOSAL_QUESTIONNAIRE, proposalDevelopmentDocument);
        setAnswerQuestionnaire(getTaskAuthorizationService().isAuthorized(getUserIdentifier(), task));
    }


    /**
     *Finds the right question and answer header indexes for displaying this question in the Personnel Tab.
     * @return
     */
    public void findRevenueQuestionIndex(){
        int questionIndex = 0;
        if ( getAnswerHeaders().size() > 0 ) {

            //has post 5.2.1 questionnaire version
            oldQuestionnaireVersion = false;

            //pick the first answer header
            revenueDistributionAnswerHeaderIndex = 0;
            AnswerHeader header = getAnswerHeaders().get(revenueDistributionAnswerHeaderIndex);
            for(Answer answer : header.getAnswers()) {
                if (answer.getQuestion().getQuestionId().equalsIgnoreCase(REVENUE_DISTRIBUTION_QUESTION_ID) ||
                        answer.getQuestion().getQuestionId().equalsIgnoreCase(OLD_REVENUE_DISTRIBUTION_QUESTION_ID) ) {
                    revenueDistributionQuestionIndex = questionIndex;
                    break;
                }
                questionIndex++;
            }
        } else if (!getProposalDevelopmentDocument().getDevelopmentProposalList().isEmpty() && !getProposalDevelopmentDocument().getDevelopmentProposalList().get(0).getProposalYnqs().isEmpty()){
            oldQuestionnaireVersion = true;
            for (ProposalYnq proposalYnq: getProposalDevelopmentDocument().getDevelopmentProposalList().get(0).getProposalYnqs()){
                if ( proposalYnq.getQuestionId().equalsIgnoreCase(OLD_REVENUE_DISTRIBUTION_QUESTION_ID)){
                    revenueDistributionQuestionIndex = questionIndex;
                    break;
                }
                questionIndex++;
            }

        }
    }


    public int getRevenueDistributionAnswerHeaderIndex(){return revenueDistributionAnswerHeaderIndex;}

    public int getRevenueDistributionQuestionIndex(){return revenueDistributionQuestionIndex;}

    public boolean hasOldQuestionnaireVersion(){return oldQuestionnaireVersion;}

    public boolean isOldQuestionnaireVersion(){return oldQuestionnaireVersion;}

}
