package edu.arizona.kra.irb.noteattachment.dao.ojb;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kra.bo.AttachmentFile;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.kra.irb.noteattachment.ProtocolAttachmentProtocol;
import org.kuali.kra.protocol.noteattachment.ProtocolAttachmentBase;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.springframework.transaction.annotation.Transactional;

import edu.arizona.kra.irb.noteattachment.dao.ProtocolAttachmentProtocolDao;

@Transactional
public class ProtocolAttachmentProtocolDaoOjb extends PlatformAwareDaoBaseOjb implements ProtocolAttachmentProtocolDao{
	
	private static final String ATTACHMENT_FILE_ID_COLUMN = "id";
	private static final String[] ATTACHMENT_FILE_NAME_RESULT_COLUMNS = new String[] { "name" };
	
	private static final String PAP_FILE_ID_COLUMN = "fileId";
	private static final String PAP_ID_COLUMN = "id";
	private static final String[] PAP_SOURCE_PROTOCOL_RESULT_COLUMNS = new String[] { "protocolNumber" };
	

	@Override
	public String getSourceProtocolNumber( ProtocolAttachmentBase protocolAttachment ) {
		
		Long fileId = protocolAttachment.getFileId();
		String sourceProtocolNumber = null;
		
        if (fileId != null) {
            // find the protocol number for this attachment's most recent attachment/modification
            // by retrieving all attachments with the same fileId and ordering them by id
        	Criteria crit = new Criteria();
    		crit.addEqualTo(PAP_FILE_ID_COLUMN, fileId);
    		crit.addOrderByAscending(PAP_ID_COLUMN);
    		
    		ReportQueryByCriteria query = QueryFactory.newReportQuery(ProtocolAttachmentProtocol.class, crit);
    		query.setAttributes(PAP_SOURCE_PROTOCOL_RESULT_COLUMNS);
    		
    		Iterator iter = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
    		
    		if (iter.hasNext()) {
    			Object[] results = (Object[]) iter.next();
    			sourceProtocolNumber = (String) results[0];
    		}
        }
        // Null FileId means attachment newly added and is not in the database yet
        else {
            sourceProtocolNumber = protocolAttachment.getProtocolNumber();
        }
		return sourceProtocolNumber;
	}

	@Override
	public String getFileName( ProtocolAttachmentBase protocolAttachment ) {
		Long fileId = protocolAttachment.getFileId();
		String fileName = "";
		
		Criteria crit = new Criteria();
		crit.addEqualTo(ATTACHMENT_FILE_ID_COLUMN, fileId);
		
		ReportQueryByCriteria query = QueryFactory.newReportQuery(AttachmentFile.class, crit);
		query.setAttributes(ATTACHMENT_FILE_NAME_RESULT_COLUMNS);
		
		Iterator iter = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
		
		while (iter.hasNext()) {
			Object[] results = (Object[]) iter.next();
			fileName = (String) results[0];
		}
		
		return fileName;
	}
	
}
