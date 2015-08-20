package edu.arizona.kra.global.unit;

/**
 * This enum encapsulates the primary keys of the
 * UnitAdministrator class. It should be used instead
 * of using magic strings.
 */
public enum UnitAdministratorPrimaryKey {

	PERSON_ID("personId"),
	UNIT_ADMIN_TYPE_CODE("unitAdministratorTypeCode"),
	UNIT_NUMBER("unitNumber");

	private String value;

	private UnitAdministratorPrimaryKey(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
