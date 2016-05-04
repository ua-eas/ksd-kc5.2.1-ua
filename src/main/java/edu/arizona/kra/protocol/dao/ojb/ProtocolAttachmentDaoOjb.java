package edu.arizona.kra.protocol.dao.ojb;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.ojb.broker.accesslayer.LookupException;
import org.kuali.kra.protocol.noteattachment.ProtocolAttachmentBase;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

import edu.arizona.kra.protocol.dao.ProtocolAttachmentDao;
import edu.arizona.kra.util.DBConnection;


public class ProtocolAttachmentDaoOjb extends PlatformAwareDaoBaseOjb implements ProtocolAttachmentDao {
    private static final Logger LOG = LoggerFactory.getLogger(ProtocolAttachmentDaoOjb.class);

    private static final String PROTOCOL_NUMBER_QUERY = "SELECT * from (SELECT protocol_number FROM protocol_attachment_protocol WHERE file_id = ? ORDER BY pa_protocol_id) where ROWNUM = 1";
    private static final String FILE_CONTENTS_QUERY = "SELECT file_data FROM attachment_file WHERE file_id = ?";
    private static final String UPDATE_FILE_QUERY = "UPDATE attachment_file SET file_data = ? WHERE file_id = ?";
    private static final String FIND_EMPTY_FILES_QUERY = "SELECT file_id FROM attachment_file WHERE file_data IS NULL " +
            "AND file_id IN (SELECT file_id FROM protocol_attachment_protocol WHERE protocol_id_fk = ?)";


    @Override
    public String getSourceProtocolNumber( ProtocolAttachmentBase protocolAttachment ) throws LookupException {
        if ( protocolAttachment == null){
            throw new IllegalArgumentException("ProtocolAttachment cannot be null!");
        }
        LOG.debug("getSourceProtocolNumber() docId={} fileId={}.",protocolAttachment.getDocumentId(),protocolAttachment.getFileId());
        Long fileId = protocolAttachment.getFileId();
        String sourceProtocolNumber = null;

        if (fileId == null) {
                // Null FileId means attachment newly added and is not in the database yet
                   sourceProtocolNumber = protocolAttachment.getProtocolNumber();
        } else {
            // find the protocol number for when this attachment was originally created
            // by looking at all attachments with the same fileId and ordered by id - first one should be the original.
            Object[] params = new Object[] { fileId };
            
            try (DBConnection dbc = new DBConnection(this.getPersistenceBroker(true)) ){
                ResultSet rs = dbc.executeQuery(PROTOCOL_NUMBER_QUERY, params);
                if (rs.next()){
                    sourceProtocolNumber = rs.getString(1);
                    LOG.debug("getSourceProtocolNumber() - fileId={} sourceProtocolNumber={}.",fileId,sourceProtocolNumber);
                }
            } catch (SQLException sqle) {
                LOG.error("SQLException: " + sqle.getMessage(), sqle);
                throw new LookupException(sqle);
            } catch (LookupException le) {
                LOG.error("LookupException: " + le.getMessage(), le);
                throw le;
            }
        }
        
        LOG.debug("End getSourceProtocolNumber() result={}",sourceProtocolNumber);
        return sourceProtocolNumber;
    }


    @Override
    public byte[] getAttachmentData(Long fileId) throws LookupException {
        LOG.debug("getAttachmentData() fileId={}",fileId);
        if ( fileId == null ){
            throw new IllegalArgumentException("FileId cannot be null!");
        }
        byte[] result = null;
        Object[] params = new Object[] { fileId };

        try (DBConnection dbc = new DBConnection(this.getPersistenceBroker(true)) ){
            ResultSet rs = dbc.executeQuery(FILE_CONTENTS_QUERY, params);
            if (rs.next()){
                result = rs.getBytes(1);
                LOG.debug("getAttachmentData() - found data for fileId={} size={}.",fileId,result.length);
            }
        } catch (SQLException sqle) {
            LOG.error("SQLException: " + sqle.getMessage(), sqle);
            throw new LookupException(sqle);
        } catch (LookupException le) {
            LOG.error("LookupException: " + le.getMessage(), le);
            throw le;
        }

        LOG.debug("End getAttachmentData() fileId={}.",fileId);
        return result;
    }

    @Override
    public void saveAttachmentData(Long fileId, byte[] attachmentData) throws LookupException {
        if ( fileId == null || attachmentData == null){
            throw new IllegalArgumentException("FileId or contents cannot be null!");
        }
        LOG.debug("saveAttachmentData() fileId={} saveAttachmentData length={}.",fileId,attachmentData.length);   

        Object[] params = new Object[] { attachmentData, fileId };
        try (DBConnection dbc = new DBConnection(this.getPersistenceBroker(true)) ){

            dbc.executeQuery(UPDATE_FILE_QUERY, params);

        } catch (SQLException sqle) {
            LOG.error("SQLException: " + sqle.getMessage(), sqle);
            throw new LookupException(sqle);
        } catch (LookupException le) {
            LOG.error("LookupException: " + le.getMessage(), le);
            throw le;
        }

        LOG.debug("End saveAttachmentData() fileId={}.",fileId);
    }

    @Override
    public ArrayList<Long> checkForNullFileData(Long protocolId) throws LookupException {
        LOG.debug("checkForNullFileData() protocolId={}.",protocolId);
        if ( protocolId == null ){
            throw new IllegalArgumentException("ProtocolId cannot be null!");
        }
        Object[] params = new Object[] { protocolId };
        ArrayList<Long> emptyFileIds = new ArrayList<Long>();

        try (DBConnection dbc = new DBConnection(this.getPersistenceBroker(true)) ){

            ResultSet rs = dbc.executeQuery(FIND_EMPTY_FILES_QUERY, params);
            while (rs.next()){
                emptyFileIds.add( rs.getLong(1) );
                LOG.debug("checkForNullFileData() - found empty fileId={}!!!",rs.getLong(0));
            }

        } catch (SQLException sqle) {
            LOG.error("SQLException: " + sqle.getMessage(), sqle);
            throw new LookupException(sqle);
        } catch (LookupException le) {
            LOG.error("LookupException: " + le.getMessage(), le);
            throw le;
        }

        LOG.debug("End checkForNullFileData() protocolId={} number of empty files found={}.",protocolId,emptyFileIds.size());
        return emptyFileIds;
    }

}