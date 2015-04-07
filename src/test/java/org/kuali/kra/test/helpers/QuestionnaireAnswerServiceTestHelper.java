package org.kuali.kra.test.helpers;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kra.proposaldevelopment.bo.DevelopmentProposal;
import org.kuali.kra.questionnaire.Questionnaire;
import org.kuali.kra.questionnaire.QuestionnaireQuestion;
import org.kuali.kra.questionnaire.QuestionnaireUsage;
import org.kuali.kra.questionnaire.answer.Answer;
import org.kuali.kra.questionnaire.answer.AnswerHeader;
import org.kuali.kra.questionnaire.answer.ModuleQuestionnaireBean;
import org.kuali.kra.questionnaire.question.Question;
import org.kuali.kra.questionnaire.question.QuestionCategory;
import org.kuali.kra.questionnaire.question.QuestionExplanation;
import org.kuali.kra.questionnaire.question.QuestionType;
import org.kuali.kra.test.fixtures.AnswerFixture;
import org.kuali.kra.test.fixtures.AnswerHeaderFixture;
import org.kuali.kra.test.fixtures.QuestionCategoryFixture;
import org.kuali.kra.test.fixtures.QuestionExplanationFixture;
import org.kuali.kra.test.fixtures.QuestionFixture;
import org.kuali.kra.test.fixtures.QuestionTypeFixture;
import org.kuali.kra.test.fixtures.QuestionnaireFixture;
import org.kuali.kra.test.fixtures.QuestionnaireQuestionFixture;
import org.kuali.kra.test.fixtures.QuestionnaireUsageFixture;
import org.kuali.rice.krad.service.BusinessObjectService;

public class QuestionnaireAnswerServiceTestHelper extends TestHelper {
	private static final String QUESTION_REF_ID_FK = "questionRefIdFk";
	private static final String ANSWER_HEADER_ID_FK = "answerHeaderIdFk";
	private static final String QUESTIONNAIRE_REF_ID_FK = "questionnaireRefIdFk";

	private static final String MODULE_ITEM_CODE = "moduleItemCode";
	private static final String MODULE_SUB_ITEM_CODE = "moduleSubItemCode";
	private static final String MODULE_ITEM_KEY = "moduleItemKey";
	private static final String MODULE_SUB_ITEM_KEY = "moduleSubItemKey";

	public static class QuestionTypeTestHelper extends TestHelper {
		private static class QuestionTypeDefaultValues {
			public static final Timestamp UPDATE_TIMESTAMP = new Timestamp( 0 );
			public static final String UPDATE_USER = "QuestionTypeTestHelper";
			public static final Long VER_NBR = 1L;
			public static final String OBJ_ID = "QuestionTypeTestHelper";
		}

		public static void createData() {
			QuestionTypeTestHelper questionTypeTestHelper = new QuestionTypeTestHelper();
			questionTypeTestHelper.createQuestionType( QuestionTypeFixture.QUESTION_TYPE_001 );
			questionTypeTestHelper.createQuestionType( QuestionTypeFixture.QUESTION_TYPE_002 );
			questionTypeTestHelper.createQuestionType( QuestionTypeFixture.QUESTION_TYPE_003 );
			questionTypeTestHelper.createQuestionType( QuestionTypeFixture.QUESTION_TYPE_004 );
			questionTypeTestHelper.createQuestionType( QuestionTypeFixture.QUESTION_TYPE_005 );
			questionTypeTestHelper.createQuestionType( QuestionTypeFixture.QUESTION_TYPE_006 );
		}

		private QuestionType createQuestionType( QuestionTypeFixture fixture ) {
			BusinessObjectService businessObjectService = getService( BusinessObjectService.class );
			QuestionType qt = buildQuestionType( fixture );
			businessObjectService.save( qt );
			qt = getQuestionType( fixture );
			return qt;
		}

		private QuestionType buildQuestionType( QuestionTypeFixture fixture ) {
			QuestionType qt = new QuestionType();
			qt.setQuestionTypeId( fixture.getQuestionTypeId() );
			qt.setQuestionTypeName( fixture.getQuestionTypeName() );
			qt.setUpdateTimestamp( QuestionTypeDefaultValues.UPDATE_TIMESTAMP );
			qt.setUpdateUser( QuestionTypeDefaultValues.UPDATE_USER );
			qt.setVersionNumber( QuestionTypeDefaultValues.VER_NBR );
			qt.setObjectId( QuestionTypeDefaultValues.OBJ_ID );
			return qt;
		}

		private QuestionType getQuestionType( QuestionTypeFixture fixture ) {
			BusinessObjectService businessObjectService = getService( BusinessObjectService.class );
			QuestionType qt = businessObjectService.findBySinglePrimaryKey( QuestionType.class, fixture.getQuestionTypeId() );
			return qt;
		}
	}

	public static class QuestionCategoryTestHelper extends TestHelper {
		private static class QuestionTypeDefaultValues {
			public static final Timestamp UPDATE_TIMESTAMP = new Timestamp( 0 );
			public static final String UPDATE_USER = "QuestionCategoryTestHelper";
			public static final Long VER_NBR = 1L;
			public static final String OBJ_ID = "QuestionCategoryTestHelper";
		}

