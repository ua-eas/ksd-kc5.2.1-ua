package edu.arizona.kra.irb.actions.correspondence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.kra.irb.Protocol;
import org.kuali.kra.protocol.actions.ProtocolActionBase;
import org.kuali.kra.protocol.correspondence.ProtocolCorrespondence;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class is meant to offload IRB ProtocolAction correspondence
 * tasks from other services. Note, this was not pushed up into a
 * base service since the IRB/IUCUC re-factor is not currently
 * stable, and a need has not yet been demonstrated on the IACUC side.
 */
public class IrbProtocolActionCorrespondenceServiceImpl implements IrbProtocolActionCorrespondenceService {
	private static final Log LOG = LogFactory.getLog(IrbProtocolActionCorrespondenceServiceImpl.class);
	
	private BusinessObjectService businessObjectService;

	/**
	 * @see edu.arizona.kra.irb.actions.correspondence.IrbProtocolActionCorrespondenceService#copyActionCorrespondence(org.kuali.kra.protocol.ProtocolBase, org.kuali.kra.protocol.ProtocolBase)
	 */
	@Override
	public void copyActionCorrespondence(Protocol srcProtocol, Protocol destProtocol) {
    	List<ProtocolActionBase> srcActions = srcProtocol.getProtocolActions();
    	List<ProtocolActionBase> destActions = destProtocol.getProtocolActions();
    	
    	// The ProtocolVersion service should have already matched/copied both sets of
    	// one-to-one ProtocolActions from source/dest protocols.
    	if(ObjectUtils.isNull(srcActions) || ObjectUtils.isNull(destActions)){
    		LOG.warn("ProtocolActions are not one to one for protocol number: " + srcProtocol.getProtocolNumber());
    		return;
    	}
    	
    	// Loop over matching ProtocolActions and copy found correspondence
    	Map<ProtocolActionBase, ProtocolActionBase> oldToNewActionMap = getMatchingProtocolActions(srcActions, destActions);
		for (Entry<ProtocolActionBase, ProtocolActionBase> entry : oldToNewActionMap.entrySet()) {
			ProtocolActionBase srcAction = entry.getKey();
			ProtocolActionBase destAction = entry.getValue();
			List<ProtocolCorrespondence> copiedCorrespondences = copyMatchingCorrespondence(srcAction, destAction);
			if (copiedCorrespondences.size() > 0) {
				destAction.setProtocolCorrespondences(copiedCorrespondences);
				getBusinessObjectService().save(copiedCorrespondences);
			}
		}

	}
	
	/*
	 * Find any matching correspondence between src and dest, if found, make a copy
	 * and add it to the list to be returned.
	 */
	private List<ProtocolCorrespondence> copyMatchingCorrespondence(ProtocolActionBase srcAction, ProtocolActionBase destAction){
		List<ProtocolCorrespondence> matchingCorrepsondence = new ArrayList<ProtocolCorrespondence>();
		List<ProtocolCorrespondence> srcCorrespondences = srcAction.getProtocolCorrespondences();
		if (ObjectUtils.isNotNull(srcCorrespondences)) {
			for (ProtocolCorrespondence srcCorrespondence : srcCorrespondences) {
				ProtocolCorrespondence destCorrespondence = copyCorrespondence(srcCorrespondence, destAction);
				matchingCorrepsondence.add(destCorrespondence);
			}
		}
		return matchingCorrepsondence;
	}
	
	/*
	 * Given two lists of ProtocolActions, this method will correlate old-and-new
	 * ProtocolActions based on ActionId. Note, action ID is unique within
	 * one protocol version, and will map one-to-one between old-and-new entries
	 * at this phase of copying. 
	 */
	private Map<ProtocolActionBase, ProtocolActionBase> getMatchingProtocolActions(List<ProtocolActionBase> srcActions, List<ProtocolActionBase> destActions){
		Map<ProtocolActionBase, ProtocolActionBase> oldToNewActionMap = new HashMap<ProtocolActionBase, ProtocolActionBase>();
		for(ProtocolActionBase srcAction : srcActions){
			for(ProtocolActionBase destAction : destActions){
				if(srcAction.getActionId().equals(destAction.getActionId())){
					oldToNewActionMap.put(srcAction, destAction);
					break;
				}
			}
		}
		return oldToNewActionMap;
	}

	/*
	 * This method creates a copy of the input ProtocolCorrespondence
	 * and associates it with the input protocolAction.
	 */
	private ProtocolCorrespondence copyCorrespondence(ProtocolCorrespondence correspondence, ProtocolActionBase protocolAction){
		org.kuali.kra.irb.correspondence.ProtocolCorrespondence newCorrespondence = new org.kuali.kra.irb.correspondence.ProtocolCorrespondence();
		newCorrespondence.setActionId(correspondence.getActionId());
		newCorrespondence.setActionIdFk(protocolAction.getProtocolActionId());
		newCorrespondence.setCorrespondence(correspondence.getCorrespondence());
		newCorrespondence.setCreateTimestamp(correspondence.getCreateTimestamp());
		newCorrespondence.setCreateUser(correspondence.getCreateUser());
		newCorrespondence.setExtension(correspondence.getExtension());
		newCorrespondence.setFinalFlag(correspondence.getFinalFlag());
		newCorrespondence.setFinalFlagTimestamp(correspondence.getFinalFlagTimestamp());
		newCorrespondence.setForwardName(correspondence.getForwardName());
		newCorrespondence.setHoldingPage(correspondence.isHoldingPage());
		newCorrespondence.setId(null);
		newCorrespondence.setNewCollectionRecord(correspondence.isNewCollectionRecord());
		newCorrespondence.setProtocol(protocolAction.getProtocol());
		newCorrespondence.setProtocolAction(protocolAction);
		newCorrespondence.setProtocolCorrespondenceType(correspondence.getProtocolCorrespondenceType());
		newCorrespondence.setProtocolId(protocolAction.getProtocolId());
		newCorrespondence.setProtocolNumber(protocolAction.getProtocolNumber());
		newCorrespondence.setProtoCorrespTypeCode(correspondence.getProtoCorrespTypeCode());
		newCorrespondence.setRegenerateFlag(correspondence.isRegenerateFlag());
		newCorrespondence.setSequenceNumber(correspondence.getSequenceNumber());
		newCorrespondence.setUpdateTimestamp(correspondence.getUpdateTimestamp());
		newCorrespondence.setUpdateUser(correspondence.getUpdateUser());
		newCorrespondence.setUpdateUserSet(correspondence.isUpdateUserSet());
		newCorrespondence.setVersionNumber(correspondence.getVersionNumber());
		return newCorrespondence;
	}

	protected BusinessObjectService getBusinessObjectService() {
		return businessObjectService;
	}

	public void setBusinessObjectService(BusinessObjectService businessObjectService) {
		this.businessObjectService = businessObjectService;
	}
	
}
