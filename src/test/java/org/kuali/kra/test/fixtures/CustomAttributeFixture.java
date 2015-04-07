package org.kuali.kra.test.fixtures;

public enum CustomAttributeFixture {

	CUSTOM_ATTRIBUTE_1(1, "billingElement", "Billing Element", "1", 40, "Personnel Items for Review"),
	CUSTOM_ATTRIBUTE_2(2, "costSharingBudget", "Cost Sharing Budget", "1", 30, "Project Details");
	
	private Integer id;
	private String name;
	private String label;
	private String dataTypeCode;
	private Integer dataLength;
	private String groupName;

	private CustomAttributeFixture (Integer id, String name, String label, String dataTypeCode, Integer dataLength, String groupName) {
		this.id = id;
    	this.name = name;
    	this.label = label;
    	this.dataTypeCode = dataTypeCode;
    	this.dataLength = dataLength;
    	this.groupName = groupName;
	}

	public Integer getId() {
		return id;
	}

	public void setId( Integer id ) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel( String label ) {
		this.label = label;
	}

	public String getDataTypeCode() {
		return dataTypeCode;
	}

	public void setDataTypeCode( String dataTypeCode ) {
		this.dataTypeCode = dataTypeCode;
	}

	public Integer getDataLength() {
		return dataLength;
	}

	public void setDataLength( Integer dataLength ) {
		this.dataLength = dataLength;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName( String groupName ) {
		this.groupName = groupName;
	}

	
}