		public static void createData() {
			QuestionCategoryTestHelper questionCategoryTestHelper = new QuestionCategoryTestHelper();
			questionCategoryTestHelper.createQuestionCategory( QuestionCategoryFixture.GROUP_TYPE_001 );
			questionCategoryTestHelper.createQuestionCategory( QuestionCategoryFixture.GROUP_TYPE_002 );
			questionCategoryTestHelper.createQuestionCategory( QuestionCategoryFixture.GROUP_TYPE_003 );
			questionCategoryTestHelper.createQuestionCategory( QuestionCategoryFixture.GROUP_TYPE_004 );
			questionCategoryTestHelper.createQuestionCategory( QuestionCategoryFixture.GROUP_TYPE_005 );
			questionCategoryTestHelper.createQuestionCategory( QuestionCategoryFixture.GROUP_TYPE_006 );
			questionCategoryTestHelper.createQuestionCategory( QuestionCategoryFixture.GROUP_TYPE_007 );
		}

		private QuestionCategory createQuestionCategory( QuestionCategoryFixture fixture ) {
			BusinessObjectService businessObjectService = getService( BusinessObjectService.class );
			QuestionCategory qc = buildQuestionCategory( fixture );
			businessObjectService.save( qc );
			qc = getQuestionCategory( fixture );
			return qc;
		}

		private QuestionCategory buildQuestionCategory( QuestionCategoryFixture fixture ) {
			QuestionCategory qc = new QuestionCategory();
			qc.setCategoryTypeCode( fixture.getCategoryTypeCode() );
			qc.setCategoryName( fixture.getCategoryName() );
			qc.setUpdateTimestamp( QuestionTypeDefaultValues.UPDATE_TIMESTAMP );
			qc.setUpdateUser( QuestionTypeDefaultValues.UPDATE_USER );
			qc.setVersionNumber( QuestionTypeDefaultValues.VER_NBR );
			qc.setObjectId( QuestionTypeDefaultValues.OBJ_ID );
			return qc;
		}

		private QuestionCategory getQuestionCategory( QuestionCategoryFixture fixture ) {
			BusinessObjectService businessObjectService = getService( BusinessObjectService.class );
			QuestionCategory qc = businessObjectService.findBySinglePrimaryKey( QuestionCategory.class, fixture.getCategoryTypeCode() );
			return qc;
		}
	}

	public static class QuestionTestHelper extends TestHelper {
		private static class QuestionDefaultValues {
			public static final Integer SEQUENCE_NUMBER = 1;
			public static final String SEQUENCE_STATUS = "C";
			public static final String STATUS = "A";
			public static final String LOOKUP_CLASS = null;
			public static final String LOOKUP_RETURN = null;
			public static final Integer DISPLAYED_ANSWERS = null;
			public static final Integer MAX_ANSWERS = 1;
			public static final Timestamp UPDATE_TIMESTAMP = new Timestamp( 0 );
			public static final String UPDATE_USER = "QuestionTestHelper";
			public static final Long VER_NBR = 1L;
			public static final String OBJ_ID = "QuestionTestHelper";
			public static final String DOCUMENT_NUMBER = null;
		}

		public static void createData() {
			QuestionTestHelper questionTestHelper = new QuestionTestHelper();
			questionTestHelper.createQuestion( QuestionFixture.QUESTION_1 );
			questionTestHelper.createQuestion( QuestionFixture.QUESTION_2 );
			questionTestHelper.createQuestion( QuestionFixture.QUESTION_3 );
			questionTestHelper.createQuestion( QuestionFixture.QUESTION_4 );
			questionTestHelper.createQuestion( QuestionFixture.QUESTION_5 );
			questionTestHelper.createQuestion( QuestionFixture.QUESTION_6 );
		}

		private Question createQuestion( QuestionFixture fixture ) {
			BusinessObjectService businessObjectService = getService( BusinessObjectService.class );
			Question q = buildQuestion( fixture );
			businessObjectService.save( q );
			q = getQuestion( fixture );
			return q;
		}

		private Question buildQuestion( QuestionFixture fixture ) {
			Question q = new Question();
			q.setQuestionRefId( fixture.getQuestionRefId() );
			q.setQuestionId( fixture.getQuestionId() );
			q.setSequenceNumber( QuestionDefaultValues.SEQUENCE_NUMBER );
			q.setSequenceStatus( QuestionDefaultValues.SEQUENCE_STATUS );
			q.setQuestion( fixture.getQuestion() );
			q.setStatus( QuestionDefaultValues.STATUS );
			q.setCategoryTypeCode( fixture.getCategoryTypeCode() );
			q.setQuestionTypeId( fixture.getQuestionTypeId() );
			q.setLookupClass( QuestionDefaultValues.LOOKUP_CLASS );
			q.setLookupReturn( QuestionDefaultValues.LOOKUP_RETURN );
			q.setDisplayedAnswers( QuestionDefaultValues.DISPLAYED_ANSWERS );
			q.setMaxAnswers( QuestionDefaultValues.MAX_ANSWERS );
			q.setAnswerMaxLength( fixture.getAnswerMaxLength() );
			q.setUpdateTimestamp( QuestionDefaultValues.UPDATE_TIMESTAMP );
			q.setUpdateUser( QuestionDefaultValues.UPDATE_USER );
			q.setVersionNumber( QuestionDefaultValues.VER_NBR );
			q.setObjectId( QuestionDefaultValues.OBJ_ID );
			q.setDocumentNumber( QuestionDefaultValues.DOCUMENT_NUMBER );
			return q;
		}

