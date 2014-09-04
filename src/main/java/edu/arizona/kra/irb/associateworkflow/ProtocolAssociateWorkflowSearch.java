package edu.arizona.kra.irb.associateworkflow;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.kra.irb.Protocol;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.krad.bo.TransientBusinessObjectBase;
/**
 * 
 * This class is used to search for Protocols and their ProtocolSubmissions
 * by the CustomAttribute called "associate"
 */
@SuppressWarnings("serial")
public class ProtocolAssociateWorkflowSearch extends TransientBusinessObjectBase {
	public static final String ASSOCIATE_CUSTOM_ATTRIBUTE_NAME = "Associate";
	public static final String TRACKING_COMMENT_CUSTOM_ATTRIBUTE_NAME = "Tracking Comments";
	
	private String documentNumber;
	
	private String associateUserId;
	private String associateUniversalId;
	private String associatePersonName;
	@SuppressWarnings("unused")
	private Person associateUser;
	
	private String trackingComments;
	private Long protocolId;
	private Long submissionId;
	private String protocolNumber;
	private Integer submissionNumber;
	private String protocolStatusCode;
	private String protocolStatusDescription;
	private String submissionTypeCode;
	private String submissionTypeDescription;
	private String submissionStatusCode;
	private String submissionStatusDescription;
	private Date expirationDate;
	private Date submissionDate;
	private String committeeId;
	private String committeeName;
	private String protocolPiName;
	private Boolean chairReviewComplete;

	private PersonService personService;
	private Protocol protocol;
	
	
	@SuppressWarnings("rawtypes")
	protected LinkedHashMap toStringMapper() {
      LinkedHashMap<String, Object> hashMap = new LinkedHashMap<String, Object>();
      
      return hashMap;
	}

	public String getDocumentNumber() {
		return documentNumber;
	}

	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}

	public String getAssociateUniversalId() {
		return associateUniversalId;
	}

	public void setAssociateUniversalId(String associateUniversalId) {
		this.associateUniversalId = associateUniversalId;
	}
	
	public Person getAssociateUser() {
        if (associateUserId != null) {
            return getPersonService().getPersonByPrincipalName(associateUserId);
        }
        return null;
	}

	public void setAssociateUser(Person associateUser) {
		this.associateUser = associateUser;
	}

	public PersonService getPersonService() {
        if (this.personService == null) {
            this.personService = KraServiceLocator.getService(PersonService.class);
        }
        
        return this.personService;
	}

	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}

	public String getAssociateUserId() {
		return associateUserId;
	}

	public void setAssociateUserId(String associateUserId) {
		this.associateUserId = associateUserId;
	}

	public String getAssociatePersonName() {
		return associatePersonName;
	}

	public void setAssociatePersonName(String associatePersonName) {
		this.associatePersonName = associatePersonName;
	}

	public String getTrackingComments() {
		return trackingComments;
	}

	public void setTrackingComments(
			String trackingComments) {
		this.trackingComments = trackingComments;
	}

	public String getProtocolNumber() {
		return protocolNumber;
	}

	public void setProtocolNumber(String protocolNumber) {
		this.protocolNumber = protocolNumber;
	}

	public Integer getSubmissionNumber() {
		return submissionNumber;
	}

	public void setSubmissionNumber(Integer submissionNumber) {
		this.submissionNumber = submissionNumber;
	}

	public String getSubmissionStatusCode() {
		return submissionStatusCode;
	}

	public void setSubmissionStatusCode(String submissionStatusCode) {
		this.submissionStatusCode = submissionStatusCode;
	}

	public String getSubmissionStatusDescription() {
		return submissionStatusDescription;
	}

	public void setSubmissionStatusDescription(String submissionStatusDescription) {
		this.submissionStatusDescription = submissionStatusDescription;
	}

	public Date getSubmissionDate() {
		return submissionDate;
	}

	public void setSubmissionDate(Date submissionDate) {
		this.submissionDate = submissionDate;
	}

	public String getProtocolStatusCode() {
		return protocolStatusCode;
	}

	public void setProtocolStatusCode(String protocolStatusCode) {
		this.protocolStatusCode = protocolStatusCode;
	}

	public String getProtocolStatusDescription() {
		return protocolStatusDescription;
	}

	public void setProtocolStatusDescription(String protocolStatusDescription) {
		this.protocolStatusDescription = protocolStatusDescription;
	}

	public String getSubmissionTypeCode() {
		return submissionTypeCode;
	}

	public void setSubmissionTypeCode(String submissionTypeCode) {
		this.submissionTypeCode = submissionTypeCode;
	}
	
	public String getSubmissionTypeDescription() {
		return submissionTypeDescription;
	}

	public void setSubmissionTypeDescription(String submissionTypeDescription) {
		this.submissionTypeDescription = submissionTypeDescription;
	}

	public String getCommitteeId() {
		return committeeId;
	}

	public void setCommitteeId(String committeeId) {
		this.committeeId = committeeId;
	}
	
	public String getCommitteeName() {
		return committeeName;
	}

	public void setCommitteeName(String committeeName) {
		this.committeeName = committeeName;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public String getProtocolPiName() {
		return protocolPiName;
	}
	
	public void setProtocolPiName(String protocolPiName) {
		this.protocolPiName = protocolPiName;
	}

	public Boolean isChairReviewComplete() {
		return chairReviewComplete;
	}

	public void setChairReviewComplete(Boolean chairReviewComplete) {
		this.chairReviewComplete = chairReviewComplete;
	}

	public Long getSubmissionId() {
		return submissionId;
	}

	public void setSubmissionId(Long submissionId) {
		this.submissionId = submissionId;
	}

	public Long getProtocolId() {
		return protocolId;
	}

	public void setProtocolId(Long protocolId) {
		this.protocolId = protocolId;
	}

	public Protocol getProtocol() {
		return protocol;
	}

	public void setProtocol(Protocol protocol) {
		this.protocol = protocol;
	}
}
