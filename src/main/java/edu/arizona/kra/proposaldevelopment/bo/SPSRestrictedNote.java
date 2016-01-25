package edu.arizona.kra.proposaldevelopment.bo;

import java.sql.Date;
import java.sql.Timestamp;

import javax.persistence.Transient;

import org.kuali.kra.bo.KraPersistableBusinessObjectBase;
import org.kuali.kra.proposaldevelopment.bo.DevelopmentProposal;

public class SPSRestrictedNote extends KraPersistableBusinessObjectBase{
    private static final long serialVersionUID = 3127043554039982403L;

    private Integer id;   
    
    private String proposalNumber;
  
    private Timestamp createdDate;
    
    private String authorId;
    
    @Transient
    private String authorName;
   
    private String noteText;
    
    private String noteTopic;
    
    private Date proposalReceivedDate;
    
    private String proposalReceivedTime;
    
    private Boolean active = Boolean.TRUE;
    
    @Transient
    private DevelopmentProposal propDev;
  

   
    public Date getProposalReceivedDate() {
        return proposalReceivedDate;
    }

    public void setProposalReceivedDate(Date proposalReceivedDate) {
        this.proposalReceivedDate = proposalReceivedDate;
    }

    public String getProposalReceivedTime() {
        return proposalReceivedTime;
    }

    public void setProposalReceivedTime(String proposalReceivedTime) {
        this.proposalReceivedTime = proposalReceivedTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProposalNumber() {
        return proposalNumber;
    }

    public void setProposalNumber(String proposalNumber) {
        this.proposalNumber = proposalNumber;
    }

    public Timestamp getCreateDate() {
        return createdDate;
    }

    public void setCreateDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getNoteText() {
        return noteText;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }

    public DevelopmentProposal getPropDev() {
        return propDev;
    }

    public void setPropDev(DevelopmentProposal propDev) {
        this.propDev = propDev;
    }

    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    public String getNoteTopic() {
        return noteTopic;
    }

    public void setNoteTopic(String noteTopic) {
        this.noteTopic = noteTopic;
    }

}
