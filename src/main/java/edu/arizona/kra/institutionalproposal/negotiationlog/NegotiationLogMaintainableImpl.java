package edu.arizona.kra.institutionalproposal.negotiationlog;

import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.kra.maintenance.KraMaintainableImpl;
import org.kuali.rice.krad.service.SequenceAccessorService;

public class NegotiationLogMaintainableImpl extends KraMaintainableImpl {
	
	private static final long serialVersionUID = 6415435826060563983L;
	protected static final String NEGOTIATION_LOG_SEQUENCE_NAME = "NEGOTIATION_LOG_SEQ";
	
	private transient SequenceAccessorService sequenceAccessorService;
	
    /**
     * 
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#setGenerateDefaultValues(java.lang.String)
     */
    @Override
    public void setGenerateDefaultValues(String docTypeName) {
        super.setGenerateDefaultValues(docTypeName);
        NegotiationLog log = (NegotiationLog) getBusinessObject();
        log.setNegotiationLogId(getSequenceAccessorService().getNextAvailableSequenceNumber(NEGOTIATION_LOG_SEQUENCE_NAME, NegotiationLog.class).intValue());
    }
    
    @Override
    public void prepareForSave() {
    	NegotiationLog log = (NegotiationLog) getBusinessObject();
    	if (log.getDateReceived() != null && log.getDateClosed() != null) {
    		long daysOpen = ((log.getDateClosed().getTime() - log.getDateReceived().getTime()) / (1000 * 60 * 60 * 24)) + 1;
    		log.setDaysOpen(daysOpen);
    	} else {
    		log.setDaysOpen(null);
    	}
    	super.prepareForSave();
    }

	protected SequenceAccessorService getSequenceAccessorService() {
	    if(sequenceAccessorService == null) {
	        sequenceAccessorService = KraServiceLocator.getService(SequenceAccessorService.class);
	    }
	    return sequenceAccessorService;
	}
	
	public void setSequenceAccessorService(SequenceAccessorService sequenceAccessorService) {
	    this.sequenceAccessorService = sequenceAccessorService;
	}
}
