/*
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.osedu.org/licenses/ECL-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.arizona.kra.irb.actions.copy;

import java.util.List;

import org.kuali.kra.irb.Protocol;
import org.kuali.kra.irb.ProtocolDocument;
import org.kuali.kra.irb.actions.risklevel.ProtocolRiskLevel;
import org.kuali.kra.irb.protocol.participant.ProtocolParticipant;

/**
 * A class that encapsulates custom UA logic that pertains
 * to IRB but not IACUC.
 */
public class CustomProtocolCopyServiceImpl extends org.kuali.kra.irb.actions.copy.ProtocolCopyServiceImpl {
    
    
    /**
     * Copy lists that exist in IRB but not IACUC.
     * 
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
	protected void copyProtocolLists(ProtocolDocument srcDoc, ProtocolDocument destDoc) {
		super.copyProtocolLists(srcDoc, destDoc);
		Protocol srcProtocol = srcDoc.getProtocol();
        Protocol destProtocol = destDoc.getProtocol();
		destProtocol.setProtocolRiskLevels((List<ProtocolRiskLevel>) deepCopy(srcProtocol.getProtocolRiskLevels()));
		destProtocol.setProtocolParticipants((List<ProtocolParticipant>) deepCopy(srcProtocol.getProtocolParticipants()));
	}

}

	
