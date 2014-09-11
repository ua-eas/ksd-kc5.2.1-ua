/*
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.arizona.kra.irb.noteattachment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * This class represents the UofA Custom Protocol Attachment Protocol.
 */
public class ProtocolAttachmentProtocol extends org.kuali.kra.irb.noteattachment.ProtocolAttachmentProtocol {

    private static final long serialVersionUID = -1874526982821035896L;

    
    private String sourceProtocolAmendRenewalNumber;
    private String sourceProtocolNumber;

    
    /**
     * Returns the source protocol number for when this attachment was last added in.
     */
    public String getSourceProtocolNumber() {
        if (sourceProtocolNumber == null) {
            if (getFileId() != null) {
                // find the protocol number for this attachment's most recent attachment/modification
                // by retrieving all attachments with the same fileId and ordering them by id
                Map param = new HashMap();
                param.put("fileId", getFileId());
                BusinessObjectService businessObjectService = KraServiceLocator.getService(BusinessObjectService.class);
                List<ProtocolAttachmentProtocol> protocolAttachmentProtocols = 
                        (List<ProtocolAttachmentProtocol>) businessObjectService.findMatchingOrderBy(ProtocolAttachmentProtocol.class, param, "id", true);
                ProtocolAttachmentProtocol protocolAttachmentProtocol = protocolAttachmentProtocols.get(0);
                sourceProtocolNumber = protocolAttachmentProtocol.getProtocolNumber();
            }
            // Null FileId means attachment newly added and is not in the database yet
            else {
                sourceProtocolNumber = getProtocolNumber();
            }
        }
        // avoid further lookups when this field is accessed and NPEs
        if (sourceProtocolNumber == null) {
            sourceProtocolNumber = "";
        }
        return sourceProtocolNumber;
    }

    
    /**
     * Returns the source amendment or renewal this attachment was added in.
     */
    public String getSourceProtocolAmendRenewalNumber() {
        String sourceProtocolNumber = getSourceProtocolNumber();

        if (sourceProtocolNumber.length() >= 10) {
            sourceProtocolAmendRenewalNumber = sourceProtocolNumber.substring(10);
        } else {
            sourceProtocolAmendRenewalNumber = "";
        }

        return sourceProtocolAmendRenewalNumber;
    }
    
}