		private Question getQuestion( QuestionFixture fixture ) {
			BusinessObjectService businessObjectService = getService( BusinessObjectService.class );
			Question q = businessObjectService.findBySinglePrimaryKey( Question.class, fixture.getQuestionRefId() );
			return q;
		}
	}

	public static class QuestionExplanationTestHelper extends TestHelper {
		private static class QuestionExplanationDefaultValues {
			public static final String EXPLANATION = "QuestionExplanationTestHelper";
			public static final Timestamp UPDATE_TIMESTAMP = new Timestamp( 0 );
			public static final String UPDATE_USER = "QuestionExplanationTestHelper";
			public static final Long VER_NBR = 1L;
			public static final String OBJ_ID = "QuestionExplanationTestHelper";
		}

		public static void createData() {
			QuestionExplanationTestHelper questionExplanationTestHelper = new QuestionExplanationTestHelper();
			questionExplanationTestHelper.createQuestionExplanation( QuestionExplanationFixture.QUESTION_EXPLANATION_001 );
			questionExplanationTestHelper.createQuestionExplanation( QuestionExplanationFixture.QUESTION_EXPLANATION_002 );
			questionExplanationTestHelper.createQuestionExplanation( QuestionExplanationFixture.QUESTION_EXPLANATION_003 );
			questionExplanationTestHelper.createQuestionExplanation( QuestionExplanationFixture.QUESTION_EXPLANATION_004 );
			questionExplanationTestHelper.createQuestionExplanation( QuestionExplanationFixture.QUESTION_EXPLANATION_005 );
			questionExplanationTestHelper.createQuestionExplanation( QuestionExplanationFixture.QUESTION_EXPLANATION_006 );
			questionExplanationTestHelper.createQuestionExplanation( QuestionExplanationFixture.QUESTION_EXPLANATION_007 );
			questionExplanationTestHelper.createQuestionExplanation( QuestionExplanationFixture.QUESTION_EXPLANATION_008 );
			questionExplanationTestHelper.createQuestionExplanation( QuestionExplanationFixture.QUESTION_EXPLANATION_009 );
			questionExplanationTestHelper.createQuestionExplanation( QuestionExplanationFixture.QUESTION_EXPLANATION_010 );
			questionExplanationTestHelper.createQuestionExplanation( QuestionExplanationFixture.QUESTION_EXPLANATION_011 );
			questionExplanationTestHelper.createQuestionExplanation( QuestionExplanationFixture.QUESTION_EXPLANATION_012 );
			questionExplanationTestHelper.createQuestionExplanation( QuestionExplanationFixture.QUESTION_EXPLANATION_013 );
			questionExplanationTestHelper.createQuestionExplanation( QuestionExplanationFixture.QUESTION_EXPLANATION_014 );
			questionExplanationTestHelper.createQuestionExplanation( QuestionExplanationFixture.QUESTION_EXPLANATION_015 );
			questionExplanationTestHelper.createQuestionExplanation( QuestionExplanationFixture.QUESTION_EXPLANATION_016 );
			questionExplanationTestHelper.createQuestionExplanation( QuestionExplanationFixture.QUESTION_EXPLANATION_017 );
			questionExplanationTestHelper.createQuestionExplanation( QuestionExplanationFixture.QUESTION_EXPLANATION_018 );
			questionExplanationTestHelper.createQuestionExplanation( QuestionExplanationFixture.QUESTION_EXPLANATION_019 );
			questionExplanationTestHelper.createQuestionExplanation( QuestionExplanationFixture.QUESTION_EXPLANATION_020 );
			questionExplanationTestHelper.createQuestionExplanation( QuestionExplanationFixture.QUESTION_EXPLANATION_021 );
			questionExplanationTestHelper.createQuestionExplanation( QuestionExplanationFixture.QUESTION_EXPLANATION_022 );
			questionExplanationTestHelper.createQuestionExplanation( QuestionExplanationFixture.QUESTION_EXPLANATION_023 );
			questionExplanationTestHelper.createQuestionExplanation( QuestionExplanationFixture.QUESTION_EXPLANATION_024 );
			questionExplanationTestHelper.createQuestionExplanation( QuestionExplanationFixture.QUESTION_EXPLANATION_025 );
			questionExplanationTestHelper.createQuestionExplanation( QuestionExplanationFixture.QUESTION_EXPLANATION_026 );
			questionExplanationTestHelper.createQuestionExplanation( QuestionExplanationFixture.QUESTION_EXPLANATION_027 );
			questionExplanationTestHelper.createQuestionExplanation( QuestionExplanationFixture.QUESTION_EXPLANATION_028 );
			questionExplanationTestHelper.createQuestionExplanation( QuestionExplanationFixture.QUESTION_EXPLANATION_029 );
			questionExplanationTestHelper.createQuestionExplanation( QuestionExplanationFixture.QUESTION_EXPLANATION_030 );
		}

