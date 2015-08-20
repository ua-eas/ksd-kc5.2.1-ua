package edu.arizona.kra.irb.actions.print;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.kuali.kra.bo.CoeusModule;
import org.kuali.kra.irb.actions.print.ProtocolXmlStream;
import org.kuali.kra.protocol.noteattachment.ProtocolAttachmentProtocolBase;
import org.kuali.kra.questionnaire.answer.Answer;
import org.kuali.kra.questionnaire.answer.AnswerHeader;

import edu.mit.irb.irbnamespace.AttachmentProtocolDocument.AttachmentProtocol;
import edu.mit.irb.irbnamespace.ProtocolDocument.Protocol;
import edu.mit.irb.irbnamespace.ProtocolDocument.Protocol.Attachments;
import edu.mit.irb.irbnamespace.ProtocolDocument.Protocol.Questionnaires;
import edu.mit.irb.irbnamespace.QuestionnaireQuestionsDocument.QuestionnaireQuestions;

public class CustomProtocolXmlStream extends ProtocolXmlStream {
    
    private static final String MODULE_ITEM_CODE = "moduleItemCode";
    private static final String MODULE_ITEM_KEY = "moduleItemKey";


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
		List<AnswerHeader> questionnaireAnswerHeaders = getLatestAnswerHeadersForProtocol( protocol.getProtocolNumber() );
		for (AnswerHeader answerHeader: questionnaireAnswerHeaders){
		Questionnaires questionnairesType = protocolType.addNewQuestionnaires();
    		for ( Answer questionnaireAnswer : answerHeader.getAnswers() ) {
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
	}
	
	/**
	 * Returns all the Question Answer Headers for the given protocol number but only the latest version of the answers.
	 * @param protocolNumber
	 * @return List<AnswerHeader> - if there are no elements to return, it will return an empty array
	 */
	public List<AnswerHeader> getLatestAnswerHeadersForProtocol(String protocolNumber) {
	    Map<String, Object> fieldValues = new HashMap<String, Object>();
	    fieldValues.put(MODULE_ITEM_CODE, CoeusModule.IRB_MODULE_CODE);
	    fieldValues.put(MODULE_ITEM_KEY, protocolNumber);
	    List<AnswerHeader> allAnswerHeaders = (List<AnswerHeader>) getBusinessObjectService().findMatching(AnswerHeader.class, fieldValues);
	    List<AnswerHeader> filteredAnswerHeaders = new ArrayList<AnswerHeader>();
	    //store the answers for the same question ordered by version
	    TreeMap<Long, AnswerHeader> answerHeaderVersions = new TreeMap<Long, AnswerHeader>();
	    while ( allAnswerHeaders.size()>0 ){
	        AnswerHeader currentAnswerHeader = allAnswerHeaders.get(0);
	        answerHeaderVersions.clear();

	        for (Iterator<AnswerHeader> iterator = allAnswerHeaders.iterator(); iterator.hasNext();) {
	            AnswerHeader answerHeader = iterator.next();
	            if ( currentAnswerHeader.getQuestionnaireRefIdFk().equalsIgnoreCase( answerHeader.getQuestionnaireRefIdFk())) {
	                answerHeaderVersions.put(answerHeader.getVersionNumber(), answerHeader);
	                iterator.remove();
	            }
	        }
	        //only return the latest version for the particular answer header
	        filteredAnswerHeaders.add( answerHeaderVersions.lastEntry().getValue());
	    }
	    return filteredAnswerHeaders;
	}

}
