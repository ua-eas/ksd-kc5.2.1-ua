package org.kuali.kra.test.fixtures;

import org.kuali.kra.infrastructure.Constants;

public enum QuestionExplanationFixture {
	QUESTION_EXPLANATION_001( 9999999001L, QuestionFixture.QUESTION_1.getQuestionRefId(), Constants.QUESTION_EXPLANATION ),
	QUESTION_EXPLANATION_002( 9999999002L, QuestionFixture.QUESTION_1.getQuestionRefId(), Constants.QUESTION_POLICY ),
	QUESTION_EXPLANATION_003( 9999999003L, QuestionFixture.QUESTION_1.getQuestionRefId(), Constants.QUESTION_REGULATION ),
	QUESTION_EXPLANATION_004( 9999999004L, QuestionFixture.QUESTION_1.getQuestionRefId(), Constants.QUESTION_AFFIRMATIVE_QUESTION_CONVERSION ),
	QUESTION_EXPLANATION_005( 9999999005L, QuestionFixture.QUESTION_1.getQuestionRefId(), Constants.QUESTION_NEGATIVE_QUESTION_CONVERSION ),
	QUESTION_EXPLANATION_006( 9999999006L, QuestionFixture.QUESTION_2.getQuestionRefId(), Constants.QUESTION_EXPLANATION ),
	QUESTION_EXPLANATION_007( 9999999007L, QuestionFixture.QUESTION_2.getQuestionRefId(), Constants.QUESTION_POLICY ),
	QUESTION_EXPLANATION_008( 9999999008L, QuestionFixture.QUESTION_2.getQuestionRefId(), Constants.QUESTION_REGULATION ),
	QUESTION_EXPLANATION_009( 9999999009L, QuestionFixture.QUESTION_2.getQuestionRefId(), Constants.QUESTION_AFFIRMATIVE_QUESTION_CONVERSION ),
	QUESTION_EXPLANATION_010( 9999999010L, QuestionFixture.QUESTION_2.getQuestionRefId(), Constants.QUESTION_NEGATIVE_QUESTION_CONVERSION ),
	QUESTION_EXPLANATION_011( 9999999011L, QuestionFixture.QUESTION_3.getQuestionRefId(), Constants.QUESTION_EXPLANATION ),
	QUESTION_EXPLANATION_012( 9999999012L, QuestionFixture.QUESTION_3.getQuestionRefId(), Constants.QUESTION_POLICY ),
	QUESTION_EXPLANATION_013( 9999999013L, QuestionFixture.QUESTION_3.getQuestionRefId(), Constants.QUESTION_REGULATION ),
	QUESTION_EXPLANATION_014( 9999999014L, QuestionFixture.QUESTION_3.getQuestionRefId(), Constants.QUESTION_AFFIRMATIVE_QUESTION_CONVERSION ),
	QUESTION_EXPLANATION_015( 9999999015L, QuestionFixture.QUESTION_3.getQuestionRefId(), Constants.QUESTION_NEGATIVE_QUESTION_CONVERSION ),
	QUESTION_EXPLANATION_016( 9999999016L, QuestionFixture.QUESTION_4.getQuestionRefId(), Constants.QUESTION_EXPLANATION ),
	QUESTION_EXPLANATION_017( 9999999017L, QuestionFixture.QUESTION_4.getQuestionRefId(), Constants.QUESTION_POLICY ),
	QUESTION_EXPLANATION_018( 9999999018L, QuestionFixture.QUESTION_4.getQuestionRefId(), Constants.QUESTION_REGULATION ),
	QUESTION_EXPLANATION_019( 9999999019L, QuestionFixture.QUESTION_4.getQuestionRefId(), Constants.QUESTION_AFFIRMATIVE_QUESTION_CONVERSION ),
	QUESTION_EXPLANATION_020( 9999999020L, QuestionFixture.QUESTION_4.getQuestionRefId(), Constants.QUESTION_NEGATIVE_QUESTION_CONVERSION ),
	QUESTION_EXPLANATION_021( 9999999021L, QuestionFixture.QUESTION_5.getQuestionRefId(), Constants.QUESTION_EXPLANATION ),
	QUESTION_EXPLANATION_022( 9999999022L, QuestionFixture.QUESTION_5.getQuestionRefId(), Constants.QUESTION_POLICY ),
	QUESTION_EXPLANATION_023( 9999999023L, QuestionFixture.QUESTION_5.getQuestionRefId(), Constants.QUESTION_REGULATION ),
	QUESTION_EXPLANATION_024( 9999999024L, QuestionFixture.QUESTION_5.getQuestionRefId(), Constants.QUESTION_AFFIRMATIVE_QUESTION_CONVERSION ),
	QUESTION_EXPLANATION_025( 9999999025L, QuestionFixture.QUESTION_5.getQuestionRefId(), Constants.QUESTION_NEGATIVE_QUESTION_CONVERSION ),
	QUESTION_EXPLANATION_026( 9999999026L, QuestionFixture.QUESTION_6.getQuestionRefId(), Constants.QUESTION_EXPLANATION ),
	QUESTION_EXPLANATION_027( 9999999027L, QuestionFixture.QUESTION_6.getQuestionRefId(), Constants.QUESTION_POLICY ),
	QUESTION_EXPLANATION_028( 9999999028L, QuestionFixture.QUESTION_6.getQuestionRefId(), Constants.QUESTION_REGULATION ),
	QUESTION_EXPLANATION_029( 9999999029L, QuestionFixture.QUESTION_6.getQuestionRefId(), Constants.QUESTION_AFFIRMATIVE_QUESTION_CONVERSION ),
	QUESTION_EXPLANATION_030( 9999999030L, QuestionFixture.QUESTION_6.getQuestionRefId(), Constants.QUESTION_NEGATIVE_QUESTION_CONVERSION );

	private final long questionExplanationId;
	private final long questionRefIdFk;
	private final String explanationType;

	private QuestionExplanationFixture(long questionExplanationId, long questionRefIdFk, String explanationType) {
		this.questionExplanationId = questionExplanationId;
		this.questionRefIdFk = questionRefIdFk;
		this.explanationType = explanationType;
	}

	public long getQuestionExplanationId() {
		return questionExplanationId;
	}

	public long getQuestionRefIdFk() {
		return questionRefIdFk;
	}

	public String getExplanationType() {
		return explanationType;
	}

}