		private QuestionExplanation createQuestionExplanation( QuestionExplanationFixture fixture ) {
			BusinessObjectService businessObjectService = getService( BusinessObjectService.class );
			QuestionExplanation q = buildQuestionExplanation( fixture );
			businessObjectService.save( q );
			q = getQuestionExplanation( fixture );
			return q;
		}

		private QuestionExplanation buildQuestionExplanation( QuestionExplanationFixture fixture ) {
			QuestionExplanation qe = new QuestionExplanation();
			qe.setQuestionExplanationId( fixture.getQuestionExplanationId() );
			qe.setQuestionRefIdFk( fixture.getQuestionRefIdFk() );
			qe.setExplanationType( fixture.getExplanationType() );
			qe.setExplanation( QuestionExplanationDefaultValues.EXPLANATION );
			qe.setUpdateTimestamp( QuestionExplanationDefaultValues.UPDATE_TIMESTAMP );
			qe.setUpdateUser( QuestionExplanationDefaultValues.UPDATE_USER );
			qe.setVersionNumber( QuestionExplanationDefaultValues.VER_NBR );
			qe.setObjectId( QuestionExplanationDefaultValues.OBJ_ID );
			return qe;
		}

		private QuestionExplanation getQuestionExplanation( QuestionExplanationFixture fixture ) {
			BusinessObjectService businessObjectService = getService( BusinessObjectService.class );
			QuestionExplanation qe = businessObjectService.findBySinglePrimaryKey( QuestionExplanation.class, fixture.getQuestionExplanationId() );
			return qe;
		}
	}

	public static class QuestionnaireTestHelper extends TestHelper {
		private static class QuestionnaireDefaultValues {
			public static final String FILE_NAME = "";
			public static final byte[] TEMPLATE = null;
			public static final String DOCUMENT_NUMBER = "";
			public static final Integer SEQUENCE_NUMBER = 9999;
			public static final String DESCRIPTION = "";
			public static final Timestamp UPDATE_TIMESTAMP = new Timestamp( 0 );
			public static final String UPDATE_USER = "QuestionnaireTestHelper";
			public static final Long VERSION_NUMBER = 1L;
			public static final String OBJECT_ID = "QuestionnaireTestHelper";
			public static final boolean ACTIVE = true;
		}

		public static void createData() {
			QuestionnaireTestHelper questionnaireTestHelper = new QuestionnaireTestHelper();
			questionnaireTestHelper.createQuestionnaire( QuestionnaireFixture.QUESTIONNAIRE_1 );
		}

		private Questionnaire createQuestionnaire( QuestionnaireFixture fixture ) {
			BusinessObjectService businessObjectService = getService( BusinessObjectService.class );
			Questionnaire q = buildQuestionnaire( fixture );
			businessObjectService.save( q );
			q = getQuestionnaire( fixture );
			return q;
		}

		private Questionnaire buildQuestionnaire( QuestionnaireFixture fixture ) {
			Questionnaire q = new Questionnaire();
			q.setQuestionnaireRefId( fixture.getQuestionnaireRefId() );
			q.setQuestionnaireId( fixture.getQuestionnaireId() );
			q.setSequenceNumber( QuestionnaireDefaultValues.SEQUENCE_NUMBER );
			q.setName( fixture.getName() );
			q.setDescription( QuestionnaireDefaultValues.DESCRIPTION );
			q.setUpdateTimestamp( QuestionnaireDefaultValues.UPDATE_TIMESTAMP );
			q.setUpdateUser( QuestionnaireDefaultValues.UPDATE_USER );
			q.setActive( QuestionnaireDefaultValues.ACTIVE );
			q.setVersionNumber( QuestionnaireDefaultValues.VERSION_NUMBER );
			q.setObjectId( QuestionnaireDefaultValues.OBJECT_ID );
			q.setFileName( QuestionnaireDefaultValues.FILE_NAME );
			q.setTemplate( QuestionnaireDefaultValues.TEMPLATE );
			q.setDocumentNumber( QuestionnaireDefaultValues.DOCUMENT_NUMBER );
			return q;
		}

		private Questionnaire getQuestionnaire( QuestionnaireFixture fixture ) {
			BusinessObjectService businessObjectService = getService( BusinessObjectService.class );
			Questionnaire q = businessObjectService.findBySinglePrimaryKey( Questionnaire.class, fixture.getQuestionnaireRefId() );
			return q;
		}
	}

