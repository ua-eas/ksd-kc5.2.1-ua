package edu.arizona.kra.dao;

import java.util.ArrayList;

public interface ProtocolAttachmentDao {

	public byte[] getAttachmentData( Long fileId );

	public void saveAttachmentData( Long fileId, byte[] attachmentData );

	public ArrayList<Long> checkForNullFileData( Long protocolId );

}