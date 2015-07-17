package edu.arizona.kra.award.printing;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kra.bo.KraPersistableBusinessObjectBase;

public class AwardPrintTypeBase extends KraPersistableBusinessObjectBase {

	private static final long serialVersionUID = -4725522827463252054L;
    public static final String APPROVAL_LETTER = "1";
    public static final String WITHDRAWAL_NOTICE = "16";
    public static final String GRANT_EXEMPTION_NOTICE = "17";
    public static final String CLOSURE_NOTICE = "26";
    public static final String ABANDON_NOTICE = "28";
    public static final String NOTICE_OF_DEFERRAL = "3";
    public static final String SRR_LETTER = "4";
    public static final String SMR_LETTER = "6";
    public static final String EXPEDITED_APPROVAL_LETTER = "5";
    public static final String SUSPENSION_NOTICE = "7";
    public static final String TERMINATION_NOTICE = "8";

    private String awardPrintTypeCode;

    private String description;

    private String moduleId;
    
    private List<AwardPrintTemplate> awardPrintTemplates;

    public AwardPrintTypeBase() {
        setAwardPrintTemplates(new ArrayList<AwardPrintTemplate>());
    }

    public String getAwardPrintTypeCode() {
        return awardPrintTypeCode;
    }

    public void setAwardPrintTypeCode(String awardPrintTypeCode) {
        this.awardPrintTypeCode = awardPrintTypeCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public List<AwardPrintTemplate> getAwardPrintTemplates() {
        return awardPrintTemplates;
    }

    /**
     * 
     * This method returns the default print template.
     * @return default award print template
     */
    public AwardPrintTemplate getDefaultAwardPrintTemplate() {
        for (AwardPrintTemplate template : this.awardPrintTemplates) {
        	return template;
        }
        return null;
    }

    public void setAwardPrintTemplates(List<AwardPrintTemplate> awardPrintTemplates) {
        this.awardPrintTemplates = awardPrintTemplates;
    }
}