	public static class QuestionnaireQuestionTestHelper extends TestHelper {
		private static class QuestionnaireQuestionDefaultValues {
			private static final Integer PARENT_QUESTION_NUMBER = 0;
			private static final boolean CONDITION_FLAG = false;
			private static final String CONDITION_TYPE = null;
			private static final String CONDITION_VALUE = null;
			private static final Timestamp UPDATE_TIMESTAMP = new Timestamp( 0 );
			private static final String UPDATE_USER = "QuestionnaireQuestionTestHelper";
			private static final Integer QUESTION_SEQ_NUMBER = null;
			private static final Long VER_NBR = 1L;
			private static final String OBJ_ID = "QuestionnaireQuestionTestHelper";
			private static final String RULE_ID = null;
		}

		public static void createData() {
			QuestionnaireQuestionTestHelper questionnaireQuestionTestHelper = new QuestionnaireQuestionTestHelper();
			questionnaireQuestionTestHelper.createQuestionnaireQuestion( QuestionnaireQuestionFixture.QUESTIONNAIRE_QUESTION_001 );
			questionnaireQuestionTestHelper.createQuestionnaireQuestion( QuestionnaireQuestionFixture.QUESTIONNAIRE_QUESTION_002 );
			questionnaireQuestionTestHelper.createQuestionnaireQuestion( QuestionnaireQuestionFixture.QUESTIONNAIRE_QUESTION_003 );
			questionnaireQuestionTestHelper.createQuestionnaireQuestion( QuestionnaireQuestionFixture.QUESTIONNAIRE_QUESTION_004 );
			questionnaireQuestionTestHelper.createQuestionnaireQuestion( QuestionnaireQuestionFixture.QUESTIONNAIRE_QUESTION_005 );
			questionnaireQuestionTestHelper.createQuestionnaireQuestion( QuestionnaireQuestionFixture.QUESTIONNAIRE_QUESTION_006 );
		}

		private QuestionnaireQuestion createQuestionnaireQuestion( QuestionnaireQuestionFixture fixture ) {
			BusinessObjectService businessObjectService = getService( BusinessObjectService.class );
			QuestionnaireQuestion qq = buildQuestionnaireQuestion( fixture );
			businessObjectService.save( qq );
			qq = getQuestionnaireQuestion( fixture );
			return qq;
		}

		private QuestionnaireQuestion buildQuestionnaireQuestion( QuestionnaireQuestionFixture fixture ) {
			QuestionnaireQuestion qq = new QuestionnaireQuestion();
			qq.setQuestionnaireQuestionsId( fixture.getQuestionnaireQuestionId() );
			qq.setQuestionnaireRefIdFk( fixture.getQuestionnaireRefIdFk() );
			qq.setQuestionRefIdFk( fixture.getQuestionRefIdFk() );
			qq.setQuestionNumber( fixture.getQuestionNumber() );
			qq.setParentQuestionNumber( QuestionnaireQuestionDefaultValues.PARENT_QUESTION_NUMBER );
			qq.setConditionFlag( QuestionnaireQuestionDefaultValues.CONDITION_FLAG );
			qq.setCondition( QuestionnaireQuestionDefaultValues.CONDITION_TYPE );
			qq.setConditionValue( QuestionnaireQuestionDefaultValues.CONDITION_VALUE );
			qq.setUpdateTimestamp( QuestionnaireQuestionDefaultValues.UPDATE_TIMESTAMP );
			qq.setUpdateUser( QuestionnaireQuestionDefaultValues.UPDATE_USER );
			qq.setQuestionSeqNumber( QuestionnaireQuestionDefaultValues.QUESTION_SEQ_NUMBER );
			qq.setVersionNumber( QuestionnaireQuestionDefaultValues.VER_NBR );
			qq.setObjectId( QuestionnaireQuestionDefaultValues.OBJ_ID );
			qq.setRuleId( QuestionnaireQuestionDefaultValues.RULE_ID );
			return qq;
		}

		private QuestionnaireQuestion getQuestionnaireQuestion( QuestionnaireQuestionFixture fixture ) {
			BusinessObjectService businessObjectService = getService( BusinessObjectService.class );
			QuestionnaireQuestion qq = businessObjectService.findBySinglePrimaryKey( QuestionnaireQuestion.class, fixture.getQuestionnaireQuestionId() );
			return qq;
		}
	}

	public static class QuestionnaireUsageTestHelper extends TestHelper {
		private static class QuestionnaireUsageDefaultValues {
			public static final Integer QUESTIONNAIRE_SEQUENCE_NUMBER = 1;
			public static final String QUESTIONNAIRE_LABEL = "QuestionnaireUsageTestHelper Usage";
			public static final Timestamp UPDATE_TIMESTAMP = new Timestamp( 0 );
			public static final String UPDATE_USER = "QuestionnaireUsageTestHelper";
			public static final Long VER_NBR = 1L;
			public static final String OBJ_ID = "QuestionnaireUsageTestHelper";
			public static final boolean IS_MANDATORY = false;
			public static final String RULE_ID = null;
		}

