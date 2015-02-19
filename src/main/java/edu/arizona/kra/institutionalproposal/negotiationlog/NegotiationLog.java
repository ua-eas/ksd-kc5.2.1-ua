package edu.arizona.kra.institutionalproposal.negotiationlog;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;
import org.kuali.kra.award.home.AwardType;
import org.kuali.kra.bo.KcPerson;
import org.kuali.kra.bo.KraPersistableBusinessObjectBase;
import org.kuali.kra.bo.Sponsor;
import org.kuali.kra.bo.Unit;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public class NegotiationLog extends KraPersistableBusinessObjectBase {

	private static final long serialVersionUID = -4465299922061420069L;
	private Integer negotiationLogId;
	
	private Integer migratedNegotiationId;

	private String negotiatorPersonId;
	private String negotiatorName;
	private KcPerson negotiator;
	private Boolean closed;
	private String proposalNumber;
	private String piPersonId;
	private String piName;
	private KcPerson principalInvestigator;
	private String unitNumber;
	private String account;
	private String sponsorCode;
	private String negotiationAgreementType;
    private Integer awardTypeCode;
    private String sponsorAwardNumber;
    private String modificationNumber;
    private String title;
    private KualiDecimal amount;
    private Date startDate;
    private Date endDate;
    private Date backstop;
    private String location;
    private String spsPreawardComments;
    private String orcaComments;
    private Date dateReceived;
    private Date negotiationStart;
    private Date negotiationComplete;
    private Date dateClosed;
    private Long daysOpen;
    
    private String negotiationLogMaintenanceDocumentLookup;
    
    private Unit leadUnit;
	private Sponsor sponsor;
	private AwardType awardType;
    
	
	protected LinkedHashMap toStringMapper() {
		LinkedHashMap hashMap = new LinkedHashMap();
        hashMap.put("negotiationLogId", negotiationLogId);
        hashMap.put("proposalNumber", proposalNumber);
        hashMap.put("piPersonId", piPersonId);
        return hashMap;
	}
	
	public Integer getNegotiationLogId() {
		return negotiationLogId;
	}

	public void setNegotiationLogId(Integer negotiationLogId) {
		this.negotiationLogId = negotiationLogId;
	}
	
	public Integer getMigratedNegotiationId() {
	    if (migratedNegotiationId==null){
	        migratedNegotiationId = new Integer(0);
	    }
        return migratedNegotiationId;
    }

    public void setMigratedNegotiationId(Integer migratedNegotiationId) {
        this.migratedNegotiationId = migratedNegotiationId;
    }

	public String getNegotiatorPersonId() {
		return negotiatorPersonId;
	}

	public void setNegotiatorPersonId(String negotiatorPersonId) {
		this.negotiatorPersonId = negotiatorPersonId;
		KcPerson negotiator = getNegotiator();
		if (negotiator != null) {
			setNegotiatorName(negotiator.getFullName());
		}
	}

	public String getPiPersonId() {
		return piPersonId;
	}

	public void setPiPersonId(String piPersonId) {
		this.piPersonId = piPersonId;
		KcPerson pi = getPrincipalInvestigator();
		if (pi != null) {
			setPiName(pi.getFullName());
		}
	}

	public KcPerson getNegotiator() {
		try {
			if (negotiator == null || !StringUtils.equals(negotiator.getIdentifier(), getNegotiatorPersonId())
					|| StringUtils.isBlank(negotiator.getUserName())) {
				negotiator = KcPerson.fromPersonId(getNegotiatorPersonId());
			} 
		} catch (IllegalArgumentException e) {
			return null;
		}
		return negotiator;
	}

	public KcPerson getPrincipalInvestigator() {
		try {
			if (principalInvestigator == null || !StringUtils.equals(principalInvestigator.getPersonId(), getPiPersonId())
					|| StringUtils.isBlank(principalInvestigator.getUserName())) {
				principalInvestigator = KcPerson.fromPersonId(getPiPersonId());
			}
		} catch (IllegalArgumentException e) {
			return null;
		}
		return principalInvestigator;
	}	
			
    public Unit getLeadUnit() {
        return leadUnit;
    }
    
    public void setLeadUnit(Unit leadUnit) {
        this.leadUnit = leadUnit;
        this.unitNumber = leadUnit != null ? leadUnit.getUnitNumber() : null;
    }

	public String getUnitNumber() {
		return unitNumber;
	}

	public void setUnitNumber(String leadUnitNumber) {
		this.unitNumber = leadUnitNumber;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}
	
    public Sponsor getSponsor() {
        return sponsor;
    }
    
    public void setSponsor(Sponsor sponsor) {
        this.sponsor = sponsor;
        this.sponsorCode = sponsor != null ? sponsor.getSponsorCode() : null;
    }

	public String getSponsorCode() {
		return sponsorCode;
	}

	public void setSponsorCode(String sponsorCode) {
		this.sponsorCode = sponsorCode;
	}

	public String getNegotiationAgreementType() {
		return negotiationAgreementType;
	}

	public void setNegotiationAgreementType(String negotiationAgreementType) {
		this.negotiationAgreementType = negotiationAgreementType;
	}
	
    public AwardType getAwardType() {
        return awardType;
    }
    
    public void setAwardType(AwardType awardType) {
        this.awardType = awardType;
        this.awardTypeCode = awardType != null ? awardType.getAwardTypeCode() : null;
    }

	public Integer getAwardTypeCode() {
		return awardTypeCode;
	}

	public void setAwardTypeCode(Integer awardTypeCode) {
		this.awardTypeCode = awardTypeCode;
	}

	public String getSponsorAwardNumber() {
		return sponsorAwardNumber;
	}

	public void setSponsorAwardNumber(String sponsorAwardNumber) {
		this.sponsorAwardNumber = sponsorAwardNumber;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public KualiDecimal getAmount() {
		return amount;
	}

	public void setAmount(KualiDecimal amount) {
		this.amount = amount;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getBackstop() {
		return backstop;
	}

	public void setBackstop(Date backstop) {
		this.backstop = backstop;
	}

	public String getSpsPreawardComments() {
		return spsPreawardComments;
	}

	public void setSpsPreawardComments(String spsPreawardComments) {
		this.spsPreawardComments = spsPreawardComments;
	}

	public String getOrcaComments() {
		return orcaComments;
	}

	public void setOrcaComments(String orcaComments) {
		this.orcaComments = orcaComments;
	}

	public String getModificationNumber() {
		return modificationNumber;
	}

	public void setModificationNumber(String modificationNumber) {
		this.modificationNumber = modificationNumber;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getNegotiatorName() {
		return negotiatorName;
	}

	public void setNegotiatorName(String negotiatorName) {
		this.negotiatorName = negotiatorName;
	}

	public String getPiName() {
		return piName;
	}

	public void setPiName(String piName) {
		this.piName = piName;
	}
	
	public String getSponsorName() {
		if (getSponsor() == null || !StringUtils.equals(getSponsor().getSponsorCode(), getSponsorCode())) {
			this.refreshReferenceObject("sponsor");
		}
		if (getSponsor() != null) {
			return getSponsor().getSponsorName();
		} else {
			return null;
		}
	}
	
	public void setSponsorName(String sponsorName) {
		//do nothing
	}

	public String getProposalNumber() {
		return proposalNumber;
	}

	public void setProposalNumber(String proposalNumber) {
		this.proposalNumber = proposalNumber;
	}

	public Boolean getClosed() {
		return closed;
	}

	public void setClosed(Boolean closed) {
		this.closed = closed;
	}
	
	public Date getDateReceived() {
		return dateReceived;
	}
	
	public void setDateReceived(Date dateReceived) {
		this.dateReceived = dateReceived;
	}

	public Date getNegotiationStart() {
		return negotiationStart;
	}
	
	public void setNegotiationStart(Date negotiationStart) {
		this.negotiationStart = negotiationStart;
	}

	public Date getNegotiationComplete() {
		return negotiationComplete;
	}
	
	public void setNegotiationComplete(Date negotiationComplete) {
		this.negotiationComplete = negotiationComplete;
	}
	
	public Date getDateClosed() {
		return dateClosed;
	}
	
	public void setDateClosed(Date dateClosed) {
		this.dateClosed = dateClosed;
	}
	
	public Long getDaysOpen() {
		return daysOpen;
	}
	
	public void setDaysOpen(Long daysOpen) {
		this.daysOpen = daysOpen;
	}
	
	/**
	 * Generate link for calling the negotiation log migration service
	 * @return
	 */
	public String getNegotiationLogMaintenanceDocumentLookup() {
	    if ( this.getNegotiationLogId() == null)
	        return "";
	    //Properties params = new Properties();
	    //params.put(KRADConstants.RETURN_LOCATION_PARAMETER, "portal.do");
        //params.put(KRADConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, getDocumentTypeName());
        //params.put(KRADConstants.RETURN_LOCATION_PARAMETER, getReturnLocation());
        //params.put("negotiationLogId", this.getNegotiationLogId().toString());
        //return "http://localhost:8080/kc-dev/negotiationNegotiation.do?methodToCall=migrate&amp;negotiationLogId="+this.getNegotiationLogId().toString();
	    //ConfigProperties.application.url
	    return "http://localhost:8080/kc-dev/portal.do?channelTitle=Migrate%20Negotiation%20Log&channelUrl=http://127.0.0.1:8080/kc-dev/negotiationNegotiation.do?methodToCall=migrate&amp;negotiationLogId="+this.getNegotiationLogId().toString();
	}
}
