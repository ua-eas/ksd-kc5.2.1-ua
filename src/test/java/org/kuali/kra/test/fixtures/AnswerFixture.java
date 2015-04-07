package org.kuali.kra.test.fixtures;

public enum AnswerFixture {
	ANSWER_001( 9999001L, AnswerHeaderFixture.ANSWER_HEADER_001.getAnswerHeaderId(), QuestionFixture.QUESTION_1.getQuestionRefId(), QuestionnaireQuestionFixture.QUESTIONNAIRE_QUESTION_001.getQuestionnaireQuestionId(), 1 ),
	ANSWER_002( 9999002L, AnswerHeaderFixture.ANSWER_HEADER_001.getAnswerHeaderId(), QuestionFixture.QUESTION_2.getQuestionRefId(), QuestionnaireQuestionFixture.QUESTIONNAIRE_QUESTION_002.getQuestionnaireQuestionId(), 2 ),
	ANSWER_003( 9999003L, AnswerHeaderFixture.ANSWER_HEADER_001.getAnswerHeaderId(), QuestionFixture.QUESTION_3.getQuestionRefId(), QuestionnaireQuestionFixture.QUESTIONNAIRE_QUESTION_003.getQuestionnaireQuestionId(), 3 ),
	ANSWER_004( 9999004L, AnswerHeaderFixture.ANSWER_HEADER_001.getAnswerHeaderId(), QuestionFixture.QUESTION_4.getQuestionRefId(), QuestionnaireQuestionFixture.QUESTIONNAIRE_QUESTION_004.getQuestionnaireQuestionId(), 4 ),
	ANSWER_005( 9999005L, AnswerHeaderFixture.ANSWER_HEADER_001.getAnswerHeaderId(), QuestionFixture.QUESTION_5.getQuestionRefId(), QuestionnaireQuestionFixture.QUESTIONNAIRE_QUESTION_005.getQuestionnaireQuestionId(), 5 ),
	ANSWER_006( 9999006L, AnswerHeaderFixture.ANSWER_HEADER_001.getAnswerHeaderId(), QuestionFixture.QUESTION_6.getQuestionRefId(), QuestionnaireQuestionFixture.QUESTIONNAIRE_QUESTION_006.getQuestionnaireQuestionId(), 6 );

	private final long questionnaireAnswerId;
	private final long questionnaireAnswerHeaderIdFk;
	private final long questionRefIdFk;
	private final long questionnaireQuestionIdFk;
	private final int questionNumber;

	private AnswerFixture(long questionnaireAnswerId, long questionnaireAnswerHeaderIdFk, long questionRefIdFk, long questionnaireQuestionIdFk, int questionNumber) {
		this.questionnaireAnswerId = questionnaireAnswerId;
		this.questionnaireAnswerHeaderIdFk = questionnaireAnswerHeaderIdFk;
		this.questionRefIdFk = questionRefIdFk;
		this.questionnaireQuestionIdFk = questionnaireQuestionIdFk;
		this.questionNumber = questionNumber;
	}

	public long getQuestionnaireAnswerId() {
		return questionnaireAnswerId;
	}

	public long getQuestionnaireAnswerHeaderIdFk() {
		return questionnaireAnswerHeaderIdFk;
	}

	public long getQuestionRefIdFk() {
		return questionRefIdFk;
	}

	public long getQuestionnaireQuestionIdFk() {
		return questionnaireQuestionIdFk;
	}

	public int getQuestionNumber() {
		return questionNumber;
	}

}
