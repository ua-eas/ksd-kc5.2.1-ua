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
package edu.arizona.kra.irb;

import org.kuali.kra.infrastructure.KeyConstants;
import org.kuali.kra.protocol.ProtocolFormBase;
import org.kuali.kra.protocol.actions.ActionHelperBase;

import edu.arizona.kra.irb.actions.CustomActionHelper;
import org.kuali.kra.protocol.noteattachment.ProtocolAttachmentProtocolBase;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kns.util.ActionFormUtilMap;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This is the custom code for the protocol form class for UofA
 */
public class CustomProtocolForm extends org.kuali.kra.irb.ProtocolForm {
	private static final long serialVersionUID = -9117901812636757211L;
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomProtocolForm.class);


    public CustomProtocolForm() throws Exception {
        super();
    }

    @Override
    protected ActionHelperBase createNewActionHelperInstanceHook(ProtocolFormBase protocolForm) throws Exception {
        return new CustomActionHelper((CustomProtocolForm)protocolForm);
    }


    /**
     * UAR-2279 - Fix from IU: Prevent scrambling attachment data. If the user uses "back" or multiple tabs in their browser
     * while they are editing a protocol then both tabs will have the same value for documentFormKey. The forms
     * will both try to update the same document in the session. If changes made in one tab modify the ordering of the
     * attachments then saving from the other tab will write data to the wrong attachment objects. This code
     * tries to detect that by comparing a new hidden form field containing the id of the ProtocolAttachmentProtocol
     * to the id from the attachmentProtocols array that is about to be updated. If they don't match then throw an
     * exception so the user sees an error instead of saving bad data.
     */

    public void populate(HttpServletRequest request) {

        for (Map.Entry<String, String[]> entry : (Set<Map.Entry>)request.getParameterMap().entrySet()) {
            String paramName = entry.getKey();
            if (paramName.startsWith("document.protocolList[0].attachmentProtocols[") && paramName.endsWith("].id")) {

                // Extract the index to the attachment array from the parameter name
                int index = Integer.parseInt(paramName.substring(45, paramName.lastIndexOf(']')));

                String formAttachmentId = entry.getValue()[0];
                String actualAttachmentId = "";

                // If index is valid and the specified attachment's id is not null then assign it to actualAttachmentId
                List<ProtocolAttachmentProtocolBase> attachments = getProtocolDocument().getProtocol().getAttachmentProtocols();
                if (index < attachments.size()) {
                    actualAttachmentId = attachments.get(index).getId() == null ? "" : attachments.get(index).getId().toString();
                }

                if (!actualAttachmentId.equals(formAttachmentId)) {
                    ConfigurationService kualiConfiguration = CoreApiServiceLocator.getKualiConfigurationService();
                    RuntimeException e = new RuntimeException( kualiConfiguration.getPropertyValueAsString(KeyConstants.AUDIT_ERROR_PROTOCOL_ATTACHMENT_NOT_IN_SYNC) );
                    LOG.error("Protocol Attachments Not in Sync Exception: "+e.getMessage());
                    throw e;
                }
            }
        }

        super.populate(request);

    }
 

}
