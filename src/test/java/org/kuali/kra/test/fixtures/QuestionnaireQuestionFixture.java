package org.kuali.kra.test.fixtures;

public enum QuestionnaireQuestionFixture {
	QUESTIONNAIRE_QUESTION_001( 999999999001L, QuestionnaireFixture.QUESTIONNAIRE_1.getQuestionnaireRefId(), QuestionFixture.QUESTION_1.getQuestionRefId(), 1 ),
	QUESTIONNAIRE_QUESTION_002( 999999999002L, QuestionnaireFixture.QUESTIONNAIRE_1.getQuestionnaireRefId(), QuestionFixture.QUESTION_2.getQuestionRefId(), 2 ),
	QUESTIONNAIRE_QUESTION_003( 999999999003L, QuestionnaireFixture.QUESTIONNAIRE_1.getQuestionnaireRefId(), QuestionFixture.QUESTION_3.getQuestionRefId(), 3 ),
	QUESTIONNAIRE_QUESTION_004( 999999999004L, QuestionnaireFixture.QUESTIONNAIRE_1.getQuestionnaireRefId(), QuestionFixture.QUESTION_4.getQuestionRefId(), 4 ),
	QUESTIONNAIRE_QUESTION_005( 999999999005L, QuestionnaireFixture.QUESTIONNAIRE_1.getQuestionnaireRefId(), QuestionFixture.QUESTION_5.getQuestionRefId(), 5 ),
	QUESTIONNAIRE_QUESTION_006( 999999999006L, QuestionnaireFixture.QUESTIONNAIRE_1.getQuestionnaireRefId(), QuestionFixture.QUESTION_6.getQuestionRefId(), 6 );

	private final long questionnaireQuestionId;
	private final String questionnaireRefIdFk;
	private final long questionRefIdFk;
	private final int questionNumber;

	private QuestionnaireQuestionFixture(long questionnaireQuestionId, String questionnaireRefIdFk, long questionRefIdFk, int questionNumber) {
		this.questionnaireQuestionId = questionnaireQuestionId;
		this.questionnaireRefIdFk = questionnaireRefIdFk;
		this.questionRefIdFk = questionRefIdFk;
		this.questionNumber = questionNumber;
	}

	public long getQuestionnaireQuestionId() {
		return questionnaireQuestionId;
	}

	public String getQuestionnaireRefIdFk() {
		return questionnaireRefIdFk;
	}

	public long getQuestionRefIdFk() {
		return questionRefIdFk;
	}

	public int getQuestionNumber() {
		return questionNumber;
	}

}
