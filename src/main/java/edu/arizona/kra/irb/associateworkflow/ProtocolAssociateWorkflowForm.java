package edu.arizona.kra.irb.associateworkflow;

import java.util.List;

import org.kuali.rice.kns.web.struts.form.KualiForm;

@SuppressWarnings("deprecation")
public class ProtocolAssociateWorkflowForm extends KualiForm {
	private static final long serialVersionUID = 4830500744451697391L;
	
	public ProtocolAssociateWorkflowForm() {
		super();
	}
	
	private List<ProtocolAssociateWorkflowLinkInfo> linksList;

	public List<ProtocolAssociateWorkflowLinkInfo> getLinksList() {
		return linksList;
	}

	public void setLinksList(List<ProtocolAssociateWorkflowLinkInfo> linksList) {
		this.linksList = linksList;
	}
}
