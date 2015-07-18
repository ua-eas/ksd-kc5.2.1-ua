package edu.arizona.kra.irb.actions.print;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kra.bo.CoeusModule;
import org.kuali.kra.bo.CoeusSubModule;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.kra.irb.actions.print.ProtocolXmlStream;
import org.kuali.kra.protocol.ProtocolBase;
import org.kuali.kra.protocol.noteattachment.ProtocolAttachmentProtocolBase;
import org.kuali.kra.questionnaire.answer.Answer;
import org.kuali.kra.questionnaire.answer.AnswerHeader;
import org.kuali.kra.questionnaire.answer.QuestionnaireAnswerService;

import edu.mit.irb.irbnamespace.AttachmentProtocolDocument.AttachmentProtocol;
import edu.mit.irb.irbnamespace.ProtocolDocument.Protocol;
import edu.mit.irb.irbnamespace.ProtocolDocument.Protocol.Attachments;
import edu.mit.irb.irbnamespace.ProtocolDocument.Protocol.Questionnaires;
import edu.mit.irb.irbnamespace.QuestionnaireQuestionsDocument.QuestionnaireQuestions;

public class CustomProtocolXmlStream extends ProtocolXmlStream {

	/* (non-Javadoc)
	 * @see org.kuali.kra.irb.actions.print.ProtocolXmlStream#getProtocol(org.kuali.kra.irb.Protocol)
	 */
	@Override
	public Protocol getProtocol(org.kuali.kra.irb.Protocol protocol) {
		Protocol protocolType =  super.getProtocol(protocol);
		
		addAttachments(protocol, protocolType);
		
		return protocolType;
	}

	/* (non-Javadoc)
	 * @see org.kuali.kra.irb.actions.print.ProtocolXmlStream#getProtocol(org.kuali.kra.irb.Protocol, java.lang.Integer)
	 */
	@Override
	public Protocol getProtocol(org.kuali.kra.irb.Protocol protocolInfoBean, Integer submissionNumber) {
		Protocol protocolType =  super.getProtocol(protocolInfoBean, submissionNumber);
		
		addAttachments(protocolInfoBean, protocolType);
		
		addQuestionnaires( protocolInfoBean, protocolType );

		return protocolType;
	}
	
	protected void addAttachments(org.kuali.kra.irb.Protocol protocol, Protocol protocolType) {
    	List<ProtocolAttachmentProtocolBase> attachmentProtocols = protocol.getAttachmentProtocols();
    	if(attachmentProtocols == null || attachmentProtocols.size() == 0) {
    		return;
    	}
    	
    	Attachments attachments = protocolType.addNewAttachments();
    	for(ProtocolAttachmentProtocolBase attachmentProtocol : attachmentProtocols) {
    		
    		if ( attachmentProtocol.isActive() ) {
    			AttachmentProtocol attachmentProtocolType = attachments.addNewAttachmentProtocol();
        	    attachmentProtocolType.setAttachmentType(attachmentProtocol.getType().getDescription());
        	    attachmentProtocolType.setAttachmentStatus(attachmentProtocol.getStatus().getDescription());
        	    attachmentProtocolType.setAttachmentUploadUser(attachmentProtocol.getUpdateUser());
        	    attachmentProtocolType.setAttachmentUploadUserFullName(attachmentProtocol.getUpdateUserFullName());
        	    attachmentProtocolType.setAttachmentUpdateTimestamp(getDateTimeService().getCalendar(attachmentProtocol.getUpdateTimestamp()));
        	    String attachmentComments = attachmentProtocol.getComments();
        	    if(attachmentComments != null) {
        	    	attachmentProtocolType.setAttachmentComments(attachmentComments);
        	    }
        	    attachmentProtocolType.setAttachmentFilename(attachmentProtocol.getFile().getName());
        	    attachmentProtocolType.setAttachmentDescription(attachmentProtocol.getDescription());
        	    attachmentProtocolType.setAttachmentCreateTimestamp(getDateTimeService().getCalendar(attachmentProtocol.getCreateTimestamp()));
        	    attachmentProtocolType.setAttachmentVersion(BigInteger.valueOf(attachmentProtocol.getAttachmentVersion()));
        	    attachmentProtocolType.setAttachmentProtocolNumber(attachmentProtocol.getSourceProtocolNumber());
    		}
    	}
    }

	protected void addQuestionnaires( org.kuali.kra.irb.Protocol protocol, Protocol protocolType ) {
		AnswerHeader questionnaireAnswerHeader = getQuestionnaireAnswerHeader( protocol.getProtocolNumber() );
		Questionnaires questionnairesType = protocolType.addNewQuestionnaires();
		for ( Answer questionnaireAnswer : questionnaireAnswerHeader.getAnswers() ) {
			QuestionnaireQuestions questionnaireQuestionsType = questionnairesType.addNewQuestionnaireQuestions();
			String questionID = questionnaireAnswer.getQuestion().getQuestionId();
			String question = questionnaireAnswer.getQuestion().getQuestion();
			String questionnaireAnswerID = questionnaireAnswer.getId().toString();
			String answer = questionnaireAnswer.getAnswer();
			questionnaireQuestionsType.setQuestionID( questionID );
			questionnaireQuestionsType.setQuestion( question );
			questionnaireQuestionsType.setQuestionnaireAnswerID( questionnaireAnswerID );
			questionnaireQuestionsType.setAnswer( answer );
		}
	}

	private AnswerHeader getQuestionnaireAnswerHeader( String protocolNumber ) {
		Map<String, Object> fieldValues = new HashMap<String, Object>();
		fieldValues.put( MODULE_ITEM_CODE, CoeusModule.IRB_MODULE_CODE );
		fieldValues.put( MODULE_ITEM_KEY, protocolNumber );
		List<AnswerHeader> answerHeaders = (List<AnswerHeader>) getBusinessObjectService().findMatching( AnswerHeader.class, fieldValues );
		if ( answerHeaders == null ) {
			AnswerHeader ah = new AnswerHeader();
			ah.setAnswerHeaderId( 0L );
			List<Answer> answers = new ArrayList<Answer>();
			ah.setAnswers( answers );
		}
		Collections.sort( answerHeaders, ANSWER_HEADER_SORT );
		return answerHeaders.get( answerHeaders.size() - 1 );
	}

	private static final String MODULE_ITEM_CODE = "moduleItemCode";
	private static final String MODULE_ITEM_KEY = "moduleItemKey";

	public static final Comparator<AnswerHeader> ANSWER_HEADER_SORT = new Comparator<AnswerHeader>() {
		private final Integer BEFORE = -1;
		private final Integer AFTER = 1;
		private final Integer SAME = 1;

		@Override
		public int compare( AnswerHeader arg0, AnswerHeader arg1 ) {
			if ( arg0.getAnswerHeaderId() < arg1.getAnswerHeaderId() ) {
				return BEFORE;
			}
			if ( arg0.getAnswerHeaderId() > arg1.getAnswerHeaderId() ) {
				return AFTER;
			}
			return SAME;
		}
	};

}
