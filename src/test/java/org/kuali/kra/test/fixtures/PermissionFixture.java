package org.kuali.kra.test.fixtures;

import org.kuali.kra.infrastructure.PermissionConstants;

public enum PermissionFixture {

	VIEW_QUESTIONNAIRE("1", "KC-QUESTIONNAIRE", PermissionConstants.VIEW_QUESTIONNAIRE, true),
	MODIFY_QUESTIONNAIRE("1", "KC-QUESTIONNAIRE", PermissionConstants.MODIFY_QUESTIONNAIRE, true),
	VIEW_QUESTION("TEST1", "KC-QUESTIONNAIRE", PermissionConstants.VIEW_QUESTION, true),
	MODIFY_QUESTION("TEST2", "KC-QUESTIONNAIRE", PermissionConstants.MODIFY_QUESTION, true),
	CREATE_PROTOCOL_DOCUMENT("2", "KC-PROTOCOL", PermissionConstants.CREATE_PROTOCOL, true),
	MAINTAIN_PROTOCOL_SUBMISSIONS("3", "KC-PROTOCOL", PermissionConstants.MAINTAIN_PROTOCOL_SUBMISSIONS, true),
	VIEW_COMMITTEE("TEST3", "KC-PROTOCOL", PermissionConstants.VIEW_COMMITTEE, true),
	MODIFY_COMMITTEE("TEST4", "KC-PROTOCOL", PermissionConstants.MODIFY_COMMITTEE, true),
	PERFORM_COMMITTEE_ACTIONS("TEST5", "KC-PROTOCOL", PermissionConstants.PERFORM_COMMITTEE_ACTIONS, true),
	MODIFY_IACUC_COMMITTEE("TEST6", "KC-IACUC", PermissionConstants.MODIFY_IACUC_COMMITTEE, true),
	VIEW_IACUC_COMMITTEE("TEST7", "KC-IACUC", PermissionConstants.VIEW_IACUC_COMMITTEE, true);

	
	private String permId;
	private String namespaceCode;
	private String name;
	private boolean active;
	
	private PermissionFixture (String permId, String namespaceCode, String name, boolean active) {
		this.permId = permId;
		this.namespaceCode = namespaceCode;
		this.name = name;
		this.active = active;
	}

	public String getPermId() {
		return permId;
	}

	public String getNamespaceCode() {
		return namespaceCode;
	}

	public String getName() {
		return name;
	}

	public boolean isActive() {
		return active;
	}
			
}
