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

import org.kuali.kra.protocol.ProtocolFormBase;
import org.kuali.kra.protocol.actions.ActionHelperBase;

import edu.arizona.kra.irb.actions.CustomActionHelper;

/**
 * This class...
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class CustomProtocolForm extends org.kuali.kra.irb.ProtocolForm {
	private static final long serialVersionUID = -9117901812636757211L;

	public CustomProtocolForm() throws Exception {
        super();
    }

    @Override
    protected ActionHelperBase createNewActionHelperInstanceHook(ProtocolFormBase protocolForm) throws Exception {
        return new CustomActionHelper((CustomProtocolForm) protocolForm);
    }

}
