package org.kuali.kra.test.fixtures;

import java.util.ArrayList;

@SuppressWarnings( "serial" )
public enum QuestionnaireFixture {
	QUESTIONNAIRE_1( "999999999999", "999999", "Base Questionnaire", new ArrayList<QuestionFixture>() {
		{
			add( QuestionFixture.QUESTION_1 );
			add( QuestionFixture.QUESTION_2 );
			add( QuestionFixture.QUESTION_3 );
			add( QuestionFixture.QUESTION_4 );
			add( QuestionFixture.QUESTION_5 );
			add( QuestionFixture.QUESTION_6 );
		}
	} );

	private final String questionnaireRefId;
	private final String questionnaireId;
	private final String name;
	private final ArrayList<QuestionFixture> questionList;

	private QuestionnaireFixture(String questionnaireRefId, String questionnaireId, String name, ArrayList<QuestionFixture> questionList) {
		this.questionnaireRefId = questionnaireRefId;
		this.questionnaireId = questionnaireId;
		this.name = name;
		this.questionList = new ArrayList<QuestionFixture>( questionList );
	}

	public ArrayList<QuestionFixture> getQuestionList() {
		return questionList;
	}

	public String getQuestionnaireRefId() {
		return questionnaireRefId;
	}

	public String getQuestionnaireId() {
		return questionnaireId;
	}

	public String getName() {
		return name;
	}
}
