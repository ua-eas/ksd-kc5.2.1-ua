package org.kuali.kra.test.fixtures;


/**
 * A fixture to encapsulate Person values. These specific entries actually
 * correspond to EDS entries. This means when the tests are run in the Foundation
 * project, these will get inserted into the Entity and Person tables. When run
 * against the UA overlay, the LDAP module will pull these records from EDS. 
 * 
 */
public enum PersonFixture {

    	UAR_TEST_001("uar-test-001", "99499279", "T876237632753327488"),
    	UAR_TEST_002("uar-test-002", "96175817", "T751304540364071680"),
    	UAR_TEST_003("uar-test-003", "91391731", "T721935126977041408"),
    	UAR_TEST_004("uar-test-004", "97598560", "T497844836302101632"),
    	UAR_TEST_005("uar-test-005", "98401126", "T741347443731501696"),
    	UAR_TEST_006("uar-test-006", "98690796", "T346117386128753408"),
    	UAR_TEST_007("uar-test-007", "92878434", "T847477736277505792"),
    	UAR_TEST_008("uar-test-008", "96166297", "T113783222949132324"),
    	UAR_TEST_009("uar-test-009", "98246428", "T339463504916056992"),
    	UAR_TEST_010("uar-test-010", "94060265", "T881289723888039552");
    	
    	private String principalName;
    	private String entityId;
    	private String principalId;


    	private PersonFixture(String principalName, String entityId, String principalId) {
    		this.principalName = principalName;
    		this.entityId = entityId;
    		this.principalId = principalId;
    	}

		public String getPrincipalName() {
			return principalName;
		}

		public String getEntityId() {
			return entityId;
		}

		public String getPrincipalId() {
			return principalId;
		}

}
