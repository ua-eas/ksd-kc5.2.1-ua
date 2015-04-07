package org.kuali.kra.test.fixtures;

public enum NsfCodeFixture {

	KEY_VALUE_A_01( 2, "A.01", "Aeronautical and Astronautical - Engineering: A.01" ),
	KEY_VALUE_A_02( 6, "A.02", "Bioengineering/Biomedical - Engineering: A.02" ),
	KEY_VALUE_A_03( 9, "A.03", "Chemical - Engineering: A.03" ),
	KEY_VALUE_A_04( 11, "A.04", "Civil - Engineering: A.04" ),
	KEY_VALUE_A_05( 19, "A.05", "Electrical - Engineering: A.05" ),
	KEY_VALUE_A_06( 22, "A.06", "Mechanical  - Engineering: A.06" ),
	KEY_VALUE_A_07( 24, "A.07", "Metallurgical and Materials  - Engineering: A.07" ),
	KEY_VALUE_A_99( 26, "A.99", "Other - Engineering: A.99" ),
	KEY_VALUE_B_01( 4, "B.01", "Astronomy - Physical Sciences: B.01" ),
	KEY_VALUE_B_02( 10, "B.02", "Chemistry - Physical Sciences: B.02" ),
	KEY_VALUE_B_03( 33, "B.03", "Physics - Physical Sciences: B.03" ),
	KEY_VALUE_B_99( 30, "B.99", "Other - Physical Sciences: B.99" ),
	KEY_VALUE_C_01( 5, "C.01", "Atmospheric - Environmental Sciences: C.01" ),
	KEY_VALUE_C_02( 16, "C.02", "Earth Sciences - Environmental Sciences: C.02" ),
	KEY_VALUE_C_03( 25, "C.03", "Oceanography - Environmental Sciences: C.03" ),
	KEY_VALUE_C_99( 27, "C.99", "Other - Environmental Sciences: C.99" ),
	KEY_VALUE_D_01( 21, "D.01", "Mathematical Science: D.01" ),
	KEY_VALUE_E_01( 13, "E.01", "Computer Sciences: E.01" ),
	KEY_VALUE_F_01( 3, "F.01", "Agricultural - Life Sciences: F.01" ),
	KEY_VALUE_F_02( 7, "F.02", "Biological - Life Sciences: F.02" ),
	KEY_VALUE_F_03( 23, "F.03", "Medical - Life Sciences: F.03" ),
	KEY_VALUE_F_99( 28, "F.99", "Other - Life Sciences: F.99" ),
	KEY_VALUE_G_01( 35, "G.01", "Psychology: G.01" ),
	KEY_VALUE_H_01( 17, "H.01", "Economics - Social Sciences: H.01" ),
	KEY_VALUE_H_02( 34, "H.02", "Political Science - Social Sciences: H.02" ),
	KEY_VALUE_H_03( 37, "H.03", "Sociology - Social Sciences: H.03" ),
	KEY_VALUE_H_99( 31, "H.99", "Other - Social Sciences: H.99" ),
	KEY_VALUE_I_01( 32, "I.01", "Other Sciences, n.e.c.: I.01" ),
	KEY_VALUE_J_01( 18, "J.01", "Education - Non-Science and Engineering Fields: J.01" ),
	KEY_VALUE_J_02( 1, "J.02", "Law - Non-Science and Engineering Fields: J.02" ),
	KEY_VALUE_J_03( 20, "J.03", "Humanities - Non-Science and Engineering Fields: J.03" ),
	KEY_VALUE_J_04( 38, "J.04", "Visual and Performing Arts - Non-Science and Engineering Fields: J.04" ),
	KEY_VALUE_J_05( 8, "J.05", "Business and Management - Non-Science and Engineering Fields: J.05" ),
	KEY_VALUE_J_06( 12, "J.06", "Communications, Journalism and Library Sciences - Non-Science and Engineering Fields: J.06" ),
	KEY_VALUE_J_07( 36, "J.07", "Social Work - Non-Science and Engineering Fields: J.07" ),
	KEY_VALUE_J_98( 98, "J.98", "Costs to be allocated: J.98" ),
	KEY_VALUE_J_99( 15, "J.99", "Other - Non-Science and Engineering Fields: J.99" ),
	KEY_VALUE_Z_99( 14, "Z.99", "Costs not included in NSF report - used for reconciliation purposes: Z.99" );

	private final String code;
	private final int key;
	private final String value;

	private NsfCodeFixture(int key, String code, String value) {
		this.key = key;
		this.code = code;
		this.value = value;

	}

	public String getCode() {
		return code;
	}

	public int getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

}
