package org.kuali.kra.test.fixtures;

public enum QuestionTypeFixture {
	QUESTION_TYPE_001( 1, "Yes/No" ),
	QUESTION_TYPE_002( 2, "Yes/No/NA" ),
	QUESTION_TYPE_003( 3, "Number" ),
	QUESTION_TYPE_004( 4, "Date" ),
	QUESTION_TYPE_005( 5, "Text" ),
	QUESTION_TYPE_006( 6, "Lookup" );

	private final int questionTypeId;
	private final String questionTypeName;

	private QuestionTypeFixture(int questionTypeId, String questionTypeName) {
		this.questionTypeId = questionTypeId;
		this.questionTypeName = questionTypeName;
	}

	public int getQuestionTypeId() {
		return questionTypeId;
	}

	public String getQuestionTypeName() {
		return questionTypeName;
	}
}
