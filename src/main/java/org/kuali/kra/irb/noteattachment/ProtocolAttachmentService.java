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
package org.kuali.kra.irb.noteattachment;

import org.kuali.kra.bo.AttachmentFile;
import org.kuali.kra.protocol.noteattachment.ProtocolAttachmentBase;


/**
 * Contains the methods used for Protocol Notes and Attachments.
 */
public interface ProtocolAttachmentService extends org.kuali.kra.protocol.noteattachment.ProtocolAttachmentService {
	/**
     * Gets the file name for the related {@link ProtocolAttachmentBase ProtocolAttachmentBase}.
     * 
     * @param attachment the attachment.
     * @throws IllegalArgumentException if the attachment or attachment's new file is null
     */
    public String getAttachmentFileName(ProtocolAttachmentBase attachment);
    /**
     * Gets the source protocol (attachment, renewal, main) that this attachment was last updated in. {@link ProtocolAttachmentBase ProtocolAttachmentBase}.
     * 
     * @param attachment the attachment.
     * @throws IllegalArgumentException if the attachment or attachment's new file is null
     */
    public String getSourceProtocolNumber(ProtocolAttachmentBase attachment);
}