		public static void createData() {
			QuestionnaireUsageTestHelper questionnaireUsageTestHelper = new QuestionnaireUsageTestHelper();
			questionnaireUsageTestHelper.createQuestionnaireUsage( QuestionnaireUsageFixture.QUESTIONNAIRE_USAGE_001 );
		}

		private QuestionnaireUsage createQuestionnaireUsage( QuestionnaireUsageFixture fixture ) {
			BusinessObjectService businessObjectService = getService( BusinessObjectService.class );
			QuestionnaireUsage qu = buildQuestionnaireUsage( fixture );
			businessObjectService.save( qu );
			qu = getQuestionnaireUsage( fixture );
			return qu;
		}

		private QuestionnaireUsage buildQuestionnaireUsage( QuestionnaireUsageFixture fixture ) {
			QuestionnaireUsage qu = new QuestionnaireUsage();
			qu.setQuestionnaireUsageId( fixture.getQuestionnaireUsageId() );
			qu.setModuleItemCode( fixture.getModuleItemCode() );
			qu.setModuleSubItemCode( fixture.getModuleSubItemCode() );
			qu.setQuestionnaireRefIdFk( fixture.getQuestionnaireRefIdFk() );
			qu.setQuestionnaireSequenceNumber( QuestionnaireUsageDefaultValues.QUESTIONNAIRE_SEQUENCE_NUMBER );
			qu.setQuestionnaireLabel( QuestionnaireUsageDefaultValues.QUESTIONNAIRE_LABEL );
			qu.setUpdateTimestamp( QuestionnaireUsageDefaultValues.UPDATE_TIMESTAMP );
			qu.setUpdateUser( QuestionnaireUsageDefaultValues.UPDATE_USER );
			qu.setVersionNumber( QuestionnaireUsageDefaultValues.VER_NBR );
			qu.setObjectId( QuestionnaireUsageDefaultValues.OBJ_ID );
			qu.setMandatory( QuestionnaireUsageDefaultValues.IS_MANDATORY );
			qu.setRuleId( QuestionnaireUsageDefaultValues.RULE_ID );
			return qu;
		}

		private QuestionnaireUsage getQuestionnaireUsage( QuestionnaireUsageFixture fixture ) {
			BusinessObjectService businessObjectService = getService( BusinessObjectService.class );
			QuestionnaireUsage qu = businessObjectService.findBySinglePrimaryKey( QuestionnaireUsage.class, fixture.getQuestionnaireUsageId() );
			return qu;
		}
	}

	public static class AnswerHeaderTestHelper extends TestHelper {
		private static class AnswerHeaderDefaultValues {
			public static final boolean QUESTIONNAIRE_COMPLETED_FLAG = false;
			public static final Timestamp UPDATE_TIMESTAMP = new Timestamp( 0 );
			public static final String UPDATE_USER = "AnswerHeaderTestHelper";
			public static final Long VER_NBR = 1L;
			public static final String OBJ_ID = "AnswerHeaderTestHelper";
		}

		public static void createData( DevelopmentProposal proposal ) {
			AnswerHeaderTestHelper answerHeaderTestHelper = new AnswerHeaderTestHelper();
			answerHeaderTestHelper.createAnswerHeader( AnswerHeaderFixture.ANSWER_HEADER_001, proposal );
		}

		private AnswerHeader createAnswerHeader( AnswerHeaderFixture fixture, DevelopmentProposal proposal ) {
			BusinessObjectService businessObjectService = getService( BusinessObjectService.class );
			AnswerHeader ah = buildAnswerHeader( fixture, proposal );
			businessObjectService.save( ah );
			ah = getAnswerHeader( fixture );
			return ah;
		}

		private AnswerHeader buildAnswerHeader( AnswerHeaderFixture fixture, DevelopmentProposal proposal )
		{
			AnswerHeader ah = new AnswerHeader();
			ah.setAnswerHeaderId( fixture.getAnswerHeaderId() );
			ah.setQuestionnaireRefIdFk( fixture.getQuestionnaireRefId() );
			ah.setModuleItemCode( fixture.getModuleItemCode() );
			ah.setModuleItemKey( proposal.getProposalPerson( 0 ).getUniqueId() );
			ah.setModuleSubItemCode( fixture.getModuleSubItemCode() );
			ah.setModuleSubItemKey( fixture.getModuleSubItemKey() );
			ah.setCompleted( AnswerHeaderDefaultValues.QUESTIONNAIRE_COMPLETED_FLAG );
			ah.setUpdateTimestamp( AnswerHeaderDefaultValues.UPDATE_TIMESTAMP );
			ah.setUpdateUser( AnswerHeaderDefaultValues.UPDATE_USER );
			ah.setVersionNumber( AnswerHeaderDefaultValues.VER_NBR );
			ah.setObjectId( AnswerHeaderDefaultValues.OBJ_ID );
			return ah;
		}

