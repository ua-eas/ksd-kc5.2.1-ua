package edu.arizona.kra.award.actions;

import org.kuali.kra.bo.KraPersistableBusinessObjectBase;

/**
 * A AwardBase Action Type refers to the type of actions that an
 * that can be performed against a AwardBase document.
 */
@SuppressWarnings("serial")
public class AwardActionTypeBase extends KraPersistableBusinessObjectBase {

	private String awardActionTypeCode;

    private String description;

    private boolean triggerSubmission;

    private boolean triggerPrint;

    private boolean finalActionForBatchPrint;

    /**
     * Constructs a AwardActionType.
     */
    public AwardActionTypeBase() {
    }

    public void setAwardActionTypeCode(String awardActionTypeCode) {
        this.awardActionTypeCode = awardActionTypeCode;
    }

    public String getAwardActionTypeCode() {
        return awardActionTypeCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTriggerSubmission(boolean triggerSubmission) {
        this.triggerSubmission = triggerSubmission;
    }

    public boolean getTriggerSubmission() {
        return triggerSubmission;
    }

    public void setTriggerPrint(boolean triggerPrint) {
        this.triggerPrint = triggerPrint;
    }

    public boolean getTriggerPrint() {
        return triggerPrint;
    }

    public boolean isFinalActionForBatchPrint() {
        return finalActionForBatchPrint;
    }

    public void setFinalActionForBatchPrint(boolean finalActionForBatchPrint) {
        this.finalActionForBatchPrint = finalActionForBatchPrint;
    }
}
