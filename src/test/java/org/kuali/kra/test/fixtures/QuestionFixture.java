package org.kuali.kra.test.fixtures;

public enum QuestionFixture {
	QUESTION_1( 7106L, "70085", QuestionTypeFixture.QUESTION_TYPE_001.getQuestionTypeId(), 1, QuestionCategoryFixture.GROUP_TYPE_001.getCategoryTypeCode(), "Can you certify that the information submitted within this application is true, complete and accurate to the best of your knowledge? That any false, fictitious, or fraudulent statements or claims may subject you, as the PI/Co-PI/Co-I to criminal, civil or administrative penalties? That you agree to accept responsibility for the scientific conduct of the project and to provide the required progress reports if an award is made as a result of this application." ),
	QUESTION_2( 7107L, "70086", QuestionTypeFixture.QUESTION_TYPE_001.getQuestionTypeId(), 1, QuestionCategoryFixture.GROUP_TYPE_001.getCategoryTypeCode(), "Is there any potential for a perceived or real conflict of interest as defined in MIT's Policies and Procedures with regard to this proposal?" ),
	QUESTION_3( 7108L, "70087", QuestionTypeFixture.QUESTION_TYPE_001.getQuestionTypeId(), 1, QuestionCategoryFixture.GROUP_TYPE_001.getCategoryTypeCode(), "If this is a NIH/NSF proposal have you submitted the required financial disclosures in the web based Coeus Conflict of Interest module?" ),
	QUESTION_4( 7109L, "70088", QuestionTypeFixture.QUESTION_TYPE_001.getQuestionTypeId(), 1, QuestionCategoryFixture.GROUP_TYPE_001.getCategoryTypeCode(), "Have lobbying activities been conducted on behalf of this proposal?" ),
	QUESTION_5( 7110L, "70089", QuestionTypeFixture.QUESTION_TYPE_001.getQuestionTypeId(), 1, QuestionCategoryFixture.GROUP_TYPE_001.getCategoryTypeCode(), "Are you currently debarred, suspended, proposed for debarment, declared ineligible or voluntarily excluded from current transactions by a federal department or agency?" ),
	QUESTION_6( 7111L, "70100", QuestionTypeFixture.QUESTION_TYPE_001.getQuestionTypeId(), 1, QuestionCategoryFixture.GROUP_TYPE_001.getCategoryTypeCode(), "Are you familiar with the requirements of the Procurement Liabilities Act?" );

	private final long questionRefId;
	private final String questionId;
	private final int questionTypeId;
	private final int answerMaxLength;
	private final int categoryTypeCode;
	private final String question;

	private QuestionFixture(long questionRefId, String questionId, int questionTypeId, int answerMaxLength, int categoryTypeCode, String question) {
		this.questionRefId = questionRefId;
		this.questionId = questionId;
		this.questionTypeId = questionTypeId;
		this.answerMaxLength = answerMaxLength;
		this.categoryTypeCode = categoryTypeCode;
		this.question = question;
	}

	public long getQuestionRefId() {
		return questionRefId;
	}

	public String getQuestionId() {
		return questionId;
	}

	public int getQuestionTypeId() {
		return questionTypeId;
	}

	public int getAnswerMaxLength() {
		return answerMaxLength;
	}

	public int getCategoryTypeCode() {
		return categoryTypeCode;
	}

	public String getQuestion() {
		return question;
	}

}