		private AnswerHeader getAnswerHeader( AnswerHeaderFixture fixture ) {
			BusinessObjectService businessObjectService = getService( BusinessObjectService.class );
			AnswerHeader ah = businessObjectService.findBySinglePrimaryKey( AnswerHeader.class, fixture.getAnswerHeaderId() );
			return ah;
		}
	}

	public static class AnswerTestHelper extends TestHelper {
		private static class AnswerDefaultValues {
			public static final Integer ANSWER_NUMBER = 1;
			public static final String ANSWER = "N";
			public static final Timestamp UPDATE_TIMESTAMP = new Timestamp( 0 );
			public static final String UPDATE_USER = "AnswerTestHelper";
			public static final Long VER_NBR = 1L;
			public static final String OBJ_ID = "AnswerTestHelper";
		}

		public static void createData() {
			AnswerTestHelper answerTestHelper = new AnswerTestHelper();
			answerTestHelper.createAnswer( AnswerFixture.ANSWER_001 );
			answerTestHelper.createAnswer( AnswerFixture.ANSWER_002 );
			answerTestHelper.createAnswer( AnswerFixture.ANSWER_003 );
			answerTestHelper.createAnswer( AnswerFixture.ANSWER_004 );
			answerTestHelper.createAnswer( AnswerFixture.ANSWER_005 );
			answerTestHelper.createAnswer( AnswerFixture.ANSWER_006 );
		}

		private Answer createAnswer( AnswerFixture fixture ) {
			BusinessObjectService businessObjectService = getService( BusinessObjectService.class );
			Answer a = buildAnswer( fixture );
			businessObjectService.save( a );
			a = getAnswer( fixture );
			return a;
		}

		private Answer buildAnswer( AnswerFixture fixture ) {
			Answer a = new Answer();
			a.setId( fixture.getQuestionnaireAnswerId() );
			a.setAnswerHeaderIdFk( fixture.getQuestionnaireAnswerHeaderIdFk() );
			a.setQuestionRefIdFk( fixture.getQuestionRefIdFk() );
			a.setQuestionnaireQuestionsIdFk( fixture.getQuestionnaireQuestionIdFk() );
			a.setQuestionNumber( fixture.getQuestionNumber() );
			a.setAnswerNumber( AnswerDefaultValues.ANSWER_NUMBER );
			a.setAnswer( AnswerDefaultValues.ANSWER );
			a.setUpdateTimestamp( AnswerDefaultValues.UPDATE_TIMESTAMP );
			a.setUpdateUser( AnswerDefaultValues.UPDATE_USER );
			a.setVersionNumber( AnswerDefaultValues.VER_NBR );
			a.setObjectId( AnswerDefaultValues.OBJ_ID );
			return a;
		}

		private Answer getAnswer( AnswerFixture fixture ) {
			BusinessObjectService businessObjectService = getService( BusinessObjectService.class );
			Answer a = businessObjectService.findBySinglePrimaryKey( Answer.class, fixture.getQuestionnaireAnswerId() );
			return a;
		}
	}

	public void createQuestionnaireAnswerService( DevelopmentProposal proposal ) {
		QuestionTypeTestHelper.createData();
		QuestionCategoryTestHelper.createData();
		QuestionTestHelper.createData();
		QuestionExplanationTestHelper.createData();
		QuestionnaireTestHelper.createData();
		QuestionnaireQuestionTestHelper.createData();
		QuestionnaireUsageTestHelper.createData();
		AnswerHeaderTestHelper.createData( proposal );
		AnswerTestHelper.createData();
	};

	public AnswerHeader getAnswerHeader( ModuleQuestionnaireBean moduleQuestionnaireBean ) {
		AnswerHeader ah = getAnswerHeaders( moduleQuestionnaireBean ).get( 0 );
		return ah;
	}

	public List<AnswerHeader> getAnswerHeaders( ModuleQuestionnaireBean moduleQuestionnaireBean ) {
		List<AnswerHeader> listAnswerHeader = getAnswerHeaderList( moduleQuestionnaireBean );
		for ( AnswerHeader ah : listAnswerHeader )
		{
			Questionnaire questionnaire = getQuestionnaireById( ah.getQuestionnaireRefIdFk() );
			ah.setQuestionnaire( questionnaire );

			List<Answer> answers = getAnswerList( ah.getAnswerHeaderId() );
			ah.setAnswers( answers );

		}
		return listAnswerHeader;
	}

	private List<AnswerHeader> getAnswerHeaderList( ModuleQuestionnaireBean moduleQuestionnaireBean ) {
		BusinessObjectService businessObjectService = getService( BusinessObjectService.class );
		Map<String, String> answerHeaderFieldValues = new HashMap<String, String>();
		answerHeaderFieldValues.put( MODULE_ITEM_CODE, moduleQuestionnaireBean.getModuleItemCode() );
		answerHeaderFieldValues.put( MODULE_SUB_ITEM_CODE, moduleQuestionnaireBean.getModuleSubItemCode() );
		answerHeaderFieldValues.put( MODULE_ITEM_KEY, moduleQuestionnaireBean.getModuleItemKey() );
		answerHeaderFieldValues.put( MODULE_SUB_ITEM_KEY, moduleQuestionnaireBean.getModuleSubItemKey() );
		List<AnswerHeader> listAnswerHeader = (List<AnswerHeader>) businessObjectService.findMatching( AnswerHeader.class, answerHeaderFieldValues );
		return listAnswerHeader;
	}

