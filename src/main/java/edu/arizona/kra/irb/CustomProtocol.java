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
package edu.arizona.kra.irb;

import org.apache.commons.lang.StringUtils;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.irb.Protocol;
import org.kuali.kra.irb.ProtocolDocument;
import org.kuali.kra.protocol.noteattachment.ProtocolAttachmentFilterBase;

import edu.arizona.kra.irb.noteattachment.CustomProtocolAttachmentFilter;

/**
 * 
 * This class is the Custom UofA Protocol Business Object.
 */
public class CustomProtocol extends Protocol {
   
    private static final long serialVersionUID = 2770974427187041205L;

    /**
     * 
     * Default constructor
     */
    public CustomProtocol() {
        super();
    }
    
    /* 
     * Override the ProtocolAttachmentFilter from the super to include the UA Custom class for sorting protocol attachments on the Amendment/Renewal number
     */
    @Override
    public void initializeProtocolAttachmentFilter() {
        ProtocolAttachmentFilterBase protocolAttachmentFilter = new CustomProtocolAttachmentFilter();
        
        //Load the default set as a parameter for the attachment sort
        try {
            String defaultSortBy = getParameterService().getParameterValueAsString(ProtocolDocument.class, Constants.PARAMETER_PROTOCOL_ATTACHMENT_DEFAULT_SORT);
            if (StringUtils.isNotBlank(defaultSortBy)) {
                protocolAttachmentFilter.setSortBy(defaultSortBy);
            }
        } catch (Exception e) {
            //Let the default sort to be null... and print the exception
            e.printStackTrace();
        }        
        
        setProtocolAttachmentFilter(protocolAttachmentFilter);
    }
    
}
