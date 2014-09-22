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

import org.kuali.kra.irb.Protocol;
import org.kuali.kra.protocol.ProtocolBase;
import org.kuali.kra.irb.ProtocolForm;
import org.kuali.kra.irb.noteattachment.NotesAttachmentsHelper;

import edu.arizona.kra.irb.noteattachment.CustomProtocolAttachmentProtocol;


public class CustomNotesAttachmentsHelper extends NotesAttachmentsHelper {

    public CustomNotesAttachmentsHelper(ProtocolForm form) {
        super(form);
    }

    @Override
    protected CustomProtocolAttachmentProtocol createNewProtocolAttachmentProtocolInstanceHook(ProtocolBase protocol) {
        return new CustomProtocolAttachmentProtocol((Protocol) protocol);
    }
    
    @Override
    protected CustomProtocolAttachmentFilter createNewProtocolAttachmentFilterInstanceHook() {
        return new CustomProtocolAttachmentFilter();
    }
    
}
