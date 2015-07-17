package edu.arizona.kra.award.printing;

public enum AwardPrintTypeModuleIdConstants {

	SYSTEM("Y", "System"),
    PROTOCOL("P", "Protocol"),
    SCHEDULE("S", "Schedule"),
    PROTOCOL_SUBMISSION("U", "Protocol Submission"),
    COMMITTEE("C", "Committee");
    
    private final String code;   
    private final String description; 
    AwardPrintTypeModuleIdConstants(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode()   
    { 
        return code; 
    }

    public String getDescription() 
    { 
        return description; 
    }
}
