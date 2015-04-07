package org.kuali.kra.test.fixtures;

import java.util.HashMap;
import java.util.Map;

import org.kuali.kra.infrastructure.RoleConstants;


public enum RoleFixture {

	// ************************************************************************
	// NEGOTIATION
	//*************************************************************************
	
	// Can do everything
	NEGOTIATION_ADMIN("TEST1", RoleConstants.NEGOTIATION_ROLE_TYPE, "Negotiation Administrator", new HashMap<String, String>(), "1", true),
	
	// Can do everything, but is derived
	NEGOTIATION_NEGOTIATOR("TEST2", RoleConstants.NEGOTIATION_ROLE_TYPE, "Negotiator", new HashMap<String, String>(), "1023", true),
	
	// Can do everything but modify activities
	NEGOTIATION_CREATOR("TEST3", RoleConstants.NEGOTIATION_ROLE_TYPE, "Negotiation Creator", new HashMap<String, String>(), "1", true),
	
	// Can only view, different roles for tracking
	NEGOTIATION_INVESTIGATORS("TEST4", RoleConstants.NEGOTIATION_ROLE_TYPE, "Investigators", new HashMap<String, String>(), "1024", true), // Principle Investigator
	NEGOTIATION_PI("TEST5", RoleConstants.NEGOTIATION_ROLE_TYPE, "PI", new HashMap<String, String>(), "1024", true), // Principle Investigator
	NEGOTIATION_COI("TEST6", RoleConstants.NEGOTIATION_ROLE_TYPE, "COI", new HashMap<String, String>(), "1024", true), // Co-Investigator
	NEGOTIATION_KP("TEST7", RoleConstants.NEGOTIATION_ROLE_TYPE, "KP", new HashMap<String, String>(), "1024", true), // Key Personnel


	// ************************************************************************
	// TIME AND MONEY
	//*************************************************************************
	PROTOCOL_PI("TEST8", RoleConstants.PROTOCOL_ROLE_TYPE, "PI", new HashMap<String, String>(), "1021", true), // Principle Investigator
	
	
	// ************************************************************************
	// TIME AND MONEY
	//*************************************************************************

	TIME_AND_MONEY_VIEWER("TEST9", "KC-T", "Time And Money Viewer", new HashMap<String, String>(), "1002", true),
	TIME_AND_MONEY_MODIFIER("TEST10", "KC-T", "Time And Money Modifier", new HashMap<String, String>(), "1002", true),

	
	// ************************************************************************
	// TIME AND MONEY
	//*************************************************************************
	
	COMMITTEE_ADMIN("TEST11", "KC-COMMITTEE", "Committee Administrator", new HashMap<String, String>(), "1001", true),


	// ************************************************************************
	// MISC
	//*************************************************************************

	MAINTAIN_QUESTIONNAIRE("TEST12", "KC-QUESTIONNAIRE", "Maintain Questionnaire", new HashMap<String, String>(), "1", true),
	VIEW_QUESTIONNAIRE("TEST13", "KC-QUESTIONNAIRE", "View Questionnaire", new HashMap<String, String>(), "1", true),

	MODIFY_QUESTION("TEST14", "KC-QUESTIONNAIRE", "Modify Question", new HashMap<String, String>(), "1", true),
	VIEW_QUESTION("TEST15", "KC-QUESTIONNAIRE", "View Question", new HashMap<String, String>(), "1", true),

	//Subaward
	SUBAWARD_MODIFIER("TEST16", "KC-SUBAWARD", "Modify Subaward", new HashMap<String, String>(), "1", true),

	MODIFY_ORGANIZATIONS("TEST17", "KC-UNT", "Modify Organizations", new HashMap<String, String>(), "1002", true),

	CREATE_PROTOCOL("TEST18", "KC-UNT", "Create Protocol", new HashMap<String, String>(), "1", true),
	MAINTAIN_PROTOCOL("TEST19", "KC-UNT", "Maintain Protocol", new HashMap<String, String>(), "1", true),
	
	@SuppressWarnings("serial")
	IRB_COMMITTEE("TEST15", "KC-UNT", "IRB Committee", new HashMap<String, String>(){{put("unitNumber","*");}}, "1002", true),
	
	@SuppressWarnings("serial")
	IRB_ADMIN("TEST16", "KC-UNT", "IRB Administrator", new HashMap<String, String>(){{put("unitNumber","*");}}, "1002", true),
	USER("TEST19", "KUALI", "User", new HashMap<String, String>(), "2", true),
	
	COI_MAINTAINER("TEST20", "KC-COIDISCLOSURE", "COI Maintainer", new HashMap<String, String>(), "1", true),
	
	// As found in ProposalDevelopmentDocument:359, all lower case
	APPROVER("TEST21", "KC-PD", "approver", new HashMap<String, String>(), "1", true),
	
	// Protocol Unit Hierarchy requires these qualifications
	@SuppressWarnings("serial")
	SUPER_USER("TEST22", "KC-SYS", "KC Superuser", new HashMap<String, String>(){{put("unitNumber","*"); put("subunits","*");}}, "1002", true);


	private String roleId;
	private String namespaceCode;
	private String roleName;
	private Map<String, String> qualifications;
	private String kimTypId;
	private boolean active;


	private RoleFixture(String roleId, String namespaceCode, String roleName, Map<String, String> qualifications, String kimTypId, boolean active) {
		this.roleId = roleId;
		this.namespaceCode = namespaceCode;
		this.roleName = roleName;
		this.qualifications = qualifications;
		this.kimTypId = kimTypId;
		this.active = active;
	}

	
	
	public String getRoleId() {
		return roleId;
	}

	public String getNamespaceCode() {
		return namespaceCode;
	}

	public String getRoleName() {
		return roleName;
	}

	public Map<String, String> getQualifications() {
		return qualifications;
	}

	public String getKimTypId() {
		return kimTypId;
	}

	public boolean isActive() {
		return active;
	}

}
