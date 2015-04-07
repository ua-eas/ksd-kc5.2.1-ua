package org.kuali.kra.test.fixtures;

import org.kuali.kra.bo.CoeusModule;
import org.kuali.kra.bo.CoeusSubModule;

public enum QuestionnaireUsageFixture {
	QUESTIONNAIRE_USAGE_001( 99999001L, CoeusModule.PROPOSAL_DEVELOPMENT_MODULE_CODE, CoeusSubModule.PROPOSAL_PERSON_CERTIFICATION, QuestionnaireFixture.QUESTIONNAIRE_1.getQuestionnaireRefId() );

	private final long questionnaireUsageId;
	private final String moduleItemCode;
	private final String moduleSubItemCode;
	private final String questionnaireRefIdFk;

	private QuestionnaireUsageFixture(long questionnaireUsageId, String moduleItemCode, String moduleSubItemCode, String questionnaireRefIdFk) {
		this.questionnaireUsageId = questionnaireUsageId;
		this.moduleItemCode = moduleItemCode;
		this.moduleSubItemCode = moduleSubItemCode;
		this.questionnaireRefIdFk = questionnaireRefIdFk;
	}

	public Long getQuestionnaireUsageId() {
		return questionnaireUsageId;
	}

	public String getModuleItemCode() {
		return moduleItemCode;
	}

	public String getModuleSubItemCode() {
		return moduleSubItemCode;
	}

	public String getQuestionnaireRefIdFk() {
		return questionnaireRefIdFk;
	}

}