	private Questionnaire getQuestionnaireById( String questionnaireRefId ) {
		BusinessObjectService businessObjectService = getService( BusinessObjectService.class );
		Questionnaire q = businessObjectService.findBySinglePrimaryKey( Questionnaire.class, questionnaireRefId );

		List<QuestionnaireQuestion> questionnaireQuestions = getQuestionnaireQuestionList( q.getQuestionnaireRefId() );
		q.setQuestionnaireQuestions( questionnaireQuestions );

		List<QuestionnaireUsage> questionnaireUsages = getQuestionnaireUsageList( q.getQuestionnaireRefId() );
		q.setQuestionnaireUsages( questionnaireUsages );
		return q;
	}

	private List<QuestionnaireQuestion> getQuestionnaireQuestionList( String questionnaireRefId ) {
		BusinessObjectService businessObjectService = getService( BusinessObjectService.class );
		Map<String, String> questionnaireQuestionsFieldValues = new HashMap<String, String>();
		questionnaireQuestionsFieldValues.put( QUESTIONNAIRE_REF_ID_FK, questionnaireRefId );
		List<QuestionnaireQuestion> questionnaireQuestions = (List<QuestionnaireQuestion>) businessObjectService.findMatching( QuestionnaireQuestion.class, questionnaireQuestionsFieldValues );
		for ( QuestionnaireQuestion qq : questionnaireQuestions )
		{
			Question q = getQuestionById( qq.getQuestionRefIdFk() );
			qq.setQuestion( q );
		}
		return questionnaireQuestions;
	}

	private Question getQuestionById( Long questionRefId ) {
		BusinessObjectService businessObjectService = getService( BusinessObjectService.class );
		Question q = businessObjectService.findBySinglePrimaryKey( Question.class, questionRefId );

		QuestionCategory questionCategory = getQuestionCategoryById( q.getCategoryTypeCode() );
		q.setQuestionCategory( questionCategory );

		QuestionType questionType = getQuestionTypeById( q.getQuestionTypeId() );
		q.setQuestionType( questionType );

		List<QuestionExplanation> questionExplanations = getQuestionExplanationList( q.getQuestionRefId() );
		q.setQuestionExplanations( questionExplanations );

		return q;
	}

	private List<QuestionExplanation> getQuestionExplanationList( Long questionRefId ) {
		BusinessObjectService businessObjectService = getService( BusinessObjectService.class );
		Map<String, Long> questionExplanationFieldValues = new HashMap<String, Long>();
		questionExplanationFieldValues.put( QUESTION_REF_ID_FK, questionRefId );
		List<QuestionExplanation> questionExplanations = (List<QuestionExplanation>) businessObjectService.findMatching( QuestionExplanation.class, questionExplanationFieldValues );
		return questionExplanations;
	}

	private QuestionCategory getQuestionCategoryById( Integer categoryTypeCode ) {
		BusinessObjectService businessObjectService = getService( BusinessObjectService.class );
		QuestionCategory questionCategory = businessObjectService.findBySinglePrimaryKey( QuestionCategory.class, categoryTypeCode );
		return questionCategory;
	}

	private QuestionType getQuestionTypeById( Integer questionTypeId ) {
		BusinessObjectService businessObjectService = getService( BusinessObjectService.class );
		QuestionType questionType = businessObjectService.findBySinglePrimaryKey( QuestionType.class, questionTypeId );
		return questionType;
	}

	private List<QuestionnaireUsage> getQuestionnaireUsageList( String questionnaireRefIdFk ) {
		BusinessObjectService businessObjectService = getService( BusinessObjectService.class );
		Map<String, String> questionnaireUsageFieldValues = new HashMap<String, String>();
		questionnaireUsageFieldValues.put( QUESTIONNAIRE_REF_ID_FK, questionnaireRefIdFk );
		List<QuestionnaireUsage> questionnaireUsages = (List<QuestionnaireUsage>) businessObjectService.findMatching( QuestionnaireUsage.class, questionnaireUsageFieldValues );
		return questionnaireUsages;
	}

	private List<Answer> getAnswerList( Long answerHeaderId ) {
		BusinessObjectService businessObjectService = getService( BusinessObjectService.class );
		Map<String, Long> answerFieldValues = new HashMap<String, Long>();
		answerFieldValues.put( ANSWER_HEADER_ID_FK, answerHeaderId );
		List<Answer> answers = (List<Answer>) businessObjectService.findMatching( Answer.class, answerFieldValues );
		for ( Answer a : answers )
		{
			Question q = getQuestionById( a.getQuestionRefIdFk() );
			a.setQuestion( q );
		}
		return answers;
	}

}
