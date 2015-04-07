package org.kuali.kra.test.fixtures;

public enum QuestionCategoryFixture {
	GROUP_TYPE_001( 1, "Misc - to be grouped" ),
	GROUP_TYPE_002( 2, "S2S Forms for Proposal Development" ),
	GROUP_TYPE_003( 3, "Other Proposal Development" ),
	GROUP_TYPE_004( 4, "IRB Human Protocol" ),
	GROUP_TYPE_005( 5, "IACUC Animal Protocol" ),
	GROUP_TYPE_006( 6, "Certifications for Proposal Development" ),
	GROUP_TYPE_007( 7, "Financial Conflict of Interest" );

	private final int categoryTypeCode;
	private final String categoryName;

	private QuestionCategoryFixture(int categoryTypeCode, String categoryName) {
		this.categoryTypeCode = categoryTypeCode;
		this.categoryName = categoryName;
	}

	public int getCategoryTypeCode() {
		return categoryTypeCode;
	}

	public String getCategoryName() {
		return categoryName;
	}

}
