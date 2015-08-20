package edu.arizona.kra.global;

/**
 * This enum contains the delimiters used, per convention, by
 * implementing Maintainable classes. More specifically, these
 * are used to build unique strings in combination with an
 * object's components. This unique string is then used in
 * a lock to prevent the wrapper document from being mutated
 * by multiple users at one time.
 */
public enum DelimiterConstants {

	AFTER_CLASS("!!"),
	AFTER_FIELDNAME("^^"),
	AFTER_VALUE("::");

	private String delim;

	private DelimiterConstants (String delim) {
		this.delim = delim;
	}

	public String getValue() {
		return delim;
	}

}
