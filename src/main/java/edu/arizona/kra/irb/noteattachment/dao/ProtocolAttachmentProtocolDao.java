package edu.arizona.kra.irb.noteattachment.dao;

import org.kuali.kra.protocol.noteattachment.ProtocolAttachmentBase;

public interface ProtocolAttachmentProtocolDao {
	public String getSourceProtocolNumber(ProtocolAttachmentBase protocolAttachment);
	public String getFileName(ProtocolAttachmentBase protocolAttachment);
}
