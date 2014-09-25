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

import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.kra.irb.noteattachment.ProtocolAttachmentService;
import org.kuali.kra.protocol.ProtocolFormBase;
import org.kuali.kra.protocol.noteattachment.ProtocolAttachmentProtocolBase;
import org.kuali.kra.protocol.noteattachment.ProtocolAttachmentVersioningUtilityBase;
import org.kuali.kra.service.VersioningService;

/**
 * UofA Custom Class used for versioning protocol attachments. {@link ProtocolAttachmentVersioningUtilityBase}.
 * 
 */
public class CustomProtocolAttachmentVersioningUtility extends ProtocolAttachmentVersioningUtilityBase {

    
    public CustomProtocolAttachmentVersioningUtility(ProtocolFormBase form) {
        super(form, KraServiceLocator.getService(ProtocolAttachmentService.class), 
                    KraServiceLocator.getService(VersioningService.class));
    }

    @Override
    protected Class<? extends ProtocolAttachmentProtocolBase> getProtocolAttachmentProtocolClassHook() {
        return CustomProtocolAttachmentProtocol.class;
    }
}
