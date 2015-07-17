package edu.arizona.kra.award.printing;

import org.kuali.kra.bo.KraPersistableBusinessObjectBase;

import edu.arizona.kra.award.actions.AwardActionTypeBase;

public class ValidAwardActionPrint extends KraPersistableBusinessObjectBase {

	private static final long serialVersionUID = 1L;

    private Long validAwardActionPrintId;

    private String awardActionTypeCode;

    private String awardPrintTypeCode;

    private boolean finalFlag;

    private AwardActionTypeBase awardActionType;
    
    private AwardPrintTypeBase awardPrintType;
    
    public ValidAwardActionPrint() {
    }

    public Long getValidAwardActionPrintId() {
        return validAwardActionPrintId;
    }

    public void setValidAwardActionPrintId(Long validAwardActionPrintId) {
        this.validAwardActionPrintId = validAwardActionPrintId;
    }

    public String getAwardActionTypeCode() {
        return awardActionTypeCode;
    }

    public void setAwardActionTypeCode(String awardActionTypeCode) {
        this.awardActionTypeCode = awardActionTypeCode;
    }

    public String getAwardPrintTypeCode() {
        return awardPrintTypeCode;
    }

    public void setAwardPrintTypeCode(String awardPrintTypeCode) {
        this.awardPrintTypeCode = awardPrintTypeCode;
    }

    public boolean getFinalFlag() {
        return finalFlag;
    }

    public void setFinalFlag(boolean finalFlag) {
        this.finalFlag = finalFlag;
    }

    public AwardActionTypeBase getAwardActionType() {
        return awardActionType;
    }

    public void setAwardActionType(AwardActionTypeBase awardActionType) {
        this.awardActionType = awardActionType;
    }

    public AwardPrintTypeBase getAwardPrintType() {
        return awardPrintType;
    }

    public void setAwardPrintType(AwardPrintTypeBase awardPrintType) {
        this.awardPrintType = awardPrintType;
    }
}
