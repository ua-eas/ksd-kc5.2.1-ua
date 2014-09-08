package edu.arizona.kra.irb.associateworkflow;

import java.io.Serializable;

public class ProtocolAssociateWorkflowLinkInfo implements Serializable {
	private static final long serialVersionUID = -3497619079825351779L;

	private String url;
	private String anchorText;
	private String linkDescription;
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getAnchorText() {
		return anchorText;
	}
	public void setAnchorText(String anchorText) {
		this.anchorText = anchorText;
	}
	public String getLinkDescription() {
		return linkDescription;
	}
	public void setLinkDescription(String linkDescription) {
		this.linkDescription = linkDescription;
	}
}
