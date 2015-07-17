package edu.arizona.kra.award.printing;

import org.apache.commons.lang.StringUtils;

public class AwardPrintTypeTemplate extends AwardPrintTypeBase {

	/**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 581785796901521423L;

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
    
    @SuppressWarnings("unused")
    private transient AwardPrintTypeModuleIdConstants module;

    /**
     * 
     * This method returns the module enum specified by this {@code AwardPrintType}.
     * @return Matching {@code PrintTypeModuleIdConstants} specified by the moduleId of this
     * print type, or PrintTypeModuleIdConstants.SYSTEM if no matching co
     */
    public AwardPrintTypeModuleIdConstants getModule() {
        String moduleId = getModuleId();
        for(AwardPrintTypeModuleIdConstants module : AwardPrintTypeModuleIdConstants.values()) {
            if(StringUtils.equals(module.getCode(),moduleId)) {
                return module;
            }
        }
        return AwardPrintTypeModuleIdConstants.SYSTEM;
    }

    public void setModule(AwardPrintTypeModuleIdConstants module) {
        this.module = module;
    }
}
