package org.kuali.kra.test.fixtures;

import org.kuali.kra.bo.CoeusModule;
import org.kuali.kra.bo.CoeusSubModule;

public enum AnswerHeaderFixture {
	ANSWER_HEADER_001( 999999999001L, QuestionnaireFixture.QUESTIONNAIRE_1.getQuestionnaireRefId(), CoeusModule.PROPOSAL_DEVELOPMENT_MODULE_CODE, CoeusSubModule.PROPOSAL_PERSON_CERTIFICATION, "0" );

	private final long answerHeaderId;
	private final String questionnaireRefId;
	private final String moduleItemCode;
	private final String moduleSubItemCode;
	private final String moduleSubItemKey;

	AnswerHeaderFixture(long answerHeaderId, String questionnaireRefId, String moduleItemCode, String moduleSubItemCode, String moduleSubItemKey) {
		this.answerHeaderId = answerHeaderId;
		this.questionnaireRefId = questionnaireRefId;
		this.moduleItemCode = moduleItemCode;
		this.moduleSubItemCode = moduleSubItemCode;
		this.moduleSubItemKey = moduleSubItemKey;
	}

	public Long getAnswerHeaderId() {
		return answerHeaderId;
	}

	public String getQuestionnaireRefId() {
		return questionnaireRefId;
	}

	public String getModuleItemCode() {
		return moduleItemCode;
	}

	public String getModuleSubItemCode() {
		return moduleSubItemCode;
	}

	public String getModuleSubItemKey() {
		return moduleSubItemKey;
	}

}
