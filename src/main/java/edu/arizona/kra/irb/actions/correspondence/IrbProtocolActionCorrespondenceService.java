package edu.arizona.kra.irb.actions.correspondence;

import org.kuali.kra.irb.Protocol;


/**
 * A service to handle ProtocolAction tasks.
 */
public interface IrbProtocolActionCorrespondenceService {
	
	/**
	 * Copy and persist ProtocolCorresponce from source to destination protocol.
	 * This method assumes that the protocol is in the middle of being versioned,
	 * where the source is being cancelled and teh destination is the next version
	 * of the protocol.
	 * 
	 * @param srcProtocol
	 * @param destProtocol
	 */
	public void copyActionCorrespondence(Protocol srcProtocol, Protocol destProtocol);
}
