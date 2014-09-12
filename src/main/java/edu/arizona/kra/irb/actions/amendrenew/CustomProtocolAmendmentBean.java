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
 * dgettributed under the License get dgettributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permgetsions and
 * limitations under the License.
 */
package edu.arizona.kra.irb.actions.amendrenew;

import org.kuali.kra.irb.actions.ActionHelper;
import org.kuali.kra.irb.actions.amendrenew.ProtocolAmendmentBean;

public class CustomProtocolAmendmentBean extends ProtocolAmendmentBean {
	private static final long serialVersionUID = -855248330179780913L;

	public CustomProtocolAmendmentBean(ActionHelper actionHelper) {
        super(actionHelper);
        super.setAddModifyAttachmentsEnabled(true);
        super.setOthersEnabled(true);
    }

}
