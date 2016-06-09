package edu.arizona.kra.protocol.dao;

import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.ojb.broker.accesslayer.LookupException;
import org.kuali.kra.protocol.noteattachment.ProtocolAttachmentBase;

public interface ProtocolAttachmentDao {

    /**
     * Returns the protocol number for when this attachment was originally created by looking at all attachments with the same fileId 
     * and ordered by id - first one should be the original
     * 
     * @param protocolAttachment
     * @return String sourceProtocolNumber
     * @throws LookupException 
     * @throws SQLException 
     */
    public String getSourceProtocolNumber(ProtocolAttachmentBase protocolAttachment) throws LookupException;
    
    /**
     * Retrieves the actual data in a file when a user requests to open it.
     * 
     * @param fileId
     * @return byte[] file contents
     * @throws SQLException 
     * @throws LookupException 
     */
    public byte[] getAttachmentData(Long fileId) throws LookupException;

    /**
     * Saves the actual data in a file when a user requests to save/update it.
     * 
     * @param fileId, byte[] attachmentData
     * @throws SQLException 
     * @throws LookupException 
     */
    public void saveAttachmentData(Long fileId, byte[] attachmentData) throws LookupException;


    /**
     * Returns a list of file ids that have NULL file data - which shouldn't exist.
     * 
     * @param protocolId
     * @return ArrayList<fileIds>
     * @throws SQLException 
     * @throws LookupException 
     */
    public ArrayList<Long> checkForNullFileData(Long protocolId) throws LookupException;


}